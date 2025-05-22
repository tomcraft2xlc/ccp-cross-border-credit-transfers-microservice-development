package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import com.flowpay.ccp.auth.client.AuthConstants;
import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.business.log.handler.process.Process;
import com.flowpay.ccp.business.log.handler.process.ProcessInfo;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.SendFileOverFTP;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.ISOMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.ProduceCoverageMessage;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.InformazioniSistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.resources.poll.client.PollService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthorizedCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(AuthorizedCreditTransferWorker.class);

    private static String getID(String resourceName, UUID resourceID) {
        return "ccp.credit.transfer.cross.border.retrieve.account:" + resourceName + ":" + resourceID;
    }

    @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL)
    JobSubscriber subscriber;

    JobPublisher jobPublisher;
    JobPublisher externalJobPublisher;

    PollService pollService;

    DataSources dataSources;

    URL registryEndpoint;

    ServiceConfig serviceConfig;

    BanksConfig banksConfig;

    ReactiveRedisDataSource redisDataSource;

    AuthorizedCreditTransferWorker(
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) JobSubscriber subscriber,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> internalEmitter,
            @Channel(Constants.FTP_SERVICE_CHANNEL_NAME) MutinyEmitter<JobData> externalEmitter,
            PollService pollService,
            DataSources dataSources,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") URL registryEndpoint,
            ServiceConfig serviceConfig,
            BanksConfig banksConfig,
            ReactiveRedisDataSource redisDataSource
    ) {
        this.subscriber = subscriber;
        this.externalJobPublisher = new JobPublisher(externalEmitter);
        this.jobPublisher = new JobPublisher(internalEmitter);
        this.pollService = pollService;
        this.dataSources = dataSources;
        this.registryEndpoint = registryEndpoint;
        this.serviceConfig = serviceConfig;
        this.banksConfig = banksConfig;
        this.redisDataSource = redisDataSource;
    }

    public void onStart(@Observes StartupEvent event) {
        LOG.info("Starting AuthorizedCreditTransfreWorker");
        this.subscriber.subscribe(
                Constants.JOB_AUTHORIZED_CREDIT_TRANSFER,
                new HandleCreditTransferPayload.Deserializer(),
                pollService.jobWithPollableData(Constants.JOB_RESTART_AUTHORIZED_CREDIT_TRANSFER, this::produceXML)
        );
    }

    public Uni<Void> produceXML(HandleCreditTransferPayload payload) {
        var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var pool = dataSources.dataSource(authenticatedUser);
        var repository = new BonificoExtraSepa.Entity().repository(pool);
        return repository.getByIdLockedForSending(payload.idBonificoExtraSepa())
                .map(BonificoExtraSepa::withLinkedEntities)
                .flatMap(creditTransferWithLinkedEntities -> creditTransferWithLinkedEntities.loadAll(pool).collect().asList().replaceWith(creditTransferWithLinkedEntities))
                .flatMap(creditTransferWithLinkedEntities -> this.produceXML(
                        creditTransferWithLinkedEntities,
                        pool,
                        authenticatedUser,
                        banksConfig.bank().get(authenticatedUser.<String>getAttribute(AuthConstants.ABI_ATTRIBUTE))));
    }

    //Probabilmente da rimuovere tutta questa retrieve degli account in quanto li abbiamo già a DB
//    @Process
//    @ProcessInfo("authorized:retrieve-account-data")
//    @RestartingProcess
//    public Uni<Void> retrieveAccountData(HandleCreditTransferPayload payload, PgPool pool, SecurityIdentity authenticatedUser, RestartPollableJob process) {
//
//        var entity = new BonificoExtraSepa.Entity();
//        var repository = entity.repository(pool);
//
//        return repository.getByIDLocked(payload.idBonificoExtraSepa())
//        .map(BonificoExtraSepa::linkedEntities)
//        .flatMap(creditTransferWithLinkedEntities -> creditTransferWithLinkedEntities.loadDebtorAccount(pool).replaceWith(creditTransferWithLinkedEntities))
//        .flatMap(creditTransferWithLinkedEntities -> {
//            if (creditTransferWithLinkedEntities.debtorAccount() == null) {
//                return this.getDebtorAccount(creditTransferWithLinkedEntities, authenticatedUser);
//            } else {
//                return this.jobPublisher.scheduleJob(
//                        new JobData(
//                                UUID.randomUUID(),
//                                Constants.JOB_PRODUCE_XML,
//                                payload
//                        )
//                );
//            }
//        });
//    }

//    private BiFunction<String, String, Uni<? extends DebtorAccountConvertible>> getAccountFunction(SottoTipologiaBonifico kind, AccountType accountType, SecurityIdentity authenticatedUser) {
//        var client = QuarkusRestClientBuilder.newBuilder()
//                .register(new CabelCredentialForwardFilter(authenticatedUser))
//                .register(new PollableResourceAcceptedHandler())
//                .baseUrl(registryEndpoint)
//                .build(RegistryClient.class);
//
//        if (kind.eBancaABanca()) {
//            //TODO: update this when we have the correct APIs
//            return client::getAccountData;
//        } else {
//            if (accountType == AccountType.ACCOUNT) {
//                return client::getAccountData;
//            } else {
//                return client::getVoceContabile;
//            }
//        }
//    }

