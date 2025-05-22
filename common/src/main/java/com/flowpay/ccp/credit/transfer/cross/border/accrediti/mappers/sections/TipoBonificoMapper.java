package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.TipoBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import com.prowidesoftware.swift.model.mx.dic.SettlementInstruction7;
import com.prowidesoftware.swift.model.mx.dic.SettlementMethod1Code;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper
public interface  TipoBonificoMapper {

    @Mapping(target = "sottoTipologiaBonifico", source = ".")
    @Mapping(target = "cov", source = ".")
    TipoBonifico map(MxPacs00800108 pacs, @Context AccreditoMappingContext context);

    default SottoTipologiaBonifico sottoTipologiaBonifico(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return context.sottoTipologiaBonifico();
    }

    default Boolean cov(MxPacs00800108 pacs) {
        return Optional.ofNullable(pacs.getFIToFICstmrCdtTrf().getGrpHdr())
        .map(GroupHeader93::getSttlmInf).map(SettlementInstruction7::getSttlmMtd).map(element -> element == SettlementMethod1Code.CLRG)
        .orElse(false);
    }
}
