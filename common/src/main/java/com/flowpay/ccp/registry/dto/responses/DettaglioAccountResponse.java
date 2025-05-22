package com.flowpay.ccp.registry.dto.responses;

public record DettaglioAccountResponse(
        String accountName,
        String iban,
        String addressLine,
        String addressLocality,
        String addressCountry,
        String addressPostalCode,
        String fiscalCode
) {
}
