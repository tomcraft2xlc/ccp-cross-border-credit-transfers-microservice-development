package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliImporto;
import com.prowidesoftware.swift.model.mx.dic.ActiveCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface ImportoMapper {

    ImportoMapper INSTANCE = Mappers.getMapper(ImportoMapper.class);

    Importo map(BigDecimal importo, String divisa);

    @Mapping(target = "importo", source = "value")
    @Mapping(target = "divisa", source = "ccy")
    Importo map(ActiveCurrencyAndAmount amount);

    @Mapping(target = "importo", source = "value")
    @Mapping(target = "divisa", source = "ccy")
    Importo map(ActiveOrHistoricCurrencyAndAmount amount);

    DettagliImporto map(BigDecimal importo, String divisa, BigDecimal cambio);

    @Mapping(target = "importo", source = "amount.value")
    @Mapping(target = "divisa", source = "amount.ccy")
    DettagliImporto map(ActiveOrHistoricCurrencyAndAmount amount, BigDecimal cambio);
}
