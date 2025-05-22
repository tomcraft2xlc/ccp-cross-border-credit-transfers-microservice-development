package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.FlagSiNo;

public record ExtraSepaCheckBonificoConfermaInput(
        TipoRichiesta tipoRichiesta,
        TipoMessaggio tipoMessaggio,
        String tidBonifico,
        Rapporto rapportoDare,
        Rapporto rapportoAvere,
        BigDecimal importoBonifico,
        BigDecimal controvaloreImporto,
        String divisaBonifico,
        BigDecimal valoreCambio,
        LocalDate dataCreazione,
        LocalDate dataEsecuzione,
        LocalDate dataValutaDare,
        LocalDate dataValutaAvere,
        SistemaRegolamento sistemaRegolamento,
        FlagSiNo stp,
        FlagSiNo flagForzaturaEmbargo,
        RegolamentoCommissioniValuta regolamentoCommissioniClienteCc,
        RegolamentoCommissioniValuta regolamentoCommissioniBancaCc,
        String causaleBonifico,
        TipoSpese tipoSpese,
        String paeseBonifico,
        List<String> ndgPresentatori,
        List<String> ndgTitolariEffettivi,
        List<Commissione> commissioniCliente,
        Commissione commissioneBanca,
        Anagrafica beneficiario,
        Anagrafica ordinante,
        Banca bancaMittente,
        Banca bancaDestinatario,
        Banca bancaCopertura,
        String riferimentoCorrispondeneMittenteRapporto,
        String riferimentoCorrispondeneMittenteIntestazione,
        Banca corrispettivoMittente,
        Banca corrispettivoRicevente,
        Banca istitutoTerzoRimborso,
        List<Banca> istruttrici,
        List<Banca> intermediarie,
        String causaleDescrittiva,
        String istruzioniPagamentoBancaRicevente,
        List<String> istruzioniPagamentoBancaBeneficiario,
        BigDecimal importoTotaleSpeseIn

) {
    public enum RegolamentoCommissioniValuta {
        EURO, VALUTA;

        public static RegolamentoCommissioniValuta fromDbValue(RegolamentoCommissione regolamentoCommissione) {
            if (regolamentoCommissione == null) {
                return null;
            }

            return switch (regolamentoCommissione) {
                case EURO -> EURO;
                case DIVISA -> VALUTA;
            };
        }
    }

    public enum TipoSpese {
        SHAR, CRED, DEBT;

        public static TipoSpese fromDbValue(TipologiaCommissioni tipologiaCommissioni) {
            if (tipologiaCommissioni == null) {
                return null;
            }

            return switch (tipologiaCommissioni) {
                case SHARED -> SHAR;
                case CREDITOR -> CRED;
                case DEBTOR -> DEBT;
            };
        }
    }

    public record Rapporto(
            String numero,
            Integer voceContabile,
            String divisa) {
    }

    public record Commissione(
            String codice,
            BigDecimal importo) {
    }

    public record Banca(
            String bic,
            String intestazione,
            String rapporto,
            String codiceLei) {
    }

    public record Anagrafica(
            String rapporto,
            String bic,
            String intestazione,
            String indirizzo,
            String numeroCivico,
            String citta,
            String paese,
            String codicePostale,
            Banca banca) {
    }
}