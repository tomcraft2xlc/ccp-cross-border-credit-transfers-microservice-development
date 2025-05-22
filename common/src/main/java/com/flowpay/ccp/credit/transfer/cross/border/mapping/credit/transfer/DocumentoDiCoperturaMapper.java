package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.DettagliDocumentoDiCopertura;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account.InformazioniRapportoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.intermediary.InformazioniIntermediarioMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamentoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.settlement_system.SistemaDiRegolamentoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapportoBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(config = MappingCommonConfig.class,
uses = {
        SistemaDiRegolamentoMapper.class,
        InformazioniIntermediarioMapper.class,
        InformazioniAggiuntivePagamentoMapper.class
})
public abstract class DocumentoDiCoperturaMapper {

    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected InformazioniRapportoMapper informazioniRapportoMapper;

    @Mapping(target = "rapportoCorrispondenteMittente", source = "informazioniRapportiBonificoExtraSepa")
    @Mapping(target = "tid", source = "entity.entity.tidDocumentoCollegato")
    @Mapping(target = "infoSistemaDiRegolamento", source = "informazioniSistemaDiRegolamentoDocumentoCollegato")
    @Mapping(target = "bancaIstruttrice1", source = "informazioniIntermediari", qualifiedByName = "toBancaIstruttrice1DtoDocumentoCollegato")
    @Mapping(target = "bancaIstruttrice2", source = "informazioniIntermediari", qualifiedByName = "toBancaIstruttrice2DtoDocumentoCollegato")
    @Mapping(target = "bancaIstruttrice3", source = "informazioniIntermediari", qualifiedByName = "toBancaIstruttrice3DtoDocumentoCollegato")
    @Mapping(target = "bancaIntermediaria1", source = "informazioniIntermediari", qualifiedByName = "toBancaIntermediaria1DtoDocumentoCollegato")
    @Mapping(target = "bancaIntermediaria2", source = "informazioniIntermediari", qualifiedByName = "toBancaIntermediaria2DtoDocumentoCollegato")
    @Mapping(target = "bancaIntermediaria3", source = "informazioniIntermediari", qualifiedByName = "toBancaIntermediaria3DtoDocumentoCollegato")
    @Mapping(target = "riferimentiAggiuntivi", source = "informazioniAggiuntivePagamentoDocumentoCollegato")
    @Mapping(target = "causaleDescrittiva", source = "informazioniCausaleDocumentoCollegato")
    abstract DettagliDocumentoDiCopertura toDTO(BonificoExtraSepa.WithLinkedEntities entity);

    Rapporto toRapportoCorrispondenteMittente(Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> rapporti) {
        if (rapporti == null) {
            return null;
        }
        var corrispondete = rapporti.stream().filter(rapporto -> rapporto.getEntity().tipoInformazioniRapporto() == TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE_DOCUMENTO_COLLEGATO)
                .findFirst();
        return corrispondete.map(withLinkedEntities -> informazioniRapportoMapper.toDto(withLinkedEntities.informazioniRapporto)).orElse(null);
    }

    public String causale(Collection<InformazioniCausale.WithLinkedEntities> causali) {
        if (causali == null) {
            return null;
        }
        var causale = causali.stream().findFirst();
        return causale.map(value -> value.getEntity().causaleDescrittiva()).orElse(null);
    }
}
