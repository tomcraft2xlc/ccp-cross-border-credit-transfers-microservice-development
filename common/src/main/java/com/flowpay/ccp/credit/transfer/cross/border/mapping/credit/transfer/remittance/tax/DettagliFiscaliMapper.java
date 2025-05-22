package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance.tax;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.InformazioniFiscali;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliFiscali;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class }, uses = { AttoreFiscaleMapper.class,
        RecordDettagliFiscaliMapper.class })
public interface DettagliFiscaliMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idInformazioniCausale", source = "informazioniCausale.entity.id")
    @Mapping(target = "importoImponibile", source = "informazioniFiscali.imponibile.importo")
    @Mapping(target = "divisaImportoImponibile", source = "informazioniFiscali.imponibile.divisa")
    @Mapping(target = "importoImposta", source = "informazioniFiscali.imposta.importo")
    @Mapping(target = "divisaImportoImposta", source = "informazioniFiscali.imposta.divisa")
    @DtoToBareEntity
    DettagliFiscali bareFromDto(
            InformazioniFiscali informazioniFiscali,
            InformazioniCausale.WithLinkedEntities informazioniCausale);

    @Mapping(target = "attoriFiscali", source = "informazioniFiscali", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "recordsDettagliFiscali", source = "informazioniFiscali", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    void fillLinked(@MappingTarget DettagliFiscali.WithLinkedEntities dettagliFiscali,
            @Context DettagliFiscali.WithLinkedEntities dettagliFiscaliContext,
            InformazioniFiscali informazioniFiscali,
            InformazioniCausale.WithLinkedEntities informazioniCausale);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default DettagliFiscali.WithLinkedEntities fromDto(
            InformazioniFiscali informazioniFiscali,
            @Context InformazioniCausale.WithLinkedEntities informazioniCausale) {
        var entity = bareFromDto(informazioniFiscali, informazioniCausale).withLinkedEntities();
        fillLinked(entity, entity, informazioniFiscali, informazioniCausale);
        return entity;
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "imponibile.importo", source = "entity.importoImponibile")
    @Mapping(target = "imponibile.divisa", source = "entity.divisaImportoImponibile")
    @Mapping(target = "imposta.importo", source = "entity.importoImposta")
    @Mapping(target = "imposta.divisa", source = "entity.divisaImportoImposta")
    @Mapping(target = "recordFiscali", source = "recordsDettagliFiscali")
    @Mapping(target = "debitore", source = "attoriFiscali", qualifiedByName = "toDebitoreDto")
    @Mapping(target = "creditore", source = "attoriFiscali", qualifiedByName = "toCreditoreDto")
    @Mapping(target = "debitoreEffettivo", source = "attoriFiscali", qualifiedByName = "toDebitoreEffettivoDto")
    InformazioniFiscali toDto(DettagliFiscali.WithLinkedEntities dettagliFiscali);

}
