package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import com.flowpay.ccp.auth.client.AuthConstants;
import com.flowpay.ccp.auth.client.CabelCredentialForwardFilter;
import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.business.log.handler.process.Process;
import com.flowpay.ccp.business.log.handler.process.ProcessInfo;
import com.flowpay.ccp.business.log.handler.process.RestartingProcess;
import com.flowpay.ccp.cip.client.CIPClient;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.cip.RestartProcess;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.errors.InternalProcessError;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.net.URL;
import java.util.UUID;


//@SuppressWarnings("unused")
public class HandleCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(HandleCreditTransferWorker.class);


    JobSubscriber jobSubscriber;

    JobPublisher jobPublisher;

    DataSources sources;

    ServiceConfig config;

    URL cipUrl;

    CreditTransferService service;

    HandleCreditTransferWorker(
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) JobSubscriber jobSubscriber,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> channel,
            DataSources sources,
            ServiceConfig config,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.cip.client\".url") URL cipUrl,
            CreditTransferService service
    ) {
        this.jobSubscriber = jobSubscriber;
        this.jobPublisher = new JobPublisher(channel);
        this.sources = sources;
        this.config = config;
        this.cipUrl = cipUrl;
        this.service = service;
    }

    public void start(@Observes StartupEvent ignoredEvent) {
        LOG.info("Starting HandleCreditTransferWorker");
        jobSubscriber.subscribe(Constants.JOB_HANDLE_CREDIT_TRANSFER, new HandleCreditTransferPayload.Deserializer(), this::handleCreditTransfer);
        jobSubscriber.subscribe(Constants.JOB_RESTART_PROCESS, new RestartProcess.Deserializer(), this::restartProcess);
    }

    Uni<Void> restartProcess(RestartProcess process) {
        var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var filter = new CabelCredentialForwardFilter(authenticatedUser);
        CIPClient client = QuarkusRestClientBuilder.newBuilder().register(filter)
                .baseUrl(cipUrl).build(CIPClient.class);
        String abi = authenticatedUser.getAttribute(AuthConstants.ABI_ATTRIBUTE);

        var dataSource = sources.getDataSource(abi);
        var entity = new BonificoExtraSepa.Entity();
        var repository = entity.repository(dataSource);

        return repository.getByIdLocked(process.creditTransferID())
                .flatMap(creditTransfer -> {
                    if (creditTransfer.stato() != process.status()) {
                        throw new InternalProcessError("credit transfer status does not match");
                    }

                    return switch (creditTransfer.stato()) {
                        case TO_BE_MANAGED -> repository.unlock(creditTransfer.id());
                        case INSERITO -> this.processInsertedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case DA_CONFERMARE -> repository.unlock(creditTransfer.id());
                        case CONFERMATO -> this.processConfirmedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case CONFERMATO_STEP_1_COMPLETO -> repository.unlock(creditTransfer.id());
                        case CONFERMATO_STEP_2 -> this.processConfirmedStep2CreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case ELIMINATO -> repository.unlock(creditTransfer.id());
                        case WCL_NON_PASSATA -> repository.unlock(creditTransfer.id());
                        case WCL_NEGATA -> repository.unlock(creditTransfer.id());
                        case DA_AUTORIZZARE -> this.processToBeAuthorizedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case AUTORIZZATO -> this.processAuthorizedCreditTransfer(creditTransfer, authenticatedUser, process);
                        case INVIATO -> this.processSentCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case REGOLATO -> repository.unlock(creditTransfer.id());
                        case IN_ERRORE -> repository.unlock(creditTransfer.id());
                        case NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO -> this.processNotAuthorizedReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case NON_AUTORIZZATO -> repository.unlock(creditTransfer.id());
                        case RIFIUTATO_RIMBORSO_PROGRAMMATO -> this.processRejectedReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case RIFIUTATO -> repository.unlock(creditTransfer.id());
                        case STORNO_RICHIESTO -> this.processChargeBackRequestedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case STORNO_INVIATO -> repository.unlock(creditTransfer.id());
                        case STORNO_ACCETTATO -> repository.unlock(creditTransfer.id());
                        case STORNO_RIFIUTATO -> repository.unlock(creditTransfer.id());
                        case STORNO_RIMBORSO_PROGRAMMATO -> this.processChargeBackReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, process);
                        case RIMBORSATO -> repository.unlock(creditTransfer.id());
                    };
                });
    }

    Uni<Void> handleCreditTransfer(HandleCreditTransferPayload payload) {
        var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var filter = new CabelCredentialForwardFilter(authenticatedUser);
        CIPClient client = QuarkusRestClientBuilder.newBuilder().register(filter)
                .baseUrl(cipUrl)
                .build(CIPClient.class);

        var dataSource = sources.dataSource(authenticatedUser);
        var entity = new BonificoExtraSepa.Entity();
        var repository = entity.repository(dataSource);

        return repository.getByIdAndLock(payload.idBonificoExtraSepa())
                .flatMap(creditTransfer -> switch (creditTransfer.stato()) {
                    case TO_BE_MANAGED -> repository.unlock(creditTransfer.id());
                    case INSERITO -> this.processInsertedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case DA_CONFERMARE -> repository.unlock(creditTransfer.id());
                    case CONFERMATO -> this.processConfirmedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case CONFERMATO_STEP_1_COMPLETO -> repository.unlock(creditTransfer.id());
                    case CONFERMATO_STEP_2 -> this.processConfirmedStep2CreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case ELIMINATO -> repository.unlock(creditTransfer.id());
                    case WCL_NON_PASSATA -> repository.unlock(creditTransfer.id());
                    case WCL_NEGATA -> repository.unlock(creditTransfer.id());
                    case DA_AUTORIZZARE -> this.processToBeAuthorizedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case AUTORIZZATO -> this.processAuthorizedCreditTransfer(creditTransfer, authenticatedUser,null);
                    case INVIATO -> this.processSentCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case REGOLATO -> repository.unlock(creditTransfer.id());
                    case IN_ERRORE -> repository.unlock(creditTransfer.id());
                    case NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO -> this.processNotAuthorizedReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case NON_AUTORIZZATO -> repository.unlock(creditTransfer.id());
                    case RIFIUTATO_RIMBORSO_PROGRAMMATO -> this.processRejectedReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case RIFIUTATO -> repository.unlock(creditTransfer.id());
                    case STORNO_RICHIESTO -> this.processChargeBackRequestedCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case STORNO_INVIATO -> repository.unlock(creditTransfer.id());
                    case STORNO_ACCETTATO -> repository.unlock(creditTransfer.id());
                    case STORNO_RIFIUTATO -> repository.unlock(creditTransfer.id());
                    case STORNO_RIMBORSO_PROGRAMMATO -> this.processChargeBackReimbursementScheduledCreditTransfer(creditTransfer, dataSource, authenticatedUser, client, null);
                    case RIMBORSATO -> repository.unlock(creditTransfer.id());
                });
    }

    @Process
    @ProcessInfo("process-inserted-credit-transfer")
    @RestartingProcess
    Uni<Void> processInsertedCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return this.jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_CHECK_CREDIT_TRANSFER_SETTLEMENT,
                        new HandleCreditTransferPayload(creditTransfer.id())
                )
        );
    }

    @Process
    @ProcessInfo("process-confirmed-credit-transfer")
    @RestartingProcess
    Uni<Void> processConfirmedCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return this.jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_CONFIRMED_CREDIT_TRANSFER,
                        new HandleCreditTransferPayload(creditTransfer.id())
                )
        );
    }

    @Process
    @ProcessInfo("process-confirmed-step2-credit-transfer")
    @RestartingProcess
    Uni<Void> processConfirmedStep2CreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return this.jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_CONFIRMED_STEP_2_CREDIT_TRANSFER,
                        new HandleCreditTransferPayload(creditTransfer.id())
                )
        );
    }

    Uni<Void> processToBeAuthorizedCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return this.jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_TO_BE_AUTHORIZED_CREDIT_TRANSFER,
                        new HandleCreditTransferPayload(creditTransfer.id())
                )
        );
    }

    Uni<Void> processCancelledReimbursementScheduledCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

    @Process
    @ProcessInfo("process-authorized-credit-transfer")
    @RestartingProcess
    Uni<Void> processAuthorizedCreditTransfer(BonificoExtraSepa creditTransfer, SecurityIdentity authenticatedUser, RestartProcess process) {
        return this.jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_AUTHORIZED_CREDIT_TRANSFER,
                        new HandleCreditTransferPayload(creditTransfer.id())
                )
        );
    }

    Uni<Void> processSentCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

    Uni<Void> processNotAuthorizedReimbursementScheduledCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

    Uni<Void> processRejectedReimbursementScheduledCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

    Uni<Void> processChargeBackRequestedCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

    Uni<Void> processChargeBackReimbursementScheduledCreditTransfer(BonificoExtraSepa creditTransfer, PgPool dataSource, SecurityIdentity authenticatedUser, CIPClient client, RestartProcess restartProcessInfo) {
        return Uni.createFrom().voidItem();
    }

}
