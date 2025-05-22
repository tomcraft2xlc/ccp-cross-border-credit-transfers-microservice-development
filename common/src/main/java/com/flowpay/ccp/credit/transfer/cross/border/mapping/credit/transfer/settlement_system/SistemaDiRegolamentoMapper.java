package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.settlement_system;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.InformazioniSistemaDiRegolamento;

@Mapper(config = MappingCommonConfig.class, imports = {UUID.class})
public interface SistemaDiRegolamentoMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "createdAt", ignore = true)
    @DtoToBareEntity
    InformazioniSistemaDiRegolamento bareFromDto(InfoSistemaDiRegolamento commissioniBanca,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa, Boolean informazioniDocumentoCollegato);

    void fillLinked(@MappingTarget InformazioniSistemaDiRegolamento.WithLinkedEntities commissioneBanca,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InfoSistemaDiRegolamento commissioniBanca);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default InformazioniSistemaDiRegolamento.WithLinkedEntities fromDto(InfoSistemaDiRegolamento infoSistemaDiRegolamento,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(infoSistemaDiRegolamento, bonificoExtraSepa, false).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, infoSistemaDiRegolamento);
        return entity;
    }

    @MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument
    default InformazioniSistemaDiRegolamento.WithLinkedEntities fromDTOLinkedDocument(
            InfoSistemaDiRegolamento infoSistemaDiRegolamento,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(infoSistemaDiRegolamento, bonificoExtraSepa, true).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, infoSistemaDiRegolamento);
        return entity;
    }

    default SistemaDiRegolamento fromInfoDto(InfoSistemaDiRegolamento infoSistemaDiRegolamento) {
        if (infoSistemaDiRegolamento == null) { return null; }
        return infoSistemaDiRegolamento.sistemaDiRegolamento();
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "sistemaDiRegolamento", source = "bonificoExtraSepa.entity.sistemaDiRegolamento")
    InfoSistemaDiRegolamento toDto(InformazioniSistemaDiRegolamento.WithLinkedEntities informazioniSistemaDiRegolamento);


}
