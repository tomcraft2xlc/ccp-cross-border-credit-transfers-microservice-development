package com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary;

import com.flowpay.ccp.credit.transfer.cross.border.dto.intermediary.bank.BankInfo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record IntermediaryInfo(
        @NotNull
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

        /* public IntermediaryInfo(BonificoExtraSepa creditTransfer) {
                this(
                        new BankInfo(creditTransfer.destinationBankBIC()),
                        Optional.ofNullable(creditTransfer.creditorBankBIC())
                                .map(creditorBIC -> new BankInfo(
                                        creditorBIC,
                                        creditTransfer.creditorBankIBAN(),
                                        creditTransfer.creditorOtherID())).orElse(null),
                        Optional.ofNullable(creditTransfer.issuingPartyCorrespondentBankBIC()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.receivingPartyCorrespondentBankBIC()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.reimbursementBankBIC()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.intermediaryBankBIC1()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.intermediaryBankBIC2()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.intermediaryBankBIC3()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.previousIntermediaryBankBIC1()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.previousIntermediaryBankBIC2()).map(BankInfo::new).orElse(null),
                        Optional.ofNullable(creditTransfer.previousIntermediaryBankBIC3()).map(BankInfo::new).orElse(null)
                );
        } */
}
