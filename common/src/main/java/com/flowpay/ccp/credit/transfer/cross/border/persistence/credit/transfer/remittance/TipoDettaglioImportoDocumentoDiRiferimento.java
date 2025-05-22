package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance;

/**
 * Definisce i possibili dettagli dell'importo in un documento di riferimento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/RfrdDocInf/LineDtls/Amt}.<p>
 *
 * Questa enumerazione rappresenta i diversi tipi di importo che possono essere
 * specificati in un documento di riferimento all'interno di una transazione di pagamento.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipo_dettaglio_importo_documento_di_riferimento AS ENUM (
 *     'IMPORTO_DOVUTO',
 *     'SCONTO',
 *     'NOTA_DI_CREDITO',
 *     'TASSE',
 *     'RETTIFICA',
 *     'IMPORTO_DISPOSTO'
 * );
 * }</pre>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
public enum TipoDettaglioImportoDocumentoDiRiferimento {

    /// Importo dovuto per il pagamento.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/DuePyblAmt`.</p>
    IMPORTO_DOVUTO,

    /// Importo dello sconto applicato.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/DscntApldAmt`.</p>
    SCONTO,

    /// Importo della nota di credito.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/CdtNoteAmt`.</p>
    NOTA_DI_CREDITO,

    /// Importo delle tasse applicate.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/TaxAmt`.</p>
    TASSE,

    /// Importo della rettifica effettuata.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/AdjstmntAmtAndRsn`.</p>
    RETTIFICA,

    /// Importo disposto per la transazione.
    /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Amt/RmtdAmt`.</p>
    IMPORTO_DISPOSTO
}