package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Enum che rappresenta le chiamate della fase di verifica
 */
public enum VerifyStep {
    RECUPERA_SALDO_RAPPORTO,
    VERIFICA_AVVERTENZE_RAPPORTO,
    VERIFICA_EMBARGO,
    VERIFICA_CAMBIO,
    VERIFICA_HOLIDAY_TABLE_PAESI,
    VERIFICA_HOLIDAY_TABLE_DIVISA,
    VERIFICA_BONIFICO
}
