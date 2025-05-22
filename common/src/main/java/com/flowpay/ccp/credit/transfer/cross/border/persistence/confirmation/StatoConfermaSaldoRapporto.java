package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;


/**
 * Rappresenta lo stato dello step 1.a del processo di conferma
 * 
 * SQL per la creazione del tipo ENUM in PostgreSQL:
 * <pre>{@code
 * CREATE TYPE stato_conferma_saldo_rapporto AS ENUM (
 *     'ATTENDE_RISPOSTA',
 *     'NECESSITA_FORZATURA_SCONFINAMENTO',
 *     'CONFERMATO',
 *     'FALLITO',
 *     'ERRORE'
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
public enum StatoConfermaSaldoRapporto implements ConfirmationCallStatus  {
    ATTENDE_RISPOSTA,
    NECESSITA_FORZATURA_SCONFINAMENTO,
    CONFERMATO,
    CONFERMATO_STEP_VERIFICA,
    FALLITO,
    ERRORE;

    @Override
    public boolean isWaitingForAnswer() {
        return this.equals(ATTENDE_RISPOSTA);
    }

    @Override
    public boolean isConfirmed() {
        return this.equals(CONFERMATO) || this.equals(CONFERMATO_STEP_VERIFICA);
    }
}
