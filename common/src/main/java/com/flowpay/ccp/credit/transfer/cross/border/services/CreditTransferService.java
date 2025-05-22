package com.flowpay.ccp.credit.transfer.cross.border.services;

import com.flowpay.ccp.auth.client.CabelCredentialForwardFilter;
import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.clients.RegistryAnagraficheClient;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.BonificoExtraSepaRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.*;
import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.BonificoExtraSepaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.Autorizzazione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.configuration.ConfigurazioniAutorizzative;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.mappatura_livelli.MappaturaLivelliAutorizzativi;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.BonificoExtraSepaListaCanaliAbilitati;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.pagination.dto.PaginationRequest;
import com.flowpay.ccp.pagination.persistence.Page;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.registry.dto.responses.RicercaBicResponse;
import com.flowpay.ccp.resources.poll.client.MultiplePollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.PollableResourceAccepted;
import com.flowpay.ccp.resources.poll.client.PollableResourceAcceptedHandler;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.javatuples.Pair;
import org.jboss.logging.Logger;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@ApplicationScoped
public class CreditTransferService {

    private static final Logger LOG = Logger.getLogger(CreditTransferService.class);

    DataSources dataSource;
    URL registryEndpointUrl;
    String bffBic;
    String selfUrl;
    BanksConfig banksConfig;
    RedisService redisClient;

    CreditTransferKindService creditTransferKindService;

    BonificoExtraSepaMapper bonificoExtraSepaMapper;

    JobPublisher jobPublisher;

    @Inject
    public CreditTransferService(DataSources dataSource, BanksConfig banksConfig,
                                 RedisService redisClient, @ConfigProperty(name = "ccp.bff.bic") String bffBic,
                                 @ConfigProperty(name = "ccp.self.url") String selfUrl,
                                 @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") URL registryEndpointUrl,
                                 BonificoExtraSepaMapper bonificoExtraSepaMapper,
                                 CreditTransferKindService creditTransferKindService,
                                 @Named(Constants.BEAN_JOB_PUBLISHER_INTERNAL) JobPublisher jobPublisher) {
        this.dataSource = dataSource;
        this.banksConfig = banksConfig;
        this.redisClient = redisClient;
        this.bffBic = bffBic;
        this.selfUrl = selfUrl;
        this.registryEndpointUrl = registryEndpointUrl;
        this.bonificoExtraSepaMapper = bonificoExtraSepaMapper;
        this.creditTransferKindService = creditTransferKindService;
        this.jobPublisher = jobPublisher;
    }

    private Set<SistemaDiRegolamento> validWithOnlyDestinationBank(RicercaBicResponse destinataria) {
        var result = new HashSet<SistemaDiRegolamento>();
        if (destinataria.flagTgt()) {
            result.add(SistemaDiRegolamento.TARGET);
        }
        if (destinataria.flagScambioChiavi()) {
            result.add(SistemaDiRegolamento.NO_TARGET);
        }
        return result;
    }

    private Set<SistemaDiRegolamento> validWithBothBanks(RicercaBicResponse destinataria, RicercaBicResponse beneficiaria) {
        if (beneficiaria == null) {
            return validWithOnlyDestinationBank(destinataria);
        }

        boolean bancaDestinatariaSuTarget = destinataria.flagTgt();
        boolean bancaDestinatariaSuCBPR = destinataria.flagScambioChiavi();
        Boolean bancaDelBeneficiarioSuTarget = beneficiaria.flagTgt();

        Set<SistemaDiRegolamento> valid;
        if (bancaDestinatariaSuTarget) {
            if (bancaDestinatariaSuCBPR) {
                if (Boolean.TRUE.equals(bancaDelBeneficiarioSuTarget)) {
                    // Mi vanno bene entrambi
                    valid = Set.of(SistemaDiRegolamento.TARGET, SistemaDiRegolamento.NO_TARGET);
                } else {
                    // Solo CBPR valido
                    valid = Set.of(SistemaDiRegolamento.NO_TARGET);
                }
            } else {
                if (Boolean.TRUE.equals(bancaDelBeneficiarioSuTarget)) {
                    // Solo Target valido
                    valid = Set.of(SistemaDiRegolamento.TARGET);
                } else {
                    throw new ErrorCodes(ErrorCodes.Codes.CONFIGURAZIONE_BANCHE_INCOMPATIBILE,
                            "Banca destinataria %s supporta solo TARGET, ma la banca del beneficiario %s non lo supporta"
                                    .formatted(destinataria.codiceBIC(),
                                            beneficiaria.codiceBIC()));
                }
            }
        } else {
            if (bancaDestinatariaSuCBPR) {
                // Solo CBPR valido
                valid = Set.of(SistemaDiRegolamento.NO_TARGET);
            } else {
                throw new ErrorCodes(ErrorCodes.Codes.CONFIGURAZIONE_BANCHE_INCOMPATIBILE,
                        "Banca destinataria %s non supporta ne TARGET ne CBPR"
                                .formatted(destinataria.codiceBIC()));
            }
        }
        return valid;
    }
    // Get the bank information about the destination bank and beneficiary bank
    // then check if they are compatible
    public Uni<Void> checkSettlementCompatibility(BonificoExtraSepa bonifico, SecurityIdentity authenticatedUser) {
        // Create the client to the registry
        RegistryAnagraficheClient registryAnagraficheClient = QuarkusRestClientBuilder.newBuilder()
                // Add the authentication headers
                .register(new CabelCredentialForwardFilter(authenticatedUser))
                // Add the handler to map failure responses to appropriate errors
                .register(new PollableResourceAcceptedHandler())
                // Connect to the registry
                .baseUrl(registryEndpointUrl)
                .build(RegistryAnagraficheClient.class);

        var connection = dataSource.dataSource(authenticatedUser);

        // Ottengo le informazioni delle due banche da controllare

        var bancaDelBeneficiario = informazioniIntermediario(bonifico, connection,
                TipoIntermediario.BANCA_DEL_BENEFICIARIO);
        var bancaDestinataria = informazioniIntermediario(bonifico, connection, TipoIntermediario.BANCA_DESTINATARIA);

        // Per ogni banca, chiama il registry e ottieni i dettagli

        var dettaglioBancaDelBeneficiario = bancaDelBeneficiario
                .map(value -> value.orElse(null))
                .flatMap(banca -> {
                    if (banca == null) {
                        return Uni.createFrom().nullItem();
                    }
                    return registryAnagraficheClient.dettaglioBanca(
                            banca.bic(),
                            false);
                });
        var dettaglioBancaDestinataria = bancaDestinataria
                .map(Optional::orElseThrow)
                .flatMap(banca -> registryAnagraficheClient.dettaglioBanca(banca.bic(), false));

        // Attendi la fine di entrambe le pipeline

        return Uni.combine().all().unis(dettaglioBancaDelBeneficiario, dettaglioBancaDestinataria)
                .collectFailures().withUni((ricercaBancaDelBeneficiario, ricercaBancaDestinataria) -> {
                    // Abbiamo tutti i dati, ora di prendere una decisione.



                    var valid = validWithBothBanks(ricercaBancaDestinataria, ricercaBancaDelBeneficiario);
                    if (!valid.contains(bonifico.sistemaDiRegolamento())) {
                        throw new ErrorCodes(ErrorCodes.Codes.SISTEMA_DI_REGOLAMENTO_NON_SUPPORTATO,
                                "Il sistema di regolamento %s non è supportato dalle banche indicate (destinataria %s, del beneficiario %s). Valori permessi sono %s"
                                        .formatted(bonifico.sistemaDiRegolamento(),
                                                ricercaBancaDestinataria.codiceBIC(),
                                                ricercaBancaDelBeneficiario != null ? ricercaBancaDelBeneficiario.codiceBIC() : null,
                                                valid.stream().map(SistemaDiRegolamento::toString)
                                                        .collect(Collectors.joining(", "))));
                    }

                    // check superato.
                    return Uni.createFrom().voidItem();
                }).onFailure().transform(error -> {
                    // If multiple polling failed, merge them and poll them together
                    if (error instanceof CompositeException composite &&
                            composite.getCauses().stream().allMatch(PollableResourceAccepted.class::isInstance)) {
                        return new MultiplePollableResourceAccepted(
                                composite.getCauses().stream().map(PollableResourceAccepted.class::cast).toList());
                    }
                    LOG.error("Error while getting bank data", error);
                    return error;
                });
    }

