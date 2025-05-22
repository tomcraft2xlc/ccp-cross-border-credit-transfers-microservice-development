package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi.InformazioniRegulatoryReporting;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.DettagliRegulatoryReporting;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RegulatoryReporting;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface DettagliRegulatoryReportingMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idRegulatoryReporting", source = "regulatoryReporting.entity.id")
    @Mapping(target = ".", source = "dettagliRegulatoryReporting.importo")
    @DtoToBareEntity
    DettagliRegulatoryReporting bareFromDto(
            InformazioniRegulatoryReporting.DettagliRegulatoryReporting dettagliRegulatoryReporting,
            RegulatoryReporting.WithLinkedEntities regulatoryReporting);

    void fillLinked(@MappingTarget DettagliRegulatoryReporting.WithLinkedEntities dettagliRegulatoryReporting,
            RegulatoryReporting.WithLinkedEntities regulatoryReporting,
            InformazioniRegulatoryReporting.DettagliRegulatoryReporting informazioniRegulatoryReporting);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default DettagliRegulatoryReporting.WithLinkedEntities fromDto(
            InformazioniRegulatoryReporting.DettagliRegulatoryReporting dettagliRegulatoryReporting,
            @Context RegulatoryReporting.WithLinkedEntities regulatoryReporting) {
        var entity = bareFromDto(dettagliRegulatoryReporting, regulatoryReporting).withLinkedEntities();
        fillLinked(entity, regulatoryReporting, dettagliRegulatoryReporting);
        return entity;
    }


    @Mapping(target = ".", source = "entity")
    @Mapping(target = "importo", source = "entity")
    InformazioniRegulatoryReporting.DettagliRegulatoryReporting toDto(
            DettagliRegulatoryReporting.WithLinkedEntities regulatoryReporting);
}
