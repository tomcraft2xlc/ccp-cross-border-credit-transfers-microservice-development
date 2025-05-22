//package test;
//
//import com.flowpay.ccp.credit.transfer.cross.border.controllers.BankInfoController;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.junit.QuarkusTest;
//import org.junit.jupiter.api.Test;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.*;
//
//@QuarkusTest
//@TestHTTPEndpoint(BankInfoController.class)
//class BankInfoTest {
//
//    @Test
//    void testGetBankInfo() {
//
//        given().headers(Utils.securityHeaders())
//        .get()
//        .then()
//        .statusCode(200)
//        .body("target.enabled", is(true))
//        .body("target.mediatedBy", is(emptyOrNullString()))
//        .body("cbpr.enabled", is(true))
//        .body("cbpr.mediatedBy", is(emptyOrNullString()))
//        .body("exchangeRateTable", is(false));
//    }
//}