    private Uni<Optional<InformazioniIntermediario>> informazioniIntermediario(BonificoExtraSepa bonifico, PgPool connection,
                                                                               TipoIntermediario tipo) {
        return new InformazioniIntermediario.Entity().repository(connection)
                .getByBonificoExtraSepaAndKind(bonifico.id(), tipo, false);// This intermediary is present on every transfer
    }

    // private Uni<Void> checkBankWhenHasTargetButNotCBPR(BanksConfig.BankConfig
    // bankData, BankData destinationBankInfo, BonificoExtraSepa request) {
    // if (Boolean.FALSE.equals(destinationBankInfo.flagTgt())) {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.OTHER_BANK_TARGET_NOT_SUPPORTED,
    // "Destination bank does not support TARGET2"
    // );
    // }
    // if (request.settlementKind() != SistemaDiRegolamento.TARGET) {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.TARGET_REQUIRED,
    // "Bank support only TARGET2 settlement"
    // );
    // }
    // if (bankData.channel().t2().mediatedBy().isPresent() &&
    // !request.destinationBankBIC().equals(bankData.channel().t2().mediatedBy().get()))
    // {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.BFF_REQUIRED,
    // Constants.errorMessageBankRequire(bankData.channel().t2().mediatedBy().get())
    // );
    // }
    // return Uni.createFrom().voidItem();
    // }

    // private Uni<Void> checkBankWhenHasCBPRButNotTarget(BanksConfig.BankConfig
    // bankData, BankData destinationBankInfo, BonificoExtraSepa request) {
    // if (Boolean.FALSE.equals(destinationBankInfo.flagCbpr())) {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.OTHER_BANK_CBPR_NOT_SUPPORTED,
    // "Destination bank does not support CBPR"
    // );
    // }
    // if (request.settlementKind() != SistemaDiRegolamento.NO_TARGET) {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.CBPR_REQUIRED,
    // "Bank support only CBPR settlement"
    // );
    // }
    // if (bankData.channel().cbpr().mediatedBy().isPresent() &&
    // !request.destinationBankBIC().equals(bankData.channel().cbpr().mediatedBy().get()))
    // {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.BFF_REQUIRED,
    // Constants.errorMessageBankRequire(bankData.channel().cbpr().mediatedBy().get())
    // );
    // }
    // return Uni.createFrom().voidItem();
    // }

    // private Uni<Void> checkBankWhenBothChannelsAreAvaiable(BanksConfig.BankConfig
    // bankData, BankData destinationBankInfo, BankData creditorBankInfo,
    // BonificoExtraSepa request, SecurityIdentity authenticatedUser) {
    // if (request.settlementKind() == SistemaDiRegolamento.TARGET) {
    // if (bankData.channel().t2().mediatedBy().isPresent() &&
    // !request.destinationBankBIC().equals(bankData.channel().t2().mediatedBy().get()))
    // {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.BFF_REQUIRED,
    // "Bank requires BFF as destination bank"
    // );
    // }
    // } else {
    // if (bankData.channel().cbpr().mediatedBy().isPresent() &&
    // !request.destinationBankBIC().equals(bankData.channel().cbpr().mediatedBy().get()))
    // {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.BFF_REQUIRED,
    // "Bank requires BFF as destination bank"
    // );
    // }
    // }
    // return new SettlmentDecisionTree(authenticatedUser, dataSource,
    // destinationBankInfo, creditorBankInfo).decide(request);
    // }

