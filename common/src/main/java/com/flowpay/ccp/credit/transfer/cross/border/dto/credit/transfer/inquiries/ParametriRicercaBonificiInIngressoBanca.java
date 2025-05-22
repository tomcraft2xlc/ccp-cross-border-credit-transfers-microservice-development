package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.pagination.WhereQueryBuilder;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParametriRicercaBonificiInIngressoBanca implements ParametriRicerca {

    @Schema(description = "Identificativo della transazione")
    @QueryParam("tid")
    public String tid;

    @Schema(description = "UETR")
    @QueryParam("uetr")
    public String uetr;

    @Schema(description = "BIC del beneficiario")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBeneficiario")
    public String bicBeneficiario;

    @Schema(
            description = "Data di ricezione del pagamento.\n\nData di inizio dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataRicezioneDa")
    public LocalDate dataRicezioneDa;

    @Schema(
            description = "Data di ricezione del pagamento.\n\nData di fine dell'intervallo",
            example = "2025-12-31"
    )
    @QueryParam("dataRicezioneA")
    public LocalDate dataRicezioneA;

    @Schema(description = "Sotto tipologia del bonifico in ingresso")
    @QueryParam("sottoTipologia")
    public SottoTipologiaBonifico sottoTipologia;

    @Schema(
            description = "Il sistema di regolamento",
            example = "TARGET"
    )
    @QueryParam("sistemaDiRegolamento")
    public SistemaDiRegolamento sistemaDiRegolamento;

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

    @Schema(description = "Nome del beneficiario")
    @QueryParam("intestazioneBeneficiario")
    public String intestazioneBeneficiario;

    @Schema(description = "Rapporto del beneficiario")
    @QueryParam("rapportoBeneficiario")
    public String rapportoBeneficiario;

    @Schema(description = "Codice BIC della banca emittente")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBancaMittente")
    public String bicBancaMittente;

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

    @QueryParam("rapportoOrdinante")
    public String rapportoOrdinante;

    public ParametriRicercaBonificiInIngressoBanca() {
    }

    public WhereQueryBuilder queryBuilder() {
        return new WhereQueryBuilder()
        .addCondition("tid = $", this.tid)
        .addCondition("uetr = $", this.uetr)
        .addConditionWithPredicate("bic_beneficiario = $", this.bicBeneficiario, bic -> bic, bicBeneficiario -> bicBeneficiario.length() == 11)
        .addConditionWithPredicate("bic_beneficiario LIKE $", this.bicBeneficiario, bic -> bic + "%", bicBeneficiario -> bicBeneficiario.length() == 8)
        .addCondition("data_ricezione >= $", this.dataRicezioneDa)
        .addCondition("data_ricezione <= $", this.dataRicezioneA)
        .addCondition("sotto_tipologia_bonifico = $", this.sottoTipologia)
        .addCondition("sistema_di_regolamento = $", this.sistemaDiRegolamento)
        .addCondition("divisa = $", this.divisa)
        .addCondition("importo >= $", this.importoDa)
        .addCondition("importo <= $", this.importoA)
        .addCondition("intestazione_beneficiario LIKE $", this.intestazioneBeneficiario, intestazione -> intestazione + "%")
        .addCondition("numero_rapporto_beneficiario = $", this.rapportoBeneficiario)
        .addConditionWithPredicate("bic_banca_emittente = $", this.bicBancaMittente, bic -> bic, bic -> bic.length() == 11)
        .addConditionWithPredicate("bic_banca_emittente LIKE $", this.bicBancaMittente, bic -> bic + "%", bic -> bic.length() == 8)
        .addCondition("data_regolamento_banca_beneficiaria >= $", this.dataRegolamentoBancaBeneficiarioDa)
        .addCondition("data_regolamento_banca_beneficiario <= $", this.dataRegolamentoBancaBeneficiarioA)
        .addCondition("codice_filiale = $", this.codiceFiliale)
        .addCondition("numero_rapporto_ordinante = $", this.rapportoOrdinante);
    }
}
