package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato dello step 1.d del processo di conferma
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_cambio AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_MODIFICA_CAMBIO',
 *     'CONFERMATO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaCambio implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_MODIFICA_CAMBIO,
    CONFERMATO, ERRORE;

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