    // private Uni<Pair<BankData, BankData>> getBankData(BonificoExtraSepa request,
    // SecurityIdentity authenticatedUser) {
    // var filter = new CabelCredentialForwardFilter(authenticatedUser);
    // var client = QuarkusRestClientBuilder.newBuilder()
    // .baseUrl(registryEndpointUrl)
    // .register(filter)
    // .register(new PollableResourceAcceptedHandler())
    // .build(RegistryClient.class);

    // Uni<BankData> creditorUni =
    // Uni.createFrom().optional(Optional.ofNullable(request.creditorBankBIC()))
    // .flatMap(bic -> {
    // if (bic != null) {
    // return client.getBankData(bic, selfUrl + "/poll");
    // } else {
    // return Uni.createFrom().nullItem();
    // }
    // });

    // var destinationBankUni = client.getBankData(request.destinationBankBIC(),
    // selfUrl + "/poll");

    // return Uni.join()
    // .all(creditorUni, destinationBankUni)
    // .andCollectFailures()
    // .onFailure().transform(error -> {
    // LOG.error("Error while getting bank data", error);
    // if (error instanceof CompositeException composite &&
    // composite.getCauses().stream().allMatch(PollableResourceAccepted.class::isInstance))
    // {
    // return new MultiplePollableResourceAccepted(
    // composite.getCauses().stream()
    // .filter(PollableResourceAccepted.class::isInstance)
    // .map(PollableResourceAccepted.class::cast).toList()
    // );
    // }
    // return error;
    // }).map(result -> new Pair<>(result.get(0), result.get(1)));
    // }

    // public Uni<Void> checkSettlementCompatibility(BonificoExtraSepa request,
    // SecurityIdentity authenticatedUser) {

    // var bankData =
    // banksConfig.bank().get(authenticatedUser.<String>getAttribute(AuthConstants.ABI_ATTRIBUTE));

    // return getBankData(request, authenticatedUser)
    // .flatMap(bankInfos -> {
    // LOG.info("Bank infos: " + bankInfos);
    // var creditorBankInfo = bankInfos.getValue0();
    // var destinationBankInfo = bankInfos.getValue1();
    // LOG.info("Destination bank info: " + destinationBankInfo + " Bank data: " +
    // bankData);
    // if (Boolean.TRUE.equals(bankData.channel().t2().attivo()) &&
    // Boolean.TRUE.equals(!bankData.channel().cbpr().attivo())) {
    // return checkBankWhenHasTargetButNotCBPR(bankData, destinationBankInfo,
    // request);
    // }
    // if (Boolean.TRUE.equals(bankData.channel().cbpr().attivo()) &&
    // Boolean.TRUE.equals(!bankData.channel().t2().attivo())) {
    // return checkBankWhenHasCBPRButNotTarget(bankData, destinationBankInfo,
    // request);
    // }
    // return checkBankWhenBothChannelsAreAvaiable(bankData, destinationBankInfo,
    // creditorBankInfo, request, authenticatedUser);
    // });
    // }

    // public Uni<BonificoExtraSepa> updateCreditTransfer(UUID idBonificoExtraSepa,
    // PatchCreditTransferRequest request, SecurityIdentity identity) {
    // var entity = new BonificoExtraSepa.Entity();
    // var repository = entity.repository(dataSource.dataSource(identity));
    // var mainBranch =
    // identity.<String>getAttribute(AuthConstants.MAIN_BRANCH_ATTRIBUTE);
    // var branches =
    // identity.<Set<String>>getAttribute(AuthConstants.BRANCH_ATTRIBUTE);
    // var list = new LinkedList<String>();
    // list.add(mainBranch);
    // list.addAll(branches);
    // return repository.get(idBonificoExtraSepa, list)
    // .map(result -> result.orElseThrow(() -> new NotFoundException("Credit
    // transfer not found")))
    // .map(creditTransfer -> {
    // if
    // (Boolean.FALSE.equals(creditTransfer.status().isTransitionAllowed(request.newStatus())))
    // {
    // throw new ErrorCodes(
    // ErrorCodes.Codes.INVALID_STATUS_TRANSITION,
    // "Invalid status transition"
    // );
    // }
    // return creditTransfer;
    // })
    // .flatMap(creditTransfer -> this.updateCreditTransfer(creditTransfer, request,
    // repository, identity));
    // }

    // private Uni<BonificoExtraSepa> updateCreditTransfer(BonificoExtraSepa
    // creditTransfer, PatchCreditTransferRequest request,
    // BonificoExtraSepa.Repository repository, SecurityIdentity identity) {

    // LocalDate settlementDate;
    // LocalDate executionDate;
    // LocalDate valueDate;

    // String destinationBankBIC;
    // String creditorBankBIC;

    // String issuingPartyCorrespondentBIC;
    // String receivingPartyCorrespondentBIC;

    // String reimbursementBankBIC;

    // String intermediaryBankBIC1;
    // String intermediaryBankBIC2;
    // String intermediaryBankBIC3;
    // String previousIntermediaryBankBIC1;
    // String previousIntermediaryBankBIC2;
    // String previousIntermediaryBankBIC3;

    // String coverageBankBIC;
    // String coverageBankAccount;
    // BigDecimal conversionRate;

    // Uni<Void> updates = Uni.createFrom().voidItem();

