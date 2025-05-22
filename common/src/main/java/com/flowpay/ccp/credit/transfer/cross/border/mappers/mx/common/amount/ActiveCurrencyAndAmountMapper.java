package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.prowidesoftware.swift.model.mx.dic.ActiveCurrencyAndAmount;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(ActiveCurrencyAndAmountMapper.Decorator.class)
public interface ActiveCurrencyAndAmountMapper {

    ActiveCurrencyAndAmountMapper INSTANCE = Mappers.getMapper(ActiveCurrencyAndAmountMapper.class);

    ActiveCurrencyAndAmount map(BigDecimal value, String ccy);

    abstract class Decorator implements ActiveCurrencyAndAmountMapper {
        private final ActiveCurrencyAndAmountMapper delegate;

        Decorator(ActiveCurrencyAndAmountMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ActiveCurrencyAndAmount map(BigDecimal value, String ccy) {
            return Utils.allFieldsEmpty(delegate.map(value, ccy));
        }
    }
}
