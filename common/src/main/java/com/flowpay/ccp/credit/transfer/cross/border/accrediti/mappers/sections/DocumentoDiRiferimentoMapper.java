package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.ReferredDocumentInformation7;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = LineaDocumentoDiRiferimentoMapper.class)
public interface DocumentoDiRiferimentoMapper {

    @Mapping(target = "tipo", source = "tp.cdOrPrtry.cd")
    @Mapping(target = "descrizione", source = "tp.cdOrPrtry.prtry")
    @Mapping(target = "emittente", source = "tp.issr")
    @Mapping(target = "numero", source = "nb")
    @Mapping(target = "data", source = "rltdDt")
    @Mapping(target = "linee", source = "lineDtls")
    DocumentoDiRiferimento map(ReferredDocumentInformation7 rfrdDocInf);
}
