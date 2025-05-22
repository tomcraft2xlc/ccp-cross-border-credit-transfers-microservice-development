package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Rappresenta lo stato di uno step che può solo fallire o riuscire
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_verifica_generico AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'VERIFICATO',
 *     'FALLITO'
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
public enum StatoVerificaGenerico implements VerifyCallStatus  {
    ATTENDE_RISPOSTA,
    VERIFICATO,
    FALLITO,
    ERRORE;

    @Override
    public boolean isVerified() {
        return this.equals(VERIFICATO);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
