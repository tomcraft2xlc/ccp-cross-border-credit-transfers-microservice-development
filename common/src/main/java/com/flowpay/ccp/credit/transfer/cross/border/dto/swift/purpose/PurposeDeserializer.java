package com.flowpay.ccp.credit.transfer.cross.border.dto.swift.purpose;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PurposeDeserializer extends StdDeserializer<Purpose> {

    public PurposeDeserializer() {
        super(Purpose.class);
    }

    @Override
    public Purpose deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        var code = node.get("code");
        if (code != null) {
            if (code.isTextual()) {
                return new CodePurpose(code.asText());
            }
            throw new IOException("Invalid code value");
        }
        var proprietary = node.get("proprietary");
        if (proprietary != null) {
            if (proprietary.isTextual()) {
                return new ProprietaryPurpose(proprietary.asText());
            }
            throw new IOException("Invalid proprietary value");
        }

        throw new IOException("Unrecognized property");
    }
}
