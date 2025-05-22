package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee;

import com.prowidesoftware.swift.model.mx.dic.ChargeBearerType1Code;

/**
 * Enum che rappresenta la tipologia di commissione applicata a una transazione bancaria.<p>
 *
 * Questa enumerazione identifica chi è responsabile del pagamento delle commissioni
 * associate a un bonifico bancario.<p>
 *
 * I valori possibili sono:<p>
 * - {@code SHARED}: Le commissioni sono condivise tra l'ordinante e il beneficiario.<p>
 * - {@code CREDITOR}: Le commissioni sono a carico del beneficiario del bonifico.<p>
 * - {@code DEBTOR}: Le commissioni sono interamente a carico dell'ordinante.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipologia_commissioni AS ENUM (
 *     'SHARED',
 *     'CREDITOR',
 *     'DEBTOR'
 * );
 * }</pre>
 */
public enum TipologiaCommissioni {

    /// Le commissioni sono condivise tra l'ordinante e il beneficiario
    SHARED,

    /// Le commissioni sono a carico del beneficiario del bonifico
    CREDITOR,

    /// Le commissioni sono interamente a carico dell'ordinante
    DEBTOR;

    public ChargeBearerType1Code asChargeBearerType1Code() {
        return switch (this) {
            case SHARED -> ChargeBearerType1Code.SHAR;
            case CREDITOR -> ChargeBearerType1Code.CRED;
            case DEBTOR -> ChargeBearerType1Code.DEBT;
        };
    }
}