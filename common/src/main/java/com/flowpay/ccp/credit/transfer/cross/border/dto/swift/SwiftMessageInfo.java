package com.flowpay.ccp.credit.transfer.cross.border.dto.swift;

import com.flowpay.ccp.credit.transfer.cross.border.dto.settlement.SettlementMethod;
import com.flowpay.ccp.credit.transfer.cross.border.dto.swift.purpose.Purpose;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import jakarta.validation.Valid;

public record SwiftMessageInfo(
        SettlementMethod settlementMethod,
        String serviceLevelCode,
        @Valid
        Purpose purpose,
        String regulatoryReporting
) {

    /* public SwiftMessageInfo(
            BonificoExtraSepa creditTransfer) {
        this(
                creditTransfer.settlementMethod(),
                creditTransfer.serviceLevelCode(),
                Purpose.from(creditTransfer),
                creditTransfer.regulatoryReportingInfo()
        );
    } */
}
