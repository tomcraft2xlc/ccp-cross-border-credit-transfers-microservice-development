//package test;
//
//import com.flowpay.ccp.auth.client.CabelClientAuthenticationMechanism;
//import com.flowpay.ccp.credit.transfer.cross.border.AccountType;
//import com.flowpay.ccp.credit.transfer.cross.border.Constants;
//import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
//import com.flowpay.ccp.credit.transfer.cross.border.dto.fee.FeeKind;
//import com.flowpay.ccp.credit.transfer.cross.border.dto.fee.FeeSettlementKind;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
//import com.flowpay.ccp.credit.transfer.cross.border.dto.settlement.SettlementMethod;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
//import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted.InsertedSubStatus;
//import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
//import com.flowpay.ccp.job.JobData;
//import com.flowpay.ccp.job.JobPublisher;
//import com.flowpay.ccp.persistence.DataSources;
//import com.flowpay.ccp.resources.poll.client.dto.PollBucket;
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.smallrye.common.vertx.VertxContext;
//import io.vertx.mutiny.core.Vertx;
//import io.vertx.mutiny.sqlclient.Tuple;
//import jakarta.inject.Inject;
//import test.mock.WireMock;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@QuarkusTest
//@QuarkusTestResource(WireMock.class)
//class TestWorkers {
//
//    @Inject
//    JobPublisher jobPublisher;
//
//    @Inject
//    DataSources dataSources;
//
//
//    UUID channelID = UUID.randomUUID();
//
//    @BeforeEach
//    public void setUp() {
//        var sqlClient = dataSources.getDataSource("22222");
//        sqlClient.preparedQuery("""
//        INSERT INTO channel_config (id, channel_id, required_to_send_user)
//        VALUES
//        ($1, 'test_channel', false)
//        """).execute(Tuple.of(channelID)).await().indefinitely();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        var sqlClient = dataSources.getDataSource("22222");
//        sqlClient.preparedQuery("DELETE from credit_transfer_fee_detail").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_fee").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from debtor_account").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_errored_status_info").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_inserted_status_info").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_channel_status_map").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from destination_bank_bic").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_status_history").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_channel_allowed_list").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_kind_message").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from credit_transfer_kind").execute().await().indefinitely();
//        sqlClient.preparedQuery("DELETE from channel_config").execute().await().indefinitely();
//
//    }
//
//    private static final String BOTH_BIC = "CIPBITM1XXX";
//    private static final String TARGET_BIC = "CIPBITM2XXX";
//    private static final String CBPR_BIC = "CIPBITM3XXX";
//
//    private List<BonificoExtraSepa> setupCreditTransfer(
//            UUID creditTransferKindID,
//            CreditTransferStatus status,
//            String destinationBankBIC,
//            String creditorBankBIC) {
//        var sqlClient = dataSources.getDataSource("22222");
//        var entity = new BonificoExtraSepa.Entity();
//        var repository = entity.repository(sqlClient);
//        var entityFee = new Fee.Entity();
//        var repositoryFee = entityFee.repository(sqlClient);
//        var entityDebtorAccount = new DebtorAccount.Entity();
//        var repositoryDebtorAccount = entityDebtorAccount.repository(sqlClient);
//
//        var creditTransfer = new BonificoExtraSepa(
//                UUID.randomUUID(),
//                "0000000000000001",
//                channelID,
//                creditTransferKindID,
//                AccountType.ACCOUNT,
//                "account_id",
//                "debtor_id",
//                "owner_id",
//                null,
//                "other",
//                "creditor_name",
//                "creditor_address_line",
//                "creditor_location",
//                "IT",
//                "11111",
//                destinationBankBIC,
//                creditorBankBIC,
//                null,
//                null,
//                "BICIPCORXXX",
//                "BICRPCORXXX",
//                "BICRMBR1XXX",
//                "BICINTM1XXX",
//                "BICINTM2XXX",
//                "BICINTM3XXX",
//                "BICINTM4XXX",
//                "BICINTM5XXX",
//                "BICINTM6XXX",
//                SistemaDiRegolamento.TARGET,
//                false,
//                new BigDecimal(100),
//                "USD",
//                new BigDecimal("0.1234"),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                "BICCVRG1XXX",
//                "coverage_bank_account_id",
//                "remittance_information",
//                "creditor_bank_instructions",
//                "transaction_code",
//                SettlementMethod.INSTRUCTING_AGENT,
//                "service_level_code",
//                "purpose_code_iso",
//                null,
//                "regulatory_reporting",
//                status,
//                null,
//                false,
//                "branch",
//                "TEST_USER"
//        );
//        repository.run(entity.insert(creditTransfer)).await().indefinitely();
//        var fee = new Fee(
//              UUID.randomUUID(),
//                creditTransfer.id(),
//                FeeKind.CREDITOR,
//                new BigDecimal(1),
//                "fee_description",
//                FeeSettlementKind.CURRENCY
//        );
//        repositoryFee.run(entityFee.insert(fee)).await().indefinitely();
//        var debtorAccount = new DebtorAccount(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                "account_name",
//                "iban",
//                null,
//                "address_line",
//                "address_locality",
//                "IT",
//                "11111",
//                "fiscal_code"
//        );
//        repositoryDebtorAccount.run(entityDebtorAccount.insert(debtorAccount)).await().indefinitely();
//
//        var result = new ArrayList<BonificoExtraSepa>();
//
//        result.add(creditTransfer);
//        creditTransfer = new BonificoExtraSepa(
//                UUID.randomUUID(),
//                "0000000000000001",
//                channelID,
//                creditTransferKindID,
//                AccountType.ACCOUNT,
//                "account_id",
//                "debtor_id",
//                "owner_id",
//                "iban",
//                null,
//                "creditor_name",
//                "creditor_address_line",
//                "creditor_location",
//                "IT",
//                "11111",
//                destinationBankBIC,
//                creditorBankBIC,
//                null,
//                null,
//                "BRTOITT4XXX",
//                "BRTOITT5XXX",
//                "BRTOITT6XXX",
//                "BRTOITT7XXX",
//                "BRTOITT8XXX",
//                "BRTOITT9XXX",
//                "BRTOIT10XXX",
//                "BRTOIT11XXX",
//                "BRTOIT12XXX",
//                SistemaDiRegolamento.NO_TARGET,
//                true,
//                new BigDecimal(100),
//                "USD",
//                new BigDecimal("0.1234"),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                "BICCVRG1XXX",
//                "coverage_bank_account_id",
//                "remittance_information",
//                "creditor_bank_instructions",
//                "transaction_code",
//                SettlementMethod.INSTRUCTING_AGENT,
//                "service_level_code",
//                "purpose_code_iso",
//                null,
//                "regulatory_reporting",
//                status,
//                null,
//                false,
//                "branch",
//                "TEST_USER"
//        );
//        repository.run(entity.insert(creditTransfer)).await().indefinitely();
//        fee = new Fee(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                FeeKind.CREDITOR,
//                new BigDecimal(1),
//                "fee_description",
//                FeeSettlementKind.CURRENCY
//        );
//        repositoryFee.run(entityFee.insert(fee)).await().indefinitely();
//        debtorAccount = new DebtorAccount(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                "account_name",
//                "iban",
//                null,
//                "address_line",
//                "address_locality",
//                "IT",
//                "11111",
//                "fiscal_code"
//        );
//        repositoryDebtorAccount.run(entityDebtorAccount.insert(debtorAccount)).await().indefinitely();
//        result.add(creditTransfer);
//
//        creditTransfer = new BonificoExtraSepa(
//                UUID.randomUUID(),
//                "0000000000000001",
//                channelID,
//                creditTransferKindID,
//                AccountType.ACCOUNT,
//                "account_id",
//                "debtor_id",
//                "owner_id",
//                null,
//                "accountID",
//                "creditor_name",
//                "creditor_address_line",
//                "creditor_location",
//                "IT",
//                "11111",
//                destinationBankBIC,
//                creditorBankBIC,
//                null,
//                null,
//                "BRTOITT4XXX",
//                "BRTOITT5XXX",
//                "BRTOITT6XXX",
//                "BRTOITT7XXX",
//                "BRTOITT8XXX",
//                "BRTOITT9XXX",
//                "BRTOIT10XXX",
//                "BRTOIT11XXX",
//                "BRTOIT12XXX",
//                SistemaDiRegolamento.NO_TARGET,
//                false,
//                new BigDecimal(100),
//                "USD",
//                new BigDecimal("0.1234"),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                "BICCVRG1XXX",
//                "coverage_bank_account_id",
//                "remittance_information",
//                "creditor_bank_instructions",
//                "transaction_code",
//                SettlementMethod.INSTRUCTING_AGENT,
//                "service_level_code",
//                "purpose_code_iso",
//                null,
//                "regulatory_reporting",
//                status,
//                null,
//                false,
//                "branch",
//                "TEST_USER"
//        );
//        repository.run(entity.insert(creditTransfer)).await().indefinitely();
//        fee = new Fee(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                FeeKind.CREDITOR,
//                new BigDecimal(1),
//                "fee_description",
//                FeeSettlementKind.CURRENCY
//        );
//        repositoryFee.run(entityFee.insert(fee)).await().indefinitely();
//        debtorAccount = new DebtorAccount(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                "account_name",
//                "iban",
//                null,
//                "address_line",
//                "address_locality",
//                "IT",
//                "11111",
//                "fiscal_code"
//        );
//        repositoryDebtorAccount.run(entityDebtorAccount.insert(debtorAccount)).await().indefinitely();
//
//        result.add(creditTransfer);
//        creditTransfer = new BonificoExtraSepa(
//                UUID.randomUUID(),
//                "0000000000000001",
//                channelID,
//                creditTransferKindID,
//                AccountType.ACCOUNT,
//                "account_id",
//                "debtor_id",
//                "owner_id",
//                "iban",
//                null,
//                "creditor_name",
//                "creditor_address_line",
//                "creditor_location",
//                "IT",
//                "11111",
//                destinationBankBIC,
//                creditorBankBIC,
//                null,
//                null,
//                "BRTOITT4XXX",
//                "BRTOITT5XXX",
//                "BRTOITT6XXX",
//                "BRTOITT7XXX",
//                "BRTOITT8XXX",
//                "BRTOITT9XXX",
//                "BRTOIT10XXX",
//                "BRTOIT11XXX",
//                "BRTOIT12XXX",
//                SistemaDiRegolamento.TARGET,
//                false,
//                new BigDecimal(100),
//                "USD",
//                new BigDecimal("0.1234"),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                "BICCVRG1XXX",
//                "coverage_bank_account_id",
//                "remittance_information",
//                "creditor_bank_instructions",
//                "transaction_code",
//                SettlementMethod.INSTRUCTING_AGENT,
//                "service_level_code",
//                "purpose_code_iso",
//                null,
//                "regulatory_reporting",
//                status,
//                null,
//                false,
//                "branch",
//                "TEST_USER"
//        );
//        repository.run(entity.insert(creditTransfer)).await().indefinitely();
//        fee = new Fee(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                FeeKind.CREDITOR,
//                new BigDecimal(1),
//                "fee_description",
//                FeeSettlementKind.CURRENCY
//        );
//        repositoryFee.run(entityFee.insert(fee)).await().indefinitely();
//        debtorAccount = new DebtorAccount(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                "account_name",
//                "iban",
//                null,
//                "address_line",
//                "address_locality",
//                "IT",
//                "11111",
//                "fiscal_code"
//        );
//        repositoryDebtorAccount.run(entityDebtorAccount.insert(debtorAccount)).await().indefinitely();
//
//        result.add(creditTransfer);
//        creditTransfer = new BonificoExtraSepa(
//                UUID.randomUUID(),
//                "0000000000000001",
//                channelID,
//                creditTransferKindID,
//                AccountType.ACCOUNT,
//                "account_id",
//                "debtor_id",
//                "owner_id",
//                "iban",
//                null,
//                "creditor_name",
//                "creditor_address_line",
//                "creditor_location",
//                "IT",
//                "11111",
//                destinationBankBIC,
//                creditorBankBIC,
//                null,
//                null,
//                "BRTOITT4XXX",
//                "BRTOITT5XXX",
//                "BRTOITT6XXX",
//                "BRTOITT7XXX",
//                "BRTOITT8XXX",
//                "BRTOITT9XXX",
//                "BRTOIT10XXX",
//                "BRTOIT11XXX",
//                "BRTOIT12XXX",
//                SistemaDiRegolamento.TARGET,
//                true,
//                new BigDecimal(100),
//                "USD",
//                new BigDecimal("0.1234"),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now(),
//                "BICCVRG1XXX",
//                "coverage_bank_account_id",
//                "remittance_information",
//                "creditor_bank_instructions",
//                "transaction_code",
//                SettlementMethod.INSTRUCTING_AGENT,
//                "service_level_code",
//                "purpose_code_iso",
//                null,
//                "regulatory_reporting",
//                status,
//                null,
//                false,
//                "branch",
//                "TEST_USER"
//        );
//        repository.run(entity.insert(creditTransfer)).await().indefinitely();
//        fee = new Fee(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                FeeKind.CREDITOR,
//                new BigDecimal(1),
//                "fee_description",
//                FeeSettlementKind.CURRENCY
//        );
//        repositoryFee.run(entityFee.insert(fee)).await().indefinitely();
//        debtorAccount = new DebtorAccount(
//                UUID.randomUUID(),
//                creditTransfer.id(),
//                "account_name",
//                "iban",
//                null,
//                "address_line",
//                "address_locality",
//                "IT",
//                "11111",
//                "fiscal_code"
//        );
//        repositoryDebtorAccount.run(entityDebtorAccount.insert(debtorAccount)).await().indefinitely();
//
//        result.add(creditTransfer);
//        return result;
//    }
//
//    @Test
//    void testHandleInsertedCreditTransfer() {
//        var sqlClient = dataSources.getDataSource("22222");
//        var creditTransferKindID = UUID.randomUUID();
//        sqlClient.preparedQuery("""
//        INSERT INTO credit_transfer_kind (id, name, description, produce_mt_999, is_bank_to_bank)
//        VALUES
//        ($1, 'test_kind', 'test_kind_description', false, false)
//        """).execute(Tuple.of(creditTransferKindID)).await().indefinitely();
//
//        sqlClient.preparedQuery("""
//        INSERT INTO credit_transfer_channel_allowed_list (id, credit_transfer_kind_id, channel_id)
//        VALUES
//        (gen_random_uuid(), $1, $2)
//        """).execute(Tuple.of(creditTransferKindID, channelID)).await().indefinitely();
//
//        var creditTransfers = setupCreditTransfer(
//                creditTransferKindID,
//                CreditTransferStatus.INSERITO,
//                "NOTREADY",
//                "NOTREADY");
//        var creditTransfer = creditTransfers.get(0);
//        var ctx = VertxContext.createNewDuplicatedContext(Vertx.vertx().getOrCreateContext().getDelegate());
//        var identity = CabelClientAuthenticationMechanism.build(
//                "TEST_PROFILE",
//                List.of(""),
//                "22222",
//                "TEST_ROLE",
//                "TOKEN",
//                "branch",
//                List.of(),
//                "test_channel"
//        );
//        ctx.executeBlocking(() -> {
//            CabelClientAuthenticationMechanism.setupTestMetadata(identity);
//            return jobPublisher.scheduleJob(new JobData(
//                    UUID.randomUUID(),
//                    Constants.JOB_HANDLE_CREDIT_TRANSFER,
//                    new HandleCreditTransferPayload(
//                            creditTransfer.id()
//                    )
//            )).await().indefinitely();
//        });
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        var entity = new BonificoExtraSepa.Entity();
//        var repository = entity.repository(sqlClient);
//        var result = repository.get(creditTransfer.id(), List.of(creditTransfer.branch()))
//                .await().indefinitely().orElseThrow();
//
//        var history = result.linkedEntities().getHistory(sqlClient).await().indefinitely();
//
//        assertTrue(result.isUpdating());
//        assertEquals(CreditTransferStatus.INSERITO, result.status());
//        assertEquals(1, history.size());
//        assertEquals(InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_IN_ATTESA.name(), history.get(0).newStatus());
//
//        ctx.executeBlocking(() -> {
//            CabelClientAuthenticationMechanism.setupTestMetadata(identity);
//            return jobPublisher.scheduleJob(new JobData(
//                    UUID.randomUUID(),
//                    Constants.JOB_RESTART_CHECK_CREDIT_TRANSFER_SETTLEMENT,
//                    new PollBucket(
//                            "bank_data",
//                            "bank_data",
//                            WireMock.firstResourceID,
//                            "TEST_PROFILE",
//                            true
//                    )
//            )).await().indefinitely();
//        });
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        result = repository.get(creditTransfer.id(), List.of(creditTransfer.branch()))
//        .await().indefinitely().orElseThrow();
//
//        history = result.linkedEntities().getHistory(sqlClient).await().indefinitely();
//
//        assertTrue(result.isUpdating());
//        assertEquals(CreditTransferStatus.INSERITO, result.status());
//        assertEquals(2, history.size());
//        assertEquals(InsertedSubStatus.CONTROLLO_SISTEMA_DI_REGOLAMENTO_FALLITO.name(), history.get(0).newStatus());
//    }
//
////    @Test
////    public void testHandleInsertedCreditTransferWithResults() {
////        var sqlClient = dataSources.getDataSource("22222");
////        var creditTransferKindID = UUID.randomUUID();
////        sqlClient.preparedQuery("""
////        INSERT INTO credit_transfer_kind (id, name, description, produce_mt_999)
////        VALUES
////        ($1, 'test_kind', 'test_kind_description', false)
////        """).execute(Tuple.of(creditTransferKindID)).await().indefinitely();
////
////        sqlClient.preparedQuery("""
////        INSERT INTO credit_transfer_channel_allowed_list (id, credit_transfer_kind_id, channel_id)
////        VALUES
////        (gen_random_uuid(), $1, $2)
////        """).execute(Tuple.of(creditTransferKindID, channelID)).await().indefinitely();
////
////        var creditTransfers = setupCreditTransfer(
////                creditTransferKindID,
////                CreditTransferStatus.INSERTED,
////                bothBIC,
////                bothBIC);
////        var creditTransfer = creditTransfers.get(0);
////        var ctx = VertxContext.createNewDuplicatedContext(Vertx.vertx().getOrCreateContext().getDelegate());
////        var identity = CabelClientAuthenticationMechanism.build(
////                "TEST_PROFILE",
////                List.of(""),
////                "22222",
////                "TEST_ROLE",
////                "TOKEN",
////                "branch",
////                List.of(),
////                "test_channel"
////        );
////        ctx.executeBlocking(() -> {
////            CabelClientAuthenticationMechanism.setupTestMetadata(identity);
////            return jobPublisher.scheduleJob(new JobData(
////                    UUID.randomUUID(),
////                    Constants.JOB_HANDLE_CREDIT_TRANSFER,
////                    new HandleCreditTransferPayload(
////                            creditTransfer.id()
////                    )
////            )).await().indefinitely();
////        });
////
////        try {
////            Thread.sleep(1000);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        }
////
////        var entity = new CreditTransfer.Entity();
////        var repository = entity.repository(sqlClient);
////        var result = repository.get(creditTransfer.id(), List.of(creditTransfer.branch()))
////                .await().indefinitely().get();
////
////        var res = result.linkedEntities();
////        res.loadErroredStatusInfo(sqlClient).await().indefinitely();
////
////        assertFalse(result.isUpdating());
////        assertEquals(CreditTransferStatus.TO_BE_CONFIRMED, result.status());
////        assertEquals(InsertedSubStatus.SETTLEMENT_CHECK_PASSED.name(), result.internalStatus());
////    }
//
//    @Test
//    void testHandleAuthorizedCreditTransfer() {
//        var sqlClient = dataSources.getDataSource("22222");
//        var creditTransferKindID = UUID.randomUUID();
//        sqlClient.preparedQuery("""
//        INSERT INTO credit_transfer_kind (id, name, description, produce_mt_999, is_bank_to_bank)
//        VALUES
//        ($1, 'test_kind', 'test_kind_description', false, false)
//        """).execute(Tuple.of(creditTransferKindID)).await().indefinitely();
//
//        sqlClient.preparedQuery("""
//        INSERT INTO credit_transfer_channel_allowed_list (id, credit_transfer_kind_id, channel_id)
//        VALUES
//        (gen_random_uuid(), $1, $2)
//        """).execute(Tuple.of(creditTransferKindID, channelID)).await().indefinitely();
//
//        sqlClient.preparedQuery("""
//        INSERT INTO credit_transfer_kind_message (id, credit_transfer_kind_id, message_id, converter_implementation_class_name)
//        VALUES
//        (gen_random_uuid(), $1, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mapping.pacs008.Pacs008StandardMapperImpl'),
//        (gen_random_uuid(), $1, 'pacs.008.001.08COVE', 'com.flowpay.ccp.credit.transfer.cross.border.mapping.pacs008.Pacs008CoveStandardMapper'),
//        (gen_random_uuid(), $1, 'pacs.009.001.08COVE', 'com.flowpay.ccp.credit.transfer.cross.border.mapping.pacs009.Pacs009CoveStandardMapper'),
//        (gen_random_uuid(), $1, 'pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mapping.pacs009.Pacs009CoreStandardMapper')
//        """).execute(Tuple.of(creditTransferKindID)).await().indefinitely();
//
//        var creditTransfers = setupCreditTransfer(
//                creditTransferKindID,
//                CreditTransferStatus.AUTORIZZATO,
//                "BICDSTN1XXX",
//                "BICCRDT1XXX");
//        var creditTransfer = creditTransfers.get(0);
//        var ctx = VertxContext.createNewDuplicatedContext(Vertx.vertx().getOrCreateContext().getDelegate());
//        var identity = CabelClientAuthenticationMechanism.build(
//                "TEST_PROFILE",
//                List.of(""),
//                "22222",
//                "TEST_ROLE",
//                "TOKEN",
//                "branch",
//                List.of(),
//                "test_channel"
//        );
//        ctx.executeBlocking(() -> {
//            CabelClientAuthenticationMechanism.setupTestMetadata(identity);
//            return jobPublisher.scheduleJob(new JobData(
//                    UUID.randomUUID(),
//                    Constants.JOB_HANDLE_CREDIT_TRANSFER,
//                    new HandleCreditTransferPayload(
//                            creditTransfer.id()
//                    )
//            )).await().indefinitely();
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        var entity = new BonificoExtraSepa.Entity();
//        var repository = entity.repository(sqlClient);
//        var result = repository.get(creditTransfer.id(), List.of(creditTransfer.branch()))
//                .await().indefinitely().get();
//
//        assertFalse(result.isUpdating());
//        assertEquals(CreditTransferStatus.INVIATO, result.status());
//    }
//
//}
