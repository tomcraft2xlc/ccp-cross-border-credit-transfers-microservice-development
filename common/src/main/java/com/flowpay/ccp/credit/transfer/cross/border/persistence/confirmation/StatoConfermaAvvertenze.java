package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato di uno step che può solo fallire o riuscire
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_avvertenze AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'CONFERMATO',
 *     'BLOCCO_DARE',
 *     'BLOCCO_TOTALE',
 *     'FALLITO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaAvvertenze implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    CONFERMATO,
    CONFERMATO_STEP_VERIFICA,
    BLOCCO_DARE,
    BLOCCO_TOTALE,
    FALLITO, ERRORE;

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO) || this.equals(CONFERMATO_STEP_VERIFICA);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
