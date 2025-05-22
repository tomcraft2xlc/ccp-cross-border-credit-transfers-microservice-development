package com.flowpay.ccp.credit.transfer.cross.border.exceptions;

import com.flowpay.ccp.business.log.handler.process.ProcessInterruption;

import java.util.UUID;

public class ChangeDateInterrupted extends RuntimeException implements ProcessInterruption {

  private final UUID id;

  public ChangeDateInterrupted(String message, UUID id) {
    super(message);
    this.id = id;
  }

  @Override
  public String getIdentifier() {
    return id.toString();
  }
}
