package com.flowpay.ccp.credit.transfer.cross.border.dto.account;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(
        title = "Account ID",
        description = "Un account identificato da un ID"
)
public record IDAccountInfo(
        @NotBlank
        String accountID
) implements AccountInfo {
}
