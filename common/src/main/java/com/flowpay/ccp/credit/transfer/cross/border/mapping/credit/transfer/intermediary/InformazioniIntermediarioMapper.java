package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.intermediary;

import java.util.*;
import java.util.stream.Stream;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.DettagliDocumentoDiCopertura;
import org.jboss.logging.Logger;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaBancaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account.InformazioniRapportoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.address.IndirizzoPostaleMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import jakarta.inject.Inject;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class }, uses = { IndirizzoPostaleMapper.class,
        InformazioniRapportoMapper.class })
public abstract class InformazioniIntermediarioMapper {

    private static final Logger log = Logger.getLogger(InformazioniIntermediarioMapper.class);
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
    abstract InformazioniIntermediario bareFromDto(Intermediario intermediario,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale,
            TipoIntermediario tipoIntermediario,
            Boolean intermediarioDocumentoCollegato);

    abstract void fillLinked(
            @MappingTarget InformazioniIntermediario.WithLinkedEntities informazioniIntermediario,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniRapporto.WithLinkedEntities informazioniRapporto,
            IndirizzoPostale.WithLinkedEntities indirizzoPostale);

    @DtoToEntityWithLinkedEntitiesMainDocument
    public InformazioniIntermediario.WithLinkedEntities fromDto(Intermediario intermediario,
            @Context TipoIntermediario tipo,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            Boolean intermediarioDocumentoCollegato) {
        if (intermediario == null) {
            return null;
        }
        var indirizzoPostale = indirizzoPostaleMapper.fromDto(intermediario.indirizzo());
        var informazioniRapporto = informazioniRapportoMapper.fromDto(intermediario.rapporto());

        var entity = bareFromDto(intermediario, bonificoExtraSepa, informazioniRapporto,
                indirizzoPostale, tipo, intermediarioDocumentoCollegato).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, informazioniRapporto, indirizzoPostale);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public Collection<InformazioniIntermediario.WithLinkedEntities> fromDto(
            InserisciBonificoExtraSepaRichiesta richiesta,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext) {
        if (richiesta == null)
            return null;

        var intermediari = new ArrayList<InformazioniIntermediario.WithLinkedEntities>(14);

        var altriIntermediari = richiesta.altriIntermediari();
        var documentoDiCopertura = richiesta.documentoDiCopertura();

        Stream.of(
                fromDto(richiesta.bancaOrdinante(), TipoIntermediario.BANCA_DELL_ORDINANTE,
                        bonificoExtraSepaContext, false),
                fromDto(richiesta.bancaDestinataria(), TipoIntermediario.BANCA_DESTINATARIA,
                        bonificoExtraSepaContext, false),
                fromDto(richiesta.bancaDelBeneficiario(), TipoIntermediario.BANCA_DEL_BENEFICIARIO,
                        bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIstruttrice1).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_1, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIstruttrice2).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_2, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIstruttrice3).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_3, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIntermediaria1).orElse(null),
                        TipoIntermediario.BANCA_INTERMEDIARIA_1, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIntermediaria2).orElse(null),
                        TipoIntermediario.BANCA_INTERMEDIARIA_2, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaIntermediaria3).orElse(null),
                        TipoIntermediario.BANCA_INTERMEDIARIA_3, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaCorrispondenteMittente).orElse(null),
                        TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::bancaCorrispondenteRicevente).orElse(null),
                        TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(altriIntermediari).map(AltriIntermediari::istitutoTerzoDiRimborso).orElse(null),
                        TipoIntermediario.ISTITUTO_TERZO_DI_RIMBORSO, bonificoExtraSepaContext, false),
                fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIstruttrice1).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_1, bonificoExtraSepaContext, true),
                fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIstruttrice2).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_2, bonificoExtraSepaContext, true),
                fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIstruttrice3).orElse(null),
                        TipoIntermediario.BANCA_ISTRUTTRICE_3, bonificoExtraSepaContext, true),
                fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIntermediaria1).orElse(null),
                        TipoIntermediario.BANCA_INTERMEDIARIA_1, bonificoExtraSepaContext, true),
                    fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIntermediaria2).orElse(null),
                                TipoIntermediario.BANCA_INTERMEDIARIA_2, bonificoExtraSepaContext, true),
                        fromDto(Optional.ofNullable(documentoDiCopertura).map(DettagliDocumentoDiCopertura::bancaIntermediaria3).orElse(null),
                                TipoIntermediario.BANCA_INTERMEDIARIA_3, bonificoExtraSepaContext, true))
                .filter(Objects::nonNull).forEach(intermediari::add);

        if (richiesta instanceof InserisciBonificoExtraSepaBancaRichiesta bancaRichiesta) {
            Stream.of(
                    fromDto(bancaRichiesta.ordinante(), TipoIntermediario.ORDINANTE,
                            bonificoExtraSepaContext, false),
                    fromDto(bancaRichiesta.beneficiario(), TipoIntermediario.BANCA_BENEFICIARIA,
                            bonificoExtraSepaContext, false))
                    .filter(Objects::nonNull).forEach(intermediari::add);
        }

        return intermediari;
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "rapporto", source = "informazioniRapporto")
    @Mapping(target = "indirizzo", source = "indirizzoPostale")
    abstract Intermediario toDto(InformazioniIntermediario.WithLinkedEntities infoIntermediario);

    public Intermediario toDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari,
            @Context TipoIntermediario tipoIntermediario, Boolean documentoCollegato) {
        if (intermediari == null) {
            return null;
        }
        return intermediari.stream().filter(
                intermediario -> intermediario.getEntity().tipoIntermediario() == tipoIntermediario)
                .filter(intermediario -> intermediario.getEntity().intermediarioDocumentoCollegato() == documentoCollegato)
                .findAny().map(this::toDto).orElse(null);
    }

    @Named("toOrdinanteDto")
    public Intermediario toOrdinanteDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.ORDINANTE, false);
    }

    @Named("toBancaDellOrdinanteDto")
    public Intermediario toBancaDellOrdinanteDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_DELL_ORDINANTE, false);
    }

    @Named("toBancaDestinatariaDto")
    public Intermediario toBancaDestinatariaDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_DESTINATARIA, false);
    }

    @Named("toBancaBeneficiariaDto")
    public Intermediario toBancaBeneficiariaDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_BENEFICIARIA, false);
    }

    @Named("toBancaDelBeneficiarioDto")
    public Intermediario toBancaDelBeneficiarioDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO, false);
    }

    @Named("toBancaIstruttrice1Dto")
    public Intermediario toBancaIstruttrice1Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1, false);
    }

    @Named("toBancaIstruttrice2Dto")
    public Intermediario toBancaIstruttrice2Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2, false);
    }

    @Named("toBancaIstruttrice3Dto")
    public Intermediario toBancaIstruttrice3Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3, false);
    }

    @Named("toBancaIntermediaria1Dto")
    public Intermediario toBancaIntermediaria1Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1, false);
    }

    @Named("toBancaIntermediaria2Dto")
    public Intermediario toBancaIntermediaria2Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2, false);
    }

    @Named("toBancaIntermediaria3Dto")
    public Intermediario toBancaIntermediaria3Dto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3, false);
    }

    @Named("toBancaIstruttrice1DtoDocumentoCollegato")
    public Intermediario toBancaIstruttrice1DtoDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1, true);
    }

    @Named("toBancaIstruttrice2DtoDocumentoCollegato")
    public Intermediario toBancaIstruttrice2DtoDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2, true);
    }

    @Named("toBancaIstruttrice3DtoDocumentoCollegato")
    public Intermediario toBancaIstruttrice3DtoDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3, true);
    }

    @Named("toBancaIntermediaria1DtoDocumentoCollegato")
    public Intermediario toBancaIntermediaria1DtDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1, true);
    }

    @Named("toBancaIntermediaria2DtoDocumentoCollegato")
    public Intermediario toBancaIntermediaria2DtoDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2, true);
    }

    @Named("toBancaIntermediaria3DtoDocumentoCollegato")
    public Intermediario toBancaIntermediaria3DtoDocumentoCollegato(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3, true);
    }

    @Named("toBancaCorrispondenteMittenteDto")
    public Intermediario toBancaCorrispondenteMittenteDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE, false);
    }

    @Named("toBancaCorrispondenteRiceventeDto")
    public Intermediario toBancaCorrispondenteRiceventeDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE, false);
    }

    @Named("toIstitutoTerzoDiRimborsoDto")
    public Intermediario toIstitutoTerzoDiRimborsoDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return toDto(intermediari, TipoIntermediario.ISTITUTO_TERZO_DI_RIMBORSO, false);
    }

    @Mapping(target = "bancaCorrispondenteMittente", source = "intermediari", qualifiedByName = "toBancaCorrispondenteMittenteDto")
    @Mapping(target = "bancaCorrispondenteRicevente", source = "intermediari", qualifiedByName = "toBancaCorrispondenteRiceventeDto")
    @Mapping(target = "bancaIstruttrice1", source = "intermediari", qualifiedByName = "toBancaIstruttrice1Dto")
    @Mapping(target = "bancaIstruttrice2", source = "intermediari", qualifiedByName = "toBancaIstruttrice2Dto")
    @Mapping(target = "bancaIstruttrice3", source = "intermediari", qualifiedByName = "toBancaIstruttrice3Dto")
    @Mapping(target = "bancaIntermediaria1", source = "intermediari", qualifiedByName = "toBancaIntermediaria1Dto")
    @Mapping(target = "bancaIntermediaria2", source = "intermediari", qualifiedByName = "toBancaIntermediaria2Dto")
    @Mapping(target = "bancaIntermediaria3", source = "intermediari", qualifiedByName = "toBancaIntermediaria3Dto")
    @Mapping(target = "istitutoTerzoDiRimborso", source = "intermediari", qualifiedByName = "toIstitutoTerzoDiRimborsoDto")
    public abstract AltriIntermediari toAltriIntermediariDto(
            Collection<InformazioniIntermediario.WithLinkedEntities> intermediari,
            InformazioniRapporto.WithLinkedEntities riferimentiCorrispondenteMittente);

    public AltriIntermediari toAltriIntermediariDto(
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        log.debug("bonificoExtraSepa: " + (bonificoExtraSepa != null));
        if (bonificoExtraSepa == null) {
            return null;
        }

        log.debug("informazioniRapportiBonificoExtraSepa != null: " + (bonificoExtraSepa.informazioniRapportiBonificoExtraSepa != null));

        return toAltriIntermediariDto(bonificoExtraSepa.informazioniIntermediari,
                bonificoExtraSepa.informazioniRapportiBonificoExtraSepa != null ? bonificoExtraSepa.informazioniRapportiBonificoExtraSepa.stream()
                        .filter(rapporto -> rapporto.getEntity()
                                .tipoInformazioniRapporto() == TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE)
                        .findAny().map(infoRapporto -> infoRapporto.informazioniRapporto)
                        .orElse(null) : null);
    }

}
