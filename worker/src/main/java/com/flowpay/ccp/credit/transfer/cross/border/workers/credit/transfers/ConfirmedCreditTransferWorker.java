package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import com.flowpay.ccp.auth.client.AuthConstants;
import com.flowpay.ccp.auth.client.CabelCredentialForwardFilter;
import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.cip.client.CIPClient;
import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.cip.client.dto.CIPRequest;
import com.flowpay.ccp.cip.client.dto.HTTPResponseKind;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.clients.RegistryAnagraficheClient;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.ConfirmationFailure;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.MultipleResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.ResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation.DatiConfermaBonificoUpdater;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation.ExtraSepaCheckBonificoConfermaInputMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.ConfirmationStep;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonificoAvvertenza;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonificoErroreTecnico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConferma;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaAvvertenze;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaCambio;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaGenerico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaSaldoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted.InsertedSubStatus;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaAvvertenze;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaSaldoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.utils.TransactionUtils;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.CIPWrapperReply;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.RestartConfirmationProcessPayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.CabelOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroControlloRapportoInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroControlloRapportoOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroElencoPaesiInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroElencoPaesiOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroRecuperaSaldoRapportoInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.EsteroRecuperaSaldoRapportoOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.FlagSiNo;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.Segnalazione;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaCambioInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaCambioOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaOutput;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.registry.dto.responses.RicercaBicResponse;
import com.flowpay.ccp.resources.poll.client.MultiplePollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.PollService;
import com.flowpay.ccp.resources.poll.client.PollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.PollableResourceAcceptedHandler;
import com.flowpay.ccp.resources.poll.client.dto.PollBucket;
import com.google.common.collect.Streams;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.sqlclient.SqlClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.GenericType;

