package com.flowpay.ccp.credit.transfer.cross.border;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.flowpay.ccp.persistence.EnumDeserializer;
import com.flowpay.ccp.persistence.EnumSerializer;

public enum AccountType {

    ACCOUNT,
    VOCE_CONTABILE;

    @JsonCreator
    @EnumDeserializer
    public static AccountType fromString(String value) {
        return AccountType.valueOf(value.toUpperCase());
    }

    @Override
    @JsonValue
    @EnumSerializer
    public String toString() {
        return this.name().toLowerCase();
    }
}
