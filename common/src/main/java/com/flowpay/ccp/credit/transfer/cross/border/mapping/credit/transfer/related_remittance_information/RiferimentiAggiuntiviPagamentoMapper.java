package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi.DettaglioRiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.address.IndirizzoPostaleMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RiferimentiAggiuntiviPagamento;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappingCommonConfig.class, imports = {UUID.class},
        uses = {IndirizzoPostaleMapper.class})
public abstract class RiferimentiAggiuntiviPagamentoMapper {
    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected IndirizzoPostaleMapper indirizzoPostaleMapper;

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "idIndirizzoPostale", source = "indirizzoPostale.entity.id")
    @DtoToBareEntity
    abstract RiferimentiAggiuntiviPagamento bareFromDto(
            DettaglioRiferimentiAggiuntivi dettaglioRiferimentiAggiuntivi,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    abstract void fillLinked(
            @MappingTarget RiferimentiAggiuntiviPagamento.WithLinkedEntities riferimentiAggiuntiviPagamenti,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    @DtoToEntityWithLinkedEntitiesMainDocument
    RiferimentiAggiuntiviPagamento.WithLinkedEntities fromDto(
            DettaglioRiferimentiAggiuntivi dettaglioRiferimentiAggiuntivi,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (dettaglioRiferimentiAggiuntivi == null)
            return null;
        var indirizzoPostale =
                indirizzoPostaleMapper.fromDto(dettaglioRiferimentiAggiuntivi.indirizzoPostale());

        var entity =
                bareFromDto(dettaglioRiferimentiAggiuntivi, indirizzoPostale, bonificoExtraSepa)
                        .withLinkedEntities();
        fillLinked(entity, indirizzoPostale, bonificoExtraSepa);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public Collection<RiferimentiAggiuntiviPagamento.WithLinkedEntities> fromDto(
            RiferimentiAggiuntivi riferimentiAggiuntivi,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext) {
        if (riferimentiAggiuntivi == null)
            return null;

        var riferimenti = new ArrayList<RiferimentiAggiuntiviPagamento.WithLinkedEntities>(2);

        Stream.of(fromDto(riferimentiAggiuntivi.riferimentiAggiuntivi1(), bonificoExtraSepaContext),
                fromDto(riferimentiAggiuntivi.riferimentiAggiuntivi2(), bonificoExtraSepaContext))
                .filter(Objects::nonNull).forEach(riferimenti::add);

        return riferimenti;
    }

    public static record DettagliRiferimentiAggiuntiviPartialDto(
            DettaglioRiferimentiAggiuntivi riferimentiAggiuntivi1,
            DettaglioRiferimentiAggiuntivi riferimentiAggiuntivi2) {
    }

    @Mapping(target = ".", source = "entity")
    public abstract DettaglioRiferimentiAggiuntivi toDto(
            RiferimentiAggiuntiviPagamento.WithLinkedEntities entity);

    public DettagliRiferimentiAggiuntiviPartialDto toPartialDto(
            Collection<RiferimentiAggiuntiviPagamento.WithLinkedEntities> entities) {
        // This chooses a order for the riferimentiAggiuntivi. There is no guarantee that this order is 
        // the same of the original DTO, as it is not saved in the database. If the order must be mantained
        // the database has to be changed.
        if (entities == null) {
            return new DettagliRiferimentiAggiuntiviPartialDto(null, null);
        }
        var list = entities.stream().limit(2).toList();
        if (list.size() >= 2) {
            return new DettagliRiferimentiAggiuntiviPartialDto(toDto(list.get(0)), toDto(list.get(1)));
        } else {
            return new DettagliRiferimentiAggiuntiviPartialDto(null, null);
        }
    }
}
