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
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RegulatoryReporting;

@Mapper(config = MappingCommonConfig.class, imports = {UUID.class},
        uses = {DettagliRegulatoryReportingMapper.class})
public interface RegulatoryReportingMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @DtoToBareEntity
    RegulatoryReporting bareFromDto(InformazioniRegulatoryReporting informazioniRegulatoryReporting,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    @Mapping(target = "dettagliRegulatoryReportings",
            source = "informazioniRegulatoryReporting.dettagliRegulatoryReporting",
            qualifiedBy = {DtoToEntityWithLinkedEntitiesMainDocument.class})
    void fillLinked(@MappingTarget RegulatoryReporting.WithLinkedEntities regulatoryReporting,
            @Context RegulatoryReporting.WithLinkedEntities regulatoryReportingContext,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniRegulatoryReporting informazioniRegulatoryReporting);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default RegulatoryReporting.WithLinkedEntities fromDto(
            InformazioniRegulatoryReporting informazioniRegulatoryReporting,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(informazioniRegulatoryReporting, bonificoExtraSepa)
                .withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, informazioniRegulatoryReporting);
        return entity;
    }


    @Mapping(target = ".", source = "entity")
    @Mapping(target = "dettagliRegulatoryReporting", source = "dettagliRegulatoryReportings")
    InformazioniRegulatoryReporting toDto(
            RegulatoryReporting.WithLinkedEntities regulatoryReporting);
}
