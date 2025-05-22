package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Indirizzo;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;

@Mapper(config = MappingCommonConfig.class)
public interface IndirizzoPostaleMapper {

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "createdAt", ignore = true)
    @DtoToBareEntity
    IndirizzoPostale bareFromDto(Indirizzo indirizzoPostale);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default IndirizzoPostale.WithLinkedEntities fromDto(Indirizzo indirizzoPostale) {
        var bare = bareFromDto(indirizzoPostale);
        if (bare == null) {
            return null;
        }
        return bare.withLinkedEntities();
    }

    @Mapping(target = ".", source = "entity")
    Indirizzo toDto(IndirizzoPostale.WithLinkedEntities indirizzoPostale);
}