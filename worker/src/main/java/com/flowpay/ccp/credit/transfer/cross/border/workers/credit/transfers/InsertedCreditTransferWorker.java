package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.business.log.handler.process.Process;
import com.flowpay.ccp.business.log.handler.process.ProcessInfo;
import com.flowpay.ccp.business.log.handler.process.RestartingProcess;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.RestartPollableJob;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.MultipleResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.ResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted.InsertedSubStatus;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.resources.poll.client.MultiplePollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.PollService;
import com.flowpay.ccp.resources.poll.client.PollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.dto.PollBucket;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;

public class InsertedCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(InsertedCreditTransferWorker.class);

    private static String getID(String resourceName, UUID resourceID) {
        return InsertedCreditTransferWorker.class.getName() + ".check.settlement:" + resourceName + ":" + resourceID;
    }

    URL registryURL;
    CreditTransferService service;
    DataSources dataSources;
    JobPublisher jobPublisher;
    JobSubscriber jobSubscriber;
    ReactiveRedisDataSource redisClient;
    PollService pollService;
    ServiceConfig config;
    URL cipUrl;

    InsertedCreditTransferWorker(
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") URL registryURL,
            CreditTransferService service,
            DataSources dataSources,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> channel,
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) JobSubscriber jobSubscriber,
            ReactiveRedisDataSource redisClient,
            PollService pollService,
            ServiceConfig config,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.cip.client\".url") URL cipUrl) {
        this.registryURL = registryURL;
        this.service = service;
        this.dataSources = dataSources;
        this.redisClient = redisClient;
        this.pollService = pollService;
        this.jobPublisher = new JobPublisher(channel);
        this.jobSubscriber = jobSubscriber;
        this.config = config;
        this.cipUrl = cipUrl;
    }

    public void onStart(@Observes StartupEvent event) {
        LOG.info("Starting InsertedCreditTransferWorker");
        jobSubscriber.subscribe(
                Constants.JOB_CHECK_CREDIT_TRANSFER_SETTLEMENT,
                new HandleCreditTransferPayload.Deserializer(),
                this.pollService.jobWithPollableData(Constants.JOB_RESTART_CHECK_CREDIT_TRANSFER_SETTLEMENT,
                        this::checkSettlementKindJob));
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_CHECK_CREDIT_TRANSFER_SETTLEMENT,
                new PollBucket.Deserializer(),
                this.pollService.jobWithPollableData(Constants.JOB_RESTART_CHECK_CREDIT_TRANSFER_SETTLEMENT,
                        this::restartCheckSettlementKindJob));
    }

    public Uni<Void> checkSettlementKindJob(HandleCreditTransferPayload payload) {
        return this.checkSettlementKind(payload, null);
    }

    public Uni<Void> restartCheckSettlementKindJob(PollBucket bucket) {
        var creditTransferID = this.redisClient.value(UUID.class)
                .getdel(getID(bucket.resourceName(), bucket.resourceID()));
        var processID = this.redisClient.value(String.class).getdel(bucket.resourceName() + ":" + bucket.resourceID());
        return Uni.combine().all().unis(creditTransferID, processID).withUni((credID, id) -> {
            return this.checkSettlementKind(
                    new HandleCreditTransferPayload(credID),
                    new RestartPollableJob(
                            Objects.requireNonNullElseGet(
                                    id,
                                    () -> bucket.resourceName() + ":" + bucket.resourceID())));
        });
    }

    @Process
    @ProcessInfo("inserted:check-settlement-kind")
    @RestartingProcess
    public Uni<Void> checkSettlementKind(
            HandleCreditTransferPayload payload,
            RestartPollableJob restartProcess) {
        LOG.info("Checking settlement kind for credit transfer " + payload.idBonificoExtraSepa());
        var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var entity = new BonificoExtraSepa.Entity();
        var repository = entity.repository(dataSources.dataSource(authenticatedUser));
        return repository.getByIdLocked(payload.idBonificoExtraSepa())
                .flatMap(creditTransfer -> service.checkSettlementCompatibility(creditTransfer, authenticatedUser)
                        .flatMap(ignored -> {
                            LOG.info("Settlement check passed for credit transfer " + payload.idBonificoExtraSepa());
                            return repository.addNewStatusHistory(creditTransfer,
                                    InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_PASSATO.name(),
                                    "Le verifiche di compatibilità sul sistema di regolamento hanno dato esito positivo.")
                                    .call(() -> jobPublisher.scheduleJob(
                                            new JobData(
                                                    UUID.randomUUID(),
                                                    Constants.JOB_VERIFY_CREDIT_TRANSFER,
                                                    new HandleCreditTransferPayload(payload.idBonificoExtraSepa()))));
                        })
                        .onFailure().recoverWithUni(throwable -> {
                            LOG.error("Settlement check failed for credit transfer " + payload.idBonificoExtraSepa(),
                                    throwable);
                            if (throwable instanceof PollableResourceAccepted pollable) {
                                return repository.addNewStatusHistory(
                                        creditTransfer,
                                        InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_IN_ATTESA.name(),
                                        "Le verifiche di compatibilità procederrano quando saranno disponibile le informazioni sulle banche.")
                                        .flatMap(ignored -> redisClient.value(UUID.class)
                                                .setex(getID(pollable.getResourceName(), pollable.getPollResourceID()),
                                                        3600, payload.idBonificoExtraSepa()))
                                        .replaceWith(Uni.createFrom().failure(
                                                new ResourceNotReadyInterruption(pollable)));
                            }
                            if (throwable instanceof MultiplePollableResourceAccepted pollable) {
                                var result = repository.addNewStatusHistory(
                                        creditTransfer,
                                        InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_IN_ATTESA.name(),
                                        "Le verifiche di compatibilità procederrano quando saranno disponibile le informazioni sulle banche.");
                                var id = UUID.randomUUID();
                                for (var cause : pollable.getPollableResourceAccepteds()) {
                                    result = result.flatMap(ignored -> redisClient.value(UUID.class).setex(
                                            getID(cause.getResourceName(), cause.getPollResourceID()), 3600,
                                            payload.idBonificoExtraSepa()));
                                }
                                return result.replaceWith(Uni.createFrom()
                                        .failure(new MultipleResourceNotReadyInterruption(pollable, id)));
                            }
                            return InfoStatoErrore
                                    .storeError(creditTransfer, dataSources.dataSource(authenticatedUser), throwable)
                                    .call(() -> repository.addNewStatusHistory(creditTransfer,
                                            InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_FALLITO.name(),
                                            "Le verifiche di compatibilità sul sistema di regolamento hanno dato esito negativo."))
                                    .call(() -> repository.updateStatusAndUnlock(creditTransfer,
                                            CreditTransferStatus.IN_ERRORE))
                                    .replaceWith(Uni.createFrom().failure(throwable));
                        }));
    }
}
