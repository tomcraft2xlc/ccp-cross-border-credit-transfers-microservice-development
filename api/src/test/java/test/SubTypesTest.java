//package test;
//
//
//import com.flowpay.ccp.credit.transfer.cross.border.controllers.CreditTransferKindController;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.BonificoExtraSepaListaCanaliAbilitati;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
//import com.flowpay.ccp.persistence.DataSources;
//import io.quarkus.test.common.http.TestHTTPEndpoint;
//import io.quarkus.test.junit.QuarkusTest;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.UUID;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.is;
//
//@QuarkusTest
//@TestHTTPEndpoint(CreditTransferKindController.class)
//class SubTypesTest {
//
//    private final DataSources dataSources;
//
//    SubTypesTest(DataSources dataSources) {
//        this.dataSources = dataSources;
//    }
//
//    private static final UUID channelID1 = UUID.randomUUID();
//    private static final UUID channelID2 = UUID.randomUUID();
//
//    @BeforeEach
//    void setUp() {
//        var datasource = dataSources.getDataSource("22222");
//        var channel = new Canale(
//                channelID1,
//                "channel",
//                false
//        );
//
//        var entity = new Canale.Entity();
//        var repository = entity.repository(datasource);
//
//        repository.run(entity.insert(channel)).await().indefinitely();
//        repository.run(entity.insert(new Canale(
//                channelID2,
//                "channel_1",
//                false
//        ))).await().indefinitely();
//    }
//
//    @AfterEach
//    void tearDown() {
//        dataSources.getDataSource("22222").preparedQuery("DELETE FROM credit_transfer_channel_allowed_list").execute().await().indefinitely();
//        dataSources.getDataSource("22222").preparedQuery("DELETE FROM credit_transfer_kind").execute().await().indefinitely();
//        dataSources.getDataSource("22222").preparedQuery("DELETE FROM channel_config").execute().await().indefinitely();
//    }
//
//    @Test
//    void testGetTypes() {
//
//        var entity = new SottoTipologiaBonifico.Entity();
//        var repository = entity.repository(dataSources.getDataSource("22222"));
//
//        var kindID1 = UUID.randomUUID();
//        var kindID2 = UUID.randomUUID();
//
//        repository.run(entity.insert(
//                new SottoTipologiaBonifico(
//                        kindID1,
//                        "name",
//                        "description",
//                        false,
//                        null,
//                        false
//                )
//        )).await().indefinitely();
//
//        repository.run(entity.insert(
//                new SottoTipologiaBonifico(
//                        kindID2,
//                        "name_1",
//                        "description",
//                        false,
//                        null,
//                        false
//                )
//        )).await().indefinitely();
//
//        var allowedList = new BonificoExtraSepaListaCanaliAbilitati.Entity();
//        repository.run(allowedList.insert(
//                new BonificoExtraSepaListaCanaliAbilitati(
//                        UUID.randomUUID(),
//                        kindID1,
//                        channelID1
//                )
//        )).await().indefinitely();
//
//        repository.run(allowedList.insert(
//                new BonificoExtraSepaListaCanaliAbilitati(
//                        UUID.randomUUID(),
//                        kindID2,
//                        channelID2
//                )
//        )).await().indefinitely();
//
//        given()
//        .headers(Utils.securityHeaders())
//        .get("/client")
//        .then()
//        .statusCode(200)
//        .body("creditTransferKinds.size()", is(1))
//        .body("creditTransferKinds[0].name", is("name"))
//        .body("creditTransferKinds[0].description", is("description"));
//    }
//}
