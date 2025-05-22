package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowpay.ccp.credit.transfer.cross.border.dto.settlement.CoverageBankAccount;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public record PatchCreditTransferInfo(
        @Valid
        CoverageBankAccount coverageBankAccount,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal conversionRate
) {
}