    // if (creditTransfer.status() == CreditTransferStatus.TO_BE_MANAGED) {
    // settlementDate =
    // Objects.requireNonNullElse(request.creditorBankRegulationDate(),
    // creditTransfer.creditorBankRegulationDate());
    // executionDate = Objects.requireNonNullElse(request.executionDate(),
    // LocalDate.now());
    // valueDate = Objects.requireNonNullElse(request.valueDate(),
    // creditTransfer.valueDate());
    // if (request.feeInfo() != null) {
    // updates = updates.flatMap(ignored -> this.updateFee(creditTransfer,
    // request.feeInfo(), identity));
    // }
    // if (request.intermediaryInfo() != null) {
    // var intermediaryInfo = request.intermediaryInfo();
    // destinationBankBIC =
    // Objects.requireNonNullElse(intermediaryInfo.destinationBank(), new
    // BankInfo(creditTransfer.destinationBankBIC())).bic();
    // creditorBankBIC = Objects.requireNonNullElse(intermediaryInfo.creditorBank(),
    // new BankInfo(creditTransfer.creditorBankBIC())).bic();
    // issuingPartyCorrespondentBIC =
    // Objects.requireNonNullElse(intermediaryInfo.issuingPartyCorrespondentBank(),
    // new BankInfo(creditTransfer.issuingPartyCorrespondentBankBIC())).bic();
    // receivingPartyCorrespondentBIC =
    // Objects.requireNonNullElse(intermediaryInfo.receivingPartyCorrespondentBank(),
    // new BankInfo(creditTransfer.receivingPartyCorrespondentBankBIC())).bic();
    // reimbursementBankBIC =
    // Objects.requireNonNullElse(intermediaryInfo.reimbursementBank(), new
    // BankInfo(creditTransfer.reimbursementBankBIC())).bic();
    // intermediaryBankBIC1 =
    // Objects.requireNonNullElse(intermediaryInfo.intermediaryBank1(), new
    // BankInfo(creditTransfer.intermediaryBankBIC1())).bic();
    // intermediaryBankBIC2 =
    // Objects.requireNonNullElse(intermediaryInfo.intermediaryBank2(), new
    // BankInfo(creditTransfer.intermediaryBankBIC2())).bic();
    // intermediaryBankBIC3 =
    // Objects.requireNonNullElse(intermediaryInfo.intermediaryBank3(), new
    // BankInfo(creditTransfer.intermediaryBankBIC3())).bic();
    // previousIntermediaryBankBIC1 =
    // Objects.requireNonNullElse(intermediaryInfo.previousIntermediaryBank1(), new
    // BankInfo(creditTransfer.previousIntermediaryBankBIC1())).bic();
    // previousIntermediaryBankBIC2 =
    // Objects.requireNonNullElse(intermediaryInfo.previousIntermediaryBank2(), new
    // BankInfo(creditTransfer.previousIntermediaryBankBIC2())).bic();
    // previousIntermediaryBankBIC3 =
    // Objects.requireNonNullElse(intermediaryInfo.previousIntermediaryBank3(), new
    // BankInfo(creditTransfer.previousIntermediaryBankBIC3())).bic();
    // } else {
    // destinationBankBIC = creditTransfer.destinationBankBIC();
    // creditorBankBIC = creditTransfer.creditorBankBIC();
    // issuingPartyCorrespondentBIC =
    // creditTransfer.issuingPartyCorrespondentBankBIC();
    // receivingPartyCorrespondentBIC =
    // creditTransfer.receivingPartyCorrespondentBankBIC();
    // reimbursementBankBIC = creditTransfer.reimbursementBankBIC();
    // intermediaryBankBIC1 = creditTransfer.intermediaryBankBIC1();
    // intermediaryBankBIC2 = creditTransfer.intermediaryBankBIC2();
    // intermediaryBankBIC3 = creditTransfer.intermediaryBankBIC3();
    // previousIntermediaryBankBIC1 = creditTransfer.previousIntermediaryBankBIC1();
    // previousIntermediaryBankBIC2 = creditTransfer.previousIntermediaryBankBIC2();
    // previousIntermediaryBankBIC3 = creditTransfer.previousIntermediaryBankBIC3();
    // }

    // if (request.creditTransferInfo() != null) {
    // var creditTransferInfo = request.creditTransferInfo();
    // if (creditTransferInfo.coverageBankAccount() != null) {
    // coverageBankBIC =
    // Objects.requireNonNullElse(creditTransferInfo.coverageBankAccount().bankInfo(),
    // new BankInfo(creditTransfer.coverageBankBIC())).bic();
    // coverageBankAccount =
    // Objects.requireNonNullElse(creditTransferInfo.coverageBankAccount().accountID(),
    // creditTransfer.coverageBankAccountID());
    // } else {
    // coverageBankBIC = creditTransfer.coverageBankBIC();
    // coverageBankAccount = creditTransfer.coverageBankAccountID();
    // }
    // conversionRate =
    // Objects.requireNonNullElse(creditTransferInfo.conversionRate(),
    // creditTransfer.conversionRate());
    // } else {
    // coverageBankBIC = creditTransfer.coverageBankBIC();
    // coverageBankAccount = creditTransfer.coverageBankAccountID();
    // conversionRate = creditTransfer.conversionRate();
    // }
    // } else {
    // settlementDate = creditTransfer.creditorBankRegulationDate();
    // executionDate = creditTransfer.executionDate();
    // valueDate = creditTransfer.valueDate();
    // destinationBankBIC = creditTransfer.destinationBankBIC();
    // creditorBankBIC = creditTransfer.creditorBankBIC();
    // issuingPartyCorrespondentBIC =
    // creditTransfer.issuingPartyCorrespondentBankBIC();
    // receivingPartyCorrespondentBIC =
    // creditTransfer.receivingPartyCorrespondentBankBIC();
    // reimbursementBankBIC = creditTransfer.reimbursementBankBIC();
    // intermediaryBankBIC1 = creditTransfer.intermediaryBankBIC1();
    // intermediaryBankBIC2 = creditTransfer.intermediaryBankBIC2();
    // intermediaryBankBIC3 = creditTransfer.intermediaryBankBIC3();
    // previousIntermediaryBankBIC1 = creditTransfer.previousIntermediaryBankBIC1();
    // previousIntermediaryBankBIC2 = creditTransfer.previousIntermediaryBankBIC2();
    // previousIntermediaryBankBIC3 = creditTransfer.previousIntermediaryBankBIC3();
    // coverageBankBIC = creditTransfer.coverageBankBIC();
    // coverageBankAccount = creditTransfer.coverageBankAccountID();
    // conversionRate = creditTransfer.conversionRate();
    // }

