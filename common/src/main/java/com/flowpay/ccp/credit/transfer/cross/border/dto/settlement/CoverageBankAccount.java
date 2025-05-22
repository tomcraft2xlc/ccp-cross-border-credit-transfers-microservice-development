package com.flowpay.ccp.credit.transfer.cross.border.dto.settlement;

import com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary.bank.BankInfo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CoverageBankAccount(
        @NotEmpty
        String accountID,

        @NotNull
        @Valid
        BankInfo bankInfo
) {

    /* public CoverageBankAccount(BonificoExtraSepa creditTransfer) {
        this(
                creditTransfer.coverageBankAccountID(),
                new BankInfo(creditTransfer.coverageBankBIC())
        );
    } */
}
