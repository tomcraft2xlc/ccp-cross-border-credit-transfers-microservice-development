package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForNextAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction.InstructedAmount;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions.InstructionForCreditorAgent1Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification.PartyIdentification135Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance.RemittanceInformation16Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction37;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class,
        uses = {
        InstructionForCreditorAgent1Mapper.class,
        InstructionForNextAgent1Mapper.class,
        RemittanceInformation16Mapper.class
},
imports = {
        PartyIdentification135Mapper.class,
        CashAccount38Mapper.class,
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        TipoAttore.class,
        TipoIntermediario.class
})
@DecoratedWith(CreditTransferTransaction37Mapper.Decorator.class)
public interface CreditTransferTransaction37Mapper {

    @Mapping(target = "ultmtDbtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.ORDINANTE_EFFETTIVO))")//    @Mapping(target = "initgPty", source = "")
    @Mapping(target = "initgPty", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.SOGGETTO_ISTRUTTORE))")
    @Mapping(target = "dbtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.ORDINANTE))")
    @Mapping(target = "dbtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.ORDINANTE))")
    @Mapping(target = "dbtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DELL_ORDINANTE, context))")
    @Mapping(target = "dbtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DELL_ORDINANTE))")
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
    @Mapping(target = "cdtrAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DEL_BENEFICIARIO, context))")
    @Mapping(target = "cdtrAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO))")
    @Mapping(target = "cdtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.BENEFICIARIO))")
    @Mapping(target = "cdtrAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniAttori, TipoAttore.BENEFICIARIO))")
    @Mapping(target = "ultmtCdtr", expression = "java(PartyIdentification135Mapper.INSTANCE.map(bonifico, TipoAttore.BENEFICIARIO_EFFETTIVO))")
    @Mapping(target = "instrForCdtrAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "instrForNxtAgt", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "tax", ignore = true)
    @Mapping(target = "rmtInf", source = "informazioniCausale")
    @Mapping(target = "instdAmt", source = ".", qualifiedBy = InstructedAmount.class)
    CreditTransferTransaction37 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @InstructedAmount
    default ActiveOrHistoricCurrencyAndAmount instdAmt(BonificoExtraSepa.WithLinkedEntities bonifico) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(
                bonifico.dettaglioBonificoAccountToAccount.getEntity().importo(),
                bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa()
        );
    }

    abstract class Decorator implements CreditTransferTransaction37Mapper {

        private final CreditTransferTransaction37Mapper delegate;

        Decorator(CreditTransferTransaction37Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CreditTransferTransaction37 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
