package com.flowpay.ccp.credit.transfer.cross.border.dto.address;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressInfo(
        @NotNull
        @Size(min = 1, max = 70*7)
        String addressLine,

        @NotNull
        @Size(min = 1, max = 35)
        String location,

        @NotNull
        @Size(min = 2, max = 2)
        String country,

        @Size(min = 1, max = 16)
        String postalCode
) {

    /* public AddressInfo(BonificoExtraSepa creditTransfer) {
        this(creditTransfer.creditorAddressLine(),
        creditTransfer.creditorLocation(),
        creditTransfer.creditorCountry(),
        creditTransfer.creditorPostalCode());
    } */
}