@ApplicationScoped
public class ConfirmedCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(ConfirmedCreditTransferWorker.class);

    private record RedisData(
            UUID processID,
            UUID creditTransferID) {
    }

    private static String getID(final String resourceName, final UUID resourceID) {
        return ConfirmedCreditTransferWorker.class.getName() + ".confirmation.transfer:" + resourceName + ":"
                + resourceID;
    }

    final URL registryURL;
    final CreditTransferService service;
    final DataSources dataSources;
    final JobPublisher jobPublisher;
    final JobSubscriber jobSubscriber;
    final ReactiveRedisDataSource redisClient;
    final PollService pollService;
    final ServiceConfig config;
    final URL cipUrl;
    final BanksConfig banksConfig;

    final DatiConfermaBonificoUpdater updater;
    final ExtraSepaCheckBonificoConfermaInputMapper extraSepaCheckBonificoConfermaInputMapper;

    ConfirmedCreditTransferWorker(
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") final URL registryURL,
            final CreditTransferService service,
            final DataSources dataSources,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) final MutinyEmitter<JobData> channel,
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) final JobSubscriber jobSubscriber,
            final ReactiveRedisDataSource redisClient,
            final PollService pollService,
            final ServiceConfig config,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.cip.client\".url") final URL cipUrl,
            final DatiConfermaBonificoUpdater updater,
            final ExtraSepaCheckBonificoConfermaInputMapper extraSepaCheckBonificoConfermaInputMapper,
            final BanksConfig banksConfig) {
        this.registryURL = registryURL;
        this.service = service;
        this.dataSources = dataSources;
        this.redisClient = redisClient;
        this.pollService = pollService;
        this.jobPublisher = new JobPublisher(channel);
        this.jobSubscriber = jobSubscriber;
        this.config = config;
        this.cipUrl = cipUrl;
        this.updater = updater;
        this.extraSepaCheckBonificoConfermaInputMapper = extraSepaCheckBonificoConfermaInputMapper;
        this.banksConfig = banksConfig;
    }

    public void onStart(@Observes final StartupEvent event) {
        LOG.info("Starting ConfirmedCreditTransferWorker");

        // STEP 1

        jobSubscriber.subscribe(
                Constants.JOB_CONFIRMED_CREDIT_TRANSFER,
                new HandleCreditTransferPayload.Deserializer(),
                this.pollService.jobWithPollableData(
                        Constants.JOB_RESTART_POLLABLE_CONFIRMED_CREDIT_TRANSFER,
                        this::confirmCreditTransfer));
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_POLLABLE_CONFIRMED_CREDIT_TRANSFER,
                new PollBucket.Deserializer(),
                this.pollService.jobWithPollableData(
                        Constants.JOB_RESTART_POLLABLE_CONFIRMED_CREDIT_TRANSFER,
                        this::restartConfirmCreditTransferWithPoll));
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_CONFIRMED_CREDIT_TRANSFER,
                new RestartConfirmationProcessPayload.Deserializer(),
                this.pollService.jobWithPollableData(
                        Constants.JOB_RESTART_POLLABLE_CONFIRMED_CREDIT_TRANSFER,
                        this::restartConfirmCreditTransfer));

        // STEP 2

        jobSubscriber.subscribe(
                Constants.JOB_CONFIRMED_STEP_2_CREDIT_TRANSFER,
                new HandleCreditTransferPayload.Deserializer(),
                this::confirmStep2CreditTransfer);

        jobSubscriber.subscribe(
                Constants.JOB_RESTART_CONFIRMED_STEP_2_CREDIT_TRANSFER,
                new RestartConfirmationProcessPayload.Deserializer(),
                this::restartConfirmStep2CreditTransfer);
    }

    public Uni<Void> confirmCreditTransfer(final HandleCreditTransferPayload payload) {
        LOG.infof("Starting confirmation process for %s", payload.idBonificoExtraSepa());
        return this.confirmCreditTransfer(payload.idBonificoExtraSepa(), null);
    }

    public Uni<Void> restartConfirmCreditTransfer(final RestartConfirmationProcessPayload payload) {
        LOG.infof("Restarting confirmation process for %s", payload.idBonificoExtraSepa());
        if (payload.confirmationCall() == ConfirmationStep.CONFERMA_BONIFICO) {
            return this.jobPublisher.scheduleJob(
                    new JobData(
                            UUID.randomUUID(),
                            Constants.JOB_RESTART_CONFIRMED_STEP_2_CREDIT_TRANSFER,
                            payload
                    )
            );
        }
        return this.confirmCreditTransfer(payload.idBonificoExtraSepa(), payload);
    }

    public Uni<Void> restartConfirmCreditTransferWithPoll(final PollBucket bucket) {
        return this.redisClient.value(RedisData.class)
                .getdel(getID(bucket.resourceName(), bucket.resourceID())).flatMap(redisData -> {

                    LOG.infof("Restarting confirmation process for %s", redisData.creditTransferID);
                    return this.confirmCreditTransfer(
                            redisData.creditTransferID,
                            new RestartConfirmationProcessPayload(
                                    redisData.creditTransferID,
                                    redisData.processID,
                                    false,
                                    null,
                                    null));
                });
    }

    public Uni<Void> confirmCreditTransfer(final UUID idBonifico,
            final RestartConfirmationProcessPayload restartProcessInfo) {
        final UUID processID = restartProcessInfo == null
                ? UUID.randomUUID()
                : restartProcessInfo.processID();

        // Crea i vari client

        final var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        final var filter = new CabelCredentialForwardFilter(authenticatedUser);
        // Create the client to CIP
        final CIPClient cipClient = QuarkusRestClientBuilder.newBuilder().register(filter)
                .baseUrl(cipUrl)
                .build(CIPClient.class);
        // Create the client to the registry
        final RegistryAnagraficheClient registryAnagraficheClient = QuarkusRestClientBuilder.newBuilder()
                .register(filter)
                .register(new PollableResourceAcceptedHandler())
                .baseUrl(registryURL)
                .build(RegistryAnagraficheClient.class);

        final String abi = authenticatedUser.getAttribute(AuthConstants.ABI_ATTRIBUTE);

        // ProcessInterruptions must be propagated, and the transaction must NOT be
        // rolled back on them
        return TransactionUtils.doNotRollBackOn(
                dataSources.getDataSource(abi)::withTransaction,
                ProcessInterruption.class::isInstance)
                .withTransaction(dataSource -> {

                    final var bonificoRepository = new BonificoExtraSepa.Entity().repository(dataSource);
                    final var bonificoUni = bonificoRepository.getByIdLocked(idBonifico)
                            .onFailure(NoSuchElementException.class)
                            .transform(missing -> new OtherCallsHaveFailed(processID));

                    return bonificoUni.flatMap(bonifico -> {
                        final Uni<Void> handleRestartInfo;

                        if (restartProcessInfo == null) {
                            // Start the process

                            // Load the linked entities as we will need most of them
                            final var withLinked = bonifico.withLinkedEntities();

                            handleRestartInfo = withLinked.loadAll(dataSource).onItem().ignoreAsUni()
                                    .replaceWith(withLinked)
                                    .flatMap(
                                            bonificoWithLinked -> startConfirmationProcess(authenticatedUser, cipClient,
                                                    registryAnagraficheClient, dataSource, processID,
                                                    bonificoWithLinked));
                        } else if (restartProcessInfo.isFromCIP()) {
                            // Recover the result from a CABEL call

                            final CIPReply reply = restartProcessInfo.cipReply();

                            // Recover the entity
                            final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
                            final DatiConfermaBonifico.Repository repository = entity.repository(dataSource);
                            handleRestartInfo = repository.getByIdForUpdate(processID)
                                    .plug(failIfEnded(processID))
                                    .flatMap(datiConferma -> restartConfirmationFromCabelCall(restartProcessInfo,
                                            cipClient,
                                            dataSource,
                                            processID, reply, datiConferma, bonifico));
                        } else {
                            // Recover the result from a poll

                            // Recover the entity
                            final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
                            final DatiConfermaBonifico.Repository repository = entity.repository(dataSource);
                            handleRestartInfo = repository.getByIdForUpdate(processID)
                                    .plug(failIfEnded(processID))
                                    .flatMap(
                                            datiConferma -> restartConfirmationFromRegistryResponse(
                                                    registryAnagraficheClient,
                                                    dataSource, bonifico, processID, datiConferma, cipClient));
                        }

                        return handleRestartInfo
                                .plug(handlePollInterruptions(bonificoRepository, bonifico, processID))
                                // Handle the additional failures
                                .onFailure().invoke(error -> LOG.error("errore durante la conferma", error))
                                .onFailure(throwable -> !(throwable instanceof ProcessInterruption)).recoverWithUni(
                                        // Log the failure, store the error and then unlock the transfer
                                        throwable -> Uni
                                                .combine().all().unis(
                                                        InfoStatoErrore.storeError(bonifico,
                                                                dataSource,
                                                                throwable),
                                                        bonificoRepository.addNewStatusHistory(bonifico,
                                                                InsertedSubStatus.CONFERMA_FALLITO.name(),
                                                                throwable instanceof ConfirmationFailure
                                                                        ? "La conferma hanno dato esito negativo."
                                                                        : "Un errore imprevisto è avvenuto durante la conferma."))
                                                .discardItems()
                                                .onItemOrFailure()
                                                .call(() -> bonificoRepository.updateStatusAndUnlock(bonifico,
                                                        CreditTransferStatus.IN_ERRORE, throwable.getMessage())));
                    });
                })
                .call(() -> {
                    final var bonificoRepository = new BonificoExtraSepa.Entity().repository(dataSources.getDataSource(abi));
                    return bonificoRepository.getById(idBonifico)
                    .flatMap(bonifico -> {
                        if (Boolean.FALSE.equals(bonifico.inGestione())) {
                            return this.jobPublisher.scheduleJob(
                                    new JobData(
                                            UUID.randomUUID(),
                                            Constants.JOB_HANDLE_CREDIT_TRANSFER,
                                            new HandleCreditTransferPayload(bonifico.id())));
                        }
                        return Uni.createFrom().voidItem();
                    });
                });
    }

    /**
     * Stop the restart of the confirmation process if another call has failed
     */
    private Function<Uni<DatiConfermaBonifico>, Uni<DatiConfermaBonifico>> failIfEnded(UUID processID) {
        return uni -> uni.onItem().transform(datiConferma -> {
            if (datiConferma.statoConferma().equals(StatoConferma.FALLITO)) {
                throw new OtherCallsHaveFailed(processID);
            }
            return datiConferma;
        });
    }

    private static class OtherCallsHaveFailed extends RuntimeException implements ProcessInterruption {
        private final UUID processID;

        public OtherCallsHaveFailed(UUID processID) {
            this.processID = processID;
        }

        @Override
        public String getIdentifier() {
            return processID.toString();
        }
    }

    private Function<Uni<Void>, Uni<Void>> handlePollInterruptions(
            final BonificoExtraSepa.Repository bonificoRepository,
            final BonificoExtraSepa bonifico,
            final UUID processID) {
        // Handle the pollable resources by storing to redis the ID and raising the
        // interruption
        return uni -> uni
                .onFailure(PollableResourceAccepted.class).recoverWithUni(throwable -> {
                    final var pollable = (PollableResourceAccepted) throwable;

                    final var insertStatus = bonificoRepository.addNewStatusHistory(
                            bonifico,
                            InsertedSubStatus.CONFERMA_IN_ATTESA.name(),
                            "La conferma procedera quando saranno disponibile le informazioni sulle banche.");

                    final var updateRedis = redisClient.value(RedisData.class)
                            .setex(getID(pollable.getResourceName(), pollable.getPollResourceID()),
                                    3600, new RedisData(processID, bonifico.id()));

                    return Uni.combine().all().unis(insertStatus, updateRedis).discardItems()
                            .onItem().failWith(() -> new ResourceNotReadyInterruption(pollable));
                })
                .onFailure(MultiplePollableResourceAccepted.class).recoverWithUni(throwable -> {
                    final var pollable = (MultiplePollableResourceAccepted) throwable;

                    final var insertStatus = bonificoRepository.addNewStatusHistory(
                            bonifico,
                            InsertedSubStatus.CONFERMA_IN_ATTESA.name(),
                            "La conferma procedera quando saranno disponibile le informazioni sulle banche.");

                    final RedisData redisData = new RedisData(processID, bonifico.id());

                    final var updateRedis = redisClient.withTransaction(transactionRedisClient -> {
                        final var command = transactionRedisClient.value(RedisData.class);

                        return Multi.createFrom().iterable(pollable.getPollableResourceAccepteds())
                                .onItem()
                                .transformToUniAndMerge(p -> {
                                    return command.setex(
                                            getID(p.getResourceName(), p.getPollResourceID()), 3600, redisData);
                                })
                                .onItem().ignoreAsUni();
                    });

                    return Uni.combine().all().unis(insertStatus, updateRedis).discardItems()
                            .onItem()
                            .failWith(() -> new MultipleResourceNotReadyInterruption(pollable, processID));
                });
    }

    /**
     * Starts the confirmation process, executing all the calls to the external
     * services
     */
    private Uni<Void> startConfirmationProcess(final SecurityIdentity authenticatedUser, final CIPClient cipClient,
            final RegistryAnagraficheClient registryAnagraficheClient, final SqlClient dataSource, final UUID processID,
            final BonificoExtraSepa.WithLinkedEntities bonificoWithLinked) {
        final var cipRequests = new ArrayList<CIPRequest>(5);
        final var registryRequests = new ArrayList<Uni<Void>>(1);

        final boolean isBancaABanca = Boolean.TRUE
                .equals(bonificoWithLinked.sottoTipologiaBonifico.getEntity().bancaABanca());

        // Ottiene il rapporto, con la banca o l'attore ordinante
        final InformazioniRapporto ordinante = isBancaABanca
                ? bonificoWithLinked.informazioniIntermediari.stream()
                        .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                                .equals(TipoIntermediario.ORDINANTE)
                                && !intermediario.getEntity().intermediarioDocumentoCollegato())
                        .findAny().orElseThrow().informazioniRapporto.getEntity()
                : bonificoWithLinked.informazioniAttori.stream()
                        .filter(attore -> attore.getEntity().tipo().equals(TipoAttore.ORDINANTE))
                        .findAny().orElseThrow().informazioniRapporto.getEntity();

        // Informazioni dell'intermediario destinatario
        final InformazioniIntermediario.WithLinkedEntities infoIntermediarioDestinatario = bonificoWithLinked.informazioniIntermediari
                .stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                        .equals(isBancaABanca
                                ? TipoIntermediario.BANCA_BENEFICIARIA
                                : TipoIntermediario.BANCA_DEL_BENEFICIARIO)
                        && !intermediario.getEntity().intermediarioDocumentoCollegato())
                .findAny().orElseThrow();

        // 1.a - Recupera Saldo Rapporto

        final StatoConfermaSaldoRapporto statoConfermaSaldoRapporto;

        if (!bonificoWithLinked.datiVerificaBonifico.getEntity().statoVerificaSaldoRapporto()
                .equals(StatoVerificaSaldoRapporto.VERIFICATO)) {
            // The user already forced his way
            statoConfermaSaldoRapporto = StatoConfermaSaldoRapporto.CONFERMATO_STEP_VERIFICA;
        } else if (ordinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)) {
            statoConfermaSaldoRapporto = StatoConfermaSaldoRapporto.CONFERMATO;
        } else {
            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_ESTERORECUPERASALDORAPPORTO",
                    "CONFERMA_" + ConfirmationStep.RECUPERA_SALDO_RAPPORTO,
                    new EsteroRecuperaSaldoRapportoInput(
                            EsteroRecuperaSaldoRapportoInput.TipoRichiesta.CONTROLLODISPONIBILITA,
                            ordinante.numero(),
                            bonificoWithLinked.dettaglioBonifico().importoDiAddebito(),
                            authenticatedUser.getPrincipal().getName()),
                    httpResponse(processID, bonificoWithLinked,
                            ConfirmationStep.RECUPERA_SALDO_RAPPORTO)));
            statoConfermaSaldoRapporto = StatoConfermaSaldoRapporto.ATTENDE_RISPOSTA;
        }

        // 1.b - Verifica Avvertenze Rapporto

        final StatoConfermaAvvertenze statoConfermaAvvertenzeRapporto;

        if (!bonificoWithLinked.datiVerificaBonifico.getEntity().statoVerificaAvvertenzeRapporto()
                .equals(StatoVerificaAvvertenze.VERIFICATO)) {
            // The user already forced his way
            statoConfermaAvvertenzeRapporto = StatoConfermaAvvertenze.CONFERMATO_STEP_VERIFICA;
        } else if (ordinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)) {
            statoConfermaAvvertenzeRapporto = StatoConfermaAvvertenze.CONFERMATO;
        } else {
            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CIP_ESTEROCONTROLLORAPPORTO",
                    "CONFERMA_" + ConfirmationStep.CONFERMA_AVVERTENZE_RAPPORTO,
                    new EsteroControlloRapportoInput(
                            isBancaABanca
                                    ? EsteroControlloRapportoInput.TipoRichiesta.BANCA
                                    : EsteroControlloRapportoInput.TipoRichiesta.CLIENTE,
                            EsteroControlloRapportoInput.VersoBonifico.USCITA,
                            ordinante.numero(),
                            null),
                    httpResponse(processID, bonificoWithLinked,
                            ConfirmationStep.CONFERMA_AVVERTENZE_RAPPORTO)));
            statoConfermaAvvertenzeRapporto = StatoConfermaAvvertenze.ATTENDE_RISPOSTA;
        }

        // 1.c - Verifica Embargo

        final StatoConfermaEmbargo statoConfermaEmbargo;

        if (!bonificoWithLinked.datiVerificaBonifico.getEntity().statoVerificaEmbargo()
                .equals(StatoVerificaEmbargo.VERIFICATO)) {
            // The user already forced his way
            statoConfermaEmbargo = StatoConfermaEmbargo.CONFERMATO_STEP_VERIFICA;
        } else {
            statoConfermaEmbargo = StatoConfermaEmbargo.ATTENDE_RISPOSTA;
            registryRequests.add(
                    askRegistryForEmbargo(registryAnagraficheClient, processID, bonificoWithLinked.getEntity(),
                            infoIntermediarioDestinatario.getEntity().bic(), cipClient));
        }

        // 1.d - Verifica Cambio

        final StatoConfermaCambio statoConfermaCambio;

        if (bonificoWithLinked.dettaglioBonifico().divisa().equals(ordinante.divisa())) {
            statoConfermaCambio = StatoConfermaCambio.CONFERMATO;
        } else {
            statoConfermaCambio = StatoConfermaCambio.ATTENDE_RISPOSTA;

            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_VERIFICACAMBIO",
                    "CONFERMA_" + ConfirmationStep.CONFERMA_CAMBIO,
                    new VerificaCambioInput(
                            VerificaCambioInput.TipoRichiesta.EMPTY,
                            bonificoWithLinked.dettaglioBonifico().divisa(),
                            bonificoWithLinked.dettaglioBonifico().cambio()),
                    httpResponse(processID, bonificoWithLinked, ConfirmationStep.CONFERMA_CAMBIO)));
        }

        // 1.e/f - Verifica Holiday Table Paese/Divisa

        final StatoConfermaGenerico statoConfermaHolidayTablePaese = StatoConfermaGenerico.ATTENDE_RISPOSTA;
        final StatoConfermaGenerico statoConfermaHolidayTableDivisa = StatoConfermaGenerico.ATTENDE_RISPOSTA;

        cipRequests.add(new CIPRequest(
                UUID.randomUUID().toString(),
                "CCP_VERIFICAHOLIDAYTABLE",
                "CONFERMA_" + ConfirmationStep.CONFERMA_HOLIDAY_TABLE_PAESI,
                new VerificaHolidayTableInput(
                        VerificaHolidayTableInput.TipoRichiesta.CONTROLLO,
                        VerificaHolidayTableInput.TipoCodice.PAESE,
                        infoIntermediarioDestinatario.indirizzoPostale.getEntity().paese(),
                        bonificoWithLinked.getEntity().dataRegolamentoBancaBeneficiario().atTime(12,
                                00)),
                httpResponse(processID, bonificoWithLinked,
                        ConfirmationStep.CONFERMA_HOLIDAY_TABLE_PAESI)));

        cipRequests.add(new CIPRequest(
                UUID.randomUUID().toString(),
                "CCP_VERIFICAHOLIDAYTABLE",
                "CONFERMA_" + ConfirmationStep.CONFERMA_HOLIDAY_TABLE_DIVISA,
                new VerificaHolidayTableInput(
                        VerificaHolidayTableInput.TipoRichiesta.CONTROLLO,
                        VerificaHolidayTableInput.TipoCodice.DIVISA,
                        bonificoWithLinked.dettaglioBonifico().divisa(),
                        bonificoWithLinked.getEntity().dataRegolamentoBancaBeneficiario().atTime(12,
                                00)),
                httpResponse(processID, bonificoWithLinked,
                        ConfirmationStep.CONFERMA_HOLIDAY_TABLE_DIVISA)));

        // 2 - Verifica Bonifico

        final StatoConfermaBonifico statoConfermaBonifico = StatoConfermaBonifico.NON_INVIATA;

        // Create the data holder

        final DatiConfermaBonifico.Entity datiConfermaEntity = new DatiConfermaBonifico.Entity();
        final DatiConfermaBonifico.Repository datiConfermaRepository = datiConfermaEntity
                .repository(dataSource);
        final Uni<Void> insertDatiConfermaBonifico = datiConfermaRepository
                .run(datiConfermaEntity.insert(new DatiConfermaBonifico(
                        processID,
                        bonificoWithLinked.id(),
                        Instant.now(),
                        StatoConferma.ATTENDE_RISPOSTE,
                        statoConfermaSaldoRapporto,
                        null,
                        statoConfermaAvvertenzeRapporto,
                        statoConfermaEmbargo,
                        statoConfermaCambio,
                        statoConfermaHolidayTablePaese,
                        statoConfermaHolidayTableDivisa,
                        statoConfermaBonifico)));

        // Create the pipeline

        // All the calls to run
        final Uni<Void> pipeline = Uni.combine().all().unis(Streams.concat(
                // All the cip request, passed to the client
                cipRequests.stream().map(cipClient::createRequest),
                // All the registry requests
                registryRequests.stream()).toList())
                // Run all requests, do not fail at the first
                .collectFailures().discardItems()
                // Merge all PollableResourceAccepted in a single
                // MultiplePollableResourceAccepted
                .plug(this::mergePollableResourceAccepted);

        // Before the pipeline, insert the data holder in the database
        return insertDatiConfermaBonifico
                .chain(() -> pipeline);
    }

    private Uni<Void> askRegistryForEmbargo(
            final RegistryAnagraficheClient registryAnagraficheClient,
            final UUID processID,
            final BonificoExtraSepa bonificoExtraSepa,
            final String bicIntermediarioDestinatario,
            final CIPClient cipClient) {
        return registryAnagraficheClient
                .dettaglioBanca(bicIntermediarioDestinatario, false)
                .flatMap(// Registry answered straight away
                        ricercaBicResponse -> this.askCipForEmbargo(processID, bonificoExtraSepa, ricercaBicResponse,
                                cipClient));
    }

    private Uni<Void> askCipForEmbargo(
            UUID processID,
            BonificoExtraSepa bonifico,
            final RicercaBicResponse ricercaBicResponse,
            final CIPClient client) {

        return client.createRequest(
                new CIPRequest(
                        UUID.randomUUID().toString(),
                        "CCP_ESTEROELENCOPAESI",
                        "CONFERMA_" + ConfirmationStep.CONFERMA_EMBARGO,
                        new EsteroElencoPaesiInput(
                                EsteroElencoPaesiInput.TipoRichiesta.LISTAPARZIALE,
                                ricercaBicResponse.codicePaeseIso(),
                                ricercaBicResponse.codiceBIC()),
                        httpResponse(processID, bonifico,
                                ConfirmationStep.CONFERMA_EMBARGO)));
    }

    private Uni<Void> restartConfirmationFromCabelCall(final RestartConfirmationProcessPayload restartProcessInfo,
            final CIPClient cipClient,
            final SqlClient dataSource, final UUID processID, final CIPReply reply,
            final DatiConfermaBonifico datiConferma,
            final BonificoExtraSepa creditTransfer) {

        var handlerSegnalazioni = handlerSegnalazioniCabel(dataSource, processID, datiConferma,
                creditTransfer, restartProcessInfo.confirmationCall());

        return switch (restartProcessInfo.confirmationCall()) {
            case RECUPERA_SALDO_RAPPORTO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroRecuperaSaldoRapportoOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiConfermaBonifico -> updater.updateStatoConfermaSaldoRapporto(
                                                    datiConfermaBonifico, StatoConfermaSaldoRapporto.ERRORE, null)))
                    .flatMap(response -> handleRecuperaSaldoRapporto(response, processID, dataSource,
                            datiConferma, cipClient));
            case CONFERMA_AVVERTENZE_RAPPORTO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroControlloRapportoOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiConfermaBonifico -> updater.updateStatoConfermaAvvertenzeRapporto(
                                                    datiConfermaBonifico, StatoConfermaAvvertenze.ERRORE)))
                    .flatMap(response -> handleVerificaAvvertenzeRapporto(response, processID,
                            dataSource, datiConferma, cipClient));
            case CONFERMA_EMBARGO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroElencoPaesiOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiConfermaBonifico -> updater.updateStatoConfermaEmbargo(
                                                    datiConfermaBonifico, StatoConfermaEmbargo.ERRORE)))
                    .flatMap(response -> handleEmbargoResponse(response, processID,
                            dataSource, datiConferma, cipClient));
            case CONFERMA_CAMBIO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<VerificaCambioOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(handlerSegnalazioni
                            .apply(
                                    datiConfermaBonifico -> updater.updateStatoConfermaCambio(datiConfermaBonifico,
                                            StatoConfermaCambio.ERRORE))
                            .skipping("CHKCA05"))
                    .flatMap(response -> handleVerificaCambio(response, processID,
                            dataSource, datiConferma, cipClient));
            case CONFERMA_HOLIDAY_TABLE_DIVISA, CONFERMA_HOLIDAY_TABLE_PAESI -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<VerificaHolidayTableOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(handlerSegnalazioni
                            .apply(datiConfermaBonifico -> {
                                if (restartProcessInfo.confirmationCall()
                                        .equals(ConfirmationStep.CONFERMA_HOLIDAY_TABLE_DIVISA)) {
                                    return updater.updateStatoConfermaHolidayTableDivisa(datiConfermaBonifico,
                                            StatoConfermaGenerico.ERRORE);
                                } else {
                                    return updater.updateStatoConfermaHolidayTablePaese(datiConfermaBonifico,
                                            StatoConfermaGenerico.ERRORE);
                                }
                            }))
                    .flatMap(response -> handleVerificaHolidayTable(response, processID,
                            dataSource, datiConferma,
                            restartProcessInfo.confirmationCall()
                                    .equals(ConfirmationStep.CONFERMA_HOLIDAY_TABLE_DIVISA)
                                            ? VerificaHolidayTableInput.TipoCodice.DIVISA
                                            : VerificaHolidayTableInput.TipoCodice.PAESE,
                            cipClient));
            case CONFERMA_BONIFICO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<ExtraSepaCheckBonificoConfermaOutput>>() {
                    },
                    restartProcessInfo.confirmationCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(datiConfermaBonifico -> updater.updateStatoConfermaBonifico(
                                            datiConfermaBonifico, StatoConfermaBonifico.ERRORE)))
                    .flatMap(response -> handleConfermaBonifico(response, processID,
                            dataSource, datiConferma, cipClient, creditTransfer));
            default -> throw new RuntimeException(
                    "The switch should be exaustive, %s is missing".formatted(restartProcessInfo.confirmationCall()));
        };
    }

    @FunctionalInterface
    private interface HandlerSegnalazioniCabel extends Function<CabelOutput, Uni<?>> {
        default Function<CabelOutput, Uni<?>> skipping(final String... codesToSkip) {
            return this.skipping(Set.of(codesToSkip)::contains);
        }

        @Override
        default Uni<?> apply(CabelOutput t) {
            return this.skipping(code -> false).apply(t);
        }

        Function<CabelOutput, Uni<?>> skipping(Predicate<String> codeToSkip);
    }

    private Function<Function<DatiConfermaBonifico, DatiConfermaBonifico>, HandlerSegnalazioniCabel> handlerSegnalazioniCabel(
            final SqlClient dataSource,
            final UUID processID,
            DatiConfermaBonifico datiConferma,
            final BonificoExtraSepa creditTransfer,
            final ConfirmationStep step) {

        return updateStatus -> codesToSkip -> reply -> {

            if ( // If the reply is not in error
            !reply.errored()
                    // Or if all error matched the one to skip (handled after)
                    || reply.listaSegnalazioni().stream()
                            .filter(s -> s.livello().equals(Segnalazione.Livello.ERRORE))
                            .map(Segnalazione::codice).allMatch(codesToSkip)) {
                // Continue as usual
                return Uni.createFrom().voidItem();
            }

            final var segnalazioniDaRiportare = reply.listaSegnalazioni().stream()
                    .filter(s -> !codesToSkip.test(s.codice()))
                    .toList();

            final StringBuilder errorReport = new StringBuilder();

            errorReport.append("Chiamata a CABEL per \"").append(step).append("\" fallita.");
            if (segnalazioniDaRiportare.isEmpty()) {
                errorReport.append(" Nessuna segnalazione.");
            } else {
                errorReport.append("\nSegnalazioni:");
                for (final Segnalazione segnalazione : segnalazioniDaRiportare) {
                    errorReport.append("\n  - [").append(segnalazione.livello()).append("] ")
                            .append(segnalazione.codice()).append(": ")
                            .append(segnalazione.descrizione());
                }
            }
            var datiConfermaBonifico = updateStatus.apply(datiConferma);
            final Uni<Void> errored = errored(processID, datiConfermaBonifico, dataSource,
                    new ConfirmationFailure(ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR, errorReport.toString()));

            final DatiConfermaBonificoErroreTecnico.Entity erroreTecnicoEntity = new DatiConfermaBonificoErroreTecnico.Entity();
            final DatiConfermaBonificoErroreTecnico.Repository erroreTecnicoRepository = erroreTecnicoEntity
                    .repository(dataSource);
            final List<Uni<Void>> insertTechnicalErrors = segnalazioniDaRiportare.stream()
                    .filter(s -> s.livello().equals(Segnalazione.Livello.ERRORE))
                    .map(s -> erroreTecnicoRepository.run(erroreTecnicoEntity.insert(
                            new DatiConfermaBonificoErroreTecnico(
                                    UUID.randomUUID(),
                                    datiConferma.id(),
                                    s.codice(),
                                    s.descrizione()))))
                    .toList();
            final Uni<Void> insertTechnicalError = insertTechnicalErrors.isEmpty()
                    ? Uni.createFrom().voidItem()
                    : Uni.combine().all().unis(insertTechnicalErrors).discardItems();

            return insertTechnicalError.call(() -> errored);
        };
    }

    // TODO: this is a very inefficient implementation that remakes all registry
    // calls each time the registry says that one is ready. This is fine for now as
    // there is a single registry call, but in the future it should detect what
    // resource is ready and remake the call only for that one
    private Uni<Void> restartConfirmationFromRegistryResponse(final RegistryAnagraficheClient registryAnagraficheClient,
            final SqlClient dataSource,
            final BonificoExtraSepa bonifico, final UUID processID, final DatiConfermaBonifico datiConferma,
            final CIPClient cipClient) {
        final var registryRequests = new ArrayList<Uni<Void>>(1);

        final Uni<Boolean> isBancaABanca = new SottoTipologiaBonifico.Entity()
                .repository(dataSource)
                .getByID(bonifico.idSottoTipologiaBonifico())
                .map(SottoTipologiaBonifico::bancaABanca)
                .memoize().indefinitely();

        // Informazioni dell'intermediario destinatario
        final Uni<InformazioniIntermediario> destinatario = isBancaABanca
                .flatMap(isBancaABancaV -> new InformazioniIntermediario.Entity()
                        .repository(dataSource)
                        .getByBonificoExtraSepaAndKind(bonifico.id(),
                                isBancaABancaV
                                        ? TipoIntermediario.BANCA_BENEFICIARIA
                                        : TipoIntermediario.BANCA_DEL_BENEFICIARIO,
                                false))
                .map(Optional::orElseThrow);

        // 1.c - Verifica Embargo

        if (datiConferma.statoConfermaEmbargo().isWaitingForAnswer()) {
            registryRequests.add(destinatario.flatMap(
                    info -> askRegistryForEmbargo(registryAnagraficheClient, processID, bonifico,
                            info.bic(), cipClient)));
        }

        // Create the pipeline

        if (registryRequests.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        // All the calls to run
        final Uni<Void> pipeline = Uni.combine().all().unis(registryRequests)
                // Run all requests, do not fail at the first
                .collectFailures().discardItems()
                // Merge all PollableResourceAccepted in a single
                // MultiplePollableResourceAccepted
                .plug(this::mergePollableResourceAccepted);

        return pipeline;
    }

    private Uni<Void> handleRecuperaSaldoRapporto(final EsteroRecuperaSaldoRapportoOutput response,
            final UUID processID, final SqlClient dataSource, DatiConfermaBonifico datiConferma,
            final CIPClient cipClient) {

        if (FlagSiNo.SI.equals(response.flagDisponibilita())) {
            datiConferma = updater.updateStatoConfermaSaldoRapporto(datiConferma,
                    StatoConfermaSaldoRapporto.CONFERMATO, BigDecimal.ZERO);
        } else if (FlagSiNo.SI.equals(response.flagForzaturaSconfinamento())) {
            datiConferma = updater.updateStatoConfermaSaldoRapporto(datiConferma,
                    StatoConfermaSaldoRapporto.NECESSITA_FORZATURA_SCONFINAMENTO,
                    response.importoSconfinamento());
        } else {
            datiConferma = updater.updateStatoConfermaSaldoRapporto(datiConferma,
                    StatoConfermaSaldoRapporto.FALLITO,
                    response.importoSconfinamento());
            return errored(processID, datiConferma, dataSource,
                    new ConfirmationFailure(ErrorCodes.Codes.SCONFINAMENTO_NON_PERMESSO,
                            "Conto sconfinato. Operazione non consentita."));
        }

        // Update with the new state
        return storeAfterStep1CallEnded(dataSource, datiConferma, cipClient);
    }

    private Uni<Void> handleVerificaAvvertenzeRapporto(final EsteroControlloRapportoOutput response,
            final UUID processID,
            final SqlClient dataSource, final DatiConfermaBonifico datiConferma, final CIPClient cipClient) {

        final var datiAvvertenzaEntity = new DatiConfermaBonificoAvvertenza.Entity();
        final var datiAvvertenzaRepository = datiAvvertenzaEntity.repository(dataSource);

        final Uni<Void> loadAvvertenze = Multi.createFrom().iterable(response.avvertenze())
                .onItem().transformToUniAndMerge(avvertenza -> datiAvvertenzaRepository.run(
                        datiAvvertenzaEntity.insert(new DatiConfermaBonificoAvvertenza(UUID.randomUUID(),
                                datiConferma.id(), avvertenza.codice(), avvertenza.descrizione()))))
                .onItem().ignoreAsUni();

        final DatiConfermaBonifico newDatiConferma;
        Uni<Void> doAfter = Uni.createFrom().voidItem();

        if (EsteroControlloRapportoOutput.EsitoControlloGenerale.VALIDO.equals(response.esitoControlloGenerale())) {
            newDatiConferma = updater.updateStatoConfermaAvvertenzeRapporto(datiConferma,
                    StatoConfermaAvvertenze.CONFERMATO);
            doAfter = storeAfterStep1CallEnded(dataSource, newDatiConferma, cipClient);
        } else {

            final ConfirmationFailure failure;

            if (response.esitoAvvertenze() != null && response.esitoAvvertenze().codice().equals("BLOCCODARE")) {
                newDatiConferma = updater.updateStatoConfermaAvvertenzeRapporto(datiConferma,
                        StatoConfermaAvvertenze.BLOCCO_DARE);
                failure = new ConfirmationFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_DARE,
                        "Presenza della seguente avvertenza bloccante in Dare sul Rapporto Ordinante:\n" +
                                response.esitoAvvertenze().descrizione() +
                                "\nOperazione non consentita.");
                doAfter = datiAvvertenzaRepository.run(
                        datiAvvertenzaEntity.insert(new DatiConfermaBonificoAvvertenza(
                                UUID.randomUUID(),
                                datiConferma.id(),
                                response.esitoAvvertenze().codice(),
                                response.esitoAvvertenze().descrizione())));
            } else if (response.bloccoDare()) {
                newDatiConferma = updater.updateStatoConfermaAvvertenzeRapporto(datiConferma,
                        StatoConfermaAvvertenze.BLOCCO_DARE);
                failure = new ConfirmationFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_DARE,
                        "Presenza Blocco Dare su Rapporto Ordinante. Operazione non consentita.");
            } else if (response.bloccoTotale()) {
                newDatiConferma = updater.updateStatoConfermaAvvertenzeRapporto(datiConferma,
                        StatoConfermaAvvertenze.BLOCCO_TOTALE);
                failure = new ConfirmationFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_TOTALE,
                        "Presenza Blocco Totale su Rapporto Ordinante. Operazione non consentita.");
            } else {
                newDatiConferma = updater.updateStatoConfermaAvvertenzeRapporto(datiConferma,
                        StatoConfermaAvvertenze.FALLITO);
                LOG.errorf("Unknown failure from CABEL: %s", response);
                failure = new ConfirmationFailure(ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR,
                        "Unknown failure from CABEL");
            }

            doAfter = doAfter.flatMap(ignored -> errored(processID, newDatiConferma, dataSource, failure));
        }

        // Update with the new state
        final Uni<Void> finalDoAfter = doAfter;
        return loadAvvertenze
                .chain(() -> finalDoAfter);
    }

    private Uni<Void> handleEmbargoResponse(final EsteroElencoPaesiOutput esteroPaesiOutput, final UUID processID,
            final SqlClient dataSource, DatiConfermaBonifico datiConferma, final CIPClient cipClient) {

        if (esteroPaesiOutput.listaPaesi().isEmpty()) {
            datiConferma = updater.updateStatoConfermaEmbargo(datiConferma, StatoConfermaEmbargo.FALLITO);
            return errored(processID, datiConferma, dataSource,
                    new ConfirmationFailure(
                            ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR,
                            "Ricerca paesi non ha prodotto nessun risultato"));
        }

        var paese = esteroPaesiOutput.listaPaesi().get(0);

        switch (paese.embargo()) {
            case NONSOTTOEMBARGO:
                datiConferma = updater.updateStatoConfermaEmbargo(datiConferma, StatoConfermaEmbargo.CONFERMATO);
                break;
            case SOTTOEMBARGOPARZIALE:
                datiConferma = updater.updateStatoConfermaEmbargo(datiConferma,
                        StatoConfermaEmbargo.NECESSITA_FORZATURA_EMBARGO_PARZIALE);
                break;
            case SOTTOEMBARGOTOTALE:
                datiConferma = updater.updateStatoConfermaEmbargo(datiConferma,
                        StatoConfermaEmbargo.FALLITO);
                return errored(processID, datiConferma, dataSource,
                        new ConfirmationFailure(
                                ErrorCodes.Codes.EMBARGO_TOTALE,
                                "Paese banca del beneficiario sotto embargo totale. Operazione non consentita."));
        }

        // Update with the new state
        return storeAfterStep1CallEnded(dataSource, datiConferma, cipClient);

    }

    private Uni<Void> handleVerificaCambio(final VerificaCambioOutput response, final UUID processID,
            final SqlClient dataSource,
            DatiConfermaBonifico datiConferma, final CIPClient cipClient) {

        if (response.errored()) {
            // We know the error is CHKCA05, as it is the only error code skipped by the
            // technical error handler
            datiConferma = updater.updateStatoConfermaCambio(datiConferma,
                    StatoConfermaCambio.NECESSITA_MODIFICA_CAMBIO);
            return errored(processID, datiConferma, dataSource,
                    new ConfirmationFailure(ErrorCodes.Codes.CAMBIO_SUPERIORE_AL_SCARTO,
                            "Errore: cambio superiore alla % di scarto. Operazione non consentita."));
        }

        datiConferma = updater.updateStatoConfermaCambio(datiConferma,
                StatoConfermaCambio.CONFERMATO);

        // Update with the new state
        return storeAfterStep1CallEnded(dataSource, datiConferma, cipClient);
    }

    private Uni<Void> handleVerificaHolidayTable(final VerificaHolidayTableOutput response, final UUID processID,
            final SqlClient dataSource,
            DatiConfermaBonifico datiConferma, final VerificaHolidayTableInput.TipoCodice tipoCodice,
            final CIPClient cipClient) {

        final var stato = response.festivo()
                ? StatoConfermaGenerico.FALLITO
                : StatoConfermaGenerico.CONFERMATO;

        switch (tipoCodice) {
            case DIVISA:
                datiConferma = updater.updateStatoConfermaHolidayTableDivisa(datiConferma, stato);
                break;
            case PAESE:
                datiConferma = updater.updateStatoConfermaHolidayTablePaese(datiConferma, stato);
                break;
        }

        // Update with the new state
        return storeAfterStep1CallEnded(dataSource, datiConferma, cipClient);
    }

    private Uni<Void> storeAfterStep1CallEnded(final SqlClient dataSource, final DatiConfermaBonifico datiConferma,
            final CIPClient cipClient) {
        if (datiConferma.statoConferma() != StatoConferma.ATTENDE_RISPOSTE) {
            throw new RuntimeException("Stato di conferma inaspettato: " + datiConferma.statoConferma());
        }

        final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
        final var repository = entity.repository(dataSource);

        if (!datiConferma.allStep1CallsEnded()) {
            // The calls are still ongoing. Wait for the others
            LOG.infof("Chiamate non completate, aggiorno DatiConfermaBonifico per bonifico %s",
                    datiConferma.idBonificoExtraSepa());
            return repository.run(entity.update(datiConferma));
        }

        LOG.infof("Chiamate dello step 1 completate per il processo di conferma del bonifico %s",
                datiConferma.idBonificoExtraSepa());

        final var bonificoEntity = new BonificoExtraSepa.Entity();
        final var bonificoRepository = bonificoEntity.repository(dataSource);

        return bonificoRepository.getById(datiConferma.idBonificoExtraSepa()).flatMap(bonifico -> {
            final Uni<Void> updateConferma;
            final Uni<Void> addStatusHistory;
            final Uni<Void> updateStatusAndUnlock;

            if (!datiConferma.allStep1CallsWereSuccessful()) {
                // Some calls ended with an error that can be fixed in the frontend
                LOG.infof("Conferma del bonifico %s interrotta, necessario intervento dal frontend", bonifico.id());

                updateConferma = repository
                        .run(entity.update(updater.updateStatoConferma(datiConferma, StatoConferma.DA_CONFERMARE)));

                addStatusHistory = bonificoRepository.addNewStatusHistory(bonifico,
                        InsertedSubStatus.CONFERMA_DA_CONFERMARE.name(),
                        "Lo step 1 ha evidenziato dati che necessitano modifica.");

                updateStatusAndUnlock = bonificoRepository.updateStatusAndUnlock(bonifico,
                        CreditTransferStatus.CONFERMATO_STEP_1_COMPLETO, "Waiting for frontend intervention");
            } else {
                // All calls were successful, moving to step 2
                LOG.infof("Step 1 conferma del bonifico %s completato con esito positivo", bonifico.id());

                updateConferma = repository
                        .run(entity.update(updater.updateStatoConferma(datiConferma, StatoConferma.ATTENDE_RISPOSTA_STEP_2)));

                addStatusHistory = bonificoRepository.addNewStatusHistory(bonifico,
                        InsertedSubStatus.CONFERMA_PASSATO.name(),
                        "Lo step 1 è stato completato con esito positivo.");

                updateStatusAndUnlock = bonificoRepository.updateStatusAndUnlock(bonifico,
                        CreditTransferStatus.CONFERMATO_STEP_2, "Moving to step 2");
            }

            // First update the verification data (maybe we should cancel it?) and add a new
            // status history
            return Uni.combine().all().unis(updateConferma, addStatusHistory)
                    .discardItems()
                    // then unlock the bonifico and move it to the next status
                    .call(() -> updateStatusAndUnlock);

        });

    }

    public Uni<Void> confirmStep2CreditTransfer(final HandleCreditTransferPayload payload) {
        LOG.infof("Starting step 2 confirmation process for %s", payload.idBonificoExtraSepa());
        return this.confirmStep2CreditTransfer(payload.idBonificoExtraSepa(), null);
    }

    public Uni<Void> restartConfirmStep2CreditTransfer(final RestartConfirmationProcessPayload payload) {
        LOG.infof("Restarting step 2 confirmation process for %s", payload.idBonificoExtraSepa());
        return this.confirmStep2CreditTransfer(payload.idBonificoExtraSepa(), payload);
    }

    public Uni<Void> confirmStep2CreditTransfer(final UUID idBonifico,
            final RestartConfirmationProcessPayload restartProcessInfo) {
        final UUID processID = restartProcessInfo == null
                ? UUID.randomUUID()
                : restartProcessInfo.processID();

        // Crea i vari client

        final var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        final var filter = new CabelCredentialForwardFilter(authenticatedUser);
        // Create the client to CIP
        final CIPClient cipClient = QuarkusRestClientBuilder.newBuilder().register(filter)
                .baseUrl(cipUrl)
                .build(CIPClient.class);
        // Create the client to the registry
        final RegistryAnagraficheClient registryAnagraficheClient = QuarkusRestClientBuilder.newBuilder()
                .register(filter)
                .register(new PollableResourceAcceptedHandler())
                .baseUrl(registryURL)
                .build(RegistryAnagraficheClient.class);

        final String abi = authenticatedUser.getAttribute(AuthConstants.ABI_ATTRIBUTE);

        // ProcessInterruptions must be propagated, and the transaction must NOT be
        // rolled back on them
        return TransactionUtils.doNotRollBackOn(
                dataSources.getDataSource(abi)::withTransaction,
                ProcessInterruption.class::isInstance)
                .withTransaction(dataSource -> {

                    final var bonificoRepository = new BonificoExtraSepa.Entity().repository(dataSource);
                    final var bonificoUni = bonificoRepository.getByIdLocked(idBonifico)
                            .onFailure(NoSuchElementException.class)
                            .transform(missing -> new OtherCallsHaveFailed(processID));

                    final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
                    final DatiConfermaBonifico.Repository repository = entity.repository(dataSource);
                    final var datiConfermaUni = repository.getByBonificoExtraSepaForUpdate(idBonifico)
                    .plug(failIfEnded(processID));

                    return Uni.combine().all().unis(bonificoUni, datiConfermaUni).withUni((bonifico, datiConferma) -> {
                        final Uni<Void> handleRestartInfo;

                        if (restartProcessInfo == null) {
                            // Start the process

                            // Load the linked entities as we will need most of them
                            final var withLinked = bonifico.withLinkedEntities();

                            handleRestartInfo = withLinked.loadAll(dataSource).onItem().ignoreAsUni()
                                    .replaceWith(withLinked)
                                    .flatMap(
                                            bonificoWithLinked -> startStep2ConfirmationProcess(authenticatedUser,
                                                    cipClient,
                                                    registryAnagraficheClient, dataSource, processID,
                                                    bonificoWithLinked, datiConferma));
                        } else {
                            if (!restartProcessInfo.isFromCIP()) {
                                throw new RuntimeException(
                                        "The second step does not contains polls as it do not call the registry");
                            }

                            // Recover the result from a CABEL call

                            final CIPReply reply = restartProcessInfo.cipReply();

                            // Recover the entity
                            handleRestartInfo = restartConfirmationFromCabelCall(restartProcessInfo,
                                    cipClient,
                                    dataSource,
                                    processID, reply, datiConferma, bonifico);
                        }

                        return handleRestartInfo
                                .plug(handlePollInterruptions(bonificoRepository, bonifico, processID))
                                // Handle the additional failures
                                .onFailure()
                                .invoke(error -> LOG.error("errore durante il secondo step di conferma", error))
                                .onFailure(throwable -> !(throwable instanceof ProcessInterruption)).recoverWithUni(
                                        // Log the failure, store the error and then unlock the transfer
                                        throwable -> Uni
                                                .combine().all().unis(
                                                        InfoStatoErrore.storeError(bonifico,
                                                                dataSource,
                                                                throwable),
                                                        bonificoRepository.addNewStatusHistory(bonifico,
                                                                InsertedSubStatus.CONFERMA_FALLITO.name(),
                                                                throwable instanceof ConfirmationFailure
                                                                        ? "Il secondo step della conferma ha dato esito negativo."
                                                                        : "Un errore imprevisto è avvenuto durante il secondo step della conferma."))
                                                .discardItems()
                                                .onItemOrFailure()
                                                .call(() -> bonificoRepository.updateStatusAndUnlock(bonifico,
                                                        CreditTransferStatus.IN_ERRORE, throwable.getMessage())));
                    });
                })
                .call(() -> {
                    final var bonificoRepository = new BonificoExtraSepa.Entity().repository(dataSources.getDataSource(abi));
                    return bonificoRepository.getById(idBonifico)
                            .flatMap(bonifico -> {
                                if (Boolean.FALSE.equals(bonifico.inGestione())) {
                                    return this.jobPublisher.scheduleJob(
                                            new JobData(
                                                    UUID.randomUUID(),
                                                    Constants.JOB_HANDLE_CREDIT_TRANSFER,
                                                    new HandleCreditTransferPayload(bonifico.id())));
                                }
                                return Uni.createFrom().voidItem();
                            });
                });

    }

    private Uni<Void> startStep2ConfirmationProcess(final SecurityIdentity authenticatedUser, final CIPClient cipClient,
            final RegistryAnagraficheClient registryAnagraficheClient, final SqlClient dataSource, final UUID processID,
            final BonificoExtraSepa.WithLinkedEntities bonifico, final DatiConfermaBonifico datiConferma) {

        final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
        final var repository = entity.repository(dataSource);

        var datiConfermaUpdated = updater.updateStatoConferma(datiConferma, StatoConferma.ATTENDE_RISPOSTA_STEP_2);
        datiConfermaUpdated = updater.updateStatoConfermaBonifico(datiConfermaUpdated, StatoConfermaBonifico.ATTENDE_RISPOSTA);

        final Uni<Void> updateConferma = repository
                .run(entity.update(datiConfermaUpdated));

        final BiFunction<BonificoExtraSepa.WithLinkedEntities, BanksConfig.BankConfig, ExtraSepaCheckBonificoConfermaInput> toDto = Boolean.TRUE
                .equals(bonifico.sottoTipologiaBonifico.getEntity().bancaABanca())
                        ? extraSepaCheckBonificoConfermaInputMapper::pacs009
                        : extraSepaCheckBonificoConfermaInputMapper::pacs008;

        final BanksConfig.BankConfig bankConfig = banksConfig.bank()
                .get(authenticatedUser.<String>getAttribute(AuthConstants.ABI_ATTRIBUTE));

        return updateConferma.call(() -> cipClient.createRequest(new CIPRequest(
                UUID.randomUUID().toString(),
                "CIP_EXTRASEPACHECKBONIFICOCONFERMA",
                "CONFERMA_" + ConfirmationStep.CONFERMA_BONIFICO,
                toDto.apply(bonifico, bankConfig),
                httpResponse(processID, bonifico,
                        ConfirmationStep.CONFERMA_BONIFICO))));
    }

    private Uni<Void> handleConfermaBonifico(final ExtraSepaCheckBonificoConfermaOutput response, final UUID processID,
            final SqlClient dataSource, DatiConfermaBonifico datiConferma, final CIPClient cipClient,
            final BonificoExtraSepa bonifico) {

        datiConferma = updater.updateStatoConfermaBonifico(datiConferma, StatoConfermaBonifico.CONFERMATO);

        return storeAfterStep2CallEnded(dataSource, datiConferma, response.numeroTransazione(), cipClient);
    }

    private Uni<Void> storeAfterStep2CallEnded(final SqlClient dataSource, final DatiConfermaBonifico datiConferma,
            final Long numeroTransazione,
            final CIPClient cipClient) {
        if (datiConferma.statoConferma() != StatoConferma.ATTENDE_RISPOSTA_STEP_2) {
            throw new RuntimeException("Stato di conferma inaspettato: " + datiConferma.statoConferma());
        }

        final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
        final var repository = entity.repository(dataSource);

        LOG.infof("Chiamate completate per il processo di conferma del bonifico %s",
                datiConferma.idBonificoExtraSepa());

        final var bonificoEntity = new BonificoExtraSepa.Entity();
        final var bonificoRepository = bonificoEntity.repository(dataSource);

        return bonificoRepository.getById(datiConferma.idBonificoExtraSepa()).flatMap(bonifico -> {
            final Uni<Void> updateConferma;
            final Uni<Void> addStatusHistory;
            final Uni<Void> updateStatusAndUnlock;

            // The last call was successful, the transfer is confirmed
            LOG.infof("Verifica del bonifico %s completata con esito positivo", bonifico.id());

            updateConferma = repository
                    .run(entity.update(updater.updateStatoConferma(datiConferma, StatoConferma.CONFERMATO)));

            addStatusHistory = bonificoRepository.addNewStatusHistory(bonifico,
                    InsertedSubStatus.CONTROLLO_INSERIMENTO_PASSATO.name(),
                    "Il bonifico è stato confermato con numero transazione %s"
                            .formatted(numeroTransazione));

            updateStatusAndUnlock = bonificoRepository.setNumeroTransazione(bonifico.id(), numeroTransazione)
            .call(() -> bonificoRepository.updateStatusAndUnlock(bonifico, CreditTransferStatus.DA_AUTORIZZARE));

            // First update the verification data (maybe we should cancel it?) and add a new
            // status history
            return Uni.combine().all().unis(updateConferma, addStatusHistory)
                    .discardItems()
                    // then unlock the bonifico and move it to the next status
                    .call(() -> updateStatusAndUnlock);
        });

    }

    /**
     * Un errore bloccante si è verificato. Il bonifico non può continuare ma si
     * deve ricominciare con un nuovo processo
     */
    private Uni<Void> errored(final UUID processID, final DatiConfermaBonifico datiConferma, final SqlClient dataSource,
            final ConfirmationFailure failure) {

        final DatiConfermaBonifico.Entity entity = new DatiConfermaBonifico.Entity();
        return entity.repository(dataSource)
                .run(entity.update(updater.updateStatoConferma(datiConferma, StatoConferma.FALLITO)))
                .onItem().failWith(() -> failure);
    }

    // Utils

    private <T> Uni<T> fetchReply(
            final CIPReply reply,
            final CIPClient client,
            final GenericType<CIPWrapperReply<T>> type,
            ConfirmationStep step) {
        if (Boolean.FALSE.equals(reply.success())) {
            // TODO: what can cause this?
            return Uni.createFrom().failure(new ServerErrorException(
                    "CIP client failure: " + reply.requestID(),
                    jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR));
        }

        return client.getResponse(reply.requestID())
                .invoke(response -> {
                    response.bufferEntity();
                    LOG.debug("risposta per step " + step + ":" + response.readEntity(String.class));
                })
                .map(response -> response.readEntity(type).data());
    }

    private <T> Uni<T> mergePollableResourceAccepted(final Uni<T> uni) {
        return uni
                // When a composite failure passes, that's also made only of
                // PollableResourceAccepted
                .onFailure(exception -> exception instanceof final CompositeException compositeException
                        && compositeException.getCauses().stream().allMatch(PollableResourceAccepted.class::isInstance))
                // Transform it to MultiplePollableResourceAccepted
                .transform(exception -> new MultiplePollableResourceAccepted(((CompositeException) exception)
                        .getCauses().stream().map(PollableResourceAccepted.class::cast).toList()));
    }

    private HTTPResponseKind httpResponse(
            final UUID processID,
            final BonificoExtraSepa.WithLinkedEntities bonifico,
            final ConfirmationStep step) {
        return httpResponse(processID, bonifico.getEntity(), step);
    }

    private HTTPResponseKind httpResponse(
            final UUID processID,
            final BonificoExtraSepa bonifico,
            final ConfirmationStep step) {
        return new HTTPResponseKind(
                config.url() + "/cip/results/confirmation"
                        + "/" + processID
                        + "/" + bonifico.id()
                        + "/" + step);
    }

}