    // var updatedCreditTransfer = new BonificoExtraSepa(
    // creditTransfer.id(),
    // creditTransfer.tid(),
    // creditTransfer.channelID(),
    // creditTransfer.creditTransferKindID(),
    // creditTransfer.accountType(),
    // creditTransfer.accountID(),
    // creditTransfer.debtorID(),
    // creditTransfer.ownerID(),
    // creditTransfer.creditorIBAN(),
    // creditTransfer.creditorOtherID(),
    // creditTransfer.creditorName(),
    // creditTransfer.creditorAddressLine(),
    // creditTransfer.creditorLocation(),
    // creditTransfer.creditorCountry(),
    // creditTransfer.creditorPostalCode(),
    // destinationBankBIC,
    // creditorBankBIC,
    // creditTransfer.creditorBankIBAN(),
    // creditTransfer.creditorBankOtherID(),
    // issuingPartyCorrespondentBIC,
    // receivingPartyCorrespondentBIC,
    // reimbursementBankBIC,
    // intermediaryBankBIC1,
    // intermediaryBankBIC2,
    // intermediaryBankBIC3,
    // previousIntermediaryBankBIC1,
    // previousIntermediaryBankBIC2,
    // previousIntermediaryBankBIC3,
    // creditTransfer.settlementKind(),
    // creditTransfer.stp(),
    // creditTransfer.amount(),
    // creditTransfer.currency(),
    // conversionRate,
    // creditTransfer.creationDate(),
    // executionDate,
    // valueDate,
    // settlementDate,
    // coverageBankBIC,
    // coverageBankAccount,
    // creditTransfer.unstructuredRemittanceInformation(),
    // creditTransfer.creditorBankInstruction(),
    // creditTransfer.transactionCode(),
    // creditTransfer.settlementMethod(),
    // creditTransfer.serviceLevelCode(),
    // creditTransfer.purposeCodeISO(),
    // creditTransfer.purposeCodePrivate(),
    // creditTransfer.regulatoryReportingInfo(),
    // request.newStatus(),
    // creditTransfer.createdAt(),
    // false,
    // creditTransfer.branch(),
    // creditTransfer.userProfile()
    // );

    // return updates
    // .call(() -> repository.addNewStatusHistory(creditTransfer,
    // request.newStatus().toString(), null))
    // .call(() -> repository.update(updatedCreditTransfer))
    // .replaceWith(updatedCreditTransfer);
    // }

    // private Uni<Void> updateClientFee(Fee fee,
    // List<com.flowpay.ccp.credit.transfer.cross.border.dto.fee.FeeDetail>
    // clientFees, SecurityIdentity identity) {
    // var entity = new FeeDetail.Entity();
    // var repository = entity.repository(dataSource.dataSource(identity));
    // return repository.deleteByFeeID(fee.id())
    // .call(() -> {
    // Uni<Void> result = Uni.createFrom().voidItem();
    // for (var clientFee : clientFees) {
    // var feeDetail = new FeeDetail(clientFee, fee.id());
    // result = result.call(() -> repository.run(entity.insert(feeDetail)));
    // }
    // return result;
    // });
    // }

    // private Uni<Void> updateFee(BonificoExtraSepa creditTransfer, PatchFeeInfo
    // feeInfo, SecurityIdentity identity) {
    // return creditTransfer.fee(dataSource.dataSource(identity))
    // .flatMap(fee -> {
    // var entity = new Fee.Entity();
    // var repository = entity.repository(dataSource.dataSource(identity));
    // var kind = Objects.requireNonNullElse(feeInfo.feeKind(), fee.bankFeeKind());

    // String bankFeeDescription;
    // if (feeInfo.bankFee() != null) {
    // var bankFee = feeInfo.bankFee();
    // bankFeeDescription = Objects.requireNonNullElse(bankFee.description(),
    // fee.bankFeeDescription());
    // } else {
    // bankFeeDescription = fee.bankFeeDescription();
    // }

    // BigDecimal bankFeeAmount;
    // if (feeInfo.bankFee() != null) {
    // var bankFee = feeInfo.bankFee();
    // bankFeeAmount = Objects.requireNonNullElse(bankFee.fee() instanceof FlatFee
    // amount ? amount.amount() : null, fee.bankFeeAmount());
    // } else {
    // bankFeeAmount = fee.bankFeeAmount();
    // }

    // FeeSettlementKind bankSettlementKind;
    // if (feeInfo.bankFee() != null) {
    // var bankFee = feeInfo.bankFee();
    // bankSettlementKind = Objects.requireNonNullElse(bankFee.settlement(),
    // fee.bankSettlementKind());
    // } else {
    // bankSettlementKind = fee.bankSettlementKind();
    // }
    // var updatedFee = new Fee(
    // fee.id(),
    // fee.creditTransferId(),
    // kind,
    // bankFeeAmount,
    // bankFeeDescription,
    // bankSettlementKind
    // );
    // return repository.update(updatedFee)
    // .replaceWith(updatedFee);
    // })
    // .flatMap(fee -> {
    // if (feeInfo.clientFee() != null) {
    // return this.updateClientFee(fee, feeInfo.clientFee(), identity);
    // } else {
    // return Uni.createFrom().voidItem();
    // }
    // });
    // }

