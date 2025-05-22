package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check.SistemaRegolamento;
import com.flowpay.ccp.pagination.WhereQueryBuilder;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParametriRicercaBonificiInIngressoClientela implements ParametriRicerca{

    @Schema(description = "Identificativo della transazione")
    @QueryParam("tid")
    public String tid;

    @Schema(description = "UETR")
    @QueryParam("uetr")
    public String uetr;

    @Schema(description = "Rapporto del beneficiario")
    @QueryParam("rapportoBeneficiario")
    public String rapportoBeneficiario;

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
    public String sottoTipologia;

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

    @Schema(description = "Codice BIC della banca mittente")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBancaMittente")
    public String bicBancaMittente;

    @Schema(description = "BIC della banca ordinante")
    @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
    @QueryParam("bicBancaOrdinante")
    public String bicBancaOrdinante;

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

    public ParametriRicercaBonificiInIngressoClientela() {
    }

    public WhereQueryBuilder queryBuilder() {
        return new WhereQueryBuilder()
        .addCondition("tid = $", tid)
        .addCondition("uetr = $", uetr)
        .addCondition("numero_rapporto_beneficiario = $", rapportoBeneficiario)
        .addCondition("data_ricezione >= $", dataRicezioneDa)
        .addCondition("data_ricezione <= $", dataRicezioneA)
        .addCondition("sotto_tipologia_bonifico = $", sottoTipologia)
        .addCondition("sistema_di_regolamento", sistemaDiRegolamento)
        .addCondition("divisa = $", divisa)
        .addCondition("importo >= $", importoDa)
        .addCondition("importo <= $", importoA)
        .addCondition("intestazione_beneficiario LIKE $", intestazioneBeneficiario, intestazione -> intestazione + "%")
        .addConditionWithPredicate("bic_banca_emittente LIKE $", bicBancaMittente, bic -> bic + "%", bic -> bic.length() == 8)
        .addConditionWithPredicate("bic_banca_emittente = $", bicBancaMittente, bic -> bic, bic -> bic.length() == 11)
        .addConditionWithPredicate("bic_ordinante LIKE $", bicBancaOrdinante, bic -> bic + "%", bic -> bic.length() == 8)
        .addConditionWithPredicate("bic_ordinante = $", bicBancaOrdinante, bic -> bic, bic -> bic.length() == 11)
        .addCondition("data_regolamento_banca_beneficiario >= $", dataRegolamentoBancaBeneficiarioDa)
        .addCondition("data_regolamento_banca_beneficiario <= $", dataRegolamentoBancaBeneficiarioA)
        .addCondition("codice_filiale = $", codiceFiliale)
        .addCondition("numero_rapporto_ordinante = $", rapportoOrdinante);

    }
}
