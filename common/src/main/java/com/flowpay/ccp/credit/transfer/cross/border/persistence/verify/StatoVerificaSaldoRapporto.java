package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

/**
 * Rappresenta lo stato dello step 1.a del processo di verifica
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_verifica_saldo_rapporto AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_FORZATURA_SCONFINAMENTO',
 *     'VERIFICATO',
 *     'FALLITO'
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
public enum StatoVerificaSaldoRapporto implements VerifyCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_FORZATURA_SCONFINAMENTO,
    VERIFICATO, 
    FALLITO,
    ERRORE;

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

    @Override
    public boolean isVerified() {
        return this.equals(VERIFICATO);
    }
}
