package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.kind;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;

@Mapper(config = MappingCommonConfig.class)
public interface SottoTipologiaBonificoMapper {
    @Mapping(target = "tipo", source = "nome")
    SottoTipologiaBonificoRisposta toDto(SottoTipologiaBonifico sottoTipologiaBonifico);
}
