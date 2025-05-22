package com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary.bank;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.dto.account.AccountInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(
        title = "Informazioni banca",
        description = "Informazioni su una specifica banca"
)
public record BankInfo(
        @NotNull
        @Pattern(regexp = "^[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?$")
        @Schema(title= "Bic", description="Codice BIC della banca")
        String bic,

        AccountInfo accountInfo
) {

        /* public BankInfo(String bic) {
                this(bic, null);
        }

        public BankInfo(String bic, String iban, String otherID) {
                this(bic, AccountInfo.from(iban, otherID));
        } */
}
