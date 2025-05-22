package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.party_identification;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.InfoOrganizzazione;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Organizzazione;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface OrganizzazioneMapper {

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idInformazioniAttore", source = "informazioniAttore.entity.id")
    @DtoToBareEntity
    Organizzazione bareFromDto(InfoOrganizzazione infoOrganizzazione,
            InformazioniAttore.WithLinkedEntities informazioniAttore);

    void fillLinked(@MappingTarget Organizzazione.WithLinkedEntities organizzazione,
            InformazioniAttore.WithLinkedEntities informazioniAttore);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default Organizzazione.WithLinkedEntities fromDto(
            InfoOrganizzazione infoOrganizzazione,
            @Context InformazioniAttore.WithLinkedEntities informazioniAttore) {
        if ( infoOrganizzazione == null ) {
            return null;
        }
        var entity = bareFromDto(infoOrganizzazione, informazioniAttore).withLinkedEntities();
        fillLinked(entity, informazioniAttore);
        return entity;
    }

    @Mapping(target = ".", source = "entity")
    InfoOrganizzazione toDto(Organizzazione.WithLinkedEntities organizzazione);
}