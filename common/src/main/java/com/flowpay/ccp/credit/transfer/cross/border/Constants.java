package com.flowpay.ccp.credit.transfer.cross.border;

public class Constants {

    private Constants() {
    }

    public static final String JOB_HANDLE_CREDIT_TRANSFER = "handle-credit-transfer";
    public static final String JOB_RESTART_PROCESS = "restart-credit-transfer-process";

    public static final String JOB_HANDLE_DATE_CHANGE = "handle-date-change";
    public static final String JOB_RESTART_HANDLE_DATE_CHANGE = "restart-handle-date-change";

    public static final String JOB_CHECK_CREDIT_TRANSFER_SETTLEMENT = "check-credit-transfer-settlement";
    public static final String JOB_RESTART_CHECK_CREDIT_TRANSFER_SETTLEMENT = "restart-check-credit-transfer-settlement";

    public static final String JOB_VERIFY_CREDIT_TRANSFER = "verify-credit-transfer";
    public static final String JOB_RESTART_VERIFY_CREDIT_TRANSFER = "restart-verify-credit-transfer";
    public static final String JOB_RESTART_POLLABLE_VERIFY_CREDIT_TRANSFER = "restart-pollable-verify-credit-transfer";

    public static final String JOB_CONFIRMED_CREDIT_TRANSFER = "confirmed-credit-transfer";
    public static final String JOB_RESTART_CONFIRMED_CREDIT_TRANSFER = "restart-confirmed-credit-transfer";
    public static final String JOB_RESTART_POLLABLE_CONFIRMED_CREDIT_TRANSFER = "restart-pollable-confirmed-credit-transfer";

    public static final String JOB_CONFIRMED_STEP_2_CREDIT_TRANSFER = "confirmed-step2-credit-transfer";
    public static final String JOB_RESTART_CONFIRMED_STEP_2_CREDIT_TRANSFER = "restart-confirmed-step2-credit-transfer";

    public static final String JOB_AUTHORIZED_CREDIT_TRANSFER = "authorized-credit-transfer";
    public static final String JOB_RESTART_AUTHORIZED_CREDIT_TRANSFER = "restart-authorized-credit-transfer";

    public static final String JOB_CHANGE_CONFIRMED_STATUS = "confirmed-status-change";

    public static final String JOB_TO_BE_AUTHORIZED_CREDIT_TRANSFER = "to-be-authorized-credit-transfer";
    public static final String JOB_RESTART_TO_BE_AUTHORIZED_CREDIT_TRANSFER = "restart-to-be-authorized-credit-transfer";

    public static final String JOB_VERIFY_ACCREDITO = "verify-accredito";
    public static final String JOB_RESTART_VERIFY_ACCREDITO= "restart-verify-accredito";
    public static final String JOB_RESTART_POLLABLE_VERIFY_ACCREDITO = "restart-pollable-verify-accredito";

    public static final String JOB_PRODUCE_XML = "authorized-produce-xml";

    public static final String JOB_NEW_INCOMING_XML = "process-new-file-job";

    public static final String JOB_SEND_FILE_OVER_FTP = "send-file-over-ftp";

    public static final String BEAN_JOB_PUBLISHER_INTERNAL = "internal-job-publisher";
    public static final String BEAN_JOB_SUBSCRIBER_INTERNAL = "internal-job-subscriber";
    public static final String BEAN_JOB_SUBSCRIBER_EXTERNAL = "external-job-subscriber";
    public static final String BEAN_JOB_PUBLISHER_EXTERNAL = "external-job-publisher";

    public static final String CHANNEL_INTERNAL_NAME = "cross-border-service-job-channel";
    public static final String FTP_SERVICE_CHANNEL_NAME = "ftp-service-channel";
    public static final String FTP_SERVICE_INCOMING_CHANNEL = "ftp-service-outgoing-channel";

    public static String errorMessageBankRequire(String bic) { return "Bank requires " + bic + " as destination bank"; }
    public static final String ERROR_MESSAGE_MISSING_CREDITOR_BANK = "Missing creditor bank information";
}
