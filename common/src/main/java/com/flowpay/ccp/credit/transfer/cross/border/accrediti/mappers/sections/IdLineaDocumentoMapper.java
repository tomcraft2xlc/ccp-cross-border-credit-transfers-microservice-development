package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;


import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.DocumentLineIdentification1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IdLineaDocumentoMapper {

    @Mapping(target = "codice", source = "tp.cdOrPrtry.cd")
    @Mapping(target = "codiceProprietario", source = "tp.cdOrPrtry.prtry")
    @Mapping(target = "emittente", source = "tp.issr")
    @Mapping(target = "numero", source = "nb")
    @Mapping(target = "data", source = "rltdDt")
    DocumentoDiRiferimento.IdLineaDocumento map(DocumentLineIdentification1 id);
}
