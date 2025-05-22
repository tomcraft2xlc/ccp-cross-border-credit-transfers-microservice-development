package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;

/**
 * Rappresenta lo stato generale di un processo di conferma
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma AS ENUM (
 *     'ATTENDE_RISPOSTE',
 *     'CONFERMATO',
 *     'DA_CONFERMARE',
 *     'FALLITO'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConferma {
    ATTENDE_RISPOSTE,
    CONFERMATO,
    DA_CONFERMARE,
    ATTENDE_RISPOSTA_STEP_2,
    FALLITO
}
