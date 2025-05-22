package com.flowpay.ccp.credit.transfer.cross.border.errors;

@SuppressWarnings("unused")
public class InternalProcessError extends RuntimeException {
    public InternalProcessError(String message) {
        super(message);
    }

    public InternalProcessError(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalProcessError(Throwable cause) {
        super(cause);
    }
}
