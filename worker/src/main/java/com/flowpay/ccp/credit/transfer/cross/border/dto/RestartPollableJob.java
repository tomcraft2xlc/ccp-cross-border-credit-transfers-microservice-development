package com.flowpay.ccp.credit.transfer.cross.border.dto;

import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;

public record RestartPollableJob(
        String identifier
) implements ProcessInterruption {
    @Override
    public String getIdentifier() {
        return identifier;
    }
}
