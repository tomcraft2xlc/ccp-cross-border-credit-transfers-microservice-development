package com.flowpay.ccp.credit.transfer.cross.border.errors;

@SuppressWarnings("unused")
public class MappingError extends RuntimeException {

    public MappingError(String message) {
        super(message);
    }

    public MappingError(Throwable cause) {
        super(cause);
    }
}
