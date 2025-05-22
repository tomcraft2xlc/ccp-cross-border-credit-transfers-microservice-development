package com.flowpay.ccp.credit.transfer.cross.border.exceptions;

import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.resources.poll.client.PollableResourceAccepted;

public class ResourceNotReadyInterruption extends PollableResourceAccepted implements ProcessInterruption {


    public ResourceNotReadyInterruption(PollableResourceAccepted original) {
        super(original.getMessage(), original.getPollResourceID(), original.getResourceName());
    }

    @Override
    public String getIdentifier() {
        return getResourceName() + ":" + getPollResourceID().toString();
    }
}
