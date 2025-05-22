package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleStrutturata;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.prowidesoftware.swift.model.mx.dic.StructuredRemittanceInformation16;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
                DocumentoDiRiferimentoMapper.class,
                InformazioniCreditoreMapper.class
        }
)
public interface CausaleStrutturataMapper {


    @Mapping(target = "documentoDiRiferimento", source = "rfrdDocInf")
    @Mapping(target = "informazioniCreditore", source = "cdtrRefInf")
    CausaleStrutturata map(StructuredRemittanceInformation16 causale);
}
