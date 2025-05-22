package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InformazioniFiscali(
    AttoreFiscale creditore,
    AttoreFiscale debitore,
    AttoreFiscale debitoreEffettivo,
    String amministratoreDiRiferimento,
    String dettaglioImpostaRiferimento,
    String metodo,
    Importo imponibile,
    Importo imposta,
    LocalDate scadenza,
    BigDecimal numeroProgressivoDichiarazione,
    List<@Valid RecordFiscale> recordFiscali
) {

    public record AttoreFiscale(
            String identificativoFiscale,
            String identificativo,
            String tipoContribuente,
            String titolo,
            String intestazione

    ) {

    }

    public record RecordFiscale(
        String codice,
        String categoria,
        String dettagliCategoria,
        String statusContribuente,
        String identificativoDichiarazione,
        String codiceModelloDichiarazione,
        String annoRiferimentoDichiarazione,
        String periodoRiferimentoDichiarazione,
        LocalDate periodoRiferimentoDichiarazioneDa,
        LocalDate periodoRiferimentoDichiarazioneA,
        BigDecimal percentualeImposta,
        Importo imponibile,
        Importo imposta,
        List<@Valid DettagliRecordFiscale> dettagli,
        String informazioniAggiuntive
    ) { }

    public record DettagliRecordFiscale(
            String anno,
            String periodo,
            LocalDate periodoDa,
            LocalDate periodoA,
            Importo importo
    ) { }
}
