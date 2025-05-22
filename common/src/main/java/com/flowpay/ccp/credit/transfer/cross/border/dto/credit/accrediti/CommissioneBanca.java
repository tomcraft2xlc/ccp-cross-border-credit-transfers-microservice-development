package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import java.math.BigDecimal;

public record CommissioneBanca(
        String codice,
        String descrizione,
        String divisa,
        BigDecimal importo
) {
}
