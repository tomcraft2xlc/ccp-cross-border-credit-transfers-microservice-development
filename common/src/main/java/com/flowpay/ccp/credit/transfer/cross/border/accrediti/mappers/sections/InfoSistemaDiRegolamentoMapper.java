package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AppHdr;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetTime;

@Mapper(config = MxMappingConfig.class)
public interface InfoSistemaDiRegolamentoMapper {

    @Named("create")
    default InfoSistemaDiRegolamento map(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        var info = this.create(pacs, context);
        var crd = pacs.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0);
        info = this.update(info, crd);
        info = this.update(info, pacs.getAppHdr());
        return info;
    }

    @Mapping(target = "sistemaDiRegolamento", source = ".")
    InfoSistemaDiRegolamento create(MxPacs00800108 pacs, @Context AccreditoMappingContext context);

    default SistemaDiRegolamento sistemaDiRegolamento(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return context.sistemaDiRegolamento();
    }

    @Mapping(target = "priorita", expression = "java(Priorita.fromPriority3Code(creditTransferTransaction39.getSttlmPrty()))")
    @Mapping(target = "da", source = "creditTransferTransaction39.sttlmTmReq.tillTm")
    @Mapping(target = "a", source = "creditTransferTransaction39.sttlmTmReq.frTm")
    @Mapping(target = "orarioAccredito", source = "creditTransferTransaction39.sttlmTmReq.CLSTm")
    @Mapping(target = "scadenzaUltima", source = "creditTransferTransaction39.sttlmTmReq.rjctTm")
    InfoSistemaDiRegolamento update(InfoSistemaDiRegolamento infoSistemaDiRegolamento, CreditTransferTransaction39 creditTransferTransaction39);

    @Mapping(target = "stp", source = "header")
    InfoSistemaDiRegolamento update(InfoSistemaDiRegolamento infoSistemaDiRegolamento, AppHdr header);

    default Boolean stp(AppHdr header) {
        if (header.serviceName() == null) {
            return false;
        }
        return header.serviceName().contains("stp");
    }

    default Instant from(OffsetTime time) {
        return Instant.from(time);
    }
}
