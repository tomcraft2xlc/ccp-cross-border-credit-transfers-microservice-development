package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import org.mapstruct.Context;

public interface AccreditiPersistenceMapper {
    BonificoInIngresso map(AbstractMX message, @Context AccreditoToPersistenceContext context);
}
