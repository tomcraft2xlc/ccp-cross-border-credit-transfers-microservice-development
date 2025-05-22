package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import com.flowpay.ccp.auth.client.AuthConstants;
import com.flowpay.ccp.auth.client.CabelCredentialForwardFilter;
import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.cip.client.CIPClient;
import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.cip.client.dto.CIPRequest;
import com.flowpay.ccp.cip.client.dto.HTTPResponseKind;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.clients.RegistryAnagraficheClient;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.exceptions.ChangeDateInterrupted;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.Autorizzazione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.CIPWrapperReply;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableInput.TipoCodice;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableInput.TipoRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.VerificaHolidayTableOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange.HandleDateChangePayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange.RestartHandleDateChangePayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.registry.dto.responses.RicercaBicResponse;
import com.flowpay.ccp.resources.poll.client.PollableResourceAcceptedHandler;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

// TODO: This must be rewritten as it currently checks only one between divisa and country, and it should check both

public class ChangeDateWorker {

    private static final Logger LOG = Logger.getLogger(ChangeDateWorker.class);

    CreditTransferService service;
    DataSources dataSources;
    JobPublisher jobPublisher;
    JobSubscriber jobSubscriber;
    ServiceConfig config;
    ReactiveRedisDataSource redis;
    URL cipUrl;
    URL registryURL;
    BanksConfig banksConfig;

