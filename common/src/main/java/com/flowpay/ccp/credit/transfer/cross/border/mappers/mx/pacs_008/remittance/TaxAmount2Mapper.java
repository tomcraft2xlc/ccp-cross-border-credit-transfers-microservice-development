package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.RecordDettagliFiscali;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.TaxAmount2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        config = MxMappingConfig.class,
        uses = TaxRecordDetails2Mapper.class)
@DecoratedWith(TaxAmount2Mapper.Decorator.class)
public interface TaxAmount2Mapper {

    @Mapping(target = "rate", source = "entity.percentualeImposta")
    @Mapping(target = "taxblBaseAmt", expression = "java(amt(dettagli.getEntity().importoImponibile(), dettagli.getEntity().divisaImportoImponibile()))")
    @Mapping(target = "ttlAmt", expression = "java(amt(dettagli.getEntity().importoImposta(), dettagli.getEntity().divisaImportoImposta()))")
    @Mapping(target = "dtls", source = "dettagliRecordDettagliFiscali")
    TaxAmount2 map(RecordDettagliFiscali.WithLinkedEntities dettagli);

    default ActiveOrHistoricCurrencyAndAmount amt(BigDecimal amt, String ccy) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(amt, ccy);
    }

    abstract class Decorator implements TaxAmount2Mapper {

        private final TaxAmount2Mapper delegate;

        Decorator(TaxAmount2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxAmount2 map(RecordDettagliFiscali.WithLinkedEntities dettagli) {
            return Utils.allFieldsEmpty(delegate.map(dettagli));
        }
    }
}
