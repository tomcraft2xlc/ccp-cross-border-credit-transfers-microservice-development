package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.status;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.history.StoriaStatiBonificoExtraSepa;

import java.time.Instant;

public record CreditTransferStatusHistory(
        String creditTransferStatus,
        String status,
        String note,
        Instant createdAt
) {

  /*   public CreditTransferStatusHistory(StoriaStatiBonificoExtraSepa creditTransferStatusHistory) {
        this(
                creditTransferStatusHistory.statoAttuale(),
                creditTransferStatusHistory.nuovoStato(),
                creditTransferStatusHistory.note(),
                creditTransferStatusHistory.createdAt()
        );
    } */
}
