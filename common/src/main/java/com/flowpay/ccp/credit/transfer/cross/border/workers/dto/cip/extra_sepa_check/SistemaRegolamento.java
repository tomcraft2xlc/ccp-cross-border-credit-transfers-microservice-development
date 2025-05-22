package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

public enum SistemaRegolamento {
    TARGET,
    CBPR;

    public static  SistemaRegolamento fromDBValue(SistemaDiRegolamento sistemaDiRegolamento) {
        return switch (sistemaDiRegolamento) {
            case NO_TARGET -> CBPR;
            case TARGET -> TARGET;
            default -> throw new IllegalArgumentException();
        };
    }
}