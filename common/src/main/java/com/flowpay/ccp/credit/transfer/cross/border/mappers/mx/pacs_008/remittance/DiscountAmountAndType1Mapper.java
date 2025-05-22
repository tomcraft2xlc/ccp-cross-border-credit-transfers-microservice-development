package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioImporto;
import com.prowidesoftware.swift.model.mx.dic.DiscountAmountAndType1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        imports = ActiveOrHistoricCurrencyAndAmountMapper.class
)
@DecoratedWith(DiscountAmountAndType1Mapper.Decorator.class)
public interface DiscountAmountAndType1Mapper {

    DiscountAmountAndType1Mapper INSTANCE = Mappers.getMapper(DiscountAmountAndType1Mapper.class);

    @Mapping(target = "tp.cd", source = "tipo")
    @Mapping(target = "tp.prtry", source = "dettagli")
    @Mapping(target = "amt", expression = "java(ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(importo.importo(), importo.divisa()))")
    DiscountAmountAndType1 map(DettaglioImporto importo);

    abstract class Decorator implements DiscountAmountAndType1Mapper {

        private final DiscountAmountAndType1Mapper delegate;

        Decorator(DiscountAmountAndType1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DiscountAmountAndType1 map(DettaglioImporto importo) {
            return Utils.allFieldsEmpty(delegate.map(importo));
        }
    }
}
