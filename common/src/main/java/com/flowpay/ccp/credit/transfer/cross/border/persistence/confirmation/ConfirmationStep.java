package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;

/**
 * Enum che rappresenta le chiamate della fase di conferma
 */
public enum ConfirmationStep {
    RECUPERA_SALDO_RAPPORTO,
    CONFERMA_AVVERTENZE_RAPPORTO,
    CONFERMA_EMBARGO,
    CONFERMA_CAMBIO,
    CONFERMA_HOLIDAY_TABLE_PAESI,
    CONFERMA_HOLIDAY_TABLE_DIVISA,
    CONFERMA_BONIFICO
}
