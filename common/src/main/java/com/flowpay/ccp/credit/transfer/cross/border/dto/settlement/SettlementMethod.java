package com.flowpay.ccp.credit.transfer.cross.border.dto.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.flowpay.ccp.persistence.EnumDeserializer;
import com.flowpay.ccp.persistence.EnumSerializer;

public enum SettlementMethod {

    CLEARING,
    INSTRUCTING_AGENT,
    INSTRUCTED_AGENT,
    COVERAGE;

    @Override
    @JsonValue
    @EnumSerializer
    public String toString() {
        return switch (this) {
            case CLEARING -> "clearing";
            case INSTRUCTING_AGENT -> "instructing_agent";
            case INSTRUCTED_AGENT -> "instructed_agent";
            case COVERAGE -> "coverage";
        };
    }

    @JsonCreator
    @EnumDeserializer
    public static SettlementMethod fromString(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "clearing" -> CLEARING;
            case "instructing_agent" -> INSTRUCTING_AGENT;
            case "instructed_agent" -> INSTRUCTED_AGENT;
            case "coverage" -> COVERAGE;
            default -> throw new IllegalArgumentException("Unknown value: " + value);
        };
    }
}
