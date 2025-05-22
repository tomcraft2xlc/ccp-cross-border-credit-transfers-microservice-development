package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.regulatory_reportings;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.DettagliRegulatoryReporting;
import com.google.common.base.Splitter;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.StructuredRegulatoryReporting3;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(StructuredRegulatoryReporting3Mapper.Decorator.class)
public interface StructuredRegulatoryReporting3Mapper {


    @Mapping(target = "tp", source = "entity.dettaglio")
    @Mapping(target = "dt", source = "entity.data")
    @Mapping(target = "ctry", source = "entity.paese")
    @Mapping(target = "amt", expression = "java(amt(info.getEntity().importo(), info.getEntity().divisa()))")
    @Mapping(target = "inf", expression = "java(this.formatInformazioniAggiuntive(info.getEntity().informazioniAggiuntive()))")
    @Mapping(target = "cd", ignore = true)
    StructuredRegulatoryReporting3 map(DettagliRegulatoryReporting.WithLinkedEntities info);

    default List<String> formatInformazioniAggiuntive(String informazioniAggiuntive) {
        if (informazioniAggiuntive != null && !informazioniAggiuntive.isBlank()) {
            return Splitter.fixedLength(35).splitToList(informazioniAggiuntive);
        }
        return null;
    }

    default ActiveOrHistoricCurrencyAndAmount amt(BigDecimal amt, String ccy) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(amt, ccy);
    }

    abstract class Decorator implements StructuredRegulatoryReporting3Mapper {

        private final StructuredRegulatoryReporting3Mapper delegate;

        Decorator(StructuredRegulatoryReporting3Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public StructuredRegulatoryReporting3 map(DettagliRegulatoryReporting.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }

}
