package com.flowpay.ccp.credit.transfer.cross.border.dto.account;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class AccountInfoDeserializer extends StdDeserializer<AccountInfo> {

    public AccountInfoDeserializer() {
        super(AccountInfo.class);
    }

    @Override
    public AccountInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        var names = node.fieldNames();
        while (names.hasNext()) {
            var name = names.next();
            if (name.equals("iban")) {
                return new IBANAccountInfo(node.get(name).asText());
            } else if (name.equals("accountID")) {
                return new IDAccountInfo(node.get(name).asText());
            }
        }
        throw new IOException("Unrecognized property");
    }
}
