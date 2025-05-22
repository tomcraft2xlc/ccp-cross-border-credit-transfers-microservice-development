package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.Accredito;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import org.mapstruct.Context;


public interface AccreditoMapper {
    Accredito map(AbstractMX message, @Context AccreditoMappingContext mappingContext);
}
