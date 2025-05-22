package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato dello step 2 del processo di conferma
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_bonifico AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'CONFERMATO',
 *     'FALLITO',
 *     'ERRORE',
 *     'NON_INVIATA'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaBonifico implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    CONFERMATO, FALLITO, ERRORE, NON_INVIATA;

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
