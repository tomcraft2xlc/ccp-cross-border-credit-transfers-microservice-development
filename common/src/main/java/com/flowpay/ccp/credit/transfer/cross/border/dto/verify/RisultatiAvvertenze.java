package com.flowpay.ccp.credit.transfer.cross.border.dto.verify;

import java.util.List;

public record RisultatiAvvertenze(
        StatoAvvertenze stato,
        List<DettaglioAvvertenza> avvertenze
) {
    public enum StatoAvvertenze {
        NESSUNA_AVVERTENZA,
        AVVERTENZE_PRESENTI,
        BLOCCO_DARE,
        BLOCCO_TOTALE,
        BLOCCO_DARE_CON_AVVERTENZA
    }

    public record DettaglioAvvertenza(
            String codice,
            String descrizione
    ) {
    }
}
