package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;


import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.DettagliBonifico;
import com.prowidesoftware.swift.model.mx.dic.Charges7;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
        ImportoMapper.class,
        IntermediarioMapper.class
})
public interface InformazioneAggiuntivaCommissioneMapper {

    @Mapping(target = "importo", source = "amt")
    @Mapping(target = "intermediario", source = "agt")
    DettagliBonifico.InformazioneAggiuntivaCommissione map(Charges7 charges);
}
