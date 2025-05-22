package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.InformazioniCreditore;
import com.prowidesoftware.swift.model.mx.dic.CreditorReferenceInformation2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InformazioniCreditoreMapper {

    @Mapping(target = "codice", source = "tp.cdOrPrtry.cd")
    @Mapping(target = "codiceProprietario", source = "tp.cdOrPrtry.prtry")
    @Mapping(target = "emittente", source = "tp.issr")
    @Mapping(target = "riferimentoUnivoco", source = "ref")
    InformazioniCreditore map(CreditorReferenceInformation2 creditorReferenceInformation2);

}
