package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato di uno step che può solo fallire o riuscire
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_generico AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'CONFERMATO',
 *     'FALLITO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaGenerico implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    CONFERMATO,
    FALLITO, ERRORE;

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