    /**
     * Validates and inserts a new credit transfer.
     * <p>
     * It first checks if the channel is allowed to perform the given credit
     * transfer kind. If not,
     * <p>
     * It first checks if the channel is allowed to perform the given credit
     * transfer kind. If not,
     * it throws a NotAuthorizedException.
     * <p>
     * After that, it creates a new BonificoExtraSepa entity and inserts it with its
     * linked
     * <p>
     * After that, it creates a new BonificoExtraSepa entity and inserts it with its
     * linked
     * entities. It also schedules a job to handle the credit transfer.
     *
     * @param request  the request containing the credit transfer data
     * @param request  the request containing the credit transfer data
     * @param identity the identity containing the channel to use
     * @return a Uni containing the inserted credit transfer
     */
    public Uni<BonificoExtraSepaRisposta> insertCreditTransfer(
            InserisciBonificoExtraSepaRichiesta request, SecurityIdentity identity) {
        var credential = identity.getCredential(CabelForwardedCredential.class);
        var connection = dataSource.dataSource(identity);
        return creditTransferKindService.validate(request, credential.channel(), identity)
                .map(isAllowed -> {
                    switch (isAllowed.getValue0()) {
                        case ALLOWED:
                            break;
                        case NOT_ALLOWED:
                            throw new ErrorCodes(ErrorCodes.Codes.TRANSFER_KIND_NOT_ALLOWED,
                                    "Channel " + credential.channel()
                                    + " is not allowed to perform "
                                    + request.sottoTipologiaBonifico().tipo()
                                    + " credit transfers");
                        case MISSING_FIELDS:
                            throw new ErrorCodes(ErrorCodes.Codes.MISSING_REQUIRED_FIELD,
                                    "Missing required fields for credit transfer kind "
                                    + request.sottoTipologiaBonifico().tipo());
                        case WRONG_TARGETS:
                            throw new ErrorCodes(ErrorCodes.Codes.WRONG_TRANSFER_TARGETS,
                                    "A " + (request.isBancaABanca() ? "bank to bank" : "account to account")
                                    + " tranfer was sent, but "
                                    + request.sottoTipologiaBonifico().tipo() + " has different targets");
                    }
                    return isAllowed.getValue1();
                }).flatMap(sottoTipologiaBonifico -> {
                    // Recover connected entities and setup
                    var canaleUni = new Canale.Entity().repository(connection)
                            .getByChannelID(credential.channel());

                    // TODO: additional, slower checks now that we refused trivially not allowed
                    // additions

                    return canaleUni.flatMap(canale -> new BonificoExtraSepaListaCanaliAbilitati.Entity().repository(connection)
                                    .getBySottoTipologiaBonificoAndCanale(sottoTipologiaBonifico.id(), canale.id())
                                    .map(stato -> Pair.with(stato, canale)))
                            .flatMap(pair -> {
                                var statoIniziale = pair.getValue0();
                                var canale = pair.getValue1();
                                // Create the entity
                                BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa = bonificoExtraSepaMapper
                                        .fromDto(
                                                request,
                                                canale,
                                                sottoTipologiaBonifico,
                                                statoIniziale.statoDefault());

                                LOG.debug(bonificoExtraSepa.informazioniAggiuntivePagamentoDocumentoCollegato);
                                // Insert the entity and return
                                return bonificoExtraSepa.insertAll(connection)
                                        .replaceWith(bonificoExtraSepa);
                            })
                            .flatMap(
                                    creditTransfer -> jobPublisher
                                            .scheduleJob(new JobData(UUID.randomUUID(),
                                                    Constants.JOB_HANDLE_CREDIT_TRANSFER,
                                                    new HandleCreditTransferPayload(
                                                            creditTransfer.id())))
                                            .replaceWith(creditTransfer))
                            .map(creditTransfer -> bonificoExtraSepaMapper
                                    .toInserisciDto(creditTransfer, sottoTipologiaBonifico));
                })

                ;

    }

    /**
     * Updates the status of a given BonificoExtraSepa and schedules a job to handle
     * the credit transfer.
     *
     * @param bonifico  The BonificoExtraSepa object to update.
     * @param newStatus The new status to set for the credit transfer.
     * @param identity  The security identity used to establish a database
     *                  connection.
     * @return A Uni containing the updated BonificoExtraSepa object.
     */

    Uni<BonificoExtraSepa> doUpdate(BonificoExtraSepa bonifico, CreditTransferStatus newStatus,
                                    SecurityIdentity identity) {
        var entity = new BonificoExtraSepa.Entity();
        var connection = dataSource.dataSource(identity);
        return entity.repository(connection)
                .updateStatus(bonifico.id(), newStatus)
                .replaceWith(bonificoExtraSepaMapper.changeStatus(bonifico, newStatus))
                .flatMap(bonificoDopoUpdate -> this.jobPublisher
                        .scheduleJob(
                                new JobData(UUID.randomUUID(),
                                        Constants.JOB_HANDLE_CREDIT_TRANSFER,
                                        new HandleCreditTransferPayload(
                                                bonificoDopoUpdate.id())))
                        .replaceWith(bonificoDopoUpdate));
    }

    public Uni<BonificoExtraSepaRisposta> updateBonifico(UUID id, SecurityIdentity identity,
                                                         CreditTransferStatus newStatus) {
        var connection = this.dataSource.dataSource(identity);
        return connection.withTransaction(transaction -> this.getByIDAndCheckVisibility(id, transaction, identity)
                .flatMap(bonifico -> {
                    if (Boolean.TRUE.equals(bonifico.inGestione())) {
                        return Uni.createFrom().failure(new RuntimeException());
                    }
                    if (Boolean.FALSE.equals(bonifico.stato().isTransitionAllowed(newStatus))) {
                        return Uni.createFrom().failure(new RuntimeException());
                    }
                    return this.doUpdate(bonifico, newStatus, identity);
                })
                .onFailure().transform(a -> new NotFoundException("bonifico non trovato"))
                .flatMap(bonifico -> this.toDTO(bonifico, transaction, identity)));
    }

    /**
     * Ottieni il bonifico se visibile dalla data identity
     */
    Uni<BonificoExtraSepa> getByIDAndCheckVisibility(UUID id, SqlClient connection, SecurityIdentity identity) {
        var entity = new BonificoExtraSepa.Entity();
        var credential = identity.getCredential(CabelForwardedCredential.class);
        return entity.repository(connection).getById(id)
                // Check if the branch is visible
                .onItem().transformToUni(bonificoExtraSepa -> {
                    if (Boolean.TRUE.equals(credential.isBranchVisible(bonificoExtraSepa.codiceFiliale().toString()))) {
                        return Uni.createFrom().item(bonificoExtraSepa);
                    } else {
                        return Uni.createFrom().failure(new Exception());
                    }
                })
                // Replace all errors with an opaque one
                .onFailure().transform(e -> {
                    LOG.error("error checking visibility", e);
                    return new NotFoundException("Il bonifico non esiste");
                });
    }

    private Uni<BonificoExtraSepaRisposta> toDTO(BonificoExtraSepa bonificoExtraSepa, SqlClient connection,
                                                 SecurityIdentity identity) {
        var withLinked = bonificoExtraSepa.withLinkedEntities();
        var load = withLinked.loadAll(connection).onItem().ignoreAsUni().replaceWith(withLinked);
        // Other related but not dependant entities
        var sottoTipologiaBonificoUni = new SottoTipologiaBonifico.Entity().repository(connection)
                .getByID(bonificoExtraSepa.idSottoTipologiaBonifico());
        // When everything came up from the DB, combine them into a DTO
        return Uni.combine().all().unis(load, sottoTipologiaBonificoUni).with(bonificoExtraSepaMapper::toInserisciDto);
    }

    public Uni<BonificoExtraSepaRisposta> getCreditTransfer(UUID id,
                                                            SecurityIdentity identity) {
        var connection = this.dataSource.dataSource(identity);
        return this.getByIDAndCheckVisibility(id, connection, identity)
                // Load all linked entities
                .flatMap(bonificoExtraSepa -> this.toDTO(bonificoExtraSepa, connection, identity));
    }

    public Uni<Page<BonificoDaAutorizzare>> ricercaBonificiDaAutorizzare(
            ParametriRicercaAutorizzazioneBonifico parametriRicercaAutorizzazioneBonifico,
            PaginationRequest paginationRequest,
            SecurityIdentity identity) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        var connection = this.dataSource.dataSource(identity);
        var mappaturaLivelli = new MappaturaLivelliAutorizzativi.Entity().repository(connection);
        var repository = new BonificoExtraSepa.Entity().repository(connection);
        String role = identity.getCredential(CabelForwardedCredential.class).role();
        var bonifici = repository.searchBonificiDaAutorizzare(
                parametriRicercaAutorizzazioneBonifico,
                paginationRequest.getPage(),
                paginationRequest.getPageSize()
        );
        // 1 Dato il ruolo dell'utente viene restiuita la lista di livello_autorizzazione
        var livelliAutorizzabili = mappaturaLivelli.getLevelByRuolo(role)
                .map(livelli -> livelli.stream().map(MappaturaLivelliAutorizzativi::livello)
                        .map(i -> i - 1).toList()
                );

