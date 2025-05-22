package com.flowpay.ccp.credit.transfer.cross.border.workers.dto;

import com.flowpay.ccp.cip.client.CIPReply;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.ConfirmationStep;
import com.flowpay.ccp.job.Deserializable;


public record RestartConfirmationProcessPayload(
        UUID idBonificoExtraSepa,
        UUID processID,
        boolean isFromCIP,
        ConfirmationStep confirmationCall,
        CIPReply cipReply) implements ProcessInterruption {

    @Override
    public String getIdentifier() {
        return processID.toString();
    }

    public static final class Deserializer implements Deserializable<RestartConfirmationProcessPayload> {

        @Override
        public RestartConfirmationProcessPayload init(JsonNode jsonNode) {
            return new RestartConfirmationProcessPayload(
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText()),
                    UUID.fromString(jsonNode.get("processID").asText()),
                    jsonNode.get("isFromCIP").asBoolean(),
                    ConfirmationStep.valueOf(jsonNode.get("confirmationCall").asText()),
                    new CIPReply.Deserializer().init(jsonNode.get("cipReply")));
        }
    }
}
