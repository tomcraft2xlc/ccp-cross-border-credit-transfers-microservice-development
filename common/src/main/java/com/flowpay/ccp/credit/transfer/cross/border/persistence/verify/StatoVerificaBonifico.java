package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Rappresenta lo stato dello step 2 del processo di verifica
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_verifica_bonifico AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_MODIFICA_IBAN',
 *     'VERIFICATO',
 *     'FALLITO'
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
public enum StatoVerificaBonifico implements VerifyCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_MODIFICA_IBAN,
    VERIFICATO, ERRORE;

    @Override
    public boolean isVerified() {
        return this.equals(VERIFICATO);
    }

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

}
