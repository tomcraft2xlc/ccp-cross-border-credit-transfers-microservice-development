package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

public interface VerifyCallStatus {
    /** La call deve ricevere risposta */
    boolean isWaitingForAnswer();
    /** La risposta della call è stata positiva */
    boolean isVerified();
}
