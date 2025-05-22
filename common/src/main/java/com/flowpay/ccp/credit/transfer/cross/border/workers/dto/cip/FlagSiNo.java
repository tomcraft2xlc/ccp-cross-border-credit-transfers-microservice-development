package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

public enum FlagSiNo {
    SI,
    NO;

    public boolean toBoolean() {
        return this == SI;
    }

    public static FlagSiNo fromBoolean(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? SI : NO;
    }
}