package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForNextAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.Purpose2ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.payment_identification.PaymentIdentification7Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.settlement_date_time_indication.SettlementTimeRequest2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction.InterBankSettlementAmount;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information.PaymentTypeInformation28Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.instructions.InstructionForCreditorAgent2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.remittance.RemittanceInformation2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.ActiveCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction36;
import com.prowidesoftware.swift.model.mx.dic.Priority3Code;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MxMappingConfig.class,
        uses = {
        PaymentIdentification7Mapper.class,
        PaymentTypeInformation28Mapper.class,
        SettlementTimeRequest2Mapper.class,
        InstructionForCreditorAgent2Mapper.class,
        InstructionForNextAgent1Mapper.class,
        Purpose2ChoiceMapper.class,
        RemittanceInformation2Mapper.class
},
imports = {
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        TipoIntermediario.class,
        CashAccount38Mapper.class,
        TipoInformazioniRapporto.class
})
@DecoratedWith(CreditTransferTransaction36Mapper.Decorator.class)
public interface CreditTransferTransaction36Mapper {

    default List<CreditTransferTransaction36> mapList(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        return List.of(map(bonifico, context));
    }

    @CreditTransfer36Mapping
    @Mapping(target = "pmtId", source = "entity")
    @Mapping(target = "pmtTpInf", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "sttlmPrty", expression = "java(mapPriority3Code(bonifico))")
    @Mapping(target = "sttlmTmReq", source = "informazioniSistemaDiRegolamento.entity")
    @Mapping(target = "prvsInstgAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_1, context))")
    @Mapping(target = "prvsInstgAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1))")
    @Mapping(target = "prvsInstgAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_2, context))")
    @Mapping(target = "prvsInstgAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2))")
    @Mapping(target = "prvsInstgAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_3, context))")
    @Mapping(target = "prvsInstgAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3))")
    @Mapping(target = "instgAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(context.bankConfig(), context, false))")
    @Mapping(target = "instdAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DESTINATARIA, context, false))")
    @Mapping(target = "intrmyAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_1, context))")
    @Mapping(target = "intrmyAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1))")
    @Mapping(target = "intrmyAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_2, context))")
    @Mapping(target = "intrmyAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2))")
    @Mapping(target = "intrmyAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_3, context))")
    @Mapping(target = "intrmyAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3))")
    @Mapping(target = "dbtr", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.ORDINANTE, context))")
    @Mapping(target = "dbtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.ORDINANTE))")
    @Mapping(target = "dbtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DELL_ORDINANTE, context))")
    @Mapping(target = "dbtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DELL_ORDINANTE))")
    @Mapping(target = "cdtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DEL_BENEFICIARIO, context))")
    @Mapping(target = "cdtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO))")
    @Mapping(target = "cdtr", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_BENEFICIARIA, context))")
    @Mapping(target = "cdtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_BENEFICIARIA))")
    @Mapping(target = "instrForCdtrAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "instrForNxtAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "purp", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "rmtInf", source = "informazioniCausale")
//    @Mapping(target = "undrlygCstmrCdtTrf", source = "dettaglioBonificoAccountToAccount")
    @Mapping(target = "undrlygCstmrCdtTrf", ignore = true)
    @Mapping(target = "splmtryData", ignore = true)
    CreditTransferTransaction36 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @InterBankSettlementAmount
    default ActiveCurrencyAndAmount intrBkSttlmAmt(
            BonificoExtraSepa.WithLinkedEntities bonifico) {
        return ActiveCurrencyAndAmountMapper.INSTANCE.map(bonifico.dettaglioBonificoBancaABanca.getEntity().importo(), bonifico.dettaglioBonificoBancaABanca.getEntity().divisa());
    }

    default Priority3Code mapPriority3Code(BonificoExtraSepa.WithLinkedEntities bonifico) {
        var result = bonifico.informazioniSistemaDiRegolamento.getEntity().priorita();
        if (result != null) {
            return result.asPriority3Code();
        }
        return null;
    }

    abstract class Decorator implements CreditTransferTransaction36Mapper {

        private final CreditTransferTransaction36Mapper delegate;

        Decorator(CreditTransferTransaction36Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CreditTransferTransaction36 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
