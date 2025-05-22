package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;

public interface ConfirmationCallStatus {
    /** La call deve ricevere risposta */
    boolean isWaitingForAnswer();
    /** La risposta della call è stata positiva */
    boolean isConfirmed();
}
