package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.ImportiCausale;
import com.prowidesoftware.swift.model.mx.dic.RemittanceAmount3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
        ImportoMapper.class,
        IdentificativoEImportoMapper.class,
        RettificaMapper.class
})
public interface ImportiCausaleMapper {

    @Mapping(target = "dovuto", source = "duePyblAmt")
    @Mapping(target = "sconti", source = "dscntApldAmt")
    @Mapping(target = "notaDiCredito", source = "cdtNoteAmt")
    @Mapping(target = "imposte", source = "taxAmt")
    @Mapping(target = "rettifiche", source = "adjstmntAmtAndRsn")
    ImportiCausale map(RemittanceAmount3 amt);
}
