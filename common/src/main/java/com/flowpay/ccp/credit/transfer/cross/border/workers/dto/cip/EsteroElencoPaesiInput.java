package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

public record EsteroElencoPaesiInput(
        TipoRichiesta tipoRichiesta,
        String codicePaeseIso,
        String bicPaese
) {

    public enum TipoRichiesta {
        LISTAPARZIALE
    }
}
