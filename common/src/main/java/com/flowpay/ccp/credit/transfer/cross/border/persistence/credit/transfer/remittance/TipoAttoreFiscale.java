package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance;

/**
 * Enum che rappresenta il tipo di attore fiscale coinvolto in una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/TaxRmt}.<p>
 *
 * Questa enumerazione definisce i possibili ruoli di un attore fiscale,
 * come il debitore, il creditore e il debitore finale.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipo_attore_fiscale AS ENUM (
 *     'DEBITORE',
 *     'CREDITORE',
 *     'DEBITORE_FINALE'
 * );
 * }</pre>
 */
public enum TipoAttoreFiscale {

    /// Attore fiscale che deve effettuare il pagamento delle tasse.
    /// <p>Mappato in `RmtInf/Strd/TaxRmt/Dbtr`.</p>
    DEBITORE,

    /// Attore fiscale che riceve il pagamento delle tasse.
    /// <p>Mappato in `RmtInf/Strd/TaxRmt/Cdtr`.</p>
    CREDITORE,

    /// Attore fiscale che è l'ultimo debitore della transazione fiscale.
    /// <p>Mappato in `RmtInf/Strd/TaxRmt/UltmtDbtr`.</p>
    DEBITORE_FINALE
}