package com.flowpay.ccp.credit.transfer.cross.border.mappers.converters;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.prowidesoftware.swift.model.mx.AbstractMX;

public interface XmlConverter {

    String map(AbstractMX mx, MappingContext context);
}
