package com.flowpay.ccp.credit.transfer.cross.border.dto.verify;

public record RisultatiEmbargo(
        StatoEmbargo stato
) {

    public enum StatoEmbargo {
        NON_SOTTO_EMBARGO,
        SOTTO_EMBARGO_PARZIALE,
        SOTTO_EMBARGO_TOTALE
    }
}
