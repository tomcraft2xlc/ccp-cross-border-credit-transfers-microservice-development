package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer;

import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.*;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante.InfoOrdinante;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account.InformazioniRapportoBonificoExtraSepaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account_to_account.DettaglioBonificoAccountToAccountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account_to_account.InformazioniNdgMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.bank_to_bank.DettaglioBonificoBancaABancaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee.CommissioneAccountToAccountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.fee.CommissioniBancaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.intermediary.InformazioniIntermediarioMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.kind.SottoTipologiaBonificoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.party_identification.InformazioniAttoreMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamentoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information.RegulatoryReportingMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.related_remittance_information.RiferimentiAggiuntiviPagamentoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.remittance.InformazioniCausaleMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.settlement_system.SistemaDiRegolamentoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.UUID;

@Mapper(config = MappingCommonConfig.class, uses = { SistemaDiRegolamentoMapper.class,
        InformazioniAggiuntivePagamentoMapper.class,
        InformazioniCausaleMapper.class, DettaglioBonificoBancaABancaMapper.class,
        CommissioniBancaMapper.class, DettaglioBonificoAccountToAccountMapper.class,
        CommissioneAccountToAccountMapper.class, RegulatoryReportingMapper.class,
        RiferimentiAggiuntiviPagamentoMapper.class, InformazioniAttoreMapper.class,
        InformazioniIntermediarioMapper.class, InformazioniNdgMapper.class,
        InformazioniRapportoBonificoExtraSepaMapper.class, DocumentoDiCoperturaMapper.class }, imports = { UUID.class, CabelForwardedCredential.class,
                Utils.class })
public abstract class BonificoExtraSepaMapper {
    // This is the only way to inject in mapstruct on a abstract class
    @SuppressWarnings("java:S6813")
    @Inject
    protected SecurityIdentity securityIdentity;

    @SuppressWarnings("java:S6813")
    @Inject
    protected SottoTipologiaBonificoMapper sottoTipologiaBonificoMapper;

    /**
     * Mappa una richiesta di inserimento bonifico all'entità da inserire
     * 
     * @param richiesta              La richiesta raggiunta
     * @param canale                 Il canale di pagamento
     * @param sottoTipologiaBonifico La sotto-tipologia del bonifico
     * @param stato                  Lo stato iniziale del bonifico
     * @return L'entità da inserire
     */
    // Identificativi che verranno generati in seguito
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")

