package com.flowpay.ccp.credit.transfer.cross.border.services;

import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.InserisciAutorizzazioneBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.ListaAutorizzazioniBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.authorization.ListaAutorizzazioniBonificoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.Autorizzazione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.AutorizzazioneActionEnum;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.configuration.ConfigurazioniAutorizzative;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.mappatura_livelli.MappaturaLivelliAutorizzativi;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange.HandleDateChangePayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.persistence.DataSources;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.javatuples.Pair;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class AuthorizationService {

    private final DataSources dataSources;
    private final CreditTransferService creditTransferService;
    private final BanksConfig banksConfig;
    private final ListaAutorizzazioniBonificoMapper listaAutorizzazioniBonificoMapper;
    private final JobPublisher jobPublisher;

    @Inject
    AuthorizationService(CreditTransferService creditTransferService, BanksConfig banksConfig,
            DataSources dataSources, ListaAutorizzazioniBonificoMapper listaAutorizzazioniBonificoMapper,
            JobPublisher jobPublisher) {
        this.dataSources = dataSources;
        this.creditTransferService = creditTransferService;
        this.banksConfig = banksConfig;
        this.listaAutorizzazioniBonificoMapper = listaAutorizzazioniBonificoMapper;
        this.jobPublisher = jobPublisher;
    }

    private record DatiLivelloAutorizzativo(
            Long livello,
            Boolean isLastLevel) {
    }

    /**
     * Controlla se l'utente può autorizzare un dato bonifico.
     * 
     * Questo richiede che il bonifico non sia già autorizzato, e che il livello di
     * autorizzazione del bonifico sia uno in meno di quello dell'utente.
     * 
     * @return Un opzionale vuoto se non è possibile, altrimenti un record
     *         contenente alcuni metadata sul livello di autorizzazione da dare e se
     *         è quello che autorizza definitamente.
     */
    private Uni<Optional<DatiLivelloAutorizzativo>> checkIfCanAuthorize(BonificoExtraSepa bonifico,
            SqlClient connection,
            SecurityIdentity identity, BanksConfig.BankConfig bankConfig) {
        final var configurationRepository = new ConfigurazioniAutorizzative.Entity().repository(connection);
        final var mappatturaLivelliRepository = new MappaturaLivelliAutorizzativi.Entity().repository(connection);
        final var authorizationRepository = new Autorizzazione.Entity().repository(connection);
        // Trovo i livelli necessari per autorizzare
        final Uni<Long> livelliNecessari = configurationRepository
                .search(bonifico.idSottoTipologiaBonifico(), bonifico.idCanale())
                .map(configurazione -> configurazione.map(ConfigurazioniAutorizzative::livelliDiAutorizzazione)
                        .orElse(bankConfig.defaultAuthorizationLevels()));
        // Trovo il livello di autorizzazione attuale
        final Uni<Long> livelloAttuale = authorizationRepository
                .getLast(bonifico.id(), AutorizzazioneActionEnum.AUTORIZZAZIONE)
                .map(autorizzazione -> autorizzazione.map(Autorizzazione::livelloAutorizzazione).orElse(0L));
        // Ottengo il livello di autorizzazione dell'utente attuale
        final Uni<List<MappaturaLivelliAutorizzativi>> livelliUtente = mappatturaLivelliRepository
                .getLevelByRuolo(identity.getCredential(CabelForwardedCredential.class).role());

        return Uni.combine().all().unis(livelliNecessari, livelloAttuale, livelliUtente).with(
                (livelliNecessariV, livelloAttualeV, livelliUtenteV) -> {

                    if (livelloAttualeV >= livelliNecessariV
                            || livelliUtenteV.stream().map(MappaturaLivelliAutorizzativi::livello)
                                    .noneMatch(livello -> livello == livelloAttualeV + 1)) {
                        // Già autorizzato o non al livello giusto
                        return Optional.empty();
                    }
                    return Optional.of(new DatiLivelloAutorizzativo(livelloAttualeV + 1,
                            Objects.equals(livelloAttualeV + 1, livelliNecessariV)));
                });
    }

    public Uni<ListaAutorizzazioniBonifico> create(UUID id, InserisciAutorizzazioneBonifico request,
            SecurityIdentity identity) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        final var bankConfig = banksConfig.bank().get(credential.abi());
        final PgPool dataSource = dataSources.dataSource(identity);
        return dataSource.<BonificoExtraSepa>withTransaction(
                // All writing behaviour happens inside this single transaction
                transaction -> getBonificoIfAllowed(id, identity, transaction, bankConfig)
                        .flatMap(bonificoExtraSepa -> {
                            final var entity = new Autorizzazione.Entity();
                            final var repository = entity.repository(transaction);

                            // Controlla se l'autorizzazione è valida
                            final Uni<DatiLivelloAutorizzativo> canAuthorize = checkIfCanAuthorize(
                                    bonificoExtraSepa, transaction, identity,
                                    bankConfig).map(check -> {
                                        if (check.isEmpty()) {
                                            throw new UnauthorizedException(
                                                    "utente non autorizzato ad autorizzare il bonifico");
                                        }
                                        if (Boolean.TRUE.equals(isNecessaryToChangeSettlementDate(bonificoExtraSepa, bankConfig))) {
                                            throw new UnauthorizedException(
                                                    "prima di autorizzare il bonifico è necessario modificare la data di regolamento"
                                            );
                                        }
                                        return check.get();
                                    });


                            // Autorizzazione parzialmente costruita
                            final var authBuilder = Autorizzazione.buildAutorizzazione()
                                    .bonificoExtraSepa(bonificoExtraSepa)
                                    .credentials(credential)
                                    .autorizzazioneMessaggio(request.autorizzazioneMessaggio())
                                    .autorizzazioneNotifica(request.autorizzazioneNotifica())
                                    .note(request.note());

                            if (request.autorizzazioneMessaggio()) {
                                // Happy path: authorization was given

                                // Check the notification
                                final var tipologiaBonificoRepository = new SottoTipologiaBonifico.Entity()
                                        .repository(transaction);
                                final var hasNotificaCheck = tipologiaBonificoRepository
                                        .getByID(bonificoExtraSepa.idSottoTipologiaBonifico())
                                        .map(SottoTipologiaBonifico::conNotifica);
                                final Uni<Void> notificaCheck;
                                if (request.autorizzazioneNotifica() != null) {
                                    notificaCheck = Boolean.TRUE.equals(request.autorizzazioneNotifica())
                                            ? hasNotificaCheck.flatMap(hasNotifica -> {
                                                if (Boolean.FALSE.equals(hasNotifica)) {
                                                    return Uni.createFrom().failure(new ForbiddenException(
                                                            "Non è possibile autorizzare la notifica, non è prevista per questo messaggio"));
                                                }
                                                return repository.findNotAuthorizingNotification(bonificoExtraSepa.id())
                                                        .flatMap(autorizzazione -> {
                                                            if (autorizzazione.isPresent()) {
                                                                // Qualcuno a un livello precedente ha rifiutato la
                                                                // notifica
                                                                return Uni.createFrom().failure(new ForbiddenException(
                                                                        "Cannot authorize notification as it was previously negated at %s"
                                                                                .formatted(autorizzazione.get()
                                                                                        .createdAt())));
                                                            }
                                                            return Uni.createFrom().voidItem();
                                                        });
                                            })
                                            : Uni.createFrom().voidItem();
                                } else {
                                    notificaCheck = hasNotificaCheck.map(hasNotifica -> {
                                        if (Boolean.TRUE.equals(hasNotifica)) {
                                            throw new ForbiddenException(
                                                   "E' necessario esprimere la decisione anche sulla notifica per questo tipo di messaggio"
                                            );
                                        }
                                        return null;
                                    });
                                }

                                return Uni.combine().all().unis(canAuthorize, notificaCheck)
                                        .withUni((check, ignore) -> {
                                            // Insert the autorization
                                            final var insertAuth = repository.run(entity.insert(
                                                    authBuilder.livelloAutorizzazione(check.livello()).build()));
                                            // Se è l'ultimo livello, cambia lo stato del bonifico
                                            final var statoBonificoAfter = Boolean.TRUE.equals(check.isLastLevel)
                                                    ? creditTransferService.doUpdate(bonificoExtraSepa,
                                                            CreditTransferStatus.AUTORIZZATO, identity)
                                                    : Uni.createFrom().item(bonificoExtraSepa);

                                            return Uni.combine().all().unis(statoBonificoAfter, insertAuth)
                                                    .with((stato, ignore2) -> stato);
                                        });
                            } else {
                                // bad path: authoriaztion was refused

                                return canAuthorize.flatMap(check -> {
                                    // Insert the negated authorization
                                    final var insertAuth = repository.run(entity.insert(authBuilder
                                            .livelloAutorizzazione(check.livello())
                                            .build()));
                                    // Cambio lo stato del bonifico
                                    final var statoBonificoAfter = creditTransferService.doUpdate(bonificoExtraSepa,
                                            CreditTransferStatus.NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO, identity);

                                    return Uni.combine().all().unis(statoBonificoAfter, insertAuth)
                                            .with((stato, ignore) -> stato);
                                });
                            }
                        }))
                .flatMap(statoBonificoAfter -> listAuths(dataSource, statoBonificoAfter));
    }

    public Uni<ListaAutorizzazioniBonifico> get(UUID id,
            SecurityIdentity identity) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        final var bankConfig = banksConfig.bank().get(credential.abi());
        final PgPool dataSource = dataSources.dataSource(identity);
        return getBonificoIfAllowed(id, identity, dataSource, bankConfig)
                .flatMap(statoBonifico -> listAuths(dataSource, statoBonifico));
    }

    /**
     * Recupera l'effettivo DTO di risposta, elencante le autorizzazioni già date
     */
    private Uni<ListaAutorizzazioniBonifico> listAuths(PgPool dataSource, BonificoExtraSepa statoBonifico) {
        // Get all auths
        final var repository = new Autorizzazione.Entity().repository(dataSource);
        final var repositoryKind = new SottoTipologiaBonifico.Entity().repository(dataSource);

        return Uni.combine().all().unis(repository.getAllByBonifico(statoBonifico.id())
                .collect().asList(), repositoryKind.getByID(statoBonifico.idSottoTipologiaBonifico()))
                .with((autorizzazioni, sottoTipologia) -> listaAutorizzazioniBonificoMapper.map(
                        autorizzazioni,
                        sottoTipologia.conNotifica()));
    }

    /**
     * Ottiene il bonifico se l'identity corrente è autorizzata a modificarlo e se
     * il bonifico è nello stato corretto
     */
    private Uni<BonificoExtraSepa> getBonificoIfAllowed(UUID id, SecurityIdentity identity, SqlClient transaction,
            BanksConfig.BankConfig config) {
        return creditTransferService.getByIDAndCheckVisibility(id, transaction, identity)
                .map(bonificoExtraSepa -> {
                    if (bonificoExtraSepa.stato() != CreditTransferStatus.DA_AUTORIZZARE) {
                        throw new BadRequestException("bonifico con id %s non è in stato di autorizzazione"
                                .formatted(bonificoExtraSepa.id().toString()));
                    }
                    if (Boolean.TRUE.equals(bonificoExtraSepa.inGestione())) {
                        throw new BadRequestException("bonifico con id %s non è in stato di autorizzazione"
                                .formatted(bonificoExtraSepa.id().toString()));
                    }
                    return bonificoExtraSepa;
                });
    }

    /**
     * Controlla se la data può essere cambiata, ovvero se la data di regolamento è nel passato, oppure è oggi ma siamo
     * già oltre il cut off del canale di regolamento
     */
    private Boolean isNecessaryToChangeSettlementDate(BonificoExtraSepa bonifico,
                                                      BanksConfig.BankConfig bankConfig) {
        final var today = LocalDate.now();
        var currentSettlementDate = bonifico.dataRegolamentoBancaBeneficiario();
        if (today.isBefore(currentSettlementDate)) {
            return false;
        }
        if (today.isEqual(currentSettlementDate)) {
            final var currentTime = ZonedDateTime.now();

            final var channelInfo = switch (bonifico.sistemaDiRegolamento()) {
                case TARGET -> bankConfig.channel().t2();
                case NO_TARGET -> bankConfig.channel().cbpr();
            };

            final var cutOffTime = ZonedDateTime.of(
                    today.getYear(),
                    today.getMonthValue(),
                    today.getDayOfMonth(),
                    channelInfo.oraCutOff().orElse(17),
                    channelInfo.minutoCutOff().orElse(0),
                    0,
                    0,
                    ZoneId.of("Europe/Rome"));
            return cutOffTime.isBefore(currentTime);
        }

        return true;
    }

    public Uni<ListaAutorizzazioniBonifico> cambiaData(UUID id, SecurityIdentity identity) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        final var bankConfig = banksConfig.bank().get(credential.abi());
        final PgPool dataSource = dataSources.dataSource(identity);
        return dataSource.<BonificoExtraSepa>withTransaction(
                transaction -> getBonificoIfAllowed(id, identity, transaction, bankConfig)
                        .flatMap(bonificoExtraSepa -> checkIfCanAuthorize(bonificoExtraSepa, transaction, identity,
                                bankConfig)
                                .map(check -> {
                                    if (check.isEmpty()) {
                                        throw new UnauthorizedException(
                                                "utente non autorizzato a cambiare la data di regolamento del bonifico");
                                    }
                                    return Pair.with(bonificoExtraSepa, check.get());
                                }))
                        .map(pair -> {
                            var bonificoExtraSepa = pair.getValue0();
                            if (Boolean.FALSE.equals(isNecessaryToChangeSettlementDate(bonificoExtraSepa,
                                    bankConfig))) {
                                throw new BadRequestException("non è possibile modificare la data di regolamento");
                            }
                            return pair;
                        })
                        .flatMap(pair -> {
                            var bonifico = pair.getValue0();
                            final var authEntity = new Autorizzazione.Entity();
                            final var auth = Autorizzazione.buildModificaData()
                                    .bonificoExtraSepa(bonifico)
                                    .credentials(credential)
                                    .livelloAutorizzazione(pair.getValue1().livello())
                                    .build();
                            final var insertAuth = authEntity.repository(transaction).run(authEntity.insert(auth));

                            final LocalDate proposedDate = Utils
                                    .nextPossibleBusinessDay(LocalDate.now(), bankConfig, bonifico.sistemaDiRegolamento());

                            return insertAuth
                                    .call(() -> {
                                        var repository = new BonificoExtraSepa.Entity().repository(transaction);
                                        return repository.getByIdAndLock(bonifico.id());
                                    })
                                    // As soon as the authorization is inside, schedule the job
                                    .call(() -> this.jobPublisher
                                            .scheduleJob(new JobData(UUID.randomUUID(),
                                                    Constants.JOB_HANDLE_DATE_CHANGE, new HandleDateChangePayload(
                                                            bonifico.id(), auth.id(), proposedDate))))
                                    .replaceWith(bonifico);
                        }))
                .flatMap(statoBonifico -> listAuths(dataSource, statoBonifico));
    }
}