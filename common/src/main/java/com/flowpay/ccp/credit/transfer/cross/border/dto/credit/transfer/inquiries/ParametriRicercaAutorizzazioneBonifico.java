package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import jakarta.ws.rs.QueryParam;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dati necessari per la ricerca di bonifici nel cruscotto autorizzativo
 */
public class ParametriRicercaAutorizzazioneBonifico {

    @QueryParam("ndg")
    public String ndg;

    @QueryParam("tid")
    public String tid;

    @QueryParam("uetr")
    public String uetr;

    @QueryParam("bicOrdinante")
    public String bicOrdinante;

    @QueryParam("rapportoOrdinante")
    public String rapportoOrdinante;

    @QueryParam("dataRegolamentoBancaBeneficiarioDa")
    public LocalDate dataRegolamentoBancaBeneficiarioDa;

    @QueryParam("dataRegolamentoBancaBeneficiarioA")
    public LocalDate dataRegolamentoBancaBeneficiarioA;

    @QueryParam("idCanale")
    public String idCanale;

    @QueryParam("idSottoTipologia")
    public String idSottoTipologia;

    @QueryParam("sistemaDiRegolamento")
    public String sistemaDiRegolamento;

    @QueryParam("divisa")
    public String divisa;

    @QueryParam("ordinante")
    public String ordinante;

    @QueryParam("importoDa")
    public BigDecimal importoDa;

    @QueryParam("importoA")
    public BigDecimal importoA;

    @QueryParam("bicBancaBeneficiario")
    public String bicBancaBeneficiario;

    @QueryParam("bicBancaDestinataria")
    public String bicBancaDestinataria;

    @QueryParam("codiceFiliale")
    public Long codiceFiliale;

    public ParametriRicercaAutorizzazioneBonifico() {
    }

    public ParametriRicercaAutorizzazioneBonifico(String ndg, String tid, String uetr, String bicOrdinante, String rapportoOrdinante, LocalDate dataRegolamentoBancaBeneficiarioDa, LocalDate dataRegolamentoBancaBeneficiarioA, String idCanale, String idSottoTipologia, String sistemaDiRegolamento, String divisa, String ordinante, BigDecimal importoDa, BigDecimal importoA, String bicBancaBeneficiario, String bicBancaDestinataria, Long codiceFiliale) {
        this.ndg = ndg;
        this.tid = tid;
        this.uetr = uetr;
        this.bicOrdinante = bicOrdinante;
        this.rapportoOrdinante = rapportoOrdinante;
        this.dataRegolamentoBancaBeneficiarioDa = dataRegolamentoBancaBeneficiarioDa;
        this.dataRegolamentoBancaBeneficiarioA = dataRegolamentoBancaBeneficiarioA;
        this.idCanale = idCanale;
        this.idSottoTipologia = idSottoTipologia;
        this.sistemaDiRegolamento = sistemaDiRegolamento;
        this.divisa = divisa;
        this.ordinante = ordinante;
        this.importoDa = importoDa;
        this.importoA = importoA;
        this.bicBancaBeneficiario = bicBancaBeneficiario;
        this.bicBancaDestinataria = bicBancaDestinataria;
        this.codiceFiliale = codiceFiliale;
    }
}
