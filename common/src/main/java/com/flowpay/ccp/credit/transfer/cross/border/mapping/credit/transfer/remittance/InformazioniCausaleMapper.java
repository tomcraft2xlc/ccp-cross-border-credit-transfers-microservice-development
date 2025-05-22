package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleStrutturata;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausale;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleCliente;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.party_identification.InformazioniAttoreMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance.tax.DettagliFiscaliMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;

import jakarta.inject.Inject;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class }, uses = { DettagliFiscaliMapper.class,
        InformazioniAttoreMapper.class })
public abstract class InformazioniCausaleMapper {

    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected InformazioniAttoreMapper informazioniAttoreMapper;

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "causaleDocumentoCollegato", source = "causaleDocumentoCollegato")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "idAttoreEmittenteDocumento", source = "attoreEmittenteDocumento.entity.id")
    @Mapping(target = "idAttoreRiceventeDocumento", source = "attoreRiceventeDocumento.entity.id")
    @Mapping(target = "tipoRiferimentoCreditore", source = "causaleStrutturata.informazioniCreditore.codice")
    @Mapping(target = "descrizioneRiferimentoCreditore", source = "causaleStrutturata.informazioniCreditore.codiceProprietario")
    @Mapping(target = "emittenteRiferimentoCreditore", source = "causaleStrutturata.informazioniCreditore.emittente")
    @Mapping(target = "riferimentoUnivocoCreditore", source = "causaleStrutturata.informazioniCreditore.riferimentoUnivoco")
    @Mapping(target = "ulterioriInformazioni", source = "causaleStrutturata.ulterioriInformazioni")
    @DtoToBareEntity
    abstract InformazioniCausale bareFromDto(String causaleDescrittiva,
            CausaleStrutturata causaleStrutturata,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniAttore.WithLinkedEntities attoreEmittenteDocumento,
            InformazioniAttore.WithLinkedEntities attoreRiceventeDocumento,
            Boolean causaleDocumentoCollegato);

    @Mapping(target = "bonificoExtraSepa", source = "bonificoExtraSepa")
    @Mapping(target = "dettagliFiscali", source = "causale.informazioniFiscali", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    // TODO: Map those next values
    @Mapping(target = "dettagliPignoramento", ignore = true)
    @Mapping(target = "informazioniCausaleDettaglioImporti", ignore = true)
    @Mapping(target = "informazioniDocumentiDiRiferimento", ignore = true)
    abstract void fillLinked(
            @MappingTarget InformazioniCausale.WithLinkedEntities informazioniCausale,
            @Context InformazioniCausale.WithLinkedEntities informazioniCausaleContext,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InformazioniAttore.WithLinkedEntities attoreEmittenteDocumento,
            InformazioniAttore.WithLinkedEntities attoreRiceventeDocumento,
            CausaleStrutturata causale);

    @MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument
    public InformazioniCausale.WithLinkedEntities fromDto(
            String causaleDescrittiva,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (causaleDescrittiva == null) {
            return null;
        }

        var entity = bareFromDto(causaleDescrittiva, null, bonificoExtraSepa, null,
                null, false).withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, null,
                null, null);
        return entity;
    }

    @MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument
    public Collection<InformazioniCausale.WithLinkedEntities> listFromDto(String causaleDescrittiva, @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var result = fromDto(causaleDescrittiva, bonificoExtraSepa);
        if (result == null) {
            return null;
        }
        return List.of(result);
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public InformazioniCausale.WithLinkedEntities fromDto(String causaleDescrittiva,
            CausaleStrutturata causaleStrutturata,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (causaleDescrittiva == null && causaleStrutturata == null) {
            return null;
        }

        InformazioniAttore.WithLinkedEntities attoreEmittenteDocumento = null;
        InformazioniAttore.WithLinkedEntities attoreRiceventeDocumento = null;

        if (causaleStrutturata != null) {
            attoreEmittenteDocumento = informazioniAttoreMapper
                    .fromDto(causaleStrutturata.informazioniEmittente(), null, bonificoExtraSepa);
            attoreRiceventeDocumento = informazioniAttoreMapper
                    .fromDto(causaleStrutturata.informazioniRicevente(), null, bonificoExtraSepa);
        }

        var entity = bareFromDto(causaleDescrittiva, causaleStrutturata, bonificoExtraSepa, attoreEmittenteDocumento,
                attoreRiceventeDocumento, false).withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, attoreEmittenteDocumento,
                attoreRiceventeDocumento, causaleStrutturata);
        return entity;
    }

    @MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument
    public InformazioniCausale.WithLinkedEntities fromDtoCollegato(String causaleDescrittiva,
                                                   @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (causaleDescrittiva == null) {
            return null;
        }

        var entity = bareFromDto(causaleDescrittiva, null, bonificoExtraSepa, null,
                null, true).withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, null,
                null, null);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    public Collection<InformazioniCausale.WithLinkedEntities> fromDto(DettagliCausale causale,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext) {
        if (causale == null) {
            return null;
        }

        var dettaglioCausale = causale.causale();
        if (dettaglioCausale == null) {
            return null;
        }

        var causaleDescrittiva = dettaglioCausale.getCausaleDescrittiva();
        var strutturata = dettaglioCausale.causaleStrutturata();
        if (strutturata != null) {
            return strutturata.stream().map(c -> fromDto(causaleDescrittiva, c, bonificoExtraSepaContext)).toList();
        }
        return List.of(fromDto(causaleDescrittiva, null, bonificoExtraSepaContext));
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "informazioniEmittente", source = "attoreEmittenteDocumento")
    @Mapping(target = "informazioniRicevente", source = "attoreRiceventeDocumento")

    @Mapping(target = "informazioniCreditore.codice", source = "entity.tipoRiferimentoCreditore")
    @Mapping(target = "informazioniCreditore.codiceProprietario", source = "entity.descrizioneRiferimentoCreditore")
    @Mapping(target = "informazioniCreditore.emittente", source = "entity.emittenteRiferimentoCreditore")
    @Mapping(target = "informazioniCreditore.riferimentoUnivoco", source = "entity.riferimentoUnivocoCreditore")

    @Mapping(target = "informazioniFiscali", source = "dettagliFiscali")
    // TODO: Map those next values
    @Mapping(target = "dettagliPignoramento", ignore = true)
    @Mapping(target = "importi", ignore = true)
    @Mapping(target = "documentoDiRiferimento", ignore = true)
    protected abstract CausaleStrutturata toStrutturataDto(
            InformazioniCausale.WithLinkedEntities informazioniCausale);

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "causaleStrutturata", source = ".")
    protected CausaleCliente toClienteDto(Collection<InformazioniCausale.WithLinkedEntities> informazioniCausale) {
        if (informazioniCausale == null) {
            return null;
        }
        var causaleDescrittiva = informazioniCausale.stream().filter(causale -> causale.getEntity().causaleDescrittiva() != null)
                .findFirst().map(InformazioniCausale.WithLinkedEntities::getEntity).map(InformazioniCausale::causaleDescrittiva);
        var causaliStrutturate = informazioniCausale.stream().filter(causale -> causale.getEntity().causaleDescrittiva() == null)
                .toList();

        return new CausaleCliente(
                causaleDescrittiva.orElse(null),
                causaliStrutturate.stream().map(this::toStrutturataDto).toList()
        );
    }

    @Mapping(target = "codiceCausaleTransazione", source = "bonificoExtraSepa", qualifiedByName = "getCodiceCausale")
    @Mapping(target = "causale", source = "informazioniCausale")
    public abstract DettagliCausaleCliente toClienteDto(BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);


    protected CausaleBanca toBancaDto(Collection<InformazioniCausale.WithLinkedEntities> informazioniCausale) {
        if (informazioniCausale == null) {
            return null;
        }
        var causaleDescrittiva = informazioniCausale.stream().filter(causale -> causale.getEntity().causaleDescrittiva() != null).findFirst()
                .map(InformazioniCausale.WithLinkedEntities::getEntity).map(InformazioniCausale::causaleDescrittiva);

        return causaleDescrittiva.map(CausaleBanca::new).orElse(null);

    }

    @Mapping(target = "codiceCausaleTransazione", source = "bonificoExtraSepa", qualifiedByName = "getCodiceCausale")
    @Mapping(target = "causale", source = "informazioniCausale")
    public abstract DettagliCausaleBanca toBancaDto(BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    @Named("getCodiceCausale")
    protected String getCodiceCausale(BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        if (bonificoExtraSepa == null) {
            return null;
        }
        if (bonificoExtraSepa.dettaglioBonificoAccountToAccount != null) {
            return bonificoExtraSepa.dettaglioBonificoAccountToAccount.getEntity().codiceCausaleTransazione();
        }
        ;
        return bonificoExtraSepa.dettaglioBonificoBancaABanca.getEntity().codiceCausaleTransazione();
    }
}
