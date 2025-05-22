package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioImporto;
import com.prowidesoftware.swift.model.mx.dic.TaxAmountAndType1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        imports = ActiveOrHistoricCurrencyAndAmountMapper.class
)
@DecoratedWith(TaxAmountAndType1Mapper.Decorator.class)
public interface TaxAmountAndType1Mapper {

    TaxAmountAndType1Mapper INSTANCE = Mappers.getMapper(TaxAmountAndType1Mapper.class);

    @Mapping(target = "tp.cd", source = "tipo")
    @Mapping(target = "tp.prtry", source = "dettagli")
    @Mapping(target = "amt", expression = "java(ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(importo.importo(), importo.divisa()))")
    TaxAmountAndType1 map(DettaglioImporto importo);

    abstract class Decorator implements TaxAmountAndType1Mapper {

        private final TaxAmountAndType1Mapper delegate;

        Decorator(TaxAmountAndType1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxAmountAndType1 map(DettaglioImporto importo) {
            return Utils.allFieldsEmpty(delegate.map(importo));
        }
    }
}
