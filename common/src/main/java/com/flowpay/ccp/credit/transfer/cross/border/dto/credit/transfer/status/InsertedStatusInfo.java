package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.status;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted.InfoStatoInserito;

import java.math.BigDecimal;
import java.time.Instant;

public record InsertedStatusInfo(
        String outcome,
        InsertedOutcomeCode outcomeCode,
        Integer requestID,
        Boolean isAvailable,
        BigDecimal balance,
        Boolean embargo,
        Instant createdAt
) {

    /* public InsertedStatusInfo(
            InfoStatoInserito reply
    ) {
        this(
                reply.outcome(),
                reply.outcomeCode(),
                reply.requestId(),
                reply.isAvailable(),
                reply.balance(),
                reply.embargo(),
                reply.createdAt()
        );
    } */
}
