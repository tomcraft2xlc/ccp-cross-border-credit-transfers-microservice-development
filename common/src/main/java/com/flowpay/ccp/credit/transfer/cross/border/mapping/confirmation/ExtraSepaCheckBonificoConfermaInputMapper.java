package com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaEmbargo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapportoBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.InformazioniNdg;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.TipoNdg;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.CommissioneAccountToAccount;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.FlagSiNo;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaInput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.SistemaRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaInput.Commissione;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaInput.RegolamentoCommissioniValuta;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.ExtraSepaCheckBonificoConfermaInput.TipoSpese;

@Mapper(config = MappingCommonConfig.class, uses = { SistemaRegolamento.class, FlagSiNo.class,
        RegolamentoCommissioniValuta.class,
        TipoSpese.class })
public interface ExtraSepaCheckBonificoConfermaInputMapper {

    @Mapping(target = "tipoRichiesta", constant = "OUT")
    @Mapping(target = "tidBonifico", source = "bonifico.entity.tid")
    @Mapping(target = "rapportoAvere", source = "bonifico", qualifiedByName = "rapportoBancaDiCopertura")
    @Mapping(target = "dataCreazione", source = "bonifico.entity.dataDiCreazione")
    @Mapping(target = "dataEsecuzione", source = "bonifico.entity.dataDiEsecuzione")
    @Mapping(target = "dataValutaAvere", source = "bonifico.entity.dataRegolamentoBancaBeneficiario")
    @Mapping(target = "dataValutaDare", source = "bonifico.entity.dataValutaOrdinante")
    @Mapping(target = "sistemaRegolamento", source = "bonifico.entity.sistemaDiRegolamento")
    @Mapping(target = "flagForzaturaEmbargo", source = "bonifico", qualifiedByName = "forzaturaEmbargo")
    @Mapping(target = "commissioneBanca", source = "bonifico.commissioneBanca.entity")
    @Mapping(target = "bancaDestinatario", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancaDestinataria")
    @Mapping(target = "ordinante.banca", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancaDellOrdinante")
    @Mapping(target = "bancaCopertura", source = "bonifico", qualifiedByName = "bancaDiCopertura")
    @Mapping(target = "riferimentoCorrispondeneMittenteRapporto", source = "bonifico.informazioniRapportiBonificoExtraSepa", qualifiedByName = "riferimentoCorrispondenteMittenteRapporto")
    @Mapping(target = "riferimentoCorrispondeneMittenteIntestazione", source = "bonifico.informazioniRapportiBonificoExtraSepa", qualifiedByName = "riferimentoCorrispondeneMittenteIntestazione")
    @Mapping(target = "corrispettivoMittente", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancaCorrispondenteMittente")
    @Mapping(target = "corrispettivoRicevente", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancaCorrispondenteRicevente")
    @Mapping(target = "istitutoTerzoRimborso", source = "bonifico.informazioniIntermediari", qualifiedByName = "istitutoTerzoDiRimborso")
    @Mapping(target = "istruttrici", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancheIstruttrici")
    @Mapping(target = "intermediarie", source = "bonifico.informazioniIntermediari", qualifiedByName = "bancheIntermediarie")
    @Mapping(target = "causaleDescrittiva", source = "bonifico.informazioniCausale", qualifiedByName = "causaleDescrittiva")
    @Mapping(target = "istruzioniPagamentoBancaRicevente", source = "bonifico.informazioniAggiuntivePagamento.entity", qualifiedByName = "istruzioniPagamentoBancaRicevente")
    @Mapping(target = "istruzioniPagamentoBancaBeneficiario", source = "bonifico.informazioniAggiuntivePagamento.entity", qualifiedByName = "istruzioniPagamentoBancaBeneficiario")
    @Mapping(target = "importoTotaleSpeseIn", ignore = true)
    @interface Commons {
    }

    @Commons
    @Mapping(target = "tipoMessaggio", constant = "PACS008")
    @Mapping(target = "rapportoDare", source = "bonifico", qualifiedByName = "pacs008RapportoDare")
    @Mapping(target = "importoBonifico", source = "bonifico.dettaglioBonificoAccountToAccount.entity.importo")
    @Mapping(target = "divisaBonifico", source = "bonifico.dettaglioBonificoAccountToAccount.entity.divisa")
    @Mapping(target = "valoreCambio", source = "bonifico.dettaglioBonificoAccountToAccount.entity.valoreCambio")
    @Mapping(target = "controvaloreImporto", source = "bonifico.dettaglioBonificoAccountToAccount.entity.importoDiAddebito")
    @Mapping(target = "stp", source = "bonifico.informazioniSistemaDiRegolamento.entity.stp")
    @Mapping(target = "regolamentoCommissioniClienteCc", source = "bonifico.dettaglioBonificoAccountToAccount.entity.regolamentoCommissioneClientela")
    @Mapping(target = "regolamentoCommissioniBancaCc", source = "bonifico.dettaglioBonificoAccountToAccount.entity.regolamentoCommissioneBanca")
    @Mapping(target = "causaleBonifico", source = "bonifico.dettaglioBonificoAccountToAccount.entity.codiceCausaleTransazione")
    @Mapping(target = "tipoSpese", source = "bonifico.dettaglioBonificoAccountToAccount.entity.tipologiaCommissioni")
    @Mapping(target = "paeseBonifico", source = "bonifico", qualifiedByName = "pacs008PaeseBonifico")
    @Mapping(target = "ndgPresentatori", source = "bonifico.dettaglioBonificoAccountToAccount.informazioniNdg", qualifiedByName = "ndgPresentatori")
    @Mapping(target = "ndgTitolariEffettivi", source = "bonifico.dettaglioBonificoAccountToAccount.informazioniNdg", qualifiedByName = "ndgTitolariEffettivi")
    @Mapping(target = "commissioniCliente", source = "bonifico.commissioniAccountToAccount")
    @Mapping(target = "beneficiario", source = "bonifico", qualifiedByName = "pacs008Beneficiario")
    public abstract ExtraSepaCheckBonificoConfermaInput pacs008(
            BonificoExtraSepa.WithLinkedEntities bonifico,
            BanksConfig.BankConfig bancaMittente);


    @Commons
    @Mapping(target = "tipoMessaggio", constant = "PACS009")
    @Mapping(target = "rapportoDare", source = "bonifico", qualifiedByName = "pacs009RapportoDare")
    @Mapping(target = "importoBonifico", source = "bonifico.dettaglioBonificoBancaABanca.entity.importo")
    @Mapping(target = "divisaBonifico", source = "bonifico.dettaglioBonificoBancaABanca.entity.divisa")
    @Mapping(target = "valoreCambio", source = "bonifico.dettaglioBonificoBancaABanca.entity.cambio")
    @Mapping(target = "controvaloreImporto", source = "bonifico.dettaglioBonificoBancaABanca.entity.importoDiAddebito")
    @Mapping(target = "stp", ignore = true)
    @Mapping(target = "regolamentoCommissioniClienteCc", ignore = true)
    @Mapping(target = "regolamentoCommissioniBancaCc", source = "bonifico.dettaglioBonificoBancaABanca.entity.regolamentoCommissioneBanca")
    @Mapping(target = "causaleBonifico", source = "bonifico.dettaglioBonificoBancaABanca.entity.codiceCausaleTransazione")
    @Mapping(target = "tipoSpese", ignore = true)
    @Mapping(target = "paeseBonifico", source = "bonifico", qualifiedByName = "pacs009PaeseBonifico")
    @Mapping(target = "ndgPresentatori", ignore = true)
    @Mapping(target = "ndgTitolariEffettivi", ignore = true)
    @Mapping(target = "commissioniCliente", ignore = true)
    @Mapping(target = "beneficiario", source = "bonifico", qualifiedByName = "pacs009Beneficiario")
    public abstract ExtraSepaCheckBonificoConfermaInput pacs009(
            BonificoExtraSepa.WithLinkedEntities bonifico,
            BanksConfig.BankConfig bancaMittente);

    // Rapporti

    @Named("rapportoBancaDiCopertura")
    @Mapping(target = "numero", source = "entity.ibanContoBancaDiCopertura")
    @Mapping(target = "voceContabile", ignore = true)
    @Mapping(target = "divisa", ignore = true)
    ExtraSepaCheckBonificoConfermaInput.Rapporto rapportoBancaDiCopertura(
            BonificoExtraSepa.WithLinkedEntities bonifico);

    @Named("forzaturaEmbargo")
    default FlagSiNo forzaturaEmbargo(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (bonifico.datiVerificaBonifico.getEntity().statoVerificaEmbargo().equals(StatoVerificaEmbargo.NECESSITA_FORZATURA_EMBARGO_PARZIALE)) {
            return FlagSiNo.SI;
        }
        if (bonifico.datiConfermaBonifico.getEntity().statoConfermaEmbargo().equals(StatoConfermaEmbargo.NECESSITA_FORZATURA_EMBARGO_PARZIALE)) {
            return FlagSiNo.SI;
        }
        return FlagSiNo.NO;
    }

    @Named("pacs008RapportoDare")
    default ExtraSepaCheckBonificoConfermaInput.Rapporto pacs008RapportoDare(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        final var rapporto = bonifico.informazioniAttori.stream()
                .filter(attore -> attore.getEntity().tipo().equals(TipoAttore.ORDINANTE)).findAny()
                .orElseThrow().informazioniRapporto.getEntity();
        return rapporto(rapporto);
    }

    @Named("pacs009RapportoDare")
    default ExtraSepaCheckBonificoConfermaInput.Rapporto pacs009RapportoDare(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        final var rapporto = bonifico.informazioniIntermediari.stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                        .equals(TipoIntermediario.ORDINANTE))
                .findAny()
                .orElseThrow().informazioniRapporto.getEntity();
        return rapporto(rapporto);
    }

    default ExtraSepaCheckBonificoConfermaInput.Rapporto rapporto(final InformazioniRapporto rapporto) {
        final boolean isSottoConto = rapporto.tipoRapporto().equals(TipoRapporto.SOTTO_CONTO);
        return new ExtraSepaCheckBonificoConfermaInput.Rapporto(
                rapporto.numero(),
                isSottoConto ? Integer.parseInt(rapporto.altroID()) : null,
                isSottoConto ? rapporto.divisa() : null);
    }

    @Named("pacs008PaeseBonifico")
    default String pacs008PaeseBonifico(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        return bonifico.informazioniIntermediari.stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                        .equals(TipoIntermediario.BANCA_DEL_BENEFICIARIO))
                .findAny()
                .orElseThrow().indirizzoPostale.getEntity().paese();
    }

    @Named("pacs009PaeseBonifico")
    default String pacs009PaeseBonifico(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        return bonifico.informazioniIntermediari.stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                        .equals(TipoIntermediario.BANCA_BENEFICIARIA))
                .findAny()
                .orElseThrow().indirizzoPostale.getEntity().paese();
    }

    @Named("ndgPresentatori")
    default List<String> ndgPresentatori(final Collection<InformazioniNdg.WithLinkedEntities> ndg) {
        return ndgWithType(ndg, TipoNdg.PRESENTATORE);
    }

    @Named("ndgTitolariEffettivi")
    default List<String> ndgTitolariEffettivi(final Collection<InformazioniNdg.WithLinkedEntities> ndg) {
        return ndgWithType(ndg, TipoNdg.TITOLARE_EFFETTIVO);
    }

    default List<String> ndgWithType(final Collection<InformazioniNdg.WithLinkedEntities> ndg, final TipoNdg tipo) {
        if (ndg == null || ndg.isEmpty()) {
            return List.of();
        }
        return ndg.stream().filter(infoNdg -> infoNdg.getEntity().tipo().equals(tipo))
                .map(infoNdg -> infoNdg.getEntity().ndg()).toList();
    }

    @Mapping(target = ".", source = "entity")
    Commissione commissione(final CommissioneAccountToAccount.WithLinkedEntities commissione);

    @Named("pacs008Beneficiario")
    default ExtraSepaCheckBonificoConfermaInput.Anagrafica pacs008Beneficiario(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        final var beneficiario = bonifico.informazioniAttori.stream()
                .filter(attore -> attore.getEntity().tipo().equals(TipoAttore.BENEFICIARIO)).findAny()
                .orElseThrow();
        return anagrafica(beneficiario);
    }

    @Named("pacs009Beneficiario")
    default ExtraSepaCheckBonificoConfermaInput.Anagrafica pacs009Beneficiario(
            final BonificoExtraSepa.WithLinkedEntities bonifico) {
        final var beneficiario = bonifico.informazioniIntermediari.stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario()
                        .equals(TipoIntermediario.BANCA_BENEFICIARIA))
                .findAny()
                .orElseThrow();
        return anagrafica(beneficiario);
    }

    @Mapping(target = "rapporto", source = "informazioniRapporto.entity.numero")
    @Mapping(target = "intestazione", source = "entity.intestazione")
    @Mapping(target = ".", source = "indirizzoPostale.entity")
    @Mapping(target = "codicePostale", source = "indirizzoPostale.entity.cap")
    @Mapping(target = "banca", source = "bonificoExtraSepa.informazioniIntermediari", qualifiedByName = "bancaDelBeneficiario")
    @interface AnagraficaCommons {
    }

    @AnagraficaCommons
    @Mapping(target = "bic", ignore = true)
    ExtraSepaCheckBonificoConfermaInput.Anagrafica anagrafica(InformazioniAttore.WithLinkedEntities beneficiario);

    @AnagraficaCommons
    @Mapping(target = "bic", source = "entity.bic")
    ExtraSepaCheckBonificoConfermaInput.Anagrafica anagrafica(
            InformazioniIntermediario.WithLinkedEntities beneficiario);

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "codiceLei", source = "entity.codiceLEI")
    @Mapping(target = "rapporto", source = "informazioniRapporto.entity.numero")
    ExtraSepaCheckBonificoConfermaInput.Banca banca(
            InformazioniIntermediario.WithLinkedEntities intermediario);

    default ExtraSepaCheckBonificoConfermaInput.Banca banca(
            final BanksConfig.BankConfig config) {
                return new ExtraSepaCheckBonificoConfermaInput.Banca(
                    config.bic(),
                    config.name(),
                    null,
                    config.lei()
                );
            }

    @Named("bancaDelBeneficiario")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaDelBeneficiario(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_DEL_BENEFICIARIO);
    }

    @Named("bancaDellOrdinante")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaDellOrdinante(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_DELL_ORDINANTE);
    }

    @Named("bancaBeneficiaria")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaBeneficiaria(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_BENEFICIARIA);
    }

    @Named("bancaDestinataria")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaDestinataria(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_DESTINATARIA);
    }

    @Named("bancheIstruttrici")
    default List<ExtraSepaCheckBonificoConfermaInput.Banca> bancheIstruttrici(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return Stream.of(
                banca(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_1),
                banca(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_2),
                banca(intermediari, TipoIntermediario.BANCA_ISTRUTTRICE_3)).filter(Objects::nonNull).toList();
    }

    @Named("bancheIntermediarie")
    default List<ExtraSepaCheckBonificoConfermaInput.Banca> bancheIntermediarie(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return Stream.of(
                banca(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_1),
                banca(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_2),
                banca(intermediari, TipoIntermediario.BANCA_INTERMEDIARIA_3)).filter(Objects::nonNull).toList();
    }

    @Named("bancaCorrispondenteMittente")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaCorrispondenteMittente(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE);
    }

    @Named("bancaCorrispondenteRicevente")
    default ExtraSepaCheckBonificoConfermaInput.Banca bancaCorrispondenteRicevente(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.BANCA_CORRISPONDENTE_RICEVENTE);
    }

    @Named("istitutoTerzoDiRimborso")
    default ExtraSepaCheckBonificoConfermaInput.Banca istitutoTerzoDiRimborso(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari) {
        return banca(intermediari, TipoIntermediario.ISTITUTO_TERZO_DI_RIMBORSO);
    }

    @Named("bancaDiCopertura")
    @Mapping(target = "bic", source = "entity.bicBancaDiCopertura")
    @Mapping(target = "intestazione", source = "entity.intestazioneBancaDiCopertura")
    @Mapping(target = "rapporto", source = "entity.ibanContoBancaDiCopertura")
    @Mapping(target = "codiceLei", ignore = true)
    ExtraSepaCheckBonificoConfermaInput.Banca bancaDiCopertura(
            final BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa);

    private ExtraSepaCheckBonificoConfermaInput.Banca banca(
            final Collection<InformazioniIntermediario.WithLinkedEntities> intermediari,
            final TipoIntermediario tipo) {
        return intermediari.stream()
                .filter(intermediario -> intermediario.getEntity().tipoIntermediario().equals(tipo))
                .findAny()
                .map(this::banca)
                .orElse(null);
    }

    @Named("causaleDescrittiva")
    default String causaleDescrittiva(final Collection<InformazioniCausale.WithLinkedEntities> causali) {
        var result = causali.stream()
                .map(InformazioniCausale.WithLinkedEntities::getEntity)
                .map(InformazioniCausale::causaleDescrittiva)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());
        if (result.isBlank()) {
            return null;
        }
        return result;
    }

    @Named("istruzioniPagamentoBancaRicevente")
    default String istruzioniPagamentoBancaRicevente(
            final InformazioniAggiuntivePagamento informazioniAggiuntivePagamento) {
        var result = Stream.of(
                informazioniAggiuntivePagamento.istruzioniBancaRicevente1(),
                informazioniAggiuntivePagamento.istruzioniBancaRicevente2(),
                informazioniAggiuntivePagamento.istruzioniBancaRicevente3(),
                informazioniAggiuntivePagamento.istruzioniBancaRicevente4(),
                informazioniAggiuntivePagamento.istruzioniBancaRicevente5(),
                informazioniAggiuntivePagamento.istruzioniBancaRicevente6()).filter(Objects::nonNull)
                .collect(Collectors.joining());
        if (result.isBlank()) {
            return null;
        }
        return result;
    }

    @Named("istruzioniPagamentoBancaBeneficiario")
    default List<String> istruzioniPagamentoBancaBeneficiario(
            final InformazioniAggiuntivePagamento informazioniAggiuntivePagamento) {
        return Stream.of(
                informazioniAggiuntivePagamento.istruzioneBancaDelBeneficiario1(),
                informazioniAggiuntivePagamento.istruzioneBancaDelBeneficiario2()).filter(Objects::nonNull)
                .toList();
    }


    @Named("riferimentoCorrispondenteMittenteRapporto")
    default String riferimentoCorrispondenteMittenteRapporto(
            final Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> rapporti) {
        return corrispondenteMittente(rapporti)
                .map(value -> value.informazioniRapporto)
                .map(InformazioniRapporto.WithLinkedEntities::getEntity)
                .map(InformazioniRapporto::numero)
                .orElse(null);
    }

    @Named("riferimentoCorrispondeneMittenteIntestazione")
    default String riferimentoCorrispondeneMittenteIntestazione(
            final Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> rapporti) {
        return corrispondenteMittente(rapporti)
                .map(value -> value.informazioniRapporto)
                .map(InformazioniRapporto.WithLinkedEntities::getEntity)
                .map(InformazioniRapporto::intestazioneConto)
                .orElse(null);
    }

    private Optional<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> corrispondenteMittente(
            final Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> rapporti) {
        return rapporti.stream()
                .filter(rapporto -> rapporto.getEntity().tipoInformazioniRapporto()
                        .equals(TipoInformazioniRapporto.CORRISPONDENTE_MITTENTE))
                .findAny();
    }

}
