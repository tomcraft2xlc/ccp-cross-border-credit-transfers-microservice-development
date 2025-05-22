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
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.MultipleResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.ResourceNotReadyInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.VerifyFailure;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.verify.DatiVerificaBonificoUpdater;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted.InsertedSubStatus;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonificoAvvertenza;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonificoErroreTecnico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerifica;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaAvvertenze;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaCambio;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaGenerico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaSaldoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.VerifyStep;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.utils.TransactionUtils;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.CIPWrapperReply;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.RestartVerifyProcessPayload;
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
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoVerificaInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoVerificaOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.SistemaRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.TipoMessaggio;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.TipoRichiesta;
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
public class VerifyCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(VerifyCreditTransferWorker.class);

    private record RedisData(
            UUID processID,
            UUID creditTransferID) {
    }

    private static String getID(final String resourceName, final UUID resourceID) {
        return VerifyCreditTransferWorker.class.getName() + ".verify.transfer:" + resourceName + ":" + resourceID;
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

    DatiVerificaBonificoUpdater updater;

    VerifyCreditTransferWorker(
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") final URL registryURL,
            final CreditTransferService service,
            final DataSources dataSources,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) final MutinyEmitter<JobData> channel,
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) final JobSubscriber jobSubscriber,
            final ReactiveRedisDataSource redisClient,
            final PollService pollService,
            final ServiceConfig config,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.cip.client\".url") final URL cipUrl,
            final DatiVerificaBonificoUpdater updater) {
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
    }

    public void onStart(@Observes final StartupEvent event) {
        LOG.info("Starting VerifyCreditTransferWorker");
        jobSubscriber.subscribe(
                Constants.JOB_VERIFY_CREDIT_TRANSFER,
                new HandleCreditTransferPayload.Deserializer(),
                this.pollService.jobWithPollableData(Constants.JOB_RESTART_POLLABLE_VERIFY_CREDIT_TRANSFER,
                        this::verifyCreditTransfer));
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_POLLABLE_VERIFY_CREDIT_TRANSFER,
                new PollBucket.Deserializer(),
                this.pollService.jobWithPollableData(Constants.JOB_RESTART_POLLABLE_VERIFY_CREDIT_TRANSFER,
                        this::restartVerifyCreditTransferWithPoll));
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_VERIFY_CREDIT_TRANSFER,
                new RestartVerifyProcessPayload.Deserializer(),
                this.pollService.jobWithPollableData(Constants.JOB_RESTART_POLLABLE_VERIFY_CREDIT_TRANSFER,
                        this::restartVerifyCreditTransfer));
    }

    public Uni<Void> verifyCreditTransfer(final HandleCreditTransferPayload payload) {
        LOG.infof("Starting verify process for %s", payload.idBonificoExtraSepa());
        return this.verifyCreditTransfer(payload.idBonificoExtraSepa(), null);
    }

    public Uni<Void> restartVerifyCreditTransfer(final RestartVerifyProcessPayload payload) {
        LOG.infof("Restarting verify process for %s", payload.idBonificoExtraSepa());

        return this.verifyCreditTransfer(payload.idBonificoExtraSepa(), payload);
    }

    public Uni<Void> restartVerifyCreditTransferWithPoll(final PollBucket bucket) {
        return this.redisClient.value(RedisData.class)
                .getdel(getID(bucket.resourceName(), bucket.resourceID())).flatMap(redisData -> {

                    LOG.infof("Restarting verify process for %s", redisData.creditTransferID);
                    return this.verifyCreditTransfer(
                            redisData.creditTransferID,
                            new RestartVerifyProcessPayload(
                                    redisData.creditTransferID,
                                    redisData.processID,
                                    false,
                                    null,
                                    null));
                });
    }

    public Uni<Void> verifyCreditTransfer(final UUID idBonifico, final RestartVerifyProcessPayload restartProcessInfo) {
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
                                    .flatMap(bonificoWithLinked -> startVerifyProcess(authenticatedUser, cipClient,
                                            registryAnagraficheClient, dataSource, processID,
                                            bonificoWithLinked));
                        } else if (restartProcessInfo.isFromCIP()) {
                            // Recover the result from a CABEL call

                            final CIPReply reply = restartProcessInfo.cipReply();

                            // Recover the entity
                            final DatiVerificaBonifico.Entity entity = new DatiVerificaBonifico.Entity();
                            final DatiVerificaBonifico.Repository repository = entity.repository(dataSource);
                            handleRestartInfo = repository.getByIdForUpdate(processID)
                                    .plug(failIfEnded(processID))
                                    .flatMap(datiVerifica -> restartVerifyFromCabelCall(restartProcessInfo, cipClient,
                                            dataSource,
                                            processID, reply, datiVerifica, bonifico));
                        } else {
                            // Recover the result from a poll

                            // Recover the entity
                            final DatiVerificaBonifico.Entity entity = new DatiVerificaBonifico.Entity();
                            final DatiVerificaBonifico.Repository repository = entity.repository(dataSource);
                            handleRestartInfo = repository.getByIdForUpdate(processID)
                                    .plug(failIfEnded(processID))
                                    .flatMap(
                                            datiVerifica -> restartVerifyFromRegistryResponse(registryAnagraficheClient,
                                                    dataSource, bonifico, processID, datiVerifica, cipClient));
                        }

                        return handleRestartInfo
                                .plug(handlePollInterruptions(bonificoRepository, bonifico, processID))
                                // Handle the additional failures
                                .onFailure().invoke(error -> LOG.error("errore durante la verifica", error))
                                .onFailure(throwable -> !(throwable instanceof ProcessInterruption)).recoverWithUni(
                                        // Log the failure, store the error and then unlock the transfer
                                        throwable -> Uni
                                                .combine().all().unis(
                                                        InfoStatoErrore.storeError(bonifico,
                                                                dataSource,
                                                                throwable),
                                                        bonificoRepository.addNewStatusHistory(bonifico,
                                                                InsertedSubStatus.CONTROLLO_INSERIMENTO_FALLITO.name(),
                                                                throwable instanceof VerifyFailure
                                                                        ? "Le verifiche hanno dato esito negativo."
                                                                        : "Un errore imprevisto è avvenuto durante le verifiche."))
                                                .discardItems()
                                                .onItemOrFailure()
                                                .call(() -> bonificoRepository.updateStatusAndUnlock(bonifico,
                                                        CreditTransferStatus.IN_ERRORE, throwable.getMessage())));
                    });
                });
    }

    /**
     * Stop the restart of the verification process if another call has failed
     */
    private Function<Uni<DatiVerificaBonifico>, Uni<DatiVerificaBonifico>> failIfEnded(UUID processID) {
        return uni -> uni.onItem().transform(datiVerifica -> {
            if (datiVerifica.statoVerifica().equals(StatoVerifica.FALLITO)) {
                throw new OtherCallsHaveFailed(processID);
            }
            return datiVerifica;
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

    private Function<Uni<Void>, Uni<Void>> handlePollInterruptions(final BonificoExtraSepa.Repository bonificoRepository,
            final BonificoExtraSepa bonifico,
            final UUID processID) {
        // Handle the pollable resources by storing to redis the ID and raising the
        // interruption
        return uni -> uni
                .onFailure(PollableResourceAccepted.class).recoverWithUni(throwable -> {
                    final var pollable = (PollableResourceAccepted) throwable;

                    final var insertStatus = bonificoRepository.addNewStatusHistory(
                            bonifico,
                            InsertedSubStatus.CONTROLLO_INSERIMENTO_IN_ATTESA.name(),
                            "Le verifiche procederrano quando saranno disponibile le informazioni sulle banche.");

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
                            InsertedSubStatus.CONTROLLO_INSERIMENTO_IN_ATTESA.name(),
                            "Le verifiche procederrano quando saranno disponibile le informazioni sulle banche.");

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
     * Starts the verification process, executing all the calls to the external
     * services
     */
    private Uni<Void> startVerifyProcess(final SecurityIdentity authenticatedUser, final CIPClient cipClient,
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

        final StatoVerificaSaldoRapporto statoVerificaSaldoRapporto;

        if (ordinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)) {
            statoVerificaSaldoRapporto = StatoVerificaSaldoRapporto.VERIFICATO;
        } else {
            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_ESTERORECUPERASALDORAPPORTO",
                    "VERIFICA_" + VerifyStep.RECUPERA_SALDO_RAPPORTO,
                    new EsteroRecuperaSaldoRapportoInput(
                            EsteroRecuperaSaldoRapportoInput.TipoRichiesta.CONTROLLODISPONIBILITA,
                            ordinante.numero(),
                            bonificoWithLinked.dettaglioBonifico().importoDiAddebito(),
                            authenticatedUser.getPrincipal().getName()),
                    httpResponse(processID, bonificoWithLinked,
                            VerifyStep.RECUPERA_SALDO_RAPPORTO)));
            statoVerificaSaldoRapporto = StatoVerificaSaldoRapporto.ATTENDE_RISPOSTA;
        }

        // 1.b - Verifica Avvertenze Rapporto

        final StatoVerificaAvvertenze statoVerificaAvvertenzeRapporto;

        if (ordinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)) {
            statoVerificaAvvertenzeRapporto = StatoVerificaAvvertenze.VERIFICATO;
        } else {
            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CIP_ESTEROCONTROLLORAPPORTO",
                    "VERIFICA_" + VerifyStep.VERIFICA_AVVERTENZE_RAPPORTO.toString(),
                    new EsteroControlloRapportoInput(
                            isBancaABanca
                                    ? EsteroControlloRapportoInput.TipoRichiesta.BANCA
                                    : EsteroControlloRapportoInput.TipoRichiesta.CLIENTE,
                            EsteroControlloRapportoInput.VersoBonifico.USCITA,
                            ordinante.numero(),
                            null),
                    httpResponse(processID, bonificoWithLinked,
                            VerifyStep.VERIFICA_AVVERTENZE_RAPPORTO)));
            statoVerificaAvvertenzeRapporto = StatoVerificaAvvertenze.ATTENDE_RISPOSTA;
        }

        // 1.c - Verifica Embargo

        final StatoVerificaEmbargo statoVerificaEmbargo = StatoVerificaEmbargo.ATTENDE_RISPOSTA;

        registryRequests.add(
                askRegistryForEmbargo(registryAnagraficheClient, processID, bonificoWithLinked.getEntity(),
                        infoIntermediarioDestinatario.getEntity().bic(), cipClient));

        // 1.d - Verifica Cambio

        final StatoVerificaCambio statoVerificaCambio;

        if (bonificoWithLinked.dettaglioBonifico().divisa().equals(ordinante.divisa())) {
            statoVerificaCambio = StatoVerificaCambio.VERIFICATO;
        } else {
            statoVerificaCambio = StatoVerificaCambio.ATTENDE_RISPOSTA;

            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_VERIFICACAMBIO",
                    "VERIFICA_" + VerifyStep.VERIFICA_CAMBIO.toString(),
                    new VerificaCambioInput(
                            VerificaCambioInput.TipoRichiesta.EMPTY,
                            bonificoWithLinked.dettaglioBonifico().divisa(),
                            bonificoWithLinked.dettaglioBonifico().cambio()),
                    httpResponse(processID, bonificoWithLinked, VerifyStep.VERIFICA_CAMBIO)));
        }

        // 1.e/f - Verifica Holiday Table Paese/Divisa

        final StatoVerificaGenerico statoVerificaHolidayTablePaese = StatoVerificaGenerico.ATTENDE_RISPOSTA;
        final StatoVerificaGenerico statoVerificaHolidayTableDivisa = StatoVerificaGenerico.ATTENDE_RISPOSTA;

        cipRequests.add(new CIPRequest(
                UUID.randomUUID().toString(),
                "CCP_VERIFICAHOLIDAYTABLE",
                "VERIFICA_" + VerifyStep.VERIFICA_HOLIDAY_TABLE_PAESI.toString(),
                new VerificaHolidayTableInput(
                        VerificaHolidayTableInput.TipoRichiesta.CONTROLLO,
                        VerificaHolidayTableInput.TipoCodice.PAESE,
                        infoIntermediarioDestinatario.indirizzoPostale.getEntity().paese(),
                        bonificoWithLinked.getEntity().dataRegolamentoBancaBeneficiario().atTime(12,
                                00)),
                httpResponse(processID, bonificoWithLinked,
                        VerifyStep.VERIFICA_HOLIDAY_TABLE_PAESI)));

        cipRequests.add(new CIPRequest(
                UUID.randomUUID().toString(),
                "CCP_VERIFICAHOLIDAYTABLE",
                "VERIFICA_" + VerifyStep.VERIFICA_HOLIDAY_TABLE_DIVISA.toString(),
                new VerificaHolidayTableInput(
                        VerificaHolidayTableInput.TipoRichiesta.CONTROLLO,
                        VerificaHolidayTableInput.TipoCodice.DIVISA,
                        bonificoWithLinked.dettaglioBonifico().divisa(),
                        bonificoWithLinked.getEntity().dataRegolamentoBancaBeneficiario().atTime(12,
                                00)),
                httpResponse(processID, bonificoWithLinked,
                        VerifyStep.VERIFICA_HOLIDAY_TABLE_DIVISA)));

        // 1.g - Verifica Bonifico

        final StatoVerificaBonifico statoVerificaBonifico = StatoVerificaBonifico.ATTENDE_RISPOSTA;

        if (isBancaABanca) {
            final InformazioniRapporto rapportoOrdinante = bonificoWithLinked.informazioniIntermediari
                    .stream()
                    .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                            .equals(TipoIntermediario.ORDINANTE) && !intermediario.getEntity().intermediarioDocumentoCollegato())
                    .findAny().orElseThrow().informazioniRapporto.getEntity();

            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_EXTRASEPACHECKBONIFICOVERIFICA",
                    "VERIFICA_" + VerifyStep.VERIFICA_BONIFICO.toString(),
                    new ExtraSepaCheckBonificoVerificaInput(
                            TipoRichiesta.OUT,
                            TipoMessaggio.PACS009,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.RAPPORTO)
                                    ? rapportoOrdinante.numero()
                                    : null,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)
                                    ? Integer.parseInt(rapportoOrdinante.numero())
                                    : null,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)
                                    ? rapportoOrdinante.divisa()
                                    : null,
                            // TODO: check if this is right, as the document specify to use "Rapporto",
                            // that usually means "numero"
                            bonificoWithLinked.getEntity().ibanContoBancaDiCopertura(),
                            null,
                            // bonificoWithLinked.getEntity().divisaContoBancaDiCopertura(),
                            null,
                            SistemaRegolamento
                                    .fromDBValue(bonificoWithLinked.getEntity().sistemaDiRegolamento()),
                            bonificoWithLinked.dettaglioBonifico().divisa(),
                            false,
                            bonificoWithLinked.dettaglioBonifico().codiceCausaleTransazione(),
                            null,
                            null,
                            null),
                    httpResponse(processID, bonificoWithLinked,
                            VerifyStep.VERIFICA_BONIFICO)));
        } else {
            final InformazioniRapporto rapportoOrdinante = bonificoWithLinked.informazioniAttori
                    .stream().filter(attore -> attore.getEntity().tipo().equals(TipoAttore.ORDINANTE))
                    .findAny().orElseThrow().informazioniRapporto.getEntity();

            final InformazioniRapporto rapportoBeneficiario = bonificoWithLinked.informazioniAttori
                    .stream()
                    .filter(attore -> attore.getEntity().tipo().equals(TipoAttore.BENEFICIARIO))
                    .findAny().orElseThrow().informazioniRapporto.getEntity();

            final InformazioniIntermediario.WithLinkedEntities bancaDelBeneficiario = bonificoWithLinked.informazioniIntermediari
                    .stream().filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                            .equals(TipoIntermediario.BANCA_DEL_BENEFICIARIO) && !intermediario.getEntity().intermediarioDocumentoCollegato())
                    .findAny().orElseThrow();

            final RegolamentoCommissione regolamentoCommissioneCliente = bonificoWithLinked.dettaglioBonificoAccountToAccount.getEntity().regolamentoCommissioneClientela();

            cipRequests.add(new CIPRequest(
                    UUID.randomUUID().toString(),
                    "CCP_EXTRASEPACHECKBONIFICOVERIFICA",
                    "VERIFICA_" + VerifyStep.VERIFICA_BONIFICO.toString(),
                    new ExtraSepaCheckBonificoVerificaInput(
                            TipoRichiesta.OUT,
                            TipoMessaggio.PACS008,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.RAPPORTO)
                                    ? rapportoOrdinante.numero()
                                    : null,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)
                                    ? Integer.parseInt(rapportoOrdinante.numero())
                                    : null,
                            rapportoOrdinante.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO)
                                    ? rapportoOrdinante.divisa()
                                    : null,
                            // TODO: check if this is right, as the document specify to use "Rapporto",
                            // that usually means "numero"
                            bonificoWithLinked.getEntity().ibanContoBancaDiCopertura(),
                            null,
                            // Profeti dice non inviare la divisa nei campi avere
                            // bonificoWithLinked.getEntity().divisaContoBancaDiCopertura(),
                            null,
                            SistemaRegolamento
                                    .fromDBValue(bonificoWithLinked.getEntity().sistemaDiRegolamento()),
                            bonificoWithLinked.dettaglioBonifico().divisa(),
                            regolamentoCommissioneCliente != null
                                    ? regolamentoCommissioneCliente.equals(RegolamentoCommissione.EURO)
                                    : null,
                            bonificoWithLinked.dettaglioBonifico().codiceCausaleTransazione(),
                            rapportoBeneficiario.iban() != null ? rapportoBeneficiario.iban()
                                    : rapportoBeneficiario.altroID(),

                            Optional.ofNullable(bancaDelBeneficiario.indirizzoPostale)
                                    .map(IndirizzoPostale.WithLinkedEntities::getEntity).map(IndirizzoPostale::paese)
                                    .isPresent() ? null : bancaDelBeneficiario.getEntity().bic(),
                            Optional.ofNullable(bancaDelBeneficiario.indirizzoPostale)
                                    .map(IndirizzoPostale.WithLinkedEntities::getEntity).map(IndirizzoPostale::paese)
                                    .orElse(null)),
                    httpResponse(processID, bonificoWithLinked,
                            VerifyStep.VERIFICA_BONIFICO)));
        }

        // Create the data holder

        final DatiVerificaBonifico.Entity datiVerificaEntity = new DatiVerificaBonifico.Entity();
        final DatiVerificaBonifico.Repository datiVerificaRepository = datiVerificaEntity
                .repository(dataSource);
        final Uni<Void> insertDatiVerificaBonifico = datiVerificaRepository
                .run(datiVerificaEntity.insert(new DatiVerificaBonifico(
                        processID,
                        bonificoWithLinked.id(),
                        Instant.now(),
                        StatoVerifica.ATTENDE_RISPOSTE,
                        statoVerificaSaldoRapporto,
                        null,
                        statoVerificaAvvertenzeRapporto,
                        statoVerificaEmbargo,
                        statoVerificaCambio,
                        statoVerificaHolidayTablePaese,
                        statoVerificaHolidayTableDivisa,
                        statoVerificaBonifico)));

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
        return insertDatiVerificaBonifico
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
                        "VERIFICA_" + VerifyStep.VERIFICA_EMBARGO,
                        new EsteroElencoPaesiInput(
                                EsteroElencoPaesiInput.TipoRichiesta.LISTAPARZIALE,
                                ricercaBicResponse.codicePaeseIso(),
                                ricercaBicResponse.codiceBIC()),
                        httpResponse(processID, bonifico,
                                VerifyStep.VERIFICA_EMBARGO)));
    }

    private Uni<Void> restartVerifyFromCabelCall(final RestartVerifyProcessPayload restartProcessInfo,
            final CIPClient cipClient,
            final SqlClient dataSource, final UUID processID, final CIPReply reply,
            final DatiVerificaBonifico datiVerifica,
            final BonificoExtraSepa creditTransfer) {

        var handlerSegnalazioni = handlerSegnalazioniCabel(dataSource, processID, datiVerifica,
                creditTransfer, restartProcessInfo.verifyCall());

        return switch (restartProcessInfo.verifyCall()) {
            case RECUPERA_SALDO_RAPPORTO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroRecuperaSaldoRapportoOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiVerificaBonifico -> updater.updateStatoVerificaSaldoRapporto(
                                                    datiVerificaBonifico, StatoVerificaSaldoRapporto.ERRORE, null)))
                    .flatMap(response -> handleRecuperaSaldoRapporto(response, processID, dataSource,
                            datiVerifica, cipClient));
            case VERIFICA_AVVERTENZE_RAPPORTO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroControlloRapportoOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiVerificaBonifico -> updater.updateStatoVerificaAvvertenzeRapporto(
                                                    datiVerificaBonifico, StatoVerificaAvvertenze.ERRORE)))
                    .flatMap(response -> handleVerificaAvvertenzeRapporto(response, processID,
                            dataSource, datiVerifica, cipClient));
            case VERIFICA_EMBARGO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<EsteroElencoPaesiOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(
                                            datiVerificaBonifico -> updater.updateStatoVerificaEmbargo(
                                                    datiVerificaBonifico, StatoVerificaEmbargo.ERRORE)))
                    .flatMap(response -> handleEmbargoResponse(response, processID,
                            dataSource, datiVerifica, cipClient));
            case VERIFICA_CAMBIO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<VerificaCambioOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(handlerSegnalazioni
                            .apply(
                                    datiVerificaBonifico -> updater.updateStatoVerificaCambio(datiVerificaBonifico,
                                            StatoVerificaCambio.ERRORE))
                            .skipping("CHKCA05"))
                    .flatMap(response -> handleVerificaCambio(response, processID,
                            dataSource, datiVerifica, cipClient));
            case VERIFICA_HOLIDAY_TABLE_DIVISA, VERIFICA_HOLIDAY_TABLE_PAESI -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<VerificaHolidayTableOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(handlerSegnalazioni
                            .apply(datiVerificaBonifico -> {
                                if (restartProcessInfo.verifyCall().equals(VerifyStep.VERIFICA_HOLIDAY_TABLE_DIVISA)) {
                                    return updater.updateStatoVerificaHolidayTableDivisa(datiVerificaBonifico,
                                            StatoVerificaGenerico.ERRORE);
                                } else {
                                    return updater.updateStatoVerificaHolidayTablePaese(datiVerificaBonifico,
                                            StatoVerificaGenerico.ERRORE);
                                }
                            }))
                    .flatMap(response -> handleVerificaHolidayTable(response, processID,
                            dataSource, datiVerifica,
                            restartProcessInfo.verifyCall()
                                    .equals(VerifyStep.VERIFICA_HOLIDAY_TABLE_DIVISA)
                                            ? VerificaHolidayTableInput.TipoCodice.DIVISA
                                            : VerificaHolidayTableInput.TipoCodice.PAESE,
                            cipClient));
            case VERIFICA_BONIFICO -> fetchReply(
                    reply,
                    cipClient,
                    new GenericType<CIPWrapperReply<ExtraSepaCheckBonificoVerificaOutput>>() {
                    },
                    restartProcessInfo.verifyCall())
                    .call(
                            handlerSegnalazioni
                                    .apply(datiVerificaBonifico -> updater.updateStatoVerificaBonifico(
                                            datiVerificaBonifico, StatoVerificaBonifico.ERRORE)))
                    .flatMap(response -> handleVerificaBonifico(response, processID,
                            dataSource, datiVerifica, cipClient, creditTransfer));
            default -> throw new RuntimeException(
                    "The switch should be exaustive, %s is missing".formatted(restartProcessInfo.verifyCall()));
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

    private Function<Function<DatiVerificaBonifico, DatiVerificaBonifico>, HandlerSegnalazioniCabel> handlerSegnalazioniCabel(
            final SqlClient dataSource,
            final UUID processID,
            DatiVerificaBonifico datiVerifica,
            final BonificoExtraSepa creditTransfer,
            final VerifyStep step) {

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
            var datiVerificaBonifico = updateStatus.apply(datiVerifica);
            final Uni<Void> errored = errored(processID, datiVerificaBonifico, dataSource,
                    new VerifyFailure(ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR, errorReport.toString()));

            final DatiVerificaBonificoErroreTecnico.Entity erroreTecnicoEntity = new DatiVerificaBonificoErroreTecnico.Entity();
            final DatiVerificaBonificoErroreTecnico.Repository erroreTecnicoRepository = erroreTecnicoEntity
                    .repository(dataSource);
            final List<Uni<Void>> insertTechnicalErrors = segnalazioniDaRiportare.stream()
                    .filter(s -> s.livello().equals(Segnalazione.Livello.ERRORE))
                    .map(s -> erroreTecnicoRepository.run(erroreTecnicoEntity.insert(
                            new DatiVerificaBonificoErroreTecnico(
                                    UUID.randomUUID(),
                                    datiVerifica.id(),
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
    private Uni<Void> restartVerifyFromRegistryResponse(final RegistryAnagraficheClient registryAnagraficheClient,
            final SqlClient dataSource,
            final BonificoExtraSepa bonifico, final UUID processID, final DatiVerificaBonifico datiVerifica,
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

        if (datiVerifica.statoVerificaEmbargo().isWaitingForAnswer()) {
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
            final UUID processID, final SqlClient dataSource, DatiVerificaBonifico datiVerifica,
            final CIPClient cipClient) {

        if (FlagSiNo.SI.equals(response.flagDisponibilita())) {
            datiVerifica = updater.updateStatoVerificaSaldoRapporto(datiVerifica,
                    StatoVerificaSaldoRapporto.VERIFICATO, BigDecimal.ZERO);
        } else if (FlagSiNo.SI.equals(response.flagForzaturaSconfinamento())) {
            datiVerifica = updater.updateStatoVerificaSaldoRapporto(datiVerifica,
                    StatoVerificaSaldoRapporto.NECESSITA_FORZATURA_SCONFINAMENTO,
                    response.importoSconfinamento());
        } else {
            datiVerifica = updater.updateStatoVerificaSaldoRapporto(datiVerifica,
                    StatoVerificaSaldoRapporto.FALLITO,
                    response.importoSconfinamento());
            return errored(processID, datiVerifica, dataSource,
                    new VerifyFailure(ErrorCodes.Codes.SCONFINAMENTO_NON_PERMESSO,
                            "Conto sconfinato. Operazione non consentita."));
        }

        // Update with the new state
        return storeAfterCallEnded(dataSource, datiVerifica, cipClient);
    }

    private Uni<Void> handleVerificaAvvertenzeRapporto(final EsteroControlloRapportoOutput response,
            final UUID processID,
            final SqlClient dataSource, final DatiVerificaBonifico datiVerifica, final CIPClient cipClient) {

        final var datiAvvertenzaEntity = new DatiVerificaBonificoAvvertenza.Entity();
        final var datiAvvertenzaRepository = datiAvvertenzaEntity.repository(dataSource);

        final Uni<Void> loadAvvertenze = Multi.createFrom().iterable(response.avvertenze())
                .onItem().transformToUniAndMerge(avvertenza -> datiAvvertenzaRepository.run(
                        datiAvvertenzaEntity.insert(new DatiVerificaBonificoAvvertenza(UUID.randomUUID(),
                                datiVerifica.id(), avvertenza.codice(), avvertenza.descrizione()))))
                .onItem().ignoreAsUni();

        final DatiVerificaBonifico newDatiVerifica;
        Uni<Void> doAfter = Uni.createFrom().voidItem();

        if (EsteroControlloRapportoOutput.EsitoControlloGenerale.VALIDO.equals(response.esitoControlloGenerale())) {
            newDatiVerifica = updater.updateStatoVerificaAvvertenzeRapporto(datiVerifica,
                    StatoVerificaAvvertenze.VERIFICATO);
            doAfter = storeAfterCallEnded(dataSource, newDatiVerifica, cipClient);
        } else {

            final VerifyFailure failure;

            if (response.esitoAvvertenze() != null && response.esitoAvvertenze().codice().equals("BLOCCODARE")) {
                newDatiVerifica = updater.updateStatoVerificaAvvertenzeRapporto(datiVerifica,
                        StatoVerificaAvvertenze.BLOCCO_DARE);
                failure = new VerifyFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_DARE,
                        "Presenza della seguente avvertenza bloccante in Dare sul Rapporto Ordinante:\n" +
                                response.esitoAvvertenze().descrizione() +
                                "\nOperazione non consentita.");
                doAfter = datiAvvertenzaRepository.run(
                        datiAvvertenzaEntity.insert(new DatiVerificaBonificoAvvertenza(
                                UUID.randomUUID(),
                                datiVerifica.id(),
                                response.esitoAvvertenze().codice(),
                                response.esitoAvvertenze().descrizione())));
            } else if (response.bloccoDare()) {
                newDatiVerifica = updater.updateStatoVerificaAvvertenzeRapporto(datiVerifica,
                        StatoVerificaAvvertenze.BLOCCO_DARE);
                failure = new VerifyFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_DARE,
                        "Presenza Blocco Dare su Rapporto Ordinante. Operazione non consentita.");
            } else if (response.bloccoTotale()) {
                newDatiVerifica = updater.updateStatoVerificaAvvertenzeRapporto(datiVerifica,
                        StatoVerificaAvvertenze.BLOCCO_TOTALE);
                failure = new VerifyFailure(ErrorCodes.Codes.AVVERTENZE_BLOCCO_TOTALE,
                        "Presenza Blocco Totale su Rapporto Ordinante. Operazione non consentita.");
            } else {
                newDatiVerifica = updater.updateStatoVerificaAvvertenzeRapporto(datiVerifica,
                        StatoVerificaAvvertenze.FALLITO);
                LOG.errorf("Unknown failure from CABEL: %s", response);
                failure = new VerifyFailure(ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR, "Unknown failure from CABEL");
            }

            doAfter = doAfter.flatMap(ignored -> errored(processID, newDatiVerifica, dataSource, failure));
        }

        // Update with the new state
        final Uni<Void> finalDoAfter = doAfter;
        return loadAvvertenze
                .chain(() -> finalDoAfter);
    }

    private Uni<Void> handleEmbargoResponse(final EsteroElencoPaesiOutput esteroPaesiOutput, final UUID processID,
            final SqlClient dataSource, DatiVerificaBonifico datiVerifica, final CIPClient cipClient) {

        if (esteroPaesiOutput.listaPaesi().isEmpty()) {
            datiVerifica = updater.updateStatoVerificaEmbargo(datiVerifica, StatoVerificaEmbargo.FALLITO);
            return errored(processID, datiVerifica, dataSource,
                    new VerifyFailure(
                            ErrorCodes.Codes.CABEL_CALL_TECHNICAL_ERROR,
                            "Ricerca paesi non ha prodotto nessun risultato"));
        }

        var paese = esteroPaesiOutput.listaPaesi().get(0);

        switch (paese.embargo()) {
            case NONSOTTOEMBARGO:
                datiVerifica = updater.updateStatoVerificaEmbargo(datiVerifica, StatoVerificaEmbargo.VERIFICATO);
                break;
            case SOTTOEMBARGOPARZIALE:
                datiVerifica = updater.updateStatoVerificaEmbargo(datiVerifica,
                        StatoVerificaEmbargo.NECESSITA_FORZATURA_EMBARGO_PARZIALE);
                break;
            case SOTTOEMBARGOTOTALE:
                datiVerifica = updater.updateStatoVerificaEmbargo(datiVerifica,
                        StatoVerificaEmbargo.FALLITO);
                return errored(processID, datiVerifica, dataSource,
                        new VerifyFailure(
                                ErrorCodes.Codes.EMBARGO_TOTALE,
                                "Paese banca del beneficiario sotto embargo totale. Operazione non consentita."));
        }

        // Update with the new state
        return storeAfterCallEnded(dataSource, datiVerifica, cipClient);

    }

    private Uni<Void> handleVerificaCambio(final VerificaCambioOutput response, final UUID processID,
            final SqlClient dataSource,
            DatiVerificaBonifico datiVerifica, final CIPClient cipClient) {

        if (response.errored()) {
            // We know the error is CHKCA05, as it is the only error code skipped by the
            // technical error handler
            datiVerifica = updater.updateStatoVerificaCambio(datiVerifica,
                    StatoVerificaCambio.NECESSITA_MODIFICA_CAMBIO);
            return errored(processID, datiVerifica, dataSource,
                    new VerifyFailure(ErrorCodes.Codes.CAMBIO_SUPERIORE_AL_SCARTO,
                            "Errore: cambio superiore alla % di scarto. Operazione non consentita."));
        }

        datiVerifica = updater.updateStatoVerificaCambio(datiVerifica,
                StatoVerificaCambio.VERIFICATO);

        // Update with the new state
        return storeAfterCallEnded(dataSource, datiVerifica, cipClient);
    }

    private Uni<Void> handleVerificaHolidayTable(final VerificaHolidayTableOutput response, final UUID processID,
            final SqlClient dataSource,
            DatiVerificaBonifico datiVerifica, final VerificaHolidayTableInput.TipoCodice tipoCodice,
            final CIPClient cipClient) {

        final var stato = response.festivo()
                ? StatoVerificaGenerico.FALLITO
                : StatoVerificaGenerico.VERIFICATO;

        switch (tipoCodice) {
            case DIVISA:
                datiVerifica = updater.updateStatoVerificaHolidayTableDivisa(datiVerifica, stato);
                break;
            case PAESE:
                datiVerifica = updater.updateStatoVerificaHolidayTablePaese(datiVerifica, stato);
                break;
        }

        if (stato == StatoVerificaGenerico.FALLITO) {
            return errored(processID, datiVerifica, dataSource, new VerifyFailure(
                    ErrorCodes.Codes.DATA_FESTIVA
            ));
        }

        // Update with the new state
        return storeAfterCallEnded(dataSource, datiVerifica, cipClient);
    }

    private Uni<Void> handleVerificaBonifico(final ExtraSepaCheckBonificoVerificaOutput response, final UUID processID,
            final SqlClient dataSource, final DatiVerificaBonifico datiVerifica, final CIPClient cipClient,
            final BonificoExtraSepa bonifico) {
        return new SottoTipologiaBonifico.Entity().repository(dataSource)
                .getByID(bonifico.idSottoTipologiaBonifico()).flatMap(sottoTipologiaBonifico -> {
                    final var isBancaABanca = sottoTipologiaBonifico.bancaABanca();
                    final DatiVerificaBonifico newDatiVerifica;

                    if (Boolean.TRUE.equals(isBancaABanca) && Boolean.TRUE.equals(response.ibanObbligatorio())) {
                        newDatiVerifica = updater.updateStatoVerificaBonifico(datiVerifica,
                                StatoVerificaBonifico.NECESSITA_MODIFICA_IBAN);
                        return errored(processID, newDatiVerifica, dataSource,
                                new VerifyFailure(ErrorCodes.Codes.RICHIESTO_IBAN_BENEFICIARIO,
                                        "Richiesto IBAN Beneficiario per Paese Banca del Beneficiario. Operazione non consentita."));
                    } else {
                        newDatiVerifica = updater.updateStatoVerificaBonifico(datiVerifica,
                                StatoVerificaBonifico.VERIFICATO);
                    }

                    return storeAfterCallEnded(dataSource, newDatiVerifica, cipClient);
                });
    }

    private Uni<Void> storeAfterCallEnded(final SqlClient dataSource, final DatiVerificaBonifico datiVerifica,
            final CIPClient cipClient) {
        if (datiVerifica.statoVerifica() != StatoVerifica.ATTENDE_RISPOSTE) {
            throw new RuntimeException("Stato di verifica inaspettato: " + datiVerifica.statoVerifica());
        }

        final DatiVerificaBonifico.Entity entity = new DatiVerificaBonifico.Entity();
        final var repository = entity.repository(dataSource);

        if (!datiVerifica.allCallsEnded()) {
            // The calls are still ongoing. Wait for the others
            LOG.infof("Chiamate non completate, aggiorno DatiVerificaBonifico per bonifico %s",
                    datiVerifica.idBonificoExtraSepa());
            return repository.run(entity.update(datiVerifica));
        }

        LOG.infof("Chiamate completate per il processo di verifica del bonifico %s",
                datiVerifica.idBonificoExtraSepa());

        final var bonificoEntity = new BonificoExtraSepa.Entity();
        final var bonificoRepository = bonificoEntity.repository(dataSource);

        return bonificoRepository.getById(datiVerifica.idBonificoExtraSepa()).flatMap(bonifico -> {
            final Uni<Void> updateVerifica;
            final Uni<Void> addStatusHistory;
            final Uni<Void> updateStatusAndUnlock;
            final Uni<Void> scheduleHandleJob;

            if (!datiVerifica.allCallsWereSuccessful()) {
                // Some calls ended with an error that can be fixed in the frontend
                LOG.infof("Verifica del bonifico %s interrotta, necessario intervento dal frontend", bonifico.id());

                updateVerifica = repository
                        .run(entity.update(updater.updateStatoVerifica(datiVerifica, StatoVerifica.DA_CONFERMARE)));

                addStatusHistory = bonificoRepository.addNewStatusHistory(bonifico,
                        InsertedSubStatus.CONTROLLO_INSERIMENTO_DA_CONFERMARE.name(),
                        "Le verifiche hanno evidenziato dati che necessitano modifica.");

                updateStatusAndUnlock = bonificoRepository.updateStatusAndUnlock(bonifico,
                        CreditTransferStatus.DA_CONFERMARE);

                // Non lanciare il job di gestione fino a che i dati non saranno stati corretti
                scheduleHandleJob = Uni.createFrom().voidItem();

            } else {
                // All calls were successful, the transfer was approved
                LOG.infof("Verifica del bonifico %s completata con esito positivo", bonifico.id());

                updateVerifica = repository
                        .run(entity.update(updater.updateStatoVerifica(datiVerifica, StatoVerifica.VERIFICATO)));

                addStatusHistory = bonificoRepository.addNewStatusHistory(bonifico,
                        InsertedSubStatus.CONTROLLO_INSERIMENTO_PASSATO.name(),
                        "Le verifiche sono state completate con esito positivo.");

                updateStatusAndUnlock = bonificoRepository.updateStatusAndUnlock(bonifico,
                        CreditTransferStatus.DA_CONFERMARE);

                scheduleHandleJob = this.jobPublisher.scheduleJob(
                        new JobData(
                                UUID.randomUUID(),
                                Constants.JOB_HANDLE_CREDIT_TRANSFER,
                                new HandleCreditTransferPayload(bonifico.id())));

            }

            // First update the verification data (maybe we should cancel it?) and add a new
            // status history
            return Uni.combine().all().unis(updateVerifica, addStatusHistory)
                    .discardItems()
                    // then unlock the bonifico and move it to the next status
                    .call(() -> updateStatusAndUnlock)
                    // finally schedule the next job to handle it
                    .call(() -> scheduleHandleJob);
        });

    }

    /**
     * Un errore bloccante si è verificato. Il bonifico non può continuare ma si
     * deve ricominciare con un nuovo processo
     */
    private Uni<Void> errored(final UUID processID, final DatiVerificaBonifico datiVerifica, final SqlClient dataSource,
            final VerifyFailure failure) {

        final DatiVerificaBonifico.Entity entity = new DatiVerificaBonifico.Entity();
        return entity.repository(dataSource)
                .run(entity.update(updater.updateStatoVerifica(datiVerifica, StatoVerifica.FALLITO)))
                .onItem().failWith(() -> failure);
    }

    // Utils

    private <T> Uni<T> fetchReply(
            final CIPReply reply,
            final CIPClient client,
            final GenericType<CIPWrapperReply<T>> type,
            VerifyStep step) {
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
            final VerifyStep step) {
        return httpResponse(processID, bonifico.getEntity(), step);
    }

    private HTTPResponseKind httpResponse(
            final UUID processID,
            final BonificoExtraSepa bonifico,
            final VerifyStep step) {
        return new HTTPResponseKind(
                config.url() + "/cip/results/verify"
                        + "/" + processID
                        + "/" + bonifico.id()
                        + "/" + step);
    }

}
