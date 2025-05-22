package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Rappresenta lo stato dello step 1.d del processo di verifica
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_verifica_cambio AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_MODIFICA_CAMBIO',
 *     'VERIFICATO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
public enum StatoVerificaCambio implements VerifyCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_MODIFICA_CAMBIO,
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
