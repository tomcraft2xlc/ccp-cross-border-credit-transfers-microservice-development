package com.flowpay.ccp.credit.transfer.cross.border;

import com.flowpay.ccp.auth.client.AuthConstants;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestAuthUtils {

    private TestAuthUtils() {

    }

    public Headers securityHeaders() {
        return Headers.headers(
                new Header(AuthConstants.HEADER_USER_FULLNAME, "fullName"),
                new Header(AuthConstants.HEADER_ROLE_DESCRIPTION, "roleDescription"),
                new Header(AuthConstants.HEADER_FUNCTIONALITY, "credit-transfer"),
                new Header(AuthConstants.HEADER_PROFILE, "profile"),
                new Header(AuthConstants.HEADER_ROLE, "role"),
                new Header(AuthConstants.HEADER_ABI, "22222"),
                new Header(AuthConstants.HEADER_USER_TOKEN, "USER_TOKEN"),
                new Header(AuthConstants.HEADER_BRANCH, "2"),
                new Header(AuthConstants.HEADER_MAIN_BRANCH, "1"),
                new Header(AuthConstants.HEADER_CHANNEL, "ccp"));
    }
}
