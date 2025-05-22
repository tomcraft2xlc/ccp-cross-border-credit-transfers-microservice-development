package com.flowpay.ccp.credit.transfer.cross.border;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

public enum Tipologia {
    CBPR,
    T2;

    public SistemaDiRegolamento sistemaDiRegolamento() {
        return switch (this) {
            case T2 -> SistemaDiRegolamento.TARGET;
            case CBPR -> SistemaDiRegolamento.NO_TARGET;
        };
    }
}
