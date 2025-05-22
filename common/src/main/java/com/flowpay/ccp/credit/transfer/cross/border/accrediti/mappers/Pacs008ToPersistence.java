package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Mapper(
        config = MxMappingConfig.class
)
public interface Pacs008ToPersistence extends AccreditiPersistenceMapper {

    @Override
    default BonificoInIngresso map(AbstractMX message, @Context AccreditoToPersistenceContext context) {
        var actual = (MxPacs00800108) message;
        return this.map(actual, context);
    }

    // TODO codiceFiliale Skippato.
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "idBonificoCollegato", ignore = true)
    @Mapping(target = "isBancaABanca", constant = "false")
    @Mapping(target = "tid", source = ".", qualifiedByName = "tid")
    @Mapping(target = "uetr", source = ".", qualifiedByName = "uetr")
    @Mapping(target = "bicOrdinante", source = ".", qualifiedByName = "bicOrdinante")
    @Mapping(target = "dataRicezione", expression = "java(java.time.Instant.now())")
    @Mapping(target = "sottoTipologiaBonifico", source = ".", qualifiedByName = "sottoTipologiaBonifico")
    @Mapping(target = "sistemaDiRegolamento", source = ".", qualifiedByName = "sistemaDiRegolamento")
    @Mapping(target = "divisa", source = ".", qualifiedByName = "divisa")
    @Mapping(target = "importo", source = ".", qualifiedByName = "importo")
    @Mapping(target = "intestazioneBeneficiario", source = ".", qualifiedByName = "intestazioneBeneficiario")
    @Mapping(target = "numeroRapportoBeneficiario", source = ".", qualifiedByName = "numeroRapportoBeneficiario")
    @Mapping(target = "bicBeneficiario", source = ".", qualifiedByName = "bicBeneficiario")
    @Mapping(target = "bicBancaEmittente", source = ".", qualifiedByName = "bicBancaEmittente")
    @Mapping(target = "dataRegolamentoBancaBeneficiario", source = ".", qualifiedByName = "dataRegolamentoBancaBeneficiario")
    @Mapping(target = "codiceFiliale", source = ".", qualifiedByName = "codiceFiliale")
    @Mapping(target = "intestazioneOrdinante", source = ".", qualifiedByName = "intestazioneOrdinante")
    @Mapping(target = "numeroRapportoOrdinante", source = ".", qualifiedByName = "numeroRapportoOrdinante")
    @Mapping(target = "idRapportoDiCopertura", ignore = true)
    @Mapping(target = "stato", constant = "DA_GESTIRE")
    @Mapping(target = "rawXML", source = ".", qualifiedByName = "rawXML")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "idMappaturaBonificoInIngresso", source = ".", qualifiedByName = "idMappaturaBonificoInIngresso")
    @Mapping(target = "tipologiaCommissioniBanca", ignore = true)
    @Mapping(target = "regolamentoCommissioniBanca", ignore = true)
    @Mapping(target = "regolamentoCommissioniClientela", ignore = true)
    BonificoInIngresso map(MxPacs00800108 message, @Context AccreditoToPersistenceContext context);

    @Named("rawXML")
    default String rawXML(MxPacs00800108 message) {
        return message.message();
    }

    @Named("tid")
    default String tid(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getPmtId)
                .map(PaymentIdentification7::getInstrId).orElse(null);
    }

    @Named("idMappaturaBonificoInIngresso")
    default UUID idMappaturaBonificoInIngresso(MxPacs00800108 message, @Context AccreditoToPersistenceContext context) {
        return context.idMappatura();
    }

    @Named("uetr")
    default UUID uetr(MxPacs00800108 message) {
        return UUID.fromString(this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getPmtId)
                .map(PaymentIdentification7::getUETR).orElse(""));
    }

    @Named("bicOrdinante")
    default String bicOrdinante(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getDbtrAgt)
                .map(BranchAndFinancialInstitutionIdentification6::getFinInstnId)
                .map(FinancialInstitutionIdentification18::getBICFI).orElse(null);
    }

    //TODO: fixme potrebbe essere un pacs 8 cov
    @Named("sottoTipologiaBonifico")
    default SottoTipologiaBonifico sottoTipologiaBonifico(MxPacs00800108 message) {
        return SottoTipologiaBonifico.PACS_008;
    }

    @Named("sistemaDiRegolamento")
    default SistemaDiRegolamento sistemaDiRegolamento(MxPacs00800108 message, @Context AccreditoToPersistenceContext context) {
        return context.sistemaDiRegolamento();
    }

    @Named("divisa")
    default String divisa(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getIntrBkSttlmAmt)
                .map(ActiveCurrencyAndAmount::getCcy).orElse(null);
    }

    @Named("importo")
    default BigDecimal importo(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getIntrBkSttlmAmt)
                .map(ActiveCurrencyAndAmount::getValue).orElse(null);
    }

    @Named("intestazioneBeneficiario")
    default String intestazioneBeneficiario(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getCdtr)
                .map(PartyIdentification135::getNm).orElse(null);
    }

    default String numeroRapporto(Optional<CashAccount38> account) {
        String iban = account.map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getIBAN).orElse(null);
        String otherId = account.map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getOthr)
                .map(GenericAccountIdentification1::getId)
                .orElse(null);
        return iban != null ? iban : otherId;
    }

    @Named("numeroRapportoBeneficiario")
    default String numeroRapportoBeneficiario(MxPacs00800108 message) {
        return numeroRapporto(this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getCdtrAcct));
    }

    @Named("bicBeneficiario")
    default String bicBeneficiario(MxPacs00800108 message) {
        // TODO Non sono sicuro che vada bene prendere il bic del beneficiario da qui
        return message.getAppHdr().to();
    }

    @Named("codiceFiliale")
    default Long codiceFiliale(MxPacs00800108 message, @Context AccreditoToPersistenceContext context) {
        if (context.codiceFiliale() != null) {
            return context.codiceFiliale();
        }

        return 0L;
    }

    @Named("bicBancaEmittente")
    default String bicBancaEmittente(MxPacs00800108 message) {
        return message.getAppHdr().from();
    }

    @Named("dataRegolamentoBancaBeneficiario")
    default LocalDate dataRegolamentoBancaBeneficiario(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getIntrBkSttlmDt).orElse(null);
    }

    @Named("intestazioneOrdinante")
    default String intestazioneOrdinante(MxPacs00800108 message) {
        return this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getDbtr)
                .map(PartyIdentification135::getNm).orElse(null);
    }

    @Named("numeroRapportoOrdinante")
    default String numeroRapportoOrdinante(MxPacs00800108 message) {
        return numeroRapporto(this.getFirstCreditTransferTransaction39(message)
                .map(CreditTransferTransaction39::getDbtrAcct));
    }

    default Optional<CreditTransferTransaction39> getFirstCreditTransferTransaction39(MxPacs00800108 message) {
        return Optional.of(message).map(MxPacs00800108::getFIToFICstmrCdtTrf)
                .map(FIToFICustomerCreditTransferV08::getCdtTrfTxInf)
                .map(creditTransferTransaction39s -> {
                    if (creditTransferTransaction39s.isEmpty()) {
                        return null;
                    }
                    return creditTransferTransaction39s.get(0);
                });
    }

}
