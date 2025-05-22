package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;
import java.math.BigDecimal; 

public record VerificaCambioInput(
    TipoRichiesta tipoRichiesta,
    String codiceIsoDivisa,
    BigDecimal valoreCambio

) {
    public enum TipoRichiesta {
        EMPTY
    }
}