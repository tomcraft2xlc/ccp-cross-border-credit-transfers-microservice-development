package com.flowpay.ccp.credit.transfer.cross.border.dto.creditor;

import com.flowpay.ccp.credit.transfer.cross.border.dto.account.AccountInfo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.address.AddressInfo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreditorInfo(
        @NotNull
        @Valid
        AccountInfo accountInfo,
        @NotBlank
        @Size(min = 1, max = 140)
        String name,
        @NotNull
        @Valid
        AddressInfo address
) {

        /* public CreditorInfo(BonificoExtraSepa creditTransfer) {
                this(
                        AccountInfo.from(creditTransfer),
                        creditTransfer.creditorName(),
                        new AddressInfo(creditTransfer)
                );
        } */
}
