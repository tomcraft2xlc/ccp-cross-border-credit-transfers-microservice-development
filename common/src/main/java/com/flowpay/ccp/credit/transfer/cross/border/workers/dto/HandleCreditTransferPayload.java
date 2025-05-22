package com.flowpay.ccp.credit.transfer.cross.border.workers.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.job.Deserializable;

import java.util.UUID;

public record HandleCreditTransferPayload(
        UUID idBonificoExtraSepa
) {

    public static final class Deserializer implements Deserializable<HandleCreditTransferPayload> {

        @Override
        public HandleCreditTransferPayload init(JsonNode jsonNode) {
            return new HandleCreditTransferPayload(
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText())
            );
        }
    }
}
