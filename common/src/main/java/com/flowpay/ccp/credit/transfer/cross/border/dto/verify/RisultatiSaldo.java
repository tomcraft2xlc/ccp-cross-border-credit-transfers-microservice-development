package com.flowpay.ccp.credit.transfer.cross.border.dto.verify;

import java.math.BigDecimal;

public record RisultatiSaldo(
        StatoSaldoRapporto statoSaldoRapporto,
        BigDecimal importoSconfinamento
) {
    public enum StatoSaldoRapporto {
        SALDO_DISPONIBILE,
        SALDO_NON_DISPONIBILE,
        FORZATURA_NECESSARIA
    }
}
