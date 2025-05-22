package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.charges.Charges7Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForCreditorAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForNextAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.Purpose2ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification.PartyIdentification135Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.payment_identification.PaymentIdentification7Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information.PaymentTypeInformation28Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.regulatory_reportings.RegulatoryReporting3Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance.RemittanceInformation16Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance.RemittanceLocation7Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.settlement_date_time_indication.SettlementTimeRequest2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.prowidesoftware.swift.model.mx.dic.*;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        PaymentIdentification7Mapper.class,
        PaymentTypeInformation28Mapper.class,
        SettlementTimeRequest2Mapper.class,
        Charges7Mapper.class,
        InstructionForCreditorAgent1Mapper.class,
        InstructionForNextAgent1Mapper.class,
        Purpose2ChoiceMapper.class,
        RegulatoryReporting3Mapper.class,
        RemittanceLocation7Mapper.class,
        RemittanceInformation16Mapper.class
},
imports = {
        TipoIntermediario.class,
        TipoAttore.class,
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        CashAccount38Mapper.class,
        PartyIdentification135Mapper.class
})
@DecoratedWith(CreditTransferTransaction39Mapper.Decorator.class)
public interface CreditTransferTransaction39Mapper {

    default List<CreditTransferTransaction39> mapList(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        return List.of(map(bonifico, context));
    }

    @Mapping(target = "pmtId", source = "entity")
    @Mapping(target = "pmtTpInf", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "intrBkSttlmAmt", source = ".", qualifiedBy = InterBankSettlementAmount.class)
    @Mapping(target = "intrBkSttlmDt", source = "entity.dataRegolamentoBancaBeneficiario")
    @Mapping(target = "sttlmPrty", expression = "java(mapPriority3Code(bonifico))")
    @Mapping(target = "sttlmTmIndctn", ignore = true)
    @Mapping(target = "sttlmTmReq", source = "informazioniSistemaDiRegolamento.entity")
    @Mapping(target = "accptncDtTm", ignore = true)
    @Mapping(target = "poolgAdjstmntDt", ignore = true)
    @Mapping(target = "instdAmt", source = ".", qualifiedBy = InstructedAmount.class)
    @Mapping(target = "xchgRate", source = "informazioniAggiuntivePagamento.entity.valoreCambioIstruito")
    @Mapping(target = "chrgBr", expression = "java(mapChargeBearerType1Code(bonifico))")
    @Mapping(target = "chrgsInf", source = ".")
    @Mapping(target = "prvsInstgAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_1, context))")
    @Mapping(target = "prvsInstgAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1))")
    @Mapping(target = "prvsInstgAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_2, context))")
    @Mapping(target = "prvsInstgAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2))")
    @Mapping(target = "prvsInstgAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_3, context))")
    @Mapping(target = "prvsInstgAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3))")
    @Mapping(target = "intrmyAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_1, context))")
    @Mapping(target = "intrmyAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1))")
    @Mapping(target = "intrmyAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_2, context))")
    @Mapping(target = "intrmyAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2))")
    @Mapping(target = "intrmyAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_3, context))")
    @Mapping(target = "intrmyAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3))")
    @Mapping(target = "instgAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(context.bankConfig(), context, false))")
    @Mapping(target = "instdAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DESTINATARIA, context, false))")
    @Mapping(target = "ultmtDbtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.ORDINANTE_EFFETTIVO))")
    @Mapping(target = "initgPty", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.SOGGETTO_ISTRUTTORE))")
    @Mapping(target = "dbtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.ORDINANTE))")
    @Mapping(target = "dbtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniAttori, TipoAttore.ORDINANTE))")
    @Mapping(target = "dbtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DELL_ORDINANTE, context))")
    @Mapping(target = "dbtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DELL_ORDINANTE))")
    @Mapping(target = "cdtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DEL_BENEFICIARIO, context))")
    @Mapping(target = "cdtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO))")
    @Mapping(target = "cdtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.BENEFICIARIO))")
    @Mapping(target = "cdtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniAttori, TipoAttore.BENEFICIARIO))")
    @Mapping(target = "ultmtCdtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.BENEFICIARIO_EFFETTIVO))")
    @Mapping(target = "instrForCdtrAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "instrForNxtAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "purp", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "rgltryRptg", source = "regulatoryReportings")
    @Mapping(target = "rltdRmtInf", source = ".")
    @Mapping(target = "tax", ignore = true)
    @Mapping(target = "rmtInf", source = "informazioniCausale")
    @Mapping(target = "splmtryData", ignore = true)
    CreditTransferTransaction39 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @InterBankSettlementAmount
    default ActiveCurrencyAndAmount intrBkSttlmAmt(
            BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (bonifico.commissioneBanca == null) {
            return ActiveCurrencyAndAmountMapper.INSTANCE.map(
                    bonifico.dettaglioBonificoAccountToAccount.getEntity().importo(),
                    bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa()
            );
        }
        var commissioni = bonifico.commissioneBanca.getEntity();
        if (Boolean.TRUE.equals(bonifico.sottoTipologiaBonifico.getEntity().bancaABanca())) {
            return null;
        }

        BigDecimal amount = switch (bonifico.dettaglioBonificoAccountToAccount.getEntity().tipologiaCommissioni()) {
            case DEBTOR -> bonifico.dettaglioBonificoAccountToAccount.getEntity().importo().add(commissioni.importo());
            case CREDITOR -> bonifico.dettaglioBonificoAccountToAccount.getEntity().importo().subtract(commissioni.importo());
            case SHARED -> bonifico.dettaglioBonificoAccountToAccount.getEntity().importo();
        };
        return ActiveCurrencyAndAmountMapper.INSTANCE.map(amount, bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa());
    }

    @InstructedAmount
    default ActiveOrHistoricCurrencyAndAmount instdAmt(BonificoExtraSepa.WithLinkedEntities bonifico) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(
                bonifico.dettaglioBonificoAccountToAccount.getEntity().importo(),
                bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa()
        );
    }

    default Priority3Code mapPriority3Code(BonificoExtraSepa.WithLinkedEntities bonifico) {
        var result = bonifico.informazioniSistemaDiRegolamento.getEntity().priorita();
        if (result != null) {
            return result.asPriority3Code();
        }
        return null;
    }

    default ChargeBearerType1Code mapChargeBearerType1Code(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (Boolean.TRUE.equals(bonifico.sottoTipologiaBonifico.getEntity().bancaABanca())) {
            return null;
        }
        var result = bonifico.dettaglioBonificoAccountToAccount.getEntity().tipologiaCommissioni();
        if (result != null) {
            return result.asChargeBearerType1Code();
        }
        return null;
    }

    abstract class Decorator implements CreditTransferTransaction39Mapper {

        private final CreditTransferTransaction39Mapper delegate;

        Decorator(CreditTransferTransaction39Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CreditTransferTransaction39 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
