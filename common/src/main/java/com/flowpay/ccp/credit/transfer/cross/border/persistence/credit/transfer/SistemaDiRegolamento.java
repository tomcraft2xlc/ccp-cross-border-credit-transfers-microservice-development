package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.Tipologia;

/**
 * Enum che rappresenta il sistema di regolamento per le transazioni di bonifico.<p>
 *
 * Questa enumerazione identifica il metodo di regolamento utilizzato per una transazione,
 * in conformità agli standard bancari e ai requisiti del sistema di pagamento.<p>
 *
 * I valori possibili sono:<p>
 * - {@code TARGET}: Utilizza il sistema di regolamento TARGET.<p>
 * - {@code NO_TARGET}: Utilizza il sistema CBPR+ (Cross-Border Payments and Reporting Plus).<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE sistema_di_regolamento AS ENUM (
 *     'TARGET',
 *     'NO_TARGET'
 * );
 * }</pre>
 *
 */
public enum SistemaDiRegolamento {
    /// Utilizza il sistema di regolamento TARGET.
    TARGET,
    /// Utilizza il sistema CBPR+ (Cross-Border Payments and Reporting Plus).
    NO_TARGET;

    public Tipologia tipologia() {
        return switch (this) {
            case TARGET -> Tipologia.T2;
            case NO_TARGET -> Tipologia.CBPR;
        };
    }

    public String famiglia() {
        return switch (this) {
            case TARGET -> "TGT";
            case NO_TARGET -> "CBP";
        };
    }
}
