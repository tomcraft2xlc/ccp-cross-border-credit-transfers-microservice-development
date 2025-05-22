package com.flowpay.ccp.credit.transfer.cross.border.dto;


import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.credit.transfer.cross.border.Tipologia;
import com.flowpay.ccp.job.Deserializable;

public record FileReceived(
        String fileName,
        String b64File,
        String abi,
        Tipologia tipologia
) {

    public static final class Deserializer implements Deserializable<FileReceived> {

        @Override
        public FileReceived init(JsonNode jsonNode) {
            return new FileReceived(
                    jsonNode.get("fileName").asText(),
                    jsonNode.get("b64File").asText(),
                    jsonNode.get("abi").asText(),
                    Tipologia.valueOf(jsonNode.get("tipologia").asText())
            );
        }
    }
}

