package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account;

import java.util.*;
import java.util.stream.Stream;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.DettagliDocumentoDiCopertura;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapportoBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import jakarta.inject.Inject;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public abstract class InformazioniRapportoBonificoExtraSepaMapper {
    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected InformazioniRapportoMapper informazioniRapportoMapper;

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "idInfoRapporto", source = "informazioniRapporto.entity.id")
    @DtoToBareEntity
    abstract InformazioniRapportoBonificoExtraSepa bareFromDto(
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            TipoInformazioniRapporto tipoInformazioniRapporto);

    abstract void fillLinked(
            @MappingTarget InformazioniRapportoBonificoExtraSepa.WithLinkedEntities riferimentiAggiuntiviPagamenti,
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    @DtoToEntityWithLinkedEntitiesMainDocument
    InformazioniRapportoBonificoExtraSepa.WithLinkedEntities fromDto(
            Rapporto rapporto,
            TipoInformazioniRapporto tipoInformazioniRapporto,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var informazioniRapporto = informazioniRapportoMapper.fromDto(rapporto);

        if (informazioniRapporto == null) {
            return null;
        }

        var entity = bareFromDto(informazioniRapporto, bonificoExtraSepa, tipoInformazioniRapporto)
                .withLinkedEntities();
        fillLinked(entity, informazioniRapporto, bonificoExtraSepa);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> fromDto(
            InserisciBonificoExtraSepaRichiesta richiesta,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext) {
        if (richiesta == null)
            return null;

        var riferimenti = new ArrayList<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities>(1);

        var altriIntermediari = richiesta.altriIntermediari();

        if (altriIntermediari == null) {
            return null;
        }

        Stream.of(
                fromDto(altriIntermediari.riferimentiCorrispondenteMittente(),
                        TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE, bonificoExtraSepaContext),
                fromDto(Optional.ofNullable(richiesta.documentoDiCopertura()).map(DettagliDocumentoDiCopertura::rapportoCorrispondenteMittente).orElse(null),
                        TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE_DOCUMENTO_COLLEGATO, bonificoExtraSepaContext))
                .filter(Objects::nonNull).forEach(riferimenti::add);

        return riferimenti;
    }

}