package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParametriRicercaBonificiInUscita {
    @Schema(description = "NDG del presentatore o del titolare effettivo")
    @QueryParam("ndg")
    public String ndg;

    @Schema(description = "Identificativo della transazione")
    @QueryParam("tid")
    public String tid;

    @Schema(description = "UETR")
    @QueryParam("uetr")
    public String uetr;

    @Schema(description = "BIC dell’ordinante")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicOrdinante")
    public String bicOrdinante;

    @Schema(description = "Rapporto Ordinante/Sottoconto")
    @QueryParam("rapportoOrdinante")
    public String rapportoOrdinante;

    @Schema(
            description = "Data di invio del pagamento.\n\nData di inizio dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataInvioDa")
    public String dataInvioDa;

    @Schema(
            description = "Data di invio del pagamento.\n\nData di fine dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataInvioA")
    public String dataInvioA;

    @Schema(description = "ID del canale dal quale è stato disposto il bonifico.")
    @QueryParam("idCanale")
    public String idCanale;

    @Schema(
            description = "codice del Pacs.008, codice del Pacs.008 con Pacs.009 COV",
            example = "client_extra_sepa_credit_transfer"
    )
    @QueryParam("nomeSottoTipologia")
    public String nomeSottoTipologia;

    @Schema(
            description = "Il sistema di regolamento",
            example = "TARGET"
    )
    @QueryParam("sistemaDiRegolamento")
    public String sistemaDiRegolamento;

    @Schema(
            description = "Codice della divisa del bonifico",
            example = "EUR"
    )
    @QueryParam("divisa")
    public String divisa;

    @Schema(description = "Limite inferiore del filtro sull'importo del bonifico")
    @QueryParam("importoDa")
    public BigDecimal importoDa;

    @Schema(description = "Limite superiore del filtro sull'importo del bonifico")
    @QueryParam("importoA")
    public BigDecimal importoA;

    @Schema(description = "Ordinante del bonifico")
    @QueryParam("ordinante")
    public String ordinante;

    @Schema(description = "Codice BIC della banca del beneficiario del bonifico")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBancaDelBeneficiario")
    public String bicBancaDelBeneficiario;

    @Schema(description = "Codice BIC della banca destinataria")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBancaDestinataria")
    public String bicBancaDestinataria;

    @Schema(
            description = "Data di regolamento della banca del beneficiario.\n\nData di inizio dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataRegolamentoBancaBeneficiarioDa")
    public LocalDate dataRegolamentoBancaBeneficiarioDa;

    @Schema(
            description = "Data di regolamento della banca del beneficiario.\n\nData di fine dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataRegolamentoBancaBeneficiarioA")
    public LocalDate dataRegolamentoBancaBeneficiarioA;

    @Schema(description = "Codice filiale della banca")
    @QueryParam("codiceFiliale")
    public Long codiceFiliale;

    @Schema(
            description = "Stato del bonifico",
            example = "REGOLATO"
    )
    @QueryParam("stato")
    public CreditTransferStatus stato;

    public ParametriRicercaBonificiInUscita() {
    }

    public ParametriRicercaBonificiInUscita(String ndg, String tid, String uetr, String bicOrdinante, String rapportoOrdinante, String dataInvioDa, String dataInvioA, String idCanale, String nomeSottoTipologia, String sistemaDiRegolamento, String divisa, BigDecimal importoDa, BigDecimal importoA, String ordinante, String bicBancaDelBeneficiario, String bicBancaDestinataria, LocalDate dataRegolamentoBancaBeneficiarioDa, LocalDate dataRegolamentoBancaBeneficiarioA, Long codiceFiliale, CreditTransferStatus stato) {
        this.ndg = ndg;
        this.tid = tid;
        this.uetr = uetr;
        this.bicOrdinante = bicOrdinante;
        this.rapportoOrdinante = rapportoOrdinante;
        this.dataInvioDa = dataInvioDa;
        this.dataInvioA = dataInvioA;
        this.idCanale = idCanale;
        this.nomeSottoTipologia = nomeSottoTipologia;
        this.sistemaDiRegolamento = sistemaDiRegolamento;
        this.divisa = divisa;
        this.importoDa = importoDa;
        this.importoA = importoA;
        this.ordinante = ordinante;
        this.bicBancaDelBeneficiario = bicBancaDelBeneficiario;
        this.bicBancaDestinataria = bicBancaDestinataria;
        this.dataRegolamentoBancaBeneficiarioDa = dataRegolamentoBancaBeneficiarioDa;
        this.dataRegolamentoBancaBeneficiarioA = dataRegolamentoBancaBeneficiarioA;
        this.codiceFiliale = codiceFiliale;
        this.stato = stato;
    }
}
