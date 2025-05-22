package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.credit.transfer.cross.border.TestRequestObjects;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.TestAuthUtils;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaClienteRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.BonificoExtraSepaRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaBancaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausale;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleCliente;
import com.flowpay.ccp.persistence.DataSources;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
@TestHTTPEndpoint(CreditTransferController.class)
class CreditTransferTest {

    DataSources dataSources;
    PgPool dataSource;
    TestAuthUtils authUtils;
    TestRequestObjects requestObjects;

    CreditTransferTest(DataSources dataSources, TestAuthUtils authUtils, TestRequestObjects requestObjects) {
        this.dataSources = dataSources;
        this.requestObjects = requestObjects;
        this.authUtils = authUtils;
    }

    @BeforeEach
    void setUp() {
        dataSource = dataSources.getDataSource("22222");
    }

    /**
     * Tables that needs to be cleared between two tests. The one commented out are
     * filled by static datas
     */
    static final String[] TABLES_TO_CLEAR = new String[] {
            // "canale",
            "informazioni_rapporto",
            "bonifico_extra_sepa",
            "informazioni_rapporto_bonifico_extra_sepa",
            "dettaglio_bonifico_account_to_account",
            "informazioni_ndg",
            "dettaglio_bonifico_banca_a_banca",
            "indirizzo_postale",
            "commissione_account_to_account",
            "commissione_banca_a_banca",
            "informazioni_intermediario",
            // "sotto_tipologia_bonifico",
            // "bonifico_extra_sepa_lista_canali_abilitati",
            // "sotto_tipologia_bonifico_mappatura",
            "informazioni_attore",
            "organizzazione",
            "privato",
            "regulatory_reporting",
            "dettagli_regulatory_reporting",
            "informazioni_aggiuntive_pagamento",
            "riferimenti_aggiuntivi_pagamento",
            "informazioni_causale",
            "dettagli_fiscali",
            "attore_fiscale",
            "dettaglio_importo",
            "informazioni_causale_dettaglio_importo",
            "informazioni_documento_di_riferimento",
            "dettaglio_linea_documento_di_riferimento",
            "dettaglio_linea_documento_di_riferimento_dettaglio_importo",
            "dettagli_pignoramento",
            "record_dettagli_fiscali",
            "dettagli_record_dettagli_fiscali",
            "identificativo_linea_documento",
            "informazioni_sistema_di_regolamento",
            // "bonifico_extra_sepa_canale_stato",
            "bonifico_extra_sepa_info_stato_inserito",
            "storia_stati_bonifico_extra_sepa",
            "bonifico_extra_sepa_info_stato_errore"
    };

    @AfterEach
    void tearDown() {
        dataSource.withTransaction(
                connection -> connection.preparedQuery("SET CONSTRAINTS ALL DEFERRED").execute().flatMap(v -> {
                    List<Uni<Void>> clearingCommands = new ArrayList<>(TABLES_TO_CLEAR.length);
                    for (String table : TABLES_TO_CLEAR) {
                        clearingCommands.add(
                                connection.preparedQuery(String.format("DELETE FROM %s", table)).execute()
                                        .replaceWithVoid());
                    }

                    return Uni.combine().all().unis(clearingCommands).collectFailures().discardItems();
                })).await().indefinitely();
    }

    @Test
    void iTestDovrebberoAndare() {
    }

    @Test
    void bonificoClienteDovrebbeEssereInserito() {
        var request = requestObjects.getPostCliente();
        var response = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/cliente")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    @Test
    void bonificoBancaDovrebbeEssereInserito() {
        var request = requestObjects.getPostBanca();
        var response = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/banca")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    @Test
    void bonificoBancaConNotificaDovrebbeEssereInserito() {
        var request = requestObjects.getPostBancaConNotifica();
        var response = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/banca")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    @Test
    void bonificoClienteInseritoDovrebbeEssereRiottenuto() {
        var request = requestObjects.getPostCliente();
        String id = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/cliente")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .extract().path("id");

        var response = given()
                .headers(authUtils.securityHeaders())
                .get("/{id}", id).then().log().ifError().statusCode(StatusCode.OK).contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    @Test
    void bonificoBancaInseritoDovrebbeEssereRiottenuto() {
        var request = requestObjects.getPostBanca();
        String id = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/banca")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .extract().path("id");

        var response = given()
                .headers(authUtils.securityHeaders())
                .get("/{id}", id).then().log().ifError().statusCode(StatusCode.OK).contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    @Test
    void bonificoBancaConNotificaInseritoDovrebbeEssereRiottenuto() {
        var request = requestObjects.getPostBancaConNotifica();
        String id = given()
                .contentType(ContentType.JSON)
                .headers(authUtils.securityHeaders())
                .body(request)
                .post("/banca")
                .then()
                .log().all()
                .statusCode(StatusCode.CREATED)
                .extract().path("id");

        var response = given()
                .headers(authUtils.securityHeaders())
                .get("/{id}", id).then().log().ifError().statusCode(StatusCode.OK).contentType(ContentType.JSON)
                .extract().as(BonificoExtraSepaRisposta.class);

        controllaBonificoInserito(request, response);
    }

    private void controllaBonificoInserito(InserisciBonificoExtraSepaClienteRichiesta request,
            BonificoExtraSepaRisposta response) {
        assertNotNull(response.id());
        assertNotNull(response.tid());

        assertNull(response.bonificoBanca());

        assertEquals(request.sottoTipologiaBonifico().tipo(),
                response.bonificoCliente().sottoTipologiaBonifico().tipo());
        assertEquals(request.ordinante(),
                response.bonificoCliente().ordinante());
        assertEquals(request.soggettoIstruttore(), response.bonificoCliente().soggettoIstruttore());
        assertEquals(request.debitoreEffettivo(), response.bonificoCliente().debitoreEffettivo());
        assertEquals(request.bancaOrdinante(), response.bonificoCliente().bancaOrdinante());
        assertEquals(request.beneficiario(), response.bonificoCliente().beneficiario());
        assertEquals(request.beneficiarioEffettivo(), response.bonificoCliente().beneficiarioEffettivo());
        assertEquals(request.bancaDestinataria(), response.bonificoCliente().bancaDestinataria());
        assertEquals(request.bancaDelBeneficiario(), response.bonificoCliente().bancaDelBeneficiario());
        assertEquals(request.altriIntermediari(), response.bonificoCliente().altriIntermediari());
        assertEquals(request.sistemaDiRegolamento(), response.bonificoCliente().sistemaDiRegolamento());
        checkDettagliBonifico(request.dettagliBonifico(), response.bonificoCliente().dettagliBonifico());
        checkDettagliCausale(request.dettagliCausale(), response.bonificoCliente().dettagliCausale());
        checkRiferimentiAggiuntivi(request.riferimentiAggiuntivi(),
                response.bonificoCliente().riferimentiAggiuntivi());

        if (request.user() != null) {
            assertEquals(request.user(), response.bonificoCliente().user());
        }

        assertEquals(CreditTransferStatus.INSERITO, response.infoStato().stato());
    }

    private void controllaBonificoInserito(InserisciBonificoExtraSepaBancaRichiesta request,
            BonificoExtraSepaRisposta response) {
        assertNotNull(response.id());
        assertNotNull(response.tid());

        assertNull(response.bonificoCliente());

        assertEquals(request.sottoTipologiaBonifico().tipo(),
                response.bonificoBanca().sottoTipologiaBonifico().tipo());
        assertEquals(request.ordinante(),
                response.bonificoBanca().ordinante());
        assertEquals(request.bancaOrdinante(), response.bonificoBanca().bancaOrdinante());
        assertEquals(request.beneficiario(), response.bonificoBanca().beneficiario());
        assertEquals(request.bancaDestinataria(), response.bonificoBanca().bancaDestinataria());
        assertEquals(request.bancaDelBeneficiario(), response.bonificoBanca().bancaDelBeneficiario());
        assertEquals(request.altriIntermediari(), response.bonificoBanca().altriIntermediari());
        assertEquals(request.sistemaDiRegolamento(), response.bonificoBanca().sistemaDiRegolamento());
        checkDettagliBonifico(request.dettagliBonifico(), response.bonificoBanca().dettagliBonifico());
        checkDettagliCausale(request.dettagliCausale(), response.bonificoBanca().dettagliCausale());
        checkRiferimentiAggiuntivi(request.riferimentiAggiuntivi(),
                response.bonificoBanca().riferimentiAggiuntivi());

        if (request.user() != null) {
            assertEquals(request.user(), response.bonificoBanca().user());
        }

        assertEquals(CreditTransferStatus.INSERITO, response.infoStato().stato());
    }

    private void checkDettagliBonifico(DettagliBonificoExtraSepaCliente expected,
            DettagliBonificoExtraSepaCliente given) {
        assertEquals(expected.dettagliDate(), given.dettagliDate());
        // TODO: this must be checked by hand as it contains rounded values and
        // unordered ones
    }
    private void checkDettagliBonifico(DettagliBonificoExtraSepaBanca expected,
            DettagliBonificoExtraSepaBanca given) {
        assertEquals(expected.dettagliDate(), given.dettagliDate());
        // TODO: this must be checked by hand as it contains rounded values and
        // unordered ones
        assertEquals(expected.dettagliNotifica(), given.dettagliNotifica());
    }

    private void checkDettagliCausale(DettagliCausale expected, DettagliCausale given) {
        assertEquals(expected.codiceCausaleTransazione(), given.codiceCausaleTransazione());
        // TODO: check all causale
    }

    private void checkRiferimentiAggiuntivi(RiferimentiAggiuntivi expected, RiferimentiAggiuntivi given) {
        // TODO: this must be checked by hand as it contains rounded values and
        // unordered ones
    }

    /*
     * private static final UUID channelID1 = UUID.randomUUID();
     * 
     * @ParameterizedTest
     * 
     * @CsvSource({
     * "main_branch_1, 404",
     * "main_branch, 200",
     * "branch, 200"
     * })
     * void getCreditTransferDifferentBranch(String branch, int status) {
     * 
     * var datasource = dataSources.getDataSource("22222");
     * 
     * var channelEntity = new Canale.Entity();
     * channelEntity.repository(datasource).run(channelEntity.insert(new Canale(
     * channelID1,
     * "channel",
     * false
     * ))).await().indefinitely();
     * 
     * var kindEntity = new SottoTipologiaBonifico.Entity();
     * 
     * var kind = new SottoTipologiaBonifico(
     * UUID.randomUUID(),
     * "name",
     * "description",
     * false,
     * null,
     * false
     * );
     * 
     * kindEntity.repository(datasource).run(kindEntity.insert(kind)).await().
     * indefinitely();
     * 
     * var mapEntity = new BonificoExtraSepaListaCanaliAbilitati.Entity();
     * 
     * var map = new BonificoExtraSepaListaCanaliAbilitati(
     * UUID.randomUUID(),
     * kind.id(),
     * channelID1
     * );
     * 
     * mapEntity.repository(datasource).run(mapEntity.insert(map)).await().
     * indefinitely();
     * var creditTransfer = new BonificoExtraSepa(
     * UUID.randomUUID(),
     * "0000000000000001",
     * channelID1,
     * kind.id(),
     * AccountType.ACCOUNT,
     * "account_id",
     * "debtor_id",
     * "owner_id",
     * null,
     * "other",
     * "creditor_name",
     * "creditor_address_line",
     * "creditor_location",
     * "IT",
     * "11111",
     * "BICIPCORXXX",
     * "BICIPCORXXX",
     * null,
     * null,
     * "BICIPCORXXX",
     * "BICRPCORXXX",
     * "BICRMBR1XXX",
     * "BICINTM1XXX",
     * "BICINTM2XXX",
     * "BICINTM3XXX",
     * "BICINTM4XXX",
     * "BICINTM5XXX",
     * "BICINTM6XXX",
     * SistemaDiRegolamento.TARGET,
     * false,
     * new BigDecimal(100),
     * "USD",
     * new BigDecimal("0.1234"),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * "BICCVRG1XXX",
     * "coverage_bank_account_id",
     * "remittance_information",
     * "creditor_bank_instructions",
     * "transaction_code",
     * SettlementMethod.INSTRUCTING_AGENT,
     * "service_level_code",
     * "purpose_code_iso",
     * null,
     * "regulatory_reporting",
     * CreditTransferStatus.INSERITO,
     * null,
     * false,
     * branch,
     * "TEST_USER"
     * );
     * 
     * var creditTransferEntity = new BonificoExtraSepa.Entity();
     * creditTransferEntity.repository(datasource).run(creditTransferEntity.insert(
     * creditTransfer)).await().indefinitely();
     * 
     * var fee = new Fee(
     * UUID.randomUUID(),
     * creditTransfer.id(),
     * FeeKind.CREDITOR,
     * new BigDecimal(1),
     * "fee_description",
     * FeeSettlementKind.CURRENCY
     * );
     * 
     * var feeEntity = new Fee.Entity();
     * feeEntity.repository(datasource).run(feeEntity.insert(fee)).await().
     * indefinitely();
     * 
     * given().headers(Utils.securityHeaders())
     * .get("/" + creditTransfer.id())
     * .then()
     * .statusCode(status);
     * }
     * 
     * 
     * @ParameterizedTest
     * 
     * @CsvSource({
     * "TO_BE_MANAGED, INSERTED, 200",
     * "TO_BE_MANAGED, DELETED, 200",
     * "TO_BE_MANAGED, TO_BE_CONFIRMED, 400",
     * "INSERTED, CONFIRMED, 400",
     * "TO_BE_CONFIRMED, CONFIRMED, 200",
     * "TO_BE_CONFIRMED, DELETED, 200",
     * "TO_BE_CONFIRMED, INSERTED, 400",
     * "CONFIRMED, CONFIRMED, 400",
     * "DELETED, CONFIRMED, 400",
     * "WCL_NOT_PASSED, TO_BE_AUTHORIZED, 200",
     * "WCL_NOT_PASSED, WCL_NOT_GRANTED, 200",
     * "WCL_NOT_PASSED, INSERTED, 400",
     * "WCL_NOT_GRANTED, TO_BE_AUTHORIZED, 400",
     * "TO_BE_AUTHORIZED, AUTHORIZED, 200", //TODO: probabilmente rimuovere questo
     * test
     * "TO_BE_AUTHORIZED, NOT_AUTHORIZED_REIMBURSEMENT_SCHEDULED, 200",
     * "TO_BE_AUTHORIZED, INSERTED, 400",
     * "AUTHORIZED, AUTHORIZED, 400",
     * "SENT, CHARGE_BACK_REQUESTED, 200",
     * "SENT, INSERTED, 400",
     * "REGULATED, CHARGE_BACK_REQUESTED, 200",
     * "REGULATED, INSERTED, 400",
     * "NOT_AUTHORIZED_REIMBURSEMENT_SCHEDULED, INSERTED, 400",
     * "NOT_AUTHORIZED, INSERTED, 400",
     * "REJECTED_REIMBURSEMENT_SCHEDULED, INSERTED, 400",
     * "REJECTED, INSERTED, 400",
     * "CHARGE_BACK_REQUESTED, INSERTED, 400",
     * "CHARGE_BACK_SENT, INSERTED, 400",
     * "CHARGE_BACK_REJECTED, INSERTED, 400",
     * "CHARGE_BACK_ACCEPTED, CHARGE_BACK_REIMBURSEMENT_SCHEDULED, 200",
     * "CHARGE_BACK_ACCEPTED, INSERTED, 400",
     * "CHARGE_BACK_REIMBURSEMENT_SCHEDULED, INSERTED, 400",
     * "REIMBURSED, INSERTED, 400",
     * "ERRORED, INSERTED, 400"
     * })
     * void testPatchCreditTransfer(CreditTransferStatus initialStatus,
     * CreditTransferStatus newStatus, int expectedStatusCode) {
     * var datasource = dataSources.getDataSource("22222");
     * 
     * var channelEntity = new Canale.Entity();
     * channelEntity.repository(datasource).run(channelEntity.insert(new Canale(
     * channelID1,
     * "channel",
     * false
     * ))).await().indefinitely();
     * 
     * var kindEntity = new SottoTipologiaBonifico.Entity();
     * 
     * var kind = new SottoTipologiaBonifico(
     * UUID.randomUUID(),
     * "name",
     * "description",
     * false,
     * null,
     * false
     * );
     * 
     * kindEntity.repository(datasource).run(kindEntity.insert(kind)).await().
     * indefinitely();
     * 
     * var mapEntity = new BonificoExtraSepaListaCanaliAbilitati.Entity();
     * 
     * var map = new BonificoExtraSepaListaCanaliAbilitati(
     * UUID.randomUUID(),
     * kind.id(),
     * channelID1
     * );
     * 
     * mapEntity.repository(datasource).run(mapEntity.insert(map)).await().
     * indefinitely();
     * var creditTransfer = new BonificoExtraSepa(
     * UUID.randomUUID(),
     * "0000000000000001",
     * channelID1,
     * kind.id(),
     * AccountType.ACCOUNT,
     * "account_id",
     * "debtor_id",
     * "owner_id",
     * null,
     * "other",
     * "creditor_name",
     * "creditor_address_line",
     * "creditor_location",
     * "IT",
     * "11111",
     * "BICIPCORXXX",
     * "BICIPCORXXX",
     * null,
     * null,
     * "BICIPCORXXX",
     * "BICRPCORXXX",
     * "BICRMBR1XXX",
     * "BICINTM1XXX",
     * "BICINTM2XXX",
     * "BICINTM3XXX",
     * "BICINTM4XXX",
     * "BICINTM5XXX",
     * "BICINTM6XXX",
     * SistemaDiRegolamento.TARGET,
     * false,
     * new BigDecimal(100),
     * "USD",
     * new BigDecimal("0.1234"),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * "BICCVRG1XXX",
     * "coverage_bank_account_id",
     * "remittance_information",
     * "creditor_bank_instructions",
     * "transaction_code",
     * SettlementMethod.INSTRUCTING_AGENT,
     * "service_level_code",
     * "purpose_code_iso",
     * null,
     * "regulatory_reporting",
     * initialStatus,
     * null,
     * false,
     * "main_branch",
     * "TEST_USER"
     * );
     * 
     * var creditTransferEntity = new BonificoExtraSepa.Entity();
     * creditTransferEntity.repository(datasource).run(creditTransferEntity.insert(
     * creditTransfer)).await().indefinitely();
     * 
     * var fee = new Fee(
     * UUID.randomUUID(),
     * creditTransfer.id(),
     * FeeKind.CREDITOR,
     * new BigDecimal(1),
     * "fee_description",
     * FeeSettlementKind.CURRENCY
     * );
     * 
     * var feeEntity = new Fee.Entity();
     * feeEntity.repository(datasource).run(feeEntity.insert(fee)).await().
     * indefinitely();
     * 
     * given().headers(Utils.securityHeaders())
     * .body(new PatchCreditTransferRequest(
     * newStatus,
     * null,
     * null,
     * null,
     * null,
     * null,
     * null
     * ))
     * .contentType(ContentType.JSON)
     * .patch("/" + creditTransfer.id())
     * .then()
     * .statusCode(expectedStatusCode);
     * }
     * 
     * @Test
     * void testPatchToBeManaged() {
     * var datasource = dataSources.getDataSource("22222");
     * 
     * var channelEntity = new Canale.Entity();
     * channelEntity.repository(datasource).run(channelEntity.insert(new Canale(
     * channelID1,
     * "channel",
     * false
     * ))).await().indefinitely();
     * 
     * var kindEntity = new SottoTipologiaBonifico.Entity();
     * 
     * var kind = new SottoTipologiaBonifico(
     * UUID.randomUUID(),
     * "name",
     * "description",
     * false,
     * null,
     * false
     * );
     * 
     * kindEntity.repository(datasource).run(kindEntity.insert(kind)).await().
     * indefinitely();
     * 
     * var mapEntity = new BonificoExtraSepaListaCanaliAbilitati.Entity();
     * 
     * var map = new BonificoExtraSepaListaCanaliAbilitati(
     * UUID.randomUUID(),
     * kind.id(),
     * channelID1
     * );
     * 
     * mapEntity.repository(datasource).run(mapEntity.insert(map)).await().
     * indefinitely();
     * var creditTransfer = new BonificoExtraSepa(
     * UUID.randomUUID(),
     * "0000000000000001",
     * channelID1,
     * kind.id(),
     * AccountType.ACCOUNT,
     * "account_id",
     * "debtor_id",
     * "owner_id",
     * null,
     * "other",
     * "creditor_name",
     * "creditor_address_line",
     * "creditor_location",
     * "IT",
     * "11111",
     * "BICIPCORXXX",
     * "BICIPCORXXX",
     * null,
     * null,
     * "BICIPCORXXX",
     * "BICRPCORXXX",
     * "BICRMBR1XXX",
     * "BICINTM1XXX",
     * "BICINTM2XXX",
     * "BICINTM3XXX",
     * "BICINTM4XXX",
     * "BICINTM5XXX",
     * "BICINTM6XXX",
     * SistemaDiRegolamento.TARGET,
     * false,
     * new BigDecimal(100),
     * "USD",
     * new BigDecimal("0.1234"),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * "BICCVRG1XXX",
     * "coverage_bank_account_id",
     * "remittance_information",
     * "creditor_bank_instructions",
     * "transaction_code",
     * SettlementMethod.INSTRUCTING_AGENT,
     * "service_level_code",
     * "purpose_code_iso",
     * null,
     * "regulatory_reporting",
     * CreditTransferStatus.TO_BE_MANAGED,
     * null,
     * false,
     * "main_branch",
     * "TEST_USER"
     * );
     * 
     * var creditTransferEntity = new BonificoExtraSepa.Entity();
     * creditTransferEntity.repository(datasource).run(creditTransferEntity.insert(
     * creditTransfer)).await().indefinitely();
     * 
     * var fee = new Fee(
     * UUID.randomUUID(),
     * creditTransfer.id(),
     * FeeKind.CREDITOR,
     * new BigDecimal(1),
     * "fee_description",
     * FeeSettlementKind.CURRENCY
     * );
     * 
     * var feeEntity = new Fee.Entity();
     * feeEntity.repository(datasource).run(feeEntity.insert(fee)).await().
     * indefinitely();
     * 
     * given().headers(Utils.securityHeaders())
     * .body(new PatchCreditTransferRequest(
     * CreditTransferStatus.INSERITO,
     * LocalDate.of(2025,1,1),
     * LocalDate.of(2025,1,1),
     * LocalDate.of(2025,1,1),
     * new PatchFeeInfo(
     * FeeKind.DEBTOR,
     * List.of(
     * new FeeDetail(
     * "client_fee",
     * FeeSettlementKind.CURRENCY,
     * new PercentageFee(
     * new BigDecimal("0.5"),
     * new BigDecimal("1"),
     * new BigDecimal("2")
     * )
     * ),
     * new FeeDetail(
     * "client_fee_1",
     * FeeSettlementKind.CURRENCY,
     * new FlatFee(
     * new BigDecimal("1")
     * )
     * )
     * ),
     * new FeeDetail(
     * "bank_fee",
     * FeeSettlementKind.CURRENCY,
     * new FlatFee(
     * new BigDecimal("2")
     * )
     * )
     * ),
     * new PatchIntermediaryInfo(
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * new BankInfo(
     * "BICIPC9RXXX"
     * ),
     * null,
     * null,
     * null
     * ),
     * new PatchCreditTransferInfo(
     * new CoverageBankAccount(
     * "coverage_bank_account_id",
     * new BankInfo(
     * "BICIPC9RXXX"
     * )
     * ),
     * new BigDecimal("1.234")
     * )
     * ))
     * .contentType(ContentType.JSON)
     * .patch("/" + creditTransfer.id())
     * .then()
     * .statusCode(200);
     * 
     * var creditTransferWithLinkedEntities =
     * creditTransferEntity.repository(datasource).getByIDAndLock(creditTransfer.id(
     * )).await().indefinitely().linkedEntities();
     * creditTransferWithLinkedEntities.loadAll(datasource).await().indefinitely();
     * 
     * assertEquals(LocalDate.of(2025,1,1),
     * creditTransferWithLinkedEntities.creditTransfer().executionDate());
     * assertEquals(LocalDate.of(2025,1,1),
     * creditTransferWithLinkedEntities.creditTransfer().executionDate());
     * assertEquals(LocalDate.of(2025,1,1),
     * creditTransferWithLinkedEntities.creditTransfer().valueDate());
     * assertEquals(LocalDate.of(2025,1,1),
     * creditTransferWithLinkedEntities.creditTransfer().creditorBankRegulationDate(
     * ));
     * 
     * assertEquals(2, creditTransferWithLinkedEntities.fee().details().size());
     * assertEquals("client_fee",
     * creditTransferWithLinkedEntities.fee().details().get(0).description());
     * assertEquals(new BigDecimal("2.00").setScale(2, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.fee().details().get(0).max().setScale(2,
     * RoundingMode.UNNECESSARY));
     * assertEquals(new BigDecimal("1.00").setScale(2, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.fee().details().get(0).min().setScale(2,
     * RoundingMode.UNNECESSARY));
     * assertEquals(new BigDecimal("0.50").setScale(2, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.fee().details().get(0).percentage().setScale
     * (2, RoundingMode.UNNECESSARY));
     * assertNull(creditTransferWithLinkedEntities.fee().details().get(0).flatAmount
     * ());
     * assertEquals("client_fee_1",
     * creditTransferWithLinkedEntities.fee().details().get(1).description());
     * assertEquals(new BigDecimal("1.00").setScale(2, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.fee().details().get(1).flatAmount().setScale
     * (2, RoundingMode.UNNECESSARY));
     * assertNull(creditTransferWithLinkedEntities.fee().details().get(1).max());
     * assertNull(creditTransferWithLinkedEntities.fee().details().get(1).min());
     * assertNull(creditTransferWithLinkedEntities.fee().details().get(1).percentage
     * ());
     * assertEquals("bank_fee",
     * creditTransferWithLinkedEntities.fee().fee().bankFeeDescription());
     * assertEquals(FeeKind.DEBTOR,
     * creditTransferWithLinkedEntities.fee().fee().bankFeeKind());
     * assertEquals(FeeSettlementKind.CURRENCY,
     * creditTransferWithLinkedEntities.fee().fee().bankSettlementKind());
     * assertEquals(new BigDecimal("2").setScale(2, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.fee().fee().bankFeeAmount().setScale(2,
     * RoundingMode.UNNECESSARY));
     * 
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().destinationBankBIC());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().creditorBankBIC());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().
     * issuingPartyCorrespondentBankBIC());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().
     * receivingPartyCorrespondentBankBIC());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().reimbursementBankBIC());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().intermediaryBankBIC1());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().intermediaryBankBIC2());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().intermediaryBankBIC3());
     * assertEquals("BICINTM4XXX",
     * creditTransferWithLinkedEntities.creditTransfer().
     * previousIntermediaryBankBIC1());
     * assertEquals("BICINTM5XXX",
     * creditTransferWithLinkedEntities.creditTransfer().
     * previousIntermediaryBankBIC2());
     * assertEquals("BICINTM6XXX",
     * creditTransferWithLinkedEntities.creditTransfer().
     * previousIntermediaryBankBIC3());
     * assertEquals("BICIPC9RXXX",
     * creditTransferWithLinkedEntities.creditTransfer().coverageBankBIC());
     * assertEquals("coverage_bank_account_id",
     * creditTransferWithLinkedEntities.creditTransfer().coverageBankAccountID());
     * assertEquals(new BigDecimal("1.234").setScale(3, RoundingMode.UNNECESSARY),
     * creditTransferWithLinkedEntities.creditTransfer().conversionRate().setScale(
     * 3, RoundingMode.UNNECESSARY));
     * }
     * 
     * @ParameterizedTest
     * 
     * @CsvSource({
     * "not_found, none, destinationBank, 401",
     * "name, none, destinationBank;creditorBank, 201",
     * "name, intermediaryInfo.issuingPartyCorrespondentBank.bic, destinationBank;creditorBank, 400"
     * ,
     * "name, intermediaryInfo.issuingPartyCorrespondentBank.bic;intermediaryInfo.receivingPartyCorrespondentBank.bic, destinationBank;creditorBank;issuingPartyCorrespondentBank, 400"
     * ,
     * "name, intermediaryInfo.issuingPartyCorrespondentBank.bic;intermediaryInfo.receivingPartyCorrespondentBank.bic, destinationBank;creditorBank;issuingPartyCorrespondentBank;receivingPartyCorrespondentBank, 201"
     * ,
     * })
     * void insertCreditTransfer(
     * String creditTransferKind,
     * String requiredFields,
     * String intermediaryFields,
     * int expectedStatusCode) {
     * var datasource = dataSources.getDataSource("22222");
     * 
     * var channelEntity = new Canale.Entity();
     * channelEntity.repository(datasource).run(channelEntity.insert(new Canale(
     * channelID1,
     * "channel",
     * false
     * ))).await().indefinitely();
     * 
     * var kindEntity = new SottoTipologiaBonifico.Entity();
     * 
     * String reqFields = requiredFields.equals("none") ? null :
     * requiredFields.replace(';', ',');
     * 
     * var kind = new SottoTipologiaBonifico(
     * UUID.randomUUID(),
     * "name",
     * "description",
     * false,
     * reqFields,
     * false
     * );
     * 
     * kindEntity.repository(datasource).run(kindEntity.insert(kind)).await().
     * indefinitely();
     * 
     * var mapEntity = new BonificoExtraSepaListaCanaliAbilitati.Entity();
     * 
     * var map = new BonificoExtraSepaListaCanaliAbilitati(
     * UUID.randomUUID(),
     * kind.id(),
     * channelID1
     * );
     * 
     * mapEntity.repository(datasource).run(mapEntity.insert(map)).await().
     * indefinitely();
     * 
     * var statusMapEntity = new BonificoExtraSepaCanaleStato.Entity();
     * 
     * var statusMap = new BonificoExtraSepaCanaleStato(
     * UUID.randomUUID(),
     * channelID1,
     * CreditTransferStatus.INSERITO
     * );
     * 
     * statusMapEntity.repository(datasource).run(statusMapEntity.insert(statusMap))
     * .await().indefinitely();
     * 
     * var intermediaryInfo = getIntermediaryInfo(intermediaryFields);
     * 
     * given().headers(Utils.securityHeaders())
     * .body(new InserisciBonificoExtraSepaClienteRichiesta(
     * new com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.
     * SottoTipologiaBonifico(
     * creditTransferKind
     * ),
     * new InstructingPartyAccountInfo(
     * AccountType.ACCOUNT,
     * "account_id"
     * ),
     * new InstructingPartyInfo(
     * "ndg"
     * ),
     * null,
     * new CreditorInfo(
     * new IBANAccountInfo(
     * "IBAN"
     * ),
     * "creditor_name",
     * new AddressInfo(
     * "address_line",
     * "location",
     * "IT",
     * "11111"
     * )
     * ),
     * intermediaryInfo,
     * new SettlementInfo(
     * SistemaDiRegolamento.TARGET,
     * null
     * ),
     * new CreditTransferInfo(
     * new Importo(
     * new BigDecimal("100"),
     * "USD"
     * ),
     * new BigDecimal("0.1234"),
     * null,
     * LocalDate.now(),
     * LocalDate.now(),
     * LocalDate.now(),
     * new CoverageBankAccount(
     * "coverage_bank_account_id",
     * new BankInfo(
     * "BICIPC9RXXX"
     * )
     * ),
     * new FeeInfo(
     * FeeKind.SHARED,
     * null,
     * new FeeDetail(
     * "bank_fee",
     * FeeSettlementKind.CURRENCY,
     * new FlatFee(
     * new BigDecimal("2")
     * )
     * )
     * ),
     * "remittance_information",
     * "creditor_bank_instructions",
     * "transaction_code"
     * ),
     * new SwiftMessageInfo(
     * SettlementMethod.INSTRUCTED_AGENT,
     * "service_level_code",
     * new ProprietaryPurpose("purpose_code_proprietary"),
     * "regulatory_reporting"
     * ),
     * null
     * ))
     * .contentType(ContentType.JSON)
     * .post()
     * .then()
     * .statusCode(expectedStatusCode);
     * }
     * 
     * private static IntermediaryInfo getIntermediaryInfo(String
     * intermediaryFields) {
     * var fields = intermediaryFields.split(";");
     * 
     * BankInfo destinationBank = null;
     * BankInfo creditorBank = null;
     * BankInfo issuingPartyCorrespondentBank = null;
     * BankInfo receivingPartyCorrespondentBank = null;
     * BankInfo reimbursementBank = null;
     * BankInfo intermediaryBank1 = null;
     * BankInfo intermediaryBank2 = null;
     * BankInfo intermediaryBank3 = null;
     * BankInfo previousIntermediaryBank1 = null;
     * BankInfo previousIntermediaryBank2 = null;
     * BankInfo previousIntermediaryBank3 = null;
     * 
     * for (var field : fields) {
     * switch (field) {
     * case "destinationBank":
     * destinationBank = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "creditorBank":
     * creditorBank = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "issuingPartyCorrespondentBank":
     * issuingPartyCorrespondentBank = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "receivingPartyCorrespondentBank":
     * receivingPartyCorrespondentBank = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "reimbursementBank":
     * reimbursementBank = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "intermediaryBank1":
     * intermediaryBank1 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "intermediaryBank2":
     * intermediaryBank2 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "intermediaryBank3":
     * intermediaryBank3 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "previousIntermediaryBank1":
     * previousIntermediaryBank1 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "previousIntermediaryBank2":
     * previousIntermediaryBank2 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * case "previousIntermediaryBank3":
     * previousIntermediaryBank3 = new BankInfo(
     * "BICIPC9RXXX"
     * );
     * break;
     * default:
     * break;
     * }
     * }
     * 
     * return new IntermediaryInfo(
     * destinationBank,
     * creditorBank,
     * issuingPartyCorrespondentBank,
     * receivingPartyCorrespondentBank,
     * reimbursementBank,
     * intermediaryBank1,
     * intermediaryBank2,
     * intermediaryBank3,
     * previousIntermediaryBank1,
     * previousIntermediaryBank2,
     * previousIntermediaryBank3
     * );
     * }
     */}
