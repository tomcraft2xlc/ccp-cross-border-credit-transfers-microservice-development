package com.flowpay.ccp.credit.transfer.cross.border.dto.account;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(
        title = "Account IBAN",
        description = "Un account identificato dall'IBAN"
)
public record IBANAccountInfo(
        @NotBlank
        @Schema(pattern = "AD1400080001001234567890")
        String iban
) implements AccountInfo {
}
