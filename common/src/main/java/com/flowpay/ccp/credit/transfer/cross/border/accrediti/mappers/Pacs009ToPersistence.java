package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.MxPacs00900108;
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
public abstract class Pacs009ToPersistence implements AccreditiPersistenceMapper {
    @Override
    public BonificoInIngresso map(AbstractMX message, @Context AccreditoToPersistenceContext context) {
        var actual = (MxPacs00900108) message;
        return this.map(actual);
    }

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "rawXML", source = ".", qualifiedByName = "rawXML")
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
    @Mapping(target = "intestazioneOrdinante", source = ".", qualifiedByName = "intestazioneOrdinante")
    @Mapping(target = "numeroRapportoOrdinante", source = ".", qualifiedByName = "numeroRapportoOrdinante")
    @Mapping(target = "createdAt", ignore = true)
    public abstract BonificoInIngresso map(MxPacs00900108 message);

    @Named("rawXML")
    String rawXML(MxPacs00900108 message) {
        return message.document();
    }

    @Named("tid")
    String tid(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getPmtId)
                .map(PaymentIdentification7::getInstrId).orElse(null);
    }


    @Named("uetr")
    UUID uetr(MxPacs00900108 message) {
        return UUID.fromString(this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getPmtId)
                .map(PaymentIdentification7::getUETR).orElse(""));
    }

    @Named("bicOrdinante")
    String bicOrdinante(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getDbtrAgt)
                .map(BranchAndFinancialInstitutionIdentification6::getFinInstnId)
                .map(FinancialInstitutionIdentification18::getBICFI).orElse(null);
    }

    @Named("sottoTipologiaBonifico")
    SottoTipologiaBonifico sottoTipologiaBonifico(MxPacs00900108 message) {
        return SottoTipologiaBonifico.PACS_009;
    }

    @Named("sistemaDiRegolamento")
    SistemaDiRegolamento sistemaDiRegolamento(MxPacs00900108 message) {
        boolean isTarget = Optional.of(message)
                .map(MxPacs00900108::getFICdtTrf)
                .map(FinancialInstitutionCreditTransferV08::getGrpHdr)
                .map(GroupHeader93::getSttlmInf)
                .map(SettlementInstruction7::getClrSys)
                .isPresent();
        if (isTarget) {
            return SistemaDiRegolamento.TARGET;
        }
        return SistemaDiRegolamento.NO_TARGET;
    }

    @Named("divisa")
    String divisa(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getIntrBkSttlmAmt)
                .map(ActiveCurrencyAndAmount::getCcy).orElse(null);
    }

    @Named("importo")
    BigDecimal importo(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getIntrBkSttlmAmt)
                .map(ActiveCurrencyAndAmount::getValue).orElse(null);
    }

    @Named("intestazioneBeneficiario")
    String intestazioneBeneficiario(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getCdtr)
                .map(BranchAndFinancialInstitutionIdentification6::getFinInstnId)
                .map(FinancialInstitutionIdentification18::getNm)
                .orElse(null);
    }

    @Named("numeroRapportoBeneficiario")
    String numeroRapportoBeneficiario(MxPacs00900108 message) {
        String iban = this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getCdtrAcct)
                .map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getIBAN)
                .orElse(null);
        String otherId = this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getCdtrAcct)
                .map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getOthr)
                .map(GenericAccountIdentification1::getId)
                .orElse(null);
        return iban != null ? iban : otherId;
    }

    @Named("bicBeneficiario")
    String bicBeneficiario(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getCdtr)
                .map(BranchAndFinancialInstitutionIdentification6::getFinInstnId)
                .map(FinancialInstitutionIdentification18::getBICFI)
                .orElse(null);
    }

    @Named("bicBancaEmittente")
    String bicBancaEmittente(MxPacs00900108 message) {
        return message.getAppHdr().from();
    }

    @Named("dataRegolamentoBancaBeneficiario")
    LocalDate dataRegolamentoBancaBeneficiario(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getIntrBkSttlmDt).orElse(null);
    }

    @Named("intestazioneOrdinante")
    String intestazioneOrdinante(MxPacs00900108 message) {
        return this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getDbtrAgt)
                .map(BranchAndFinancialInstitutionIdentification6::getFinInstnId)
                .map(FinancialInstitutionIdentification18::getNm).orElse(null);
    }

    @Named("numeroRapportoOrdinante")
    String numeroRapportoOrdinante(MxPacs00900108 message) {
        String iban = this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getDbtrAcct)
                .map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getIBAN)
                .orElse(null);
        String otherId = this.getFirstCreditTransferTransaction36(message)
                .map(CreditTransferTransaction36::getDbtrAcct)
                .map(CashAccount38::getId)
                .map(AccountIdentification4Choice::getOthr)
                .map(GenericAccountIdentification1::getId)
                .orElse(null);
        return iban != null ? iban : otherId;
    }

    Optional<CreditTransferTransaction36> getFirstCreditTransferTransaction36(MxPacs00900108 message) {
        return Optional.of(message).map(MxPacs00900108::getFICdtTrf)
                .map(FinancialInstitutionCreditTransferV08::getCdtTrfTxInf)
                .map(creditTransferTransaction36s -> {
                    if (creditTransferTransaction36s.isEmpty()) {
                        return null;
                    }
                    return creditTransferTransaction36s.get(0);
                });
    }

}
