package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliRecordDettagliFiscali;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.TaxRecordDetails2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        config = MxMappingConfig.class,
        uses = TaxPeriod2Mapper.class)
@DecoratedWith(TaxRecordDetails2Mapper.Decorator.class)
public interface TaxRecordDetails2Mapper {

    @Mapping(target = "prd", source = ".")
    @Mapping(target = "amt", expression = "java(amt(dettagli.getEntity().importo(), dettagli.getEntity().divisa()))")
    TaxRecordDetails2 map(DettagliRecordDettagliFiscali.WithLinkedEntities dettagli);

    default ActiveOrHistoricCurrencyAndAmount amt(BigDecimal amt, String ccy) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(amt, ccy);
    }

    abstract class Decorator implements TaxRecordDetails2Mapper {

        private final TaxRecordDetails2Mapper delegate;

        Decorator(TaxRecordDetails2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxRecordDetails2 map(DettagliRecordDettagliFiscali.WithLinkedEntities dettagli) {
            return Utils.allFieldsEmpty(delegate.map(dettagli));
        }
    }
}