        return Uni.combine().all().unis(bonifici, livelliAutorizzabili).collectFailures()
                .with((listaBonifici, listaLivelliAutorizzabili) -> {
                    ArrayList<BonificoDaAutorizzare> filteredList = new ArrayList<>();
                    listaBonifici.data().forEach(dto -> {
                        if (listaLivelliAutorizzabili.contains(dto.maxLivelloAutorizzazione()) && credential.isBranchVisible(dto.filiale().toString())) {
                            filteredList.add(dto);
                        }
                    });
                    return new Page<>(listaBonifici.totalElements() - (listaBonifici.totalElements() - filteredList.size()), filteredList);
                });
    }


    public Uni<Page<BonificoInUscita>> ricercaBonificiInUscita(
            ParametriRicercaBonificiInUscita parametri,
            PaginationRequest paginationRequest,
            SecurityIdentity identity
    ) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        var connection = this.dataSource.dataSource(identity);
        var repository = new BonificoExtraSepa.Entity().repository(connection);
        return repository.searchBonificiInUscita(
                parametri,
                paginationRequest.getPage(),
                paginationRequest.getPageSize()
        ).map(pageOfBonifici -> {
            ArrayList<BonificoInUscita> filteredList = new ArrayList<>();
            pageOfBonifici.data().forEach(dto -> {
                if (credential.isBranchVisible(dto.filiale().toString())) {
                    filteredList.add(dto);
                }
            });
            return new Page<>(pageOfBonifici.totalElements() - (pageOfBonifici.totalElements() - filteredList.size()), filteredList);
        });
    }

    public Uni<Page<BonificoInIngresso>> ricercaBonificiInIngresso(
            ParametriRicercaBonificiInIngressoBanca parametri,
            PaginationRequest paginationRequest,
            SecurityIdentity identity
    ) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        var connection = this.dataSource.dataSource(identity);
        var repository = new BonificoInIngresso.Entity().repository(connection);
        return repository.searchBonificiInIngresso(
                parametri,
                paginationRequest.getPage(),
                paginationRequest.getPageSize()
        ).map(pageOfBonifici -> filter(pageOfBonifici, credential));
    }

    public Uni<Page<BonificoInIngresso>> ricercaBonificiInIngresso(
            ParametriRicercaBonificiInIngressoClientela parametri,
            PaginationRequest paginationRequest,
            SecurityIdentity identity
    ) {
        final CabelForwardedCredential credential = identity.getCredential(CabelForwardedCredential.class);
        var connection = this.dataSource.dataSource(identity);
        var repository = new BonificoInIngresso.Entity().repository(connection);
        return repository.searchBonificiInIngresso(
                parametri,
                paginationRequest.getPage(),
                paginationRequest.getPageSize()
        ).map(pageOfBonifici -> filter(pageOfBonifici, credential));
    }

    private Page<BonificoInIngresso> filter(Page<BonificoInIngresso> bonifici, CabelForwardedCredential credential) {
        var result = bonifici.data().stream().filter(bonifico -> {
            if (bonifico.codiceFiliale() == null) {
                return true;
            }
            return credential.isBranchVisible(bonifico.codiceFiliale().toString());
        }).toList();
        return new Page<>(bonifici.totalElements() - (bonifici.totalElements() - result.size()), result);
    }

    public Uni<Void> authorizedStateTransition(BonificoExtraSepa bonifico, SqlClient connection, SecurityIdentity identity) {
        var entity = new ConfigurazioniAutorizzative.Entity();
        var repository = entity.repository(connection);
        return repository.search(bonifico.idSottoTipologiaBonifico(), bonifico.idCanale())
                .flatMap(configurazione -> {
                    var livello = configurazione.map(ConfigurazioniAutorizzative::livelliDiAutorizzazione).orElse(banksConfig.bank().get(identity.getCredential(CabelForwardedCredential.class).abi()).defaultAuthorizationLevels());
                    var livelloAutorizzativoDiDefault = configurazione.map(ConfigurazioniAutorizzative::livelloDiAutorizzazioneDiDefault).orElse(0L);
                    var kindEntity = new SottoTipologiaBonifico.Entity();
                    var kindRepository = kindEntity.repository(connection);

                    return kindRepository.getByID(bonifico.idSottoTipologiaBonifico()).map(SottoTipologiaBonifico::conNotifica)
                            .flatMap(notifica -> {
                                if (Objects.equals(livello, livelloAutorizzativoDiDefault)) {

                                    return this.autoAuthorization(bonifico, connection, identity, livelloAutorizzativoDiDefault, notifica);
                                } else {
                                    return this.authorizedStateTransition(bonifico, connection, identity, livelloAutorizzativoDiDefault, notifica);
                                }

                            });
                });
    }

    public Uni<Void> authorizedStateTransition(BonificoExtraSepa bonifico, SqlClient connection, SecurityIdentity identity, Long livelli, Boolean conNotifica) {
        var entity = new Autorizzazione.Entity();
        var repository = entity.repository(connection);


        var unis = LongStream.range(0, livelli).mapToObj(livello -> {
            var builder = Autorizzazione.buildAutorizzazione()
                    .bonificoExtraSepa(bonifico)
                    .credentials(identity.getCredential(CabelForwardedCredential.class))
                    .autorizzazioneMessaggio(true)
                    .livelloAutorizzazione(livello + 1)
                    .note("Autorizzazione automatica per regole canale");
            if (Boolean.TRUE.equals(conNotifica)) {
                builder.autorizzazioneNotifica(true);
            }
            return repository.run(entity.insert(builder.build()));
        }).toList();
        if (unis.isEmpty()) {
            return Uni.createFrom().voidItem();
        }
        return Uni.join().all(unis).andCollectFailures()
                .replaceWithVoid();
    }

    private Uni<Void> autoAuthorization(BonificoExtraSepa bonifico, SqlClient connection, SecurityIdentity identity, Long livelli, Boolean conNotifica) {
        return this.authorizedStateTransition(bonifico, connection, identity, livelli, conNotifica)
                .call(() -> this.doUpdate(bonifico, CreditTransferStatus.AUTORIZZATO, identity));
    }

    public Uni<DatiVerificaBonifico.WithLinkedEntities> getRisultatiVerifica(UUID id, SecurityIdentity identity) {
        var connection = dataSource.dataSource(identity);
        var isVisible = this.getByIDAndCheckVisibility(id, connection, identity);
        var entity = new DatiVerificaBonifico.Entity();
        var repository = entity.repository(connection);
        var result = repository.getByBonificoExtraSepa(id);
        return Uni.combine().all().unis(isVisible, result)
        .with((bonifico, risultati) -> {
              if (risultati.allCallsEnded()) {
                  return risultati;
              }
              throw new NotFoundException("risultati verifica per bonifico %s non ancora pronti".formatted(id));
        })
        .flatMap(risultati -> {
            var withLinked = risultati.withLinkedEntities();
            return withLinked.loadAll(connection).onItem().ignoreAsUni().replaceWith(withLinked);
        });
    }

    public Uni<DatiConfermaBonifico.WithLinkedEntities> getRisultatiConferma(UUID id, SecurityIdentity identity) {
        var connection = dataSource.dataSource(identity);
        var isVisible = this.getByIDAndCheckVisibility(id, connection, identity);
        var entity = new DatiConfermaBonifico.Entity();
        var repository = entity.repository(connection);
        var result = repository.getByBonificoExtraSepa(id);
        return Uni.combine().all().unis(isVisible, result)
        .with((bonifico, risultati) -> {
            if (risultati.allStep1CallsEnded()) {
                return risultati;
            }
            throw new NotFoundException("risultati conferma per bonifico %s non ancora pronti".formatted(id));
        })
        .flatMap(datiConfermaBonifico -> {
            var withLinked = datiConfermaBonifico.withLinkedEntities();
            return withLinked.loadAll(connection).onItem().ignoreAsUni().replaceWith(withLinked);
        });
    }
}
