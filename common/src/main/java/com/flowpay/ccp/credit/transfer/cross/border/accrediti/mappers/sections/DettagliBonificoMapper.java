package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.DettagliBonifico;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {
        ImportoMapper.class,
        InfoDateMapper.class,
        InformazioneAggiuntivaCommissioneMapper.class
})
public interface DettagliBonificoMapper {


    DettagliBonifico map(MxPacs00800108 pacs, @Context AccreditoMappingContext context);

    @Mapping(target = "importo", source = "intrBkSttlmAmt")
    @Mapping(target = "importoIstruito", expression = "java(ImportoMapper.INSTANCE.map(creditTransfer.getInstdAmt(), creditTransfer.getXchgRate()))")
    @Mapping(target = "informazioniAggiuntiveCommissioni", source = "chrgsInf")
    @Mapping(target = "causaleCliente", source = "rmtInf")
    DettagliBonifico map(CreditTransferTransaction39 creditTransfer, @Context AccreditoMappingContext context);

    @Mapping(target = "date", source = "pacs")
    DettagliBonifico update(DettagliBonifico dettagliBonifico, MxPacs00800108 pacs);
}
