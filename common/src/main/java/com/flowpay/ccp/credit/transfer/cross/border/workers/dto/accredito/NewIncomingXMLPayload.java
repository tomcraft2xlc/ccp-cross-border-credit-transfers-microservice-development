package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.accredito;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.job.Deserializable;

public record NewIncomingXMLPayload(
        String base64XML,
        String bankABI
) {

    public static final class Deserializer implements Deserializable<NewIncomingXMLPayload> {

        @Override
        public NewIncomingXMLPayload init(JsonNode jsonNode) {
            return new NewIncomingXMLPayload(
                    jsonNode.get("base64XML").asText(),
                    jsonNode.get("bankABI").asText()
            );
        }
    }
}
