package com.flowpay.ccp.credit.transfer.cross.border.cip;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.job.Deserializable;

import java.util.UUID;

public record RestartProcess(
        CreditTransferStatus status,
        UUID creditTransferID,
        CIPReply cipReply
) implements ProcessInterruption {
    @Override
    public String getIdentifier() {
        return status.toString()+":"+creditTransferID.toString();
    }

    public static final class Deserializer implements Deserializable<RestartProcess> {
        @Override
        public RestartProcess init(JsonNode jsonNode) {
            return new RestartProcess(
                    CreditTransferStatus.valueOf(jsonNode.get("status").asText()),
                    UUID.fromString(jsonNode.get("idBonificoExtraSepa").asText()),
                    new CIPReply.Deserializer().init(jsonNode.get("cipReply"))
            );
        }
    }
}
