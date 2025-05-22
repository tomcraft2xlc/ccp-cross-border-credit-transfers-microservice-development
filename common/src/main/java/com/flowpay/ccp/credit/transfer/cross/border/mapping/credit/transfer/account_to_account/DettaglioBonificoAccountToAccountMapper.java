package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account_to_account;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaClienteRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliImporto;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee.CommissioneAccountToAccountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee.CommissioniBancaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.DettaglioBonificoAccountToAccount;

@Mapper(config = MappingCommonConfig.class,
        imports = { UUID.class, CommissioneAccountToAccountMapper.class },
        uses = { InformazioniNdgMapper.class, CommissioniBancaMapper.class })
public interface DettaglioBonificoAccountToAccountMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = ".", source = "richiesta.dettagliBonifico.dettagliImporto")
    @Mapping(target = "valoreCambio", source = "richiesta.dettagliBonifico.dettagliImporto.cambio")
    @Mapping(target = "codiceCausaleTransazione", source = "richiesta.dettagliCausale.codiceCausaleTransazione")
    @Mapping(target = "altroIdentificativoPagamento", source = "richiesta.riferimentiAggiuntivi.altroIdentificativoPagamento")
    @Mapping(target = "importoDiAddebito", source = "richiesta.dettagliBonifico.dettagliImporto", qualifiedByName = "calcolaImportoDiAddebito")
    @Mapping(target = "regolamentoCommissioneClientela", source = "richiesta.dettagliBonifico.dettagliCommissioni.regolamentoClientela")
    @Mapping(target = "tipologiaCommissioni", source = "richiesta.dettagliBonifico.dettagliCommissioni.tipologiaCommissioni")
    @Mapping(target = "regolamentoCommissioneBanca", source = "richiesta.dettagliBonifico.dettagliCommissioni.regolamentoBanca")
    @DtoToBareEntity
    DettaglioBonificoAccountToAccount bareFromDto(InserisciBonificoExtraSepaClienteRichiesta richiesta,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    @Mapping(target = "informazioniNdg", source = "richiesta.ordinante", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class
    })
    void fillLinked(@MappingTarget DettaglioBonificoAccountToAccount.WithLinkedEntities informazioniAggiuntivePagamento,
            @Context DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            InserisciBonificoExtraSepaClienteRichiesta richiesta);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default DettaglioBonificoAccountToAccount.WithLinkedEntities fromDto(
            InserisciBonificoExtraSepaClienteRichiesta richiesta,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(richiesta, bonificoExtraSepa).withLinkedEntities();
        fillLinked(entity, entity, bonificoExtraSepa, richiesta);
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

    @Mapping(target = "dettagliImporto.divisa", source = "dettaglioBonificoAccountToAccount.entity.divisa")
    @Mapping(target = "dettagliImporto.importo", source = "dettaglioBonificoAccountToAccount.entity.importo")
    @Mapping(target = "dettagliImporto.cambio", source = "dettaglioBonificoAccountToAccount.entity.valoreCambio")
    @Mapping(target = "dettagliDate.dataCreazione", source = "entity.dataDiCreazione")
    @Mapping(target = "dettagliDate.dataEsecuzione", source = "entity.dataDiEsecuzione")
    @Mapping(target = "dettagliDate.dataValutaOrdinante", source = "entity.dataValutaOrdinante")
    @Mapping(target = "dettagliDate.dataRegolamentoBancaBeneficiario", source = "entity.dataRegolamentoBancaBeneficiario")
    @Mapping(target = "contoBancaDiCopertura.rapportoBanca", source = "entity.ibanContoBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.divisa", source = "entity.divisaContoBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.bic", source = "entity.bicBancaDiCopertura")
    @Mapping(target = "contoBancaDiCopertura.intestazione", source = "entity.intestazioneBancaDiCopertura")
    @Mapping(target = "dettagliCommissioni", source = ".")
    DettagliBonificoExtraSepaCliente toDettagliClienteDto(
            BonificoExtraSepa.WithLinkedEntities entity);

    default DettagliBonificoExtraSepaCliente.DettagliCommissioni toDettaglioCommissioni(BonificoExtraSepa.WithLinkedEntities entity) {
        return new DettagliBonificoExtraSepaCliente.DettagliCommissioni(
                entity.dettaglioBonificoAccountToAccount.getEntity().regolamentoCommissioneClientela(),
                entity.dettaglioBonificoAccountToAccount.getEntity().regolamentoCommissioneBanca(),
                entity.dettaglioBonificoAccountToAccount.getEntity().tipologiaCommissioni(),
                Optional.ofNullable(entity.commissioniAccountToAccount).map(Collection::stream).map(stream -> stream.map(CommissioneAccountToAccountMapper.INSTANCE::toDto)).map(Stream::toList).orElse(null),
                CommissioniBancaMapper.INSTANCE.toDto(entity.commissioneBanca)
        );
    }

}
