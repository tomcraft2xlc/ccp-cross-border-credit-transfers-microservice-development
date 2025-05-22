package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato dello step 1.c del processo di conferma
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_embargo AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_FORZATURA_EMBARGO_PARZIALE',
 *     'CONFERMATO',
 *     'FALLITO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaEmbargo implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_FORZATURA_EMBARGO_PARZIALE,
    CONFERMATO_STEP_VERIFICA,
    CONFERMATO,
    FALLITO, ERRORE;

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO) || this.equals(CONFERMATO_STEP_VERIFICA);
    }
}
