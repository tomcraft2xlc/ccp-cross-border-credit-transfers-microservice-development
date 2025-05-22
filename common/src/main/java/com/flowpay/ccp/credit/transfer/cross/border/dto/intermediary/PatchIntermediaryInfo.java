package com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary;

import com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary.bank.BankInfo;
import jakarta.validation.Valid;

public record PatchIntermediaryInfo(
        @Valid
        BankInfo destinationBank,
        @Valid
        BankInfo creditorBank,
        @Valid
        BankInfo issuingPartyCorrespondentBank,
        @Valid
        BankInfo receivingPartyCorrespondentBank,
        @Valid
        BankInfo reimbursementBank,
        @Valid
        BankInfo intermediaryBank1,
        @Valid
        BankInfo intermediaryBank2,
        @Valid
        BankInfo intermediaryBank3,
        @Valid
        BankInfo previousIntermediaryBank1,
        @Valid
        BankInfo previousIntermediaryBank2,
        @Valid
        BankInfo previousIntermediaryBank3
) {
}
