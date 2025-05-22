package com.flowpay.ccp.credit.transfer.cross.border.exceptions;


import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;

public class VerifyFailure extends ErrorCodes {

    public VerifyFailure(Codes code) {
        super(code);
    }

    public VerifyFailure(Codes code, String message) {
        super(code, message);
    }

}
