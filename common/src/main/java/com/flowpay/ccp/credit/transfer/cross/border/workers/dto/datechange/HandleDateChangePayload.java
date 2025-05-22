package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.job.Deserializable;

import java.time.LocalDate;
import java.util.UUID;

public record HandleDateChangePayload(
        UUID idBonificoExtraSepa,
        UUID idAutorizzazione,
        Long proposedDateEpochDay) {

    public HandleDateChangePayload(
        UUID idBonificoExtraSepa,
        UUID idAutorizzazione,
        LocalDate proposedDate
    ) {
        this(idBonificoExtraSepa, idAutorizzazione, proposedDate.toEpochDay());
    }

    public LocalDate proposedDate() {
        return LocalDate.ofEpochDay(proposedDateEpochDay);
    }

    public static final class Deserializer implements Deserializable<HandleDateChangePayload> {

        @Override
        public HandleDateChangePayload init(JsonNode jsonNode) {
            return new HandleDateChangePayload(
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText()),
                    UUID.fromString(jsonNode.get("idAutorizzazione").asText()),
                    jsonNode.get("proposedDateEpochDay").asLong()
            );
        }
    }
}
