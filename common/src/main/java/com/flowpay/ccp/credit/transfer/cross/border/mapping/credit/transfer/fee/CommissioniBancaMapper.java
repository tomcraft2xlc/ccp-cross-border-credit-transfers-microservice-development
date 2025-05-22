package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniBanca;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.CommissioneBanca;
import org.mapstruct.factory.Mappers;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface CommissioniBancaMapper {

    CommissioniBancaMapper INSTANCE = Mappers.getMapper(CommissioniBancaMapper.class);

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @DtoToBareEntity
    CommissioneBanca bareFromDto(CommissioniBanca commissioniBanca,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    void fillLinked(@MappingTarget CommissioneBanca.WithLinkedEntities commissioneBanca,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            CommissioniBanca commissioniBanca);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default CommissioneBanca.WithLinkedEntities fromDto(CommissioniBanca commissioniBanca,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (commissioniBanca == null) {
            return null;
        }
        var entity = bareFromDto(commissioniBanca, bonificoExtraSepa).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, commissioniBanca);
        return entity;
    }

    @Mapping(target = ".", source = "entity")
    CommissioniBanca toDto(CommissioneBanca.WithLinkedEntities commissioneBanca);
}
