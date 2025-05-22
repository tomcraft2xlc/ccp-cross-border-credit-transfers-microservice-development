package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Rappresenta lo stato generale di un processo di verifica
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_verifica AS ENUM (
 *     'ATTENDE_RISPOSTE',
 *     'VERIFICATO',
 *     'DA_CONFERMARE',
 *     'FALLITO'
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
public enum StatoVerifica {
    ATTENDE_RISPOSTE,
    VERIFICATO,
    DA_CONFERMARE,
    FALLITO
}
