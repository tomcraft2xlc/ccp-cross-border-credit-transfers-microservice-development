package com.flowpay.ccp.credit.transfer.cross.border.exceptions;

import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;
import com.flowpay.ccp.resources.poll.client.MultiplePollableResourceAccepted;

import java.util.UUID;

public class MultipleResourceNotReadyInterruption extends MultiplePollableResourceAccepted implements ProcessInterruption {

    private final UUID id;

    public MultipleResourceNotReadyInterruption(MultiplePollableResourceAccepted original, UUID id) {
      super(original.getPollableResourceAccepteds());
        this.id = id;
    }

    @Override
    public String getIdentifier() {
      return id.toString();
    }
}