//    private Uni<Void> getDebtorAccount(BonificoExtraSepa.CreditTransferWithLinkedEntities creditTransferWithLinkedEntities, SecurityIdentity authenticatedUser) {
//        var repository = new BonificoExtraSepa.Entity().repository(dataSources.dataSource(authenticatedUser));
//        return creditTransferWithLinkedEntities.loadKind(dataSources.dataSource(authenticatedUser))
//                .map(ignored -> getAccountFunction(creditTransferWithLinkedEntities.kind(), creditTransferWithLinkedEntities.creditTransfer().accountType(), authenticatedUser))
//                .flatMap(function -> function.apply(
//                creditTransferWithLinkedEntities.creditTransfer().accountID(),
//                serviceConfig.url() + "/poll"
//                ))
//                .flatMap(accountData -> {
//                    var entity = new DebtorAccount.Entity();
//                    var accountRepository = entity.repository(dataSources.dataSource(authenticatedUser));
//                    return accountRepository.run(entity.insert(
//                            new DebtorAccount(
//                                creditTransferWithLinkedEntities.creditTransfer(),
//                                accountData
//                            )
//                    ));
//                })
//                .flatMap(ignored -> this.jobPublisher.scheduleJob(
//                        new JobData(
//                                UUID.randomUUID(),
//                                Constants.JOB_PRODUCE_XML,
//                                new HandleCreditTransferPayload(creditTransferWithLinkedEntities.creditTransfer().id())
//                        )
//                ))
//                .onFailure().recoverWithUni(failure -> {
//                    LOG.error("Failed to retrieve account data for credit transfer: " + creditTransferWithLinkedEntities.creditTransfer().id(), failure);
//                    if (failure instanceof PollableResourceAccepted pollable) {
//                        return repository.addNewStatusHistory(
//                                creditTransferWithLinkedEntities.creditTransfer(),
//                                AuthorizedSubStatus.WAITING_FOR_ACCOUNT_RETRIEVAL.name(),
//                            "Il processo è interrotto in attesa dei dati del conto.")
//                            .flatMap(ignored -> redisDataSource.value(UUID.class).setex(getID(pollable.getResourceName(), pollable.getPollResourceID()), 3600, creditTransferWithLinkedEntities.creditTransfer().id()))
//                            .replaceWith(Uni.createFrom().failure(new ResourceNotReadyInterruption(pollable)));
//                    }
//                    return InfoStatoErrore.storeError(creditTransferWithLinkedEntities.creditTransfer(), dataSources.dataSource(authenticatedUser), failure)
//                    .flatMap(ignored ->
//                        repository.updateStatusAndUnlock(creditTransferWithLinkedEntities.creditTransfer(), CreditTransferStatus.IN_ERRORE)
//                    ).replaceWith(Uni.createFrom().failure(failure));
//                });
//    }

    @Process
    @ProcessInfo("authorized:produce-xml")
    public Uni<Void> produceXML(
            BonificoExtraSepa.WithLinkedEntities creditTransferWithLinkedEntities,
            PgPool pool,
            SecurityIdentity authenticatedUser,
            BanksConfig.BankConfig config) {
        var deepened = creditTransferWithLinkedEntities.sottoTipologiaBonifico.deepened();
        return deepened.loadAll(pool)
                .collect().asList()
                .replaceWith(deepened)
                .map(creditTransferKindMessages -> {
                    var mappers = creditTransferKindMessages.mappatureMessaggi.stream().map(message -> Utils.loadClass(message.getEntity().mappaturaClasseQualificata())).toList();
                    var isCove = mappers.stream().anyMatch(mapper -> mapper.getClass().getAnnotation(ProduceCoverageMessage.class) != null);
                    var context = new MappingContext(
                            isCove,
                            Optional
                            .ofNullable(
                                    creditTransferKindMessages.bonificoExtraSepa.informazioniSistemaDiRegolamento)
                                    .map(InformazioniSistemaDiRegolamento.WithLinkedEntities::getEntity)
                                    .map(InformazioniSistemaDiRegolamento::stp)
                                    .orElse(false),
                            config
                    );
                    return mappers.parallelStream().map(mapper -> mapper.map(creditTransferKindMessages.bonificoExtraSepa, context)).toList();
                })
                .flatMap((List<ISOMapper.ISOData> messages) -> {
                    List<Uni<Void>> unis = messages.stream().map(data -> {
                        var file = Base64.getEncoder().encodeToString(data.xml().getBytes(StandardCharsets.UTF_8));
                        LOG.info("produced fileName: " + data.fileName() + ", tipologia: " + data.tipologia() + ", contentB64: " + file);
                        return externalJobPublisher.scheduleJob(
                                new JobData(
                                        UUID.randomUUID(),
                                        Constants.JOB_SEND_FILE_OVER_FTP,
                                        new SendFileOverFTP(
                                                data.fileName(),
                                                data.tipologia(),
                                                file
                                        )
                                )
                        );
                    }).toList();
                    return Uni.join().all(unis).andFailFast().replaceWithVoid();
                })
                .flatMap(ignored -> {
                    var entity = new BonificoExtraSepa.Entity();
                    var repository = entity.repository(pool);

                    return repository.updateStatusAndUnlock(creditTransferWithLinkedEntities.getEntity(), CreditTransferStatus.INVIATO);
                })
                .onFailure()
                .recoverWithUni(error -> {

                    var entity = new BonificoExtraSepa.Entity();
                    var repository = entity.repository(pool);

                    return InfoStatoErrore.storeError(creditTransferWithLinkedEntities.getEntity(), pool, error)
                            .flatMap(ignored ->
                                    repository.updateStatusAndUnlock(creditTransferWithLinkedEntities.getEntity(), CreditTransferStatus.IN_ERRORE)
                            ).replaceWith(Uni.createFrom().failure(error));
                });
    }
}
