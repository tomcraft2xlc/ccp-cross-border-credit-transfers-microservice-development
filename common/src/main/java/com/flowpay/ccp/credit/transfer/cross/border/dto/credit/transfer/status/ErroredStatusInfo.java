package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.status;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored.InfoStatoErrore;

import java.time.Instant;

public record ErroredStatusInfo(
        String errorCode,
        String errorMessage,
        Instant createdAt
) {

    /* public ErroredStatusInfo(
            InfoStatoErrore reply
    ) {
        this(
                reply.errorCode(),
                reply.errorDescription(),
                reply.createdAt()
        ); 
    }*/
}
