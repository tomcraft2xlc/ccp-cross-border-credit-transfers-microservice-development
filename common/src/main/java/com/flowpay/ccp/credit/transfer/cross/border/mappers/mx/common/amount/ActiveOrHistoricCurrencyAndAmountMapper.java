package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(config = MxMappingConfig.class)
public interface ActiveOrHistoricCurrencyAndAmountMapper {

    ActiveOrHistoricCurrencyAndAmountMapper INSTANCE = Mappers.getMapper(ActiveOrHistoricCurrencyAndAmountMapper.class);

    ActiveOrHistoricCurrencyAndAmount map(BigDecimal value, String ccy);
}