    ChangeDateWorker(
            CreditTransferService service,
            DataSources dataSources,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> channel,
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) JobSubscriber jobSubscriber,
            ServiceConfig config,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.cip.client\".url") URL cipUrl,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") URL registryURL,
            ReactiveRedisDataSource redis,
            BanksConfig banksConfig) {
        this.service = service;
        this.dataSources = dataSources;
        this.jobPublisher = new JobPublisher(channel);
        this.jobSubscriber = jobSubscriber;
        this.config = config;
        this.cipUrl = cipUrl;
        this.registryURL = registryURL;
        this.redis = redis;
        this.banksConfig = banksConfig;
    }

    public void onStart(@Observes StartupEvent event) {
        LOG.info("Starting ChangeDateWorker");
        jobSubscriber.subscribe(
                Constants.JOB_HANDLE_DATE_CHANGE,
                new HandleDateChangePayload.Deserializer(),
                this::handleDateChange);
        jobSubscriber.subscribe(
                Constants.JOB_RESTART_HANDLE_DATE_CHANGE,
                new RestartHandleDateChangePayload.Deserializer(),
                this::handleDateChange);
    }

    public Uni<Void> handleDateChange(HandleDateChangePayload payload) {
        LOG.debug("Starting date change [authorizationId=%s, creditTransferId=%s]"
                .formatted(payload.idAutorizzazione(), payload.idBonificoExtraSepa()));

        return handleDateChange(payload.idBonificoExtraSepa(), payload.idAutorizzazione(),
                payload.proposedDateEpochDay(), null);
    }

    public Uni<Void> handleDateChange(RestartHandleDateChangePayload payload) {
        LOG.debug("Restarting date change [authorizationId=%s, creditTransferId=%s]"
                .formatted(payload.idAutorizzazione(), payload.idBonificoExtraSepa()));
        var payloadToPass = payload.fromRegistry() ? null : payload;
        return handleDateChange(payload.idBonificoExtraSepa(), payload.idAutorizzazione(),
                payload.proposedDateEpochDay(), payloadToPass);
    }

    private String redisKey(RestartHandleDateChangePayload restartHandleDateChangePayload) {
        return "ccp.cross-border.date-change." + restartHandleDateChangePayload.processID();
    }

    public Uni<Void> handleDateChange(UUID idBonificoExtraSepa, UUID idAutorizzazione, Long proposedDateEpochDay,
            RestartHandleDateChangePayload restartProcessInfo) {

        var authenticatedUser = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var filter = new CabelCredentialForwardFilter(authenticatedUser);
        // Create the client
        CIPClient cipClient = QuarkusRestClientBuilder.newBuilder()
                .register(filter)
                .baseUrl(cipUrl)
                .build(CIPClient.class);

        var registryClient = QuarkusRestClientBuilder.newBuilder()
                .register(filter)
                .register(new PollableResourceAcceptedHandler())
                .baseUrl(registryURL)
                .build(RegistryAnagraficheClient.class);

        // Get a connection to the db
        var dataSource = dataSources.getDataSource(authenticatedUser.getAttribute(AuthConstants.ABI_ATTRIBUTE));

        // Convert back the date
        var proposedDate = LocalDate.ofEpochDay(proposedDateEpochDay);

        // Fetch the related entities
        var bonificoRepo = new BonificoExtraSepa.Entity().repository(dataSource);
        var bonificoUni = bonificoRepo.getByIdLocked(idBonificoExtraSepa).map(BonificoExtraSepa::withLinkedEntities)
        .flatMap(bonifico -> bonifico.loadAll(dataSource).onItem().ignoreAsUni().replaceWith(bonifico));

        Uni<Autorizzazione> autorizzazioneUni = new Autorizzazione.Entity().repository(dataSource)
        .getById(idAutorizzazione);

        return Uni.combine().all().unis(bonificoUni, autorizzazioneUni).withUni(
                (bonifico, autorizzazione) ->
                        // Obtain the bank to query for
                        getIntermediarioDiRiferimento(bonifico.getEntity(), dataSource)
                        .flatMap(tipoIntermediario -> new InformazioniIntermediario.Entity().repository(dataSource)
                        .getAllByBonificoExtraSepa(idBonificoExtraSepa)
                        .filter(intermediari -> intermediari.tipoIntermediario().equals(tipoIntermediario))
                        .toUni())
                        .flatMap(intermediario -> {
                            var processID = Optional.ofNullable(restartProcessInfo).map(RestartHandleDateChangePayload::processID).orElse(UUID.randomUUID());
                            return registryClient.dettaglioBanca(
                                            intermediario.bic(),
                                            config.url() + "/registry/results/date-change/" +
                                                    processID + "/" +
                                                    bonifico.id() + "/" +
                                                    autorizzazione.id() + "/" +
                                                    proposedDate.toEpochDay(),
                                            false)
                                    .flatMap(infoBanca -> {
                                        if (restartProcessInfo == null) {
                                            return doCipCall(bonifico, infoBanca, autorizzazione, proposedDate, processID, dataSource, cipClient);
                                        }
                                        CIPReply reply = restartProcessInfo.cipReply();

                                        // TODO: if (!reply.success()) { }

                                        return cipClient.getResponse(reply.requestID()).flatMap(response -> {
                                            final VerificaHolidayTableOutput decoded = response
                                                    .readEntity(new GenericType<CIPWrapperReply<VerificaHolidayTableOutput>>() {
                                                    }).data();
                                            if (decoded.festivo()) {
                                                // failure, we need to check the next date
                                                final var next = Utils.nextPossibleBusinessDay(
                                                        proposedDate.plusDays(1),
                                                        banksConfig.bank().get(authenticatedUser.<String>getAttribute(AuthConstants.ABI_ATTRIBUTE)),
                                                        bonifico.getEntity().sistemaDiRegolamento());
                                                // Recurse with the new date, asking cip again
                                                return doCipCall(bonifico, infoBanca, autorizzazione, next,
                                                        restartProcessInfo.processID(), dataSource, cipClient);
                                            }

                                            // Found a business day!
                                            LOG.debug(
                                                    "Checking date change [authorizationId=%s, creditTransferId=%s, change=%s -> %s]"
                                                            .formatted(autorizzazione.id(), bonifico.id(),
                                                                    autorizzazione.dataDiRegolamentoPrecedente(), proposedDate));

                                            var set = redis.set(TipoCodice.class);
                                            return set.sadd(redisKey(restartProcessInfo), Boolean.TRUE.equals(restartProcessInfo.isPaese()) ? TipoCodice.PAESE : TipoCodice.DIVISA)
                                                    .flatMap(ignored -> set.smembers(redisKey(restartProcessInfo)))
                                                    .flatMap(values -> {
                                                        if (values.size() == 2) {
                                                            LOG.debug("Both results came out positive, changing the date");
                                                            return new BonificoExtraSepa.Entity().repository(dataSource)
                                                                    .updateDataDiRegolamentoAndUnlock(bonifico.id(), proposedDate);
                                                        } else {
                                                            LOG.debug("Just one result arrived or the other result was negative, nothing to do");
                                                            return Uni.createFrom().voidItem();
                                                        }
                                                    });
                                        });
                                    });
                        }));
    }

    private Uni<TipoIntermediario> getIntermediarioDiRiferimento(BonificoExtraSepa bonifico, PgPool dataSource) {
        // Fetch the sottotipologia
        return new SottoTipologiaBonifico.Entity().repository(dataSource).getByID(bonifico.idSottoTipologiaBonifico())
                .map(tipologia -> Boolean.TRUE.equals(tipologia.bancaABanca())
                        ? TipoIntermediario.BANCA_BENEFICIARIA
                        : TipoIntermediario.BANCA_DEL_BENEFICIARIO);
    }

    /**
     * Call CIP to check if the proposed date is a business day
     */
    private Uni<Void> doCipCall(BonificoExtraSepa.WithLinkedEntities creditTransfer, RicercaBicResponse intermediario,
            Autorizzazione auth, LocalDate proposedDate, UUID processId,
            PgPool dataSource, CIPClient client) {

        LOG.debug("Querying cip for date change [authorizationId=%s, creditTransferId=%s, proposedChange=%s -> %s]"
        .formatted(auth.id(), creditTransfer.id(), auth.dataDiRegolamentoPrecedente(), proposedDate));
        return client.createRequest(new CIPRequest(
                "date-change:paese:" + processId,
                "CCP_VERIFICAHOLIDAYTABLE",
                "handle-date-change-paesi",
                new VerificaHolidayTableInput(
                        TipoRichiesta.CONTROLLO,
                        TipoCodice.PAESE,
                        intermediario.codicePaeseIso(),
                        proposedDate.atTime(12, 0)),
                new HTTPResponseKind(config.url()
                        + "/cip/results/holiday_date"
                        + "/" + processId
                        + "/" + creditTransfer.id()
                        + "/" + auth.id()
                        + "/" + proposedDate.toEpochDay()
                        + "/true")))
                .flatMap(ignored ->
                        client.createRequest(new CIPRequest(
                                "date-change:divisa:" + processId,
                                "CCP_VERIFICAHOLIDAYTABLE",
                                "handle-date-change-divisa",
                                new VerificaHolidayTableInput(
                                        TipoRichiesta.CONTROLLO,
                                        TipoCodice.DIVISA,
                                        creditTransfer.dettaglioBonifico().divisa(),
                                        proposedDate.atTime(12, 0)),
                                new HTTPResponseKind(config.url()
                                        + "/cip/results/holiday_date"
                                        + "/" + processId
                                        + "/" + creditTransfer.id()
                                        + "/" + auth.id()
                                        + "/" + proposedDate.toEpochDay()
                                        + "/false"))))
                .onFailure().recoverWithUni(throwable -> {
                    var entity = new BonificoExtraSepa.Entity();
                    var repository = entity.repository(dataSource);
                    return InfoStatoErrore.storeError(creditTransfer.getEntity(), dataSource, throwable)
                            .flatMap(ignored -> repository
                                    .updateStatusAndUnlock(creditTransfer.getEntity(), CreditTransferStatus.IN_ERRORE,
                                            "Failed call to CIP [processId=%s]".formatted(processId))
                                    .replaceWith(Uni.createFrom().failure(throwable)));
                })
                .map(ignored -> {
                    throw new ChangeDateInterrupted(
                            "CIP request sent",
                            processId);
                });
    }
}
