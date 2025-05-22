package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.prowidesoftware.swift.model.mx.dic.CreditDebitCode;

public enum VersoEnum {
    CREDITORE,
    DEBITORE;

    public CreditDebitCode asCreditDebitCode() {
        return switch (this) {
            case DEBITORE -> CreditDebitCode.DBIT;
            case CREDITORE -> CreditDebitCode.CRDT;
        };
    }
}
