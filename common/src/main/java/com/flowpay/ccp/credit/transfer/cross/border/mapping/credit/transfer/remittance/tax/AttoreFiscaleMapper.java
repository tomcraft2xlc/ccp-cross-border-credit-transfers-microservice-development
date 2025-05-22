package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance.tax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.InformazioniFiscali;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.TipoAttoreFiscale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.AttoreFiscale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliFiscali;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public interface AttoreFiscaleMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idDettagliFiscali", source = "dettagliFiscali.entity.id")
    @DtoToBareEntity
    AttoreFiscale bareFromDto(
            InformazioniFiscali.AttoreFiscale attoreFiscale,
            DettagliFiscali.WithLinkedEntities dettagliFiscali,
            TipoAttoreFiscale tipoAttoreFiscale);

    void fillLinked(@MappingTarget AttoreFiscale.WithLinkedEntities attoreFiscale,
            DettagliFiscali.WithLinkedEntities dettagliFiscali);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default AttoreFiscale.WithLinkedEntities fromDto(
            InformazioniFiscali.AttoreFiscale attoreFiscale,
            @Context DettagliFiscali.WithLinkedEntities dettagliFiscali,
            TipoAttoreFiscale tipoAttoreFiscale) {
        var entity = bareFromDto(attoreFiscale, dettagliFiscali, tipoAttoreFiscale).withLinkedEntities();
        fillLinked(entity, dettagliFiscali);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    default Collection<AttoreFiscale.WithLinkedEntities> fromDto(
            InformazioniFiscali informazioniFiscali,
            @Context DettagliFiscali.WithLinkedEntities dettagliFiscali) {
        if (informazioniFiscali == null) {
            return null;
        }

        var entities = new ArrayList<AttoreFiscale.WithLinkedEntities>(3);

        Stream.of(
                fromDto(informazioniFiscali.creditore(), dettagliFiscali, TipoAttoreFiscale.CREDITORE),
                fromDto(informazioniFiscali.debitore(), dettagliFiscali, TipoAttoreFiscale.DEBITORE),
                fromDto(informazioniFiscali.debitoreEffettivo(), dettagliFiscali, TipoAttoreFiscale.DEBITORE_FINALE))
                .filter(Objects::nonNull).forEach(entities::add);

        return entities;
    }

    @Mapping(target = ".", source = "entity")
    InformazioniFiscali.AttoreFiscale toDto(AttoreFiscale.WithLinkedEntities attoreFiscale);

    default InformazioniFiscali.AttoreFiscale toDto(Collection<AttoreFiscale.WithLinkedEntities> attoriFiscali,
            TipoAttoreFiscale tipoAttoreFiscale) {
        if (attoriFiscali == null) {
            return null;
        }
        return attoriFiscali.stream().filter(attore -> attore.getEntity().tipoAttoreFiscale().equals(tipoAttoreFiscale))
                .findFirst().map(this::toDto).orElse(null);
    }

    @Named("toDebitoreDto")
    default InformazioniFiscali.AttoreFiscale toDebitoreDto(Collection<AttoreFiscale.WithLinkedEntities> attoreFiscale) {
        return toDto(attoreFiscale, TipoAttoreFiscale.DEBITORE);
    }

    @Named("toCreditoreDto")
    default InformazioniFiscali.AttoreFiscale toCreditoreDto(Collection<AttoreFiscale.WithLinkedEntities> attoreFiscale) {
        return toDto(attoreFiscale, TipoAttoreFiscale.CREDITORE);
    }

    @Named("toDebitoreEffettivoDto")
    default InformazioniFiscali.AttoreFiscale toDebitoreEffettivoDto(Collection<AttoreFiscale.WithLinkedEntities> attoreFiscale) {
        return toDto(attoreFiscale, TipoAttoreFiscale.DEBITORE_FINALE);
    }

}