    // Popolato da processo di conferma
    @Mapping(target = "numeroTransazione", ignore = true)
    @Mapping(target = "tid", expression = "java(tid(richiesta))")
    @Mapping(target = "tidDocumentoCollegato", expression = "java(tidDocumentoCollegato(richiesta))")
    // Riempiti da altre entity
    @Mapping(target = "idCanale", source = "canale.id")
    @Mapping(target = "idSottoTipologiaBonifico", source = "sottoTipologiaBonifico.id")
    // Dati filiale
    @Mapping(target = "codiceFiliale", expression = "java( Long.valueOf(securityIdentity.getCredential(CabelForwardedCredential.class).mainBranch()) )")
    @Mapping(target = "denominazioneFiliale", ignore = true)
    // Utente
    @Mapping(target = "utente", expression = "java( estraiUtente(richiesta, canale, securityIdentity) )")
    // Banca di copertura
    @Mapping(target = "ibanContoBancaDiCopertura", source = "richiesta.dettagliBonifico.contoBancaDiCopertura.rapportoBanca")
    @Mapping(target = "divisaContoBancaDiCopertura", source = "richiesta.dettagliBonifico.contoBancaDiCopertura.divisa")
    @Mapping(target = "bicBancaDiCopertura", source = "richiesta.dettagliBonifico.contoBancaDiCopertura.bic")
    @Mapping(target = "intestazioneBancaDiCopertura", source = "richiesta.dettagliBonifico.contoBancaDiCopertura.intestazione")
    // Date
    @Mapping(target = "dataDiCreazione", source = "richiesta.dettagliBonifico.dettagliDate.dataCreazione")
    @Mapping(target = "dataDiEsecuzione", source = "richiesta.dettagliBonifico.dettagliDate.dataEsecuzione")
    @Mapping(target = "dataValutaOrdinante", source = "richiesta.dettagliBonifico.dettagliDate.dataValutaOrdinante")
    @Mapping(target = "dataRegolamentoBancaBeneficiario", source = "richiesta.dettagliBonifico.dettagliDate.dataRegolamentoBancaBeneficiario")
    // In gestione
    @Mapping(target = "inGestione", constant = "false")
    // Campo riempito da persistence
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "istanteCreazioneMessaggio", ignore = true)
    @DtoToBareEntity
    abstract BonificoExtraSepa bareFromDto(InserisciBonificoExtraSepaRichiesta richiesta, Canale canale,
            SottoTipologiaBonifico sottoTipologiaBonifico, CreditTransferStatus stato);

    protected LocalDate nowIfNull(LocalDate data) {
        if (data == null) {
            return LocalDate.now();
        }
        return data;
    }

    protected String tid(InserisciBonificoExtraSepaRichiesta richiesta) {
        if (richiesta.tid() != null) {
            return richiesta.tid();
        }
        return Utils.generateTID();
    }

    protected String tidDocumentoCollegato(InserisciBonificoExtraSepaRichiesta richiesta) {
        if (richiesta.documentoDiCopertura() == null) {
            return null;
        }
        if (richiesta.documentoDiCopertura().tid() != null) {
            return richiesta.documentoDiCopertura().tid();
        } else {
            return Utils.generateTID();
        }
    }

    /**
     * Estrapola l'utente dal canale di pagamento e dalla richiesta
     * 
     * @param richiesta        La richiesta raggiunta
     * @param canale           Il canale di pagamento
     * @param securityIdentity La security identity con le credenziali dell'utente
     * @return L'utente come stringa
     */
    protected String estraiUtente(InserisciBonificoExtraSepaRichiesta richiesta, Canale canale,
            SecurityIdentity securityIdentity) {
        if (canale == null) {
            return null;
        }
        if (Boolean.TRUE.equals(canale.utenteRichiesto())) {
            if (richiesta == null) {
                return null;
            }
            return richiesta.user();
        }
        return securityIdentity.getCredential(CabelForwardedCredential.class).profile();
    }

    /**
     * Mappatura comune ad entrambi i tipi di bonifici
     */
    // Riempiti sono nell'implementazione effettiva
    @Mapping(target = "dettaglioBonificoBancaABanca", ignore = true)
    @Mapping(target = "commissioneBanca", ignore = true)
    @Mapping(target = "dettaglioBonificoAccountToAccount", ignore = true)
    @Mapping(target = "commissioniAccountToAccount", ignore = true)
    // Dati tecnici non ottenuti dal DTO
    @Mapping(target = "sottoTipologiaBonifico", ignore = true)
    // Mapping comuni
    @Mapping(target = "informazioniRapportiBonificoExtraSepa", source = "richiesta", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniIntermediari", source = "richiesta", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniAttori", source = "richiesta", qualifiedBy = { DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "regulatoryReportings", source = "richiesta.riferimentiAggiuntivi.regulatoryReporting", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniAggiuntivePagamento", source = "richiesta.riferimentiAggiuntivi", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniAggiuntivePagamentoDocumentoCollegato", source = "richiesta.documentoDiCopertura.riferimentiAggiuntivi", qualifiedBy = {
            MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument.class
    })
    @Mapping(target = "riferimentiAggiuntiviPagamento", source = "richiesta.riferimentiAggiuntivi", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniCausale", source = "richiesta.dettagliCausale", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniCausaleDocumentoCollegato", source = "richiesta.documentoDiCopertura.causaleDescrittiva", qualifiedBy = {
            MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument.class
    })
    @Mapping(target = "informazioniSistemaDiRegolamento", source = "richiesta.sistemaDiRegolamento", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "informazioniSistemaDiRegolamentoDocumentoCollegato", source = "richiesta.documentoDiCopertura.infoSistemaDiRegolamento", qualifiedBy = {
            MappingCommonConfig.DtoToEntityWithLinkedEntitiesLinkedDocument.class })
    // Dati riempiti dal processo di verifica e conferma
    @Mapping(target = "datiVerificaBonifico", ignore = true)
    @Mapping(target = "datiConfermaBonifico", ignore = true)
    abstract void fillLinkedCommon(
            @MappingTarget BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext,
            InserisciBonificoExtraSepaRichiesta richiesta);

    @InheritConfiguration(name = "fillLinkedCommon")
    @Mapping(target = "dettaglioBonificoAccountToAccount", source = "richiesta", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "commissioniAccountToAccount", source = "richiesta.dettagliBonifico.dettagliCommissioni.commissioniCliente", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "dettaglioBonificoBancaABanca", ignore = true)
    @Mapping(target = "commissioneBanca", source = "richiesta.dettagliBonifico.dettagliCommissioni.commissioniBanca", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    abstract void fillLinkedAccountToAccount(
            @MappingTarget BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext,
            InserisciBonificoExtraSepaClienteRichiesta richiesta);

    @InheritConfiguration(name = "fillLinkedCommon")
    @Mapping(target = "dettaglioBonificoAccountToAccount", ignore = true)
    @Mapping(target = "commissioniAccountToAccount", ignore = true)
    @Mapping(target = "dettaglioBonificoBancaABanca", source = "richiesta", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    @Mapping(target = "commissioneBanca", source = "richiesta.dettagliBonifico.dettagliCommissioni.commissioniBanca", qualifiedBy = {
            DtoToEntityWithLinkedEntitiesMainDocument.class })
    abstract void fillLinkedBankToBank(
            @MappingTarget BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa,
            @Context BonificoExtraSepa.WithLinkedEntities bonificoExtraSepaContext,
            InserisciBonificoExtraSepaBancaRichiesta richiesta);

    public BonificoExtraSepa.WithLinkedEntities fromDto(
            InserisciBonificoExtraSepaRichiesta richiesta, Canale canale,
            SottoTipologiaBonifico sottoTipologiaBonifico, CreditTransferStatus stato) {
        if (richiesta == null) {
            return null;
        }
        if (richiesta instanceof InserisciBonificoExtraSepaBancaRichiesta richiestaBanca) {
            return fromDtoBankToBank(richiestaBanca, canale, sottoTipologiaBonifico, stato);
        }
        if (richiesta instanceof InserisciBonificoExtraSepaClienteRichiesta richiestaCliente) {
            return fromDtoAccountToAccount(richiestaCliente, canale, sottoTipologiaBonifico, stato);
        }
        throw new RuntimeException(
                String.format("Mapping of request of class %s is not defined", richiesta.getClass().getName()));
    }

    /**
     * Mappa una richiesta di inserimento bonifico account ad account all'entità da
     * inserire,
     * includendo tutte le entità collegate.
     * 
     * @param richiesta              La richiesta raggiunta
     * @param canale                 Il canale di pagamento
     * @param sottoTipologiaBonifico La sotto-tipologia del bonifico
     * @param stato                  Lo stato iniziale del bonifico
     * @return L'entità da inserire
     */
    @DtoToEntityWithLinkedEntitiesMainDocument
    public BonificoExtraSepa.WithLinkedEntities fromDtoAccountToAccount(
            InserisciBonificoExtraSepaClienteRichiesta richiesta, Canale canale,
            SottoTipologiaBonifico sottoTipologiaBonifico, CreditTransferStatus stato) {
        var entity = bareFromDto(richiesta, canale, sottoTipologiaBonifico, stato).withLinkedEntities();
        fillLinkedAccountToAccount(entity, entity, richiesta);
        return entity;
    }

    /**
     * Mappa una richiesta di inserimento bonifico banca a banca all'entità da
     * inserire, includendo
     * tutte le entità collegate.
     * 
     * @param richiesta              La richiesta raggiunta
     * @param canale                 Il canale di pagamento
     * @param sottoTipologiaBonifico La sotto-tipologia del bonifico
     * @param stato                  Lo stato iniziale del bonifico
     * @return L'entità da inserire
     */
    @DtoToEntityWithLinkedEntitiesMainDocument
    public BonificoExtraSepa.WithLinkedEntities fromDtoBankToBank(
            InserisciBonificoExtraSepaBancaRichiesta richiesta, Canale canale,
            SottoTipologiaBonifico sottoTipologiaBonifico, CreditTransferStatus stato) {
        var entity = bareFromDto(richiesta, canale, sottoTipologiaBonifico, stato).withLinkedEntities();
        fillLinkedBankToBank(entity, entity, richiesta);
        return entity;
    }

    @Mapping(target = "infoAttore", source = "informazioniAttori", qualifiedByName = "toOrdinanteDto")
    @Mapping(target = "presentatore", source = "dettaglioBonificoAccountToAccount.informazioniNdg", qualifiedByName = "toPresentatoreDto")
    @Mapping(target = "titolareEffettivo", source = "dettaglioBonificoAccountToAccount.informazioniNdg", qualifiedByName = "toTitolareEffettivoDto")
    abstract InfoOrdinante toInfoOrdinante(BonificoExtraSepa.WithLinkedEntities entity);

    @Mapping(target = "sottoTipologiaBonifico.tipo", source = "sottoTipologiaBonifico.nome")
    @Mapping(target = "sottoTipologiaBonifico.descrizione", source = "sottoTipologiaBonifico.descrizione")
    @Mapping(target = "sottoTipologiaBonifico.conNotifica", source = "sottoTipologiaBonifico.conNotifica")
    @Mapping(target = "sottoTipologiaBonifico.bancaABanca", source = "sottoTipologiaBonifico.bancaABanca")
    @Mapping(target = "sistemaDiRegolamento", source = "entity.informazioniSistemaDiRegolamento")
    @Mapping(target = ".", source = "entity.entity")
    @Mapping(target = "ordinante", source = "entity")
    @Mapping(target = "soggettoIstruttore", source = "entity.informazioniAttori", qualifiedByName = "toSoggettoIstruttoreDto")
    @Mapping(target = "debitoreEffettivo", source = "entity.informazioniAttori", qualifiedByName = "toOrdinanteEffettivoDto")
    @Mapping(target = "beneficiario", source = "entity.informazioniAttori", qualifiedByName = "toBeneficiarioDto")
    @Mapping(target = "beneficiarioEffettivo", source = "entity.informazioniAttori", qualifiedByName = "toBeneficiarioEffettivoDto")
    @Mapping(target = "bancaOrdinante", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDellOrdinanteDto")
    @Mapping(target = "bancaDestinataria", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDestinatariaDto")
    @Mapping(target = "bancaDelBeneficiario", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDelBeneficiarioDto")
    @Mapping(target = "altriIntermediari", source = "entity")
    @Mapping(target = "dettagliBonifico", source = "entity")
    @Mapping(target = "dettagliCausale", source = "entity")
    @Mapping(target = "riferimentiAggiuntivi", source = "entity")
    @Mapping(target = "documentoDiCopertura", source = "entity")
    @Mapping(target = "user", source = "entity.entity.utente")
    abstract DatiBonificoExtraSepaClienteRisposta toDatiClienteDto(
            BonificoExtraSepa.WithLinkedEntities entity,
            SottoTipologiaBonifico sottoTipologiaBonifico);

    public DatiBonificoExtraSepaClienteRisposta toDatiClienteDtoWrapper(
            BonificoExtraSepa.WithLinkedEntities entity,
            @Context SottoTipologiaBonifico sottoTipologiaBonifico) {
        return toDatiClienteDto(entity, sottoTipologiaBonifico);
    }

    @Mapping(target = "sottoTipologiaBonifico.tipo", source = "sottoTipologiaBonifico.nome")
    @Mapping(target = "sottoTipologiaBonifico.descrizione", source = "sottoTipologiaBonifico.descrizione")
    @Mapping(target = "sottoTipologiaBonifico.conNotifica", source = "sottoTipologiaBonifico.conNotifica")
    @Mapping(target = "sistemaDiRegolamento", source = "entity.informazioniSistemaDiRegolamento")
    @Mapping(target = ".", source = "entity.entity")
    @Mapping(target = "ordinante", source = "entity.informazioniIntermediari", qualifiedByName = "toOrdinanteDto")
    @Mapping(target = "bancaOrdinante", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDellOrdinanteDto")
    @Mapping(target = "bancaDestinataria", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDestinatariaDto")
    @Mapping(target = "bancaDelBeneficiario", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaDelBeneficiarioDto")
    @Mapping(target = "beneficiario", source = "entity.informazioniIntermediari", qualifiedByName = "toBancaBeneficiariaDto")
    @Mapping(target = "altriIntermediari", source = "entity")
    @Mapping(target = "dettagliBonifico", source = "entity")
    @Mapping(target = "dettagliCausale", source = "entity")
    @Mapping(target = "riferimentiAggiuntivi", source = "entity")
    @Mapping(target = "user", source = "entity.entity.utente")
    @Mapping(target = "documentoDiCopertura", source = "entity")
    abstract DatiBonificoExtraSepaBancaRisposta toDatiBancaDto(
            BonificoExtraSepa.WithLinkedEntities entity,
            SottoTipologiaBonifico sottoTipologiaBonifico);

    public DatiBonificoExtraSepaBancaRisposta toDatiBancaDtoWrapper(
            BonificoExtraSepa.WithLinkedEntities entity,
            @Context SottoTipologiaBonifico sottoTipologiaBonifico) {
        return toDatiBancaDto(entity, sottoTipologiaBonifico);
    }

    @Mapping(target = "id", source = "entity.entity.id")
    @Mapping(target = "tid", source = "entity.entity.tid")
    @Mapping(target = "identificativoFlusso", source = "entity.entity.tid")
    @Mapping(target = "idTransazione", ignore = true)
    @Mapping(target = "idMessaggio", source = ".", qualifiedByName = "idMessaggio")
    @Mapping(target = "identificativoDiDefinizioneDelMessaggio", source = ".", qualifiedByName = "identificativoDiDefinizioneDelMessaggio")
    @Mapping(target = "servizioDiBusiness", source = ".", qualifiedByName = "servizioDiBusiness")
    @Mapping(target = "numeroTransazione", source = "entity.numeroTransazione")
    @Mapping(target = "numeroDiTransazioni", constant = "1L")
    @Mapping(target = "idSistemaDiClearing", ignore = true)
    @Mapping(target = "canaleDiClearing", ignore = true)
    @Mapping(target = "bonificoCliente", source = ".", defaultExpression = "java( null )", conditionExpression = "java( !sottoTipologiaBonifico.bancaABanca() )")
    @Mapping(target = "bonificoBanca", source = ".", defaultExpression = "java( null )", conditionExpression = "java( sottoTipologiaBonifico.bancaABanca() )")
    @Mapping(target = "infoStato.stato", source = "entity.stato")
    public abstract BonificoExtraSepaRisposta toInserisciDto(
            BonificoExtraSepa.WithLinkedEntities entity,
            @Context SottoTipologiaBonifico sottoTipologiaBonifico);

    @Named("idMessaggio")
    String idMessagggio(BonificoExtraSepa.WithLinkedEntities entity,
                        @Context SottoTipologiaBonifico sottoTipologiaBonifico) {
        if (entity.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return "NONREF";
        }

        return entity.getEntity().tid();
    }

    @Named("identificativoDiDefinizioneDelMessaggio")
    String identificativoDiDefinizioneDelMessaggio(BonificoExtraSepa.WithLinkedEntities entity,
                                                   @Context SottoTipologiaBonifico sottoTipologiaBonifico) {
        if (sottoTipologiaBonifico.bancaABanca()) {
            if (entity.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
                return "pacs.009.001.08CORE";
            }
            return "pacs.009.001.08";
        }
        return "pacs.008.001.08";
    }

    @Named("servizioDiBusiness")
    String servizioDiBusiness(BonificoExtraSepa.WithLinkedEntities entity,
                              @Context SottoTipologiaBonifico sottoTipologiaBonifico) {
        if (entity.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return null;
        }
        if (Boolean.TRUE.equals(entity.informazioniSistemaDiRegolamento.getEntity().stp())) {
            return "swift.cbprplus.stp.03";
        }
        return "swift.cbprplus.03";
    }

    @Mapping(target = "stato", source = "stato")
    public abstract BonificoExtraSepa changeStatus(
            BonificoExtraSepa initial,
            CreditTransferStatus stato);

    @Mapping(target = "dataRegolamentoBancaBeneficiario", source = "dataRegolamentoBancaBeneficiario")
    public abstract BonificoExtraSepa changeData(
            BonificoExtraSepa initial,
            LocalDate dataRegolamentoBancaBeneficiario);
}
