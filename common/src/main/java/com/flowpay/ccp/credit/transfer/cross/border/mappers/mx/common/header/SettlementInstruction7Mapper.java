package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.SettlementInstruction7;
import com.prowidesoftware.swift.model.mx.dic.SettlementMethod1Code;
import org.mapstruct.*;

@Mapper(
        config = MxMappingConfig.class,
        uses = ClearingSystemIdentification3ChoiceMapper.class,
        imports = {
                TipoIntermediario.class,
                BranchAndFinancialInstitutionIdentification6Mapper.class,
                CashAccount38Mapper.class,
                TipoInformazioniRapporto.class
        }
)
@DecoratedWith(SettlementInstruction7Mapper.Decorator.class)
public interface SettlementInstruction7Mapper {

    @Mapping(target = "sttlmMtd", source = ".")
    @Mapping(target = "sttlmAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniRapportiBonificoExtraSepa, TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE))")
    @Mapping(target = "clrSys", source = ".")
    @Mapping(target = "instgRmbrsmntAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE, config))")
    @Mapping(target = "instgRmbrsmntAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE))")
    @Mapping(target = "instdRmbrsmntAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE, config))")
    @Mapping(target = "instdRmbrsmntAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE))")
    @Mapping(target = "thrdRmbrsmntAgt", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, TipoIntermediario.ISTITUTO_TERZO_DI_RIMBORSO, config))")
    @Mapping(target = "thrdRmbrsmntAgtAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniIntermediari, TipoIntermediario.ISTITUTO_TERZO_DI_RIMBORSO))")
    SettlementInstruction7 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext config);

    default SettlementMethod1Code sttlmMtd(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext config) {
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.NO_TARGET) {
            if (Boolean.TRUE.equals(config.isCove())) {
                return SettlementMethod1Code.COVE;
            } else {
                return SettlementMethod1Code.INDA;
            }
        } else {
            return SettlementMethod1Code.CLRG;
        }
    }

    abstract class Decorator implements SettlementInstruction7Mapper {

        private final SettlementInstruction7Mapper delegate;

        Decorator(SettlementInstruction7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public SettlementInstruction7 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext config) {
            return Utils.allFieldsEmpty(delegate.map(bonifico,config));
        }
    }
}
