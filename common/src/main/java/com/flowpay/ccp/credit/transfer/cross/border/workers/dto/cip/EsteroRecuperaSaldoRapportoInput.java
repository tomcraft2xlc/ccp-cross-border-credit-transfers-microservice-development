package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.math.BigDecimal;

public record EsteroRecuperaSaldoRapportoInput(
    TipoRichiesta tipoRichiesta,
    String rapporto,
    BigDecimal importo,
    String utente

) {
    public enum TipoRichiesta {
        SALDORAPPORTO,
        CONTROLLODISPONIBILITA
    }
}