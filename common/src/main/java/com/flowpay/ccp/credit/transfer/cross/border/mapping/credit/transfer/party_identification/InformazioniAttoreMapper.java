package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.party_identification;

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
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaClienteRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account.InformazioniRapportoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.address.IndirizzoPostaleMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import jakarta.inject.Inject;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class }, uses = { OrganizzazioneMapper.class,
        PrivatoMapper.class, IndirizzoPostaleMapper.class,
        InformazioniRapportoMapper.class })
public abstract class InformazioniAttoreMapper {
    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected IndirizzoPostaleMapper indirizzoPostaleMapper;
    @SuppressWarnings("java:S6813")
    @Inject
    protected InformazioniRapportoMapper informazioniRapportoMapper;

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "idInfoRapporto", source = "informazioniRapporto.entity.id")
    @Mapping(target = "idIndirizzoPostale", source = "indirizzoPostale.entity.id")
    @DtoToBareEntity
    abstract InformazioniAttore bareFromDto(Attore attore,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale, TipoAttore tipo);

    @Mapping(target = "organizzazione", source = "attore.organizzazione", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "privato", source = "attore.privato", qualifiedBy = { DtoToEntityWithLinkedEntitiesMainDocument.class })
    abstract void fillLinked(
            @MappingTarget InformazioniAttore.WithLinkedEntities informazioniAttore,
            @Context InformazioniAttore.WithLinkedEntities informazioniAttoreContext,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale, Attore attore);

    @DtoToEntityWithLinkedEntitiesMainDocument
    public InformazioniAttore.WithLinkedEntities fromDto(Attore attore, @Context TipoAttore tipo,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (attore == null) {
            return null;
        }
        var indirizzoPostale = indirizzoPostaleMapper.fromDto(attore.indirizzo());
        var informazioniRapporto = informazioniRapportoMapper.fromDto(attore.rapporto());

        var entity = bareFromDto(attore, bonificoExtraSepa, informazioniRapporto, indirizzoPostale, tipo)
                .withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, informazioniRapporto, indirizzoPostale,
                attore);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public Collection<InformazioniAttore.WithLinkedEntities> fromDto(
            InserisciBonificoExtraSepaRichiesta richiesta,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext) {
        if (richiesta == null)
            return null;

        var attori = new ArrayList<InformazioniAttore.WithLinkedEntities>(5);

        if (richiesta instanceof InserisciBonificoExtraSepaClienteRichiesta clienteRichiesta) {
            Stream.of(
                    fromDto(clienteRichiesta.ordinante().infoAttore(), TipoAttore.ORDINANTE,
                            bonificoExtraSepaContext),
                    fromDto(clienteRichiesta.soggettoIstruttore(), TipoAttore.SOGGETTO_ISTRUTTORE,
                            bonificoExtraSepaContext),
                    fromDto(clienteRichiesta.debitoreEffettivo(), TipoAttore.ORDINANTE_EFFETTIVO,
                            bonificoExtraSepaContext),
                    fromDto(clienteRichiesta.beneficiario(), TipoAttore.BENEFICIARIO,
                            bonificoExtraSepaContext),
                    fromDto(clienteRichiesta.beneficiarioEffettivo(), TipoAttore.BENEFICIARIO_EFFETTIVO,
                            bonificoExtraSepaContext))
                    .filter(Objects::nonNull).forEach(attori::add);
        }

        return attori;
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "indirizzo", source = "indirizzoPostale")
    @Mapping(target = "rapporto", source = "informazioniRapporto")
    public abstract Attore toDto(InformazioniAttore.WithLinkedEntities informazioniAttore);

    public Attore toDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore,
            @Context TipoAttore tipoAttore) {
        if (informazioniAttore == null)
            return null;
        return informazioniAttore.stream()
                .filter(attore -> attore != null && attore.getEntity().tipo() == tipoAttore)
                .findAny().map(this::toDto).orElse(null);
    }

    @Named("toOrdinanteDto")
    public Attore toOrdinanteDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore) {
        return toDto(informazioniAttore, TipoAttore.ORDINANTE);
    }

    @Named("toSoggettoIstruttoreDto")
    public Attore toSoggettoIstruttoreDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore) {
        return toDto(informazioniAttore, TipoAttore.SOGGETTO_ISTRUTTORE);
    }

    @Named("toOrdinanteEffettivoDto")
    public Attore toOrdinanteEffettivoDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore) {
        return toDto(informazioniAttore, TipoAttore.ORDINANTE_EFFETTIVO);
    }

    @Named("toBeneficiarioDto")
    public Attore toBeneficiarioDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore) {
        return toDto(informazioniAttore, TipoAttore.BENEFICIARIO);
    }

    @Named("toBeneficiarioEffettivoDto")
    public Attore toBeneficiarioEffettivoDto(Collection<InformazioniAttore.WithLinkedEntities> informazioniAttore) {
        return toDto(informazioniAttore, TipoAttore.BENEFICIARIO_EFFETTIVO);
    }
}
