package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleCliente;
import com.prowidesoftware.swift.model.mx.dic.RemittanceInformation16;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper()
public interface CausaleClienteMapper {

    @Mapping(target = "causaleDescrittiva", source = "ustrd")
    @Mapping(target = "causaleStrutturata", source = "strd")
    CausaleCliente map(RemittanceInformation16 remittance);

    default String map(List<String> list) {
        return String.join("", list);
    }
}
