package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.job.Deserializable;

import java.time.LocalDate;
import java.util.UUID;

public record RestartHandleDateChangePayload(
        UUID idBonificoExtraSepa,
        UUID idAutorizzazione,
        Long proposedDateEpochDay,
        UUID processID,
        CIPReply cipReply,
        Boolean isPaese,
        Boolean fromRegistry) implements ProcessInterruption {

    @Override
    public String getIdentifier() {
        return processID.toString();
    }

    public LocalDate proposedDate() {
        return LocalDate.ofEpochDay(proposedDateEpochDay);
    }

    public static final class Deserializer implements Deserializable<RestartHandleDateChangePayload> {

        @Override
        public RestartHandleDateChangePayload init(JsonNode jsonNode) {
            return new RestartHandleDateChangePayload(
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText()),
                    UUID.fromString(jsonNode.get("idAutorizzazione").asText()),
                    jsonNode.get("proposedDateEpochDay").asLong(),
                    UUID.fromString(jsonNode.get("processID").asText()),
                    new CIPReply.Deserializer().init(jsonNode.get("cipReply")),
                    jsonNode.get("isPaese").asBoolean(),
                    jsonNode.get("fromRegistry").asBoolean());
        }
    }
}
