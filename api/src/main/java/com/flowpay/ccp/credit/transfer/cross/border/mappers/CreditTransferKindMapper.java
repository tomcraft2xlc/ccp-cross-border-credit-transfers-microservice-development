package com.flowpay.ccp.credit.transfer.cross.border.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottotipologieBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MappingCommonConfig.class)
public interface CreditTransferKindMapper {

    default SottotipologieBonificoRisposta mapDTO(List<SottoTipologiaBonifico> sottoTipi) {
        return new SottotipologieBonificoRisposta(mapList(sottoTipi));
    }

    default List<SottoTipologiaBonificoRisposta> mapList(List<SottoTipologiaBonifico> sottoTipi) {
        return sottoTipi.stream().map(this::map).toList();
    }

    @Mapping(target = "tipo", source = "nome")
    SottoTipologiaBonificoRisposta map(SottoTipologiaBonifico sottoTipo);
}
