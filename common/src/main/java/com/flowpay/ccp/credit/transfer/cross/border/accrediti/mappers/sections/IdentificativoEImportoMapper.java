package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;


import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.ImportiCausale;
import com.prowidesoftware.swift.model.mx.dic.DiscountAmountAndType1;
import com.prowidesoftware.swift.model.mx.dic.TaxAmountAndType1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ImportoMapper.class)
public interface IdentificativoEImportoMapper {

    @Mapping(target = "codice", source = "tp.cd")
    @Mapping(target = "codiceProprietario", source = "tp.prtry")
    @Mapping(target = "importo", source = "amt")
    ImportiCausale.IdentificativoEImporto map(DiscountAmountAndType1 dscnt);

    @Mapping(target = "codice", source = "tp.cd")
    @Mapping(target = "codiceProprietario", source = "tp.prtry")
    @Mapping(target = "importo", source = "amt")
    ImportiCausale.IdentificativoEImporto map(TaxAmountAndType1 dscnt);

}
