package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

public record EsteroControlloRapportoInput(
    TipoRichiesta tipoRichiesta,
    VersoBonifico versoBonfico,
    String rapporto,
    String iban
) {
    public enum TipoRichiesta {
        BANCA,
        CLIENTE
    }
    public enum VersoBonifico {
        INGRESSO,
        USCITA
    }
}