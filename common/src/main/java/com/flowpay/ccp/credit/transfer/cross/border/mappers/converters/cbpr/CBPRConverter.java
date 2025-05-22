package com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.cbpr;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.XmlConverter;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.MxWriteParams;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface CBPRConverter extends XmlConverter {

    default String map(AbstractMX mx, @Context MappingContext context) {
        var params = new MxWriteParams();
        params.includeXMLDeclaration = false;
        var header = mx.getAppHdr().xml(params);
        var document = mx.document(params);
        return header + '\n' + document;
    }
}
