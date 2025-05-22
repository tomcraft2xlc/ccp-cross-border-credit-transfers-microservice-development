package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.bank_to_bank;

import java.math.BigDecimal;
import java.util.UUID;

import org.mapstruct.Condition;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaBancaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliImporto;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaBanca.DettagliNotifica;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee.CommissioniBancaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.bank_to_bank.DettaglioBonificoBancaABanca;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class, CommissioniBancaMapper.class})
public interface DettaglioBonificoBancaABancaMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = ".", source = "richiesta.dettagliBonifico.dettagliImporto")
    @Mapping(target = "codiceCausaleTransazione", source = "richiesta.dettagliCausale.codiceCausaleTransazione")
    @Mapping(target = "importoDiAddebito", source = "richiesta.dettagliBonifico.dettagliImporto", qualifiedByName = "calcolaImportoDiAddebito")
    @Mapping(target = "informazioniAggiuntiveNotifica", source = "richiesta.dettagliBonifico.dettagliNotifica.notifica")
    @Mapping(target = "regolamentoCommissioneBanca", source = "richiesta.dettagliBonifico.dettagliCommissioni.regolamentoBanca")
    @DtoToBareEntity
    DettaglioBonificoBancaABanca bareFromDto(InserisciBonificoExtraSepaBancaRichiesta richiesta,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    void fillLinked(@MappingTarget DettaglioBonificoBancaABanca.WithLinkedEntities informazioniAggiuntivePagamento,
        BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
        InserisciBonificoExtraSepaBancaRichiesta riferimentiAggiuntivi);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default DettaglioBonificoBancaABanca.WithLinkedEntities fromDto(InserisciBonificoExtraSepaBancaRichiesta richiesta,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(richiesta, bonificoExtraSepa).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, richiesta);
        return entity;
    }

    /**
     * Calcola l'importo di addebito.
     */
    @Named("calcolaImportoDiAddebito")
    default BigDecimal calcolaImportoDiAddebito(DettagliImporto dettagliImporto) {
        if (dettagliImporto == null) {
            return null;
        }

        return Utils.calcolaImportoDiAddebito(dettagliImporto.importo(), dettagliImporto.cambio());
    }

    @Mapping(target = "dettagliImporto.divisa", source = "dettaglioBonificoBancaABanca.entity.divisa")
    @Mapping(target = "dettagliImporto.importo", source = "dettaglioBonificoBancaABanca.entity.importo")
    @Mapping(target = "dettagliImporto.cambio", source = "dettaglioBonificoBancaABanca.entity.cambio")
    @Mapping(target = "dettagliDate.dataCreazione", source = "entity.dataDiCreazione")
    @Mapping(target = "dettagliDate.dataEsecuzione", source = "entity.dataDiEsecuzione")
    @Mapping(target = "dettagliDate.dataValutaOrdinante", source = "entity.dataValutaOrdinante")
    @Mapping(target = "dettagliDate.dataRegolamentoBancaBeneficiario", source = "entity.dataRegolamentoBancaBeneficiario")
    @Mapping(target = "contoBancaDiCopertura.rapportoBanca", source = "entity.ibanContoBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.divisa", source = "entity.divisaContoBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.bic", source = "entity.bicBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.intestazione", source = "entity.intestazioneBancaDiCopertura")
    @Mapping(target = "dettagliCommissioni", source = ".")
    @Mapping(target = "dettagliNotifica", source = "dettaglioBonificoBancaABanca", conditionQualifiedByName = "contieneNotifica")
    DettagliBonificoExtraSepaBanca toDettagliBancaDto(BonificoExtraSepa.WithLinkedEntities entity);

    default DettagliBonificoExtraSepaBanca.DettagliCommissioni toDettagliCommissioni(BonificoExtraSepa.WithLinkedEntities entity) {
        return new DettagliBonificoExtraSepaBanca.DettagliCommissioni(
                entity.dettaglioBonificoBancaABanca.getEntity().regolamentoCommissioneBanca(),
                CommissioniBancaMapper.INSTANCE.toDto(entity.commissioneBanca)
        );
    }

    @Condition
    @Named("contieneNotifica")
    default boolean contieneNotifica(DettaglioBonificoBancaABanca.WithLinkedEntities dettaglioBonifico) {
        return dettaglioBonifico.getEntity().informazioniAggiuntiveNotifica() != null;
    }

    @Mapping(target = "notifica", source = "entity.informazioniAggiuntiveNotifica")
    DettagliNotifica toDettagliNotificaDto(DettaglioBonificoBancaABanca.WithLinkedEntities dettaglioBonifico);

}
