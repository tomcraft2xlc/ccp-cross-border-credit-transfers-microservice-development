package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information;

import java.util.UUID;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import jakarta.inject.Inject;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class }, uses = { RegulatoryReportingMapper.class, RiferimentiAggiuntiviPagamentoMapper.class})
public abstract class  InformazioniAggiuntivePagamentoMapper {
    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected RiferimentiAggiuntiviPagamentoMapper riferimentiAggiuntiviPagamentoMapper;

    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idBonificoExtraSepa", source = "bonificoExtraSepa.entity.id")
    @Mapping(target = "codiceIstruzioneBancaDelBeneficiario1", source = "riferimentiAggiuntivi.istruzioneBancaDelBeneficiario1.codice")
    @Mapping(target = "istruzioneBancaDelBeneficiario1", source = "riferimentiAggiuntivi.istruzioneBancaDelBeneficiario1.istruzioni")
    @Mapping(target = "codiceIstruzioneBancaDelBeneficiario2", source = "riferimentiAggiuntivi.istruzioneBancaDelBeneficiario2.codice")
    @Mapping(target = "istruzioneBancaDelBeneficiario2", source = "riferimentiAggiuntivi.istruzioneBancaDelBeneficiario2.istruzioni")
    @DtoToBareEntity
    abstract InformazioniAggiuntivePagamento bareFromDto(RiferimentiAggiuntivi riferimentiAggiuntivi,
        BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa, Boolean informazioniDocumentoCollegato);

    abstract void fillLinked(@MappingTarget InformazioniAggiuntivePagamento.WithLinkedEntities informazioniAggiuntivePagamento,
            BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            RiferimentiAggiuntivi riferimentiAggiuntivi);

    @DtoToEntityWithLinkedEntitiesMainDocument
    public InformazioniAggiuntivePagamento.WithLinkedEntities fromDto(RiferimentiAggiuntivi riferimentiAggiuntivi,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(riferimentiAggiuntivi, bonificoExtraSepa, false).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, riferimentiAggiuntivi);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesLinkedDocument
    public InformazioniAggiuntivePagamento.WithLinkedEntities fromDtoRelated(RiferimentiAggiuntivi riferimentiAggiuntivi,
        @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        var entity = bareFromDto(riferimentiAggiuntivi, bonificoExtraSepa, true).withLinkedEntities();
        fillLinked(entity, bonificoExtraSepa, riferimentiAggiuntivi);
        return entity;
    }

    @Mapping(target = ".", source = "bonificoExtraSepa.informazioniAggiuntivePagamento.entity")
    @Mapping(target = "istruzioneBancaDelBeneficiario1.codice", source = "bonificoExtraSepa.informazioniAggiuntivePagamento.entity.codiceIstruzioneBancaDelBeneficiario1")
    @Mapping(target = "istruzioneBancaDelBeneficiario1.istruzioni", source = "bonificoExtraSepa.informazioniAggiuntivePagamento.entity.istruzioneBancaDelBeneficiario1")
    @Mapping(target = "istruzioneBancaDelBeneficiario2.codice", source = "bonificoExtraSepa.informazioniAggiuntivePagamento.entity.codiceIstruzioneBancaDelBeneficiario2")
    @Mapping(target = "istruzioneBancaDelBeneficiario2.istruzioni", source = "bonificoExtraSepa.informazioniAggiuntivePagamento.entity.istruzioneBancaDelBeneficiario2")
    @Mapping(target = "altroIdentificativoPagamento", source = "bonificoExtraSepa.dettaglioBonificoAccountToAccount.entity.altroIdentificativoPagamento")
    @Mapping(target = "regulatoryReporting", source = "bonificoExtraSepa.regulatoryReportings")
    protected abstract RiferimentiAggiuntivi toDto(BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa, RiferimentiAggiuntiviPagamentoMapper.DettagliRiferimentiAggiuntiviPartialDto dettagliRiferimentiAggiuntiviPartialDto);

    public RiferimentiAggiuntivi toDto(BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa) {
        return toDto(bonificoExtraSepa, riferimentiAggiuntiviPagamentoMapper.toPartialDto(bonificoExtraSepa.riferimentiAggiuntiviPagamento));
    }

    @Mapping(target = ".", source = "informazioniAggiuntivePagamento.entity")
    @Mapping(target = "istruzioneBancaDelBeneficiario1.codice", source = "entity.codiceIstruzioneBancaDelBeneficiario1")
    @Mapping(target = "istruzioneBancaDelBeneficiario1.istruzioni", source = "entity.istruzioneBancaDelBeneficiario1")
    @Mapping(target = "istruzioneBancaDelBeneficiario2.codice", source = "entity.codiceIstruzioneBancaDelBeneficiario2")
    @Mapping(target = "istruzioneBancaDelBeneficiario2.istruzioni", source = "entity.istruzioneBancaDelBeneficiario2")
    @Mapping(target = "altroIdentificativoPagamento", ignore = true)
    @Mapping(target = "riferimentiAggiuntivi1", ignore = true)
    @Mapping(target = "riferimentiAggiuntivi2", ignore = true)
    @Mapping(target = "regulatoryReporting", ignore = true)
    public abstract RiferimentiAggiuntivi toDto(InformazioniAggiuntivePagamento.WithLinkedEntities informazioniAggiuntivePagamento);
}
