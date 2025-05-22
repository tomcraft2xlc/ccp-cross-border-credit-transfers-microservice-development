package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.AbstractMX;

public interface MxMapper {

    AbstractMX map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context);
}
