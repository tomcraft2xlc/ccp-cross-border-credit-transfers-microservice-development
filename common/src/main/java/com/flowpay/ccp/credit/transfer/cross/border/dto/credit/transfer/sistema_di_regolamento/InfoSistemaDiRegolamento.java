package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.Priorita;

import java.time.Instant;

public record InfoSistemaDiRegolamento(
        SistemaDiRegolamento sistemaDiRegolamento,
        Priorita priorita,
        Instant da,
        Instant a,
        Instant orarioAccredito,
        Instant scadenzaUltima,
        Boolean stp
) {
}
