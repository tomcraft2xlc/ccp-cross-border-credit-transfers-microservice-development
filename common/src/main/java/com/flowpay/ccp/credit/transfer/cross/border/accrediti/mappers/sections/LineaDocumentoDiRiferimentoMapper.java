package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.DocumentLineInformation1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
        IdLineaDocumentoMapper.class,

})
public interface LineaDocumentoDiRiferimentoMapper {

    @Mapping(target = "identificativi", source = "id")
    @Mapping(target = "descrizione", source = "desc")
    @Mapping(target = "importi", source = "amt")
    DocumentoDiRiferimento.LineaDocumentoDiRiferimento map(DocumentLineInformation1 line);
}
