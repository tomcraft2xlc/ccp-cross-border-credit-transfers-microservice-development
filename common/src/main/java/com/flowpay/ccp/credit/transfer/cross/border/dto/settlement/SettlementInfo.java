package com.flowpay.ccp.credit.transfer.cross.border.dto.settlement;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import jakarta.validation.constraints.NotNull;

public record SettlementInfo(
        @NotNull
        SistemaDiRegolamento sistemaDiRegolamento,
        Boolean stp
) {

        /* public SettlementInfo(BonificoExtraSepa creditTransfer) {
                this(creditTransfer.settlementKind(), creditTransfer.stp());
        } */
}
