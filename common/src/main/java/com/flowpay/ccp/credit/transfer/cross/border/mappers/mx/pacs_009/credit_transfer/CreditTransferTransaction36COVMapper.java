package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForNextAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.Purpose2ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.payment_identification.PaymentIdentification7COVMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.settlement_date_time_indication.SettlementTimeRequest2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction.InterBankSettlementAmount;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information.PaymentTypeInformation28Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.instructions.InstructionForCreditorAgent2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.remittance.RemittanceInformation2Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.ActiveCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction36;
import com.prowidesoftware.swift.model.mx.dic.Priority3Code;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class,
        uses = {
        PaymentIdentification7COVMapper.class,
        PaymentTypeInformation28Mapper.class,
        InstructionForCreditorAgent2Mapper.class,
        InstructionForNextAgent1Mapper.class,
        Purpose2ChoiceMapper.class,
        RemittanceInformation2Mapper.class,
        SettlementTimeRequest2Mapper.class,
        CreditTransferTransaction37Mapper.class

},
imports = {
        TipoIntermediario.class,
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        CashAccount38Mapper.class
})
@DecoratedWith(CreditTransferTransaction36COVMapper.Decorator.class)
public interface CreditTransferTransaction36COVMapper extends CreditTransferTransaction36Mapper {

    @Override
    @CreditTransfer36Mapping
    @Mapping(target = "pmtId", source = "entity")
    @Mapping(target = "pmtTpInf", source = "informazioniAggiuntivePagamentoDocumentoCollegato.entity")
    @Mapping(target = "sttlmPrty", expression = "java(mapPriority3Code(bonifico))")
    @Mapping(target = "sttlmTmReq", source = "informazioniSistemaDiRegolamentoDocumentoCollegato.entity")
    @Mapping(target = "prvsInstgAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_1, true, context))")
    @Mapping(target = "prvsInstgAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1, true))")
    @Mapping(target = "prvsInstgAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_2, true, context))")
    @Mapping(target = "prvsInstgAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2, true))")
    @Mapping(target = "prvsInstgAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_ISTRUTTRICE_3, true, context))")
    @Mapping(target = "prvsInstgAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3, true))")
    @Mapping(target = "instgAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(context.bankConfig(), context, false))")
    @Mapping(target = "instdAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE, context, false))")
    @Mapping(target = "intrmyAgt1", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_1, true, context))")
    @Mapping(target = "intrmyAgt1Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1, true))")
    @Mapping(target = "intrmyAgt2", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_2, true, context))")
    @Mapping(target = "intrmyAgt2Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2, true))")
    @Mapping(target = "intrmyAgt3", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_INTERMEDIARIA_3, true, context))")
    @Mapping(target = "intrmyAgt3Acct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3, true))")
    @Mapping(target = "dbtr", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DELL_ORDINANTE, context))")
    @Mapping(target = "dbtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DELL_ORDINANTE))")
    @Mapping(target = "dbtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE, context))")
    @Mapping(target = "dbtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE))")
    @Mapping(target = "cdtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE, context))")
    @Mapping(target = "cdtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE))")
    @Mapping(target = "cdtr", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DEL_BENEFICIARIO, context))")
    @Mapping(target = "cdtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO))")
    @Mapping(target = "instrForCdtrAgt", source = "informazioniAggiuntivePagamentoDocumentoCollegato.entity")
    @Mapping(target = "instrForNxtAgt", source = "informazioniAggiuntivePagamentoDocumentoCollegato.entity")
    @Mapping(target = "purp", source = "informazioniAggiuntivePagamentoDocumentoCollegato.entity")
    @Mapping(target = "rmtInf", source = "informazioniCausaleDocumentoCollegato")
    @Mapping(target = "undrlygCstmrCdtTrf", source = ".")
    @Mapping(target = "splmtryData", ignore = true)
    CreditTransferTransaction36 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @InterBankSettlementAmount
    @Override
    default ActiveCurrencyAndAmount intrBkSttlmAmt(
            BonificoExtraSepa.WithLinkedEntities bonifico) {
        return ActiveCurrencyAndAmountMapper.INSTANCE.map(
                bonifico.dettaglioBonificoAccountToAccount.getEntity().importo(),
                bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa()
        );
    }

    @Override
    default Priority3Code mapPriority3Code(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (bonifico.informazioniSistemaDiRegolamentoDocumentoCollegato == null) {
            return null;
        }
        var result = bonifico.informazioniSistemaDiRegolamentoDocumentoCollegato.getEntity().priorita();
        if (result != null) {
            return result.asPriority3Code();
        }
        return null;
    }

    abstract class Decorator implements CreditTransferTransaction36COVMapper {

        private final CreditTransferTransaction36COVMapper delegate;

        Decorator(CreditTransferTransaction36COVMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CreditTransferTransaction36 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
