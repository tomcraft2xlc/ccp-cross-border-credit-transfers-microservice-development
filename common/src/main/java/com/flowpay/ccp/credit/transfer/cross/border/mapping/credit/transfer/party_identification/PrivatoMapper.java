package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.party_identification;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.InfoPrivato;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Privato;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface PrivatoMapper {

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idInformazioniAttore", source = "informazioniAttore.entity.id")
    @DtoToBareEntity
    Privato bareFromDto(InfoPrivato infoPrivato,
            InformazioniAttore.WithLinkedEntities informazioniAttore);

    void fillLinked(@MappingTarget Privato.WithLinkedEntities privato,
            InformazioniAttore.WithLinkedEntities informazioniAttore);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default Privato.WithLinkedEntities fromDto(
            InfoPrivato infoPrivato,
            @Context InformazioniAttore.WithLinkedEntities informazioniAttore) {
        if (infoPrivato == null) {
            return null;
        }
        var entity = bareFromDto(infoPrivato, informazioniAttore).withLinkedEntities();
        fillLinked(entity, informazioniAttore);
        return entity;
    }

    @Mapping(target = ".", source = "entity")
    InfoPrivato toDto(Privato.WithLinkedEntities organizzazione);
}