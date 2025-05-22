package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance.tax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.InformazioniFiscali;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.InformazioniFiscali.RecordFiscale;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliFiscali;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.RecordDettagliFiscali;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface RecordDettagliFiscaliMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idDettagliFiscali", source = "dettagliFiscali.entity.id")
    @Mapping(target = "codiceTipo", source = "recordFiscale.codice")
    @Mapping(target = "categoriaTassazione", source = "recordFiscale.categoria")
    @Mapping(target = "dettagliCategoriaTassazione", source = "recordFiscale.dettagliCategoria")
    @Mapping(target = "statusContribuenteDebitore", source = "recordFiscale.statusContribuente")
    @Mapping(target = "importoImponibile", source = "recordFiscale.imponibile.importo")
    @Mapping(target = "divisaImportoImponibile", source = "recordFiscale.imponibile.divisa")
    @Mapping(target = "importoImposta", source = "recordFiscale.imposta.importo")
    @Mapping(target = "divisaImportoImposta", source = "recordFiscale.imposta.divisa")
    @DtoToBareEntity
    RecordDettagliFiscali bareFromDto(
            InformazioniFiscali.RecordFiscale recordFiscale,
            DettagliFiscali.WithLinkedEntities dettagliFiscali);

    /// TODO: map the subrecords
    @Mapping(target = "dettagliRecordDettagliFiscali", ignore = true)
    void fillLinked(@MappingTarget RecordDettagliFiscali.WithLinkedEntities recordFiscale,
            DettagliFiscali.WithLinkedEntities dettagliFiscali);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default RecordDettagliFiscali.WithLinkedEntities fromDto(
            InformazioniFiscali.RecordFiscale recordFiscale,
            @Context DettagliFiscali.WithLinkedEntities dettagliFiscali) {
        var entity = bareFromDto(recordFiscale, dettagliFiscali).withLinkedEntities();
        fillLinked(entity, dettagliFiscali);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    default Collection<RecordDettagliFiscali.WithLinkedEntities> fromDto(
            InformazioniFiscali informazioniFiscali,
            @Context DettagliFiscali.WithLinkedEntities dettagliFiscali) {
        if (informazioniFiscali == null) {
            return null;
        }

        var entities = new ArrayList<RecordDettagliFiscali.WithLinkedEntities>();

        informazioniFiscali.recordFiscali().stream().map(recordFiscale -> fromDto(recordFiscale, dettagliFiscali)).filter(Objects::nonNull).forEach(entities::add);

        return entities;
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "codice", source = "entity.codiceTipo")
    @Mapping(target = "categoria", source = "entity.categoriaTassazione")
    @Mapping(target = "dettagliCategoria", source = "entity.dettagliCategoriaTassazione")
    @Mapping(target = "statusContribuente", source = "entity.statusContribuenteDebitore")
    @Mapping(target = "imponibile.importo", source = "entity.importoImponibile")
    @Mapping(target = "imponibile.divisa", source = "entity.divisaImportoImponibile")
    @Mapping(target = "imposta.importo", source = "entity.importoImposta")
    @Mapping(target = "imposta.divisa", source = "entity.divisaImportoImposta")
    /// TODO: map the subrecords
    @Mapping(target = "dettagli", ignore = true)
    RecordFiscale toDto(RecordDettagliFiscali.WithLinkedEntities recordDettagliFiscali);

}
