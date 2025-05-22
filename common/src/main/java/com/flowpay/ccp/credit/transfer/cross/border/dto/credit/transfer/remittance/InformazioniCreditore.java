package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

public record InformazioniCreditore(
        String codice,
        String codiceProprietario,
        String emittente,
        String riferimentoUnivoco
) {
}
