package com.flowpay.ccp.credit.transfer.cross.border.workers.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.VerifyStep;
import com.flowpay.ccp.job.Deserializable;

import java.util.UUID;

public record RestartVerifyProcessPayload(
        UUID idBonificoExtraSepa,
        UUID processID,
        boolean isFromCIP,
        VerifyStep verifyCall,
        CIPReply cipReply) implements ProcessInterruption {

    @Override
    public String getIdentifier() {
        return processID.toString();
    }

    public static final class Deserializer implements Deserializable<RestartVerifyProcessPayload> {

        @Override
        public RestartVerifyProcessPayload init(JsonNode jsonNode) {
            return new RestartVerifyProcessPayload(
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText()),
                    UUID.fromString(jsonNode.get("processID").asText()),
                    jsonNode.get("isFromCIP").asBoolean(),
                    VerifyStep.valueOf(jsonNode.get("verifyCall").asText()),
                    new CIPReply.Deserializer().init(jsonNode.get("cipReply")));
        }
    }
}
