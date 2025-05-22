package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import java.math.BigDecimal;

public record CommissioneClientela(
        String codice,
        String descrizione,
        BigDecimal importo,
        BigDecimal percentuale,
        BigDecimal max,
        BigDecimal min,
        String divisa
) {
}
