package com.flowpay.ccp.credit.transfer.cross.border.exception;

import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@ApplicationScoped
public class ExceptionMapper {

    private record ErrorResponse(ErrorCodes.Codes code, String message) {

    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(ErrorCodes exception) {
        return RestResponse.status(exception.code.responseCode(), new ErrorResponse(
                exception.code,
                exception.getMessage()));
    }
}
