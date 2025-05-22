package com.flowpay.ccp.credit.transfer.cross.border.exceptions;


import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;

public class ConfirmationFailure extends ErrorCodes {

    public ConfirmationFailure(Codes code) {
        super(code);
    }

    public ConfirmationFailure(Codes code, String message) {
        super(code, message);
    }

}
