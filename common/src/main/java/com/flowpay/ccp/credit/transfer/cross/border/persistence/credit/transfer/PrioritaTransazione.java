package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer;

import com.prowidesoftware.swift.model.mx.dic.Priority2Code;

public enum PrioritaTransazione {

    NORMALE,
    ALTA;

    public Priority2Code asPriority2Code() {
        return switch (this) {
            case ALTA -> Priority2Code.HIGH;
            case NORMALE -> Priority2Code.NORM;
        };
    }
}
