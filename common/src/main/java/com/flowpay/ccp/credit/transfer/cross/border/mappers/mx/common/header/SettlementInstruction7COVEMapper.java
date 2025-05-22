package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account.CashAccount38Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.SettlementInstruction7;
import com.prowidesoftware.swift.model.mx.dic.SettlementMethod1Code;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        imports = {
                TipoInformazioniRapporto.class,
                CashAccount38Mapper.class
        }
)
@DecoratedWith(SettlementInstruction7COVEMapper.Decorator.class)
public interface SettlementInstruction7COVEMapper {

    @Mapping(target = "sttlmMtd", source = ".")
    @Mapping(target = "sttlmAcct", expression = "java(CashAccount38Mapper.INSTANCE.map(bonifico.informazioniRapportiBonificoExtraSepa, TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE_DOCUMENTO_COLLEGATO))")
    @Mapping(target = "clrSys.cd", ignore = true)
    @Mapping(target = "instgRmbrsmntAgt", ignore = true)
    @Mapping(target = "instgRmbrsmntAgtAcct", ignore = true)
    @Mapping(target = "instdRmbrsmntAgt", ignore = true)
    @Mapping(target = "instdRmbrsmntAgtAcct", ignore = true)
    @Mapping(target = "thrdRmbrsmntAgt", ignore = true)
    @Mapping(target = "thrdRmbrsmntAgtAcct", ignore = true)
    SettlementInstruction7 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext config);

    default SettlementMethod1Code sttlmMtd(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext config) {
        return SettlementMethod1Code.INDA;
    }

    abstract class Decorator implements SettlementInstruction7COVEMapper {

        private final SettlementInstruction7COVEMapper delegate;

        Decorator(SettlementInstruction7COVEMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public SettlementInstruction7 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext config) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, config));
        }
    }
}

