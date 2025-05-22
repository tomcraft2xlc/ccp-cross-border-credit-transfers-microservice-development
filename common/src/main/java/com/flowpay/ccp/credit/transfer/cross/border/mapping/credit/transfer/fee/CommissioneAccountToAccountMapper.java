package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniCliente;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.CommissioneAccountToAccount;
import org.mapstruct.factory.Mappers;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface CommissioneAccountToAccountMapper {

    CommissioneAccountToAccountMapper INSTANCE = Mappers.getMapper(CommissioneAccountToAccountMapper.class);

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @DtoToBareEntity
    CommissioneAccountToAccount bareFromDto(CommissioniCliente commissioniCliente,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    void fillLinked(@MappingTarget CommissioneAccountToAccount.WithLinkedEntities commissioneAccountToAccount,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            CommissioniCliente commissioniCliente);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default CommissioneAccountToAccount.WithLinkedEntities fromDto(CommissioniCliente commissioniBanca,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(commissioniBanca, bonificoExtraSepa).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, commissioniBanca);
        return entity;
    }

    @Mapping(target = ".", source = "entity")
    CommissioniCliente toDto(CommissioneAccountToAccount.WithLinkedEntities entity);
}
