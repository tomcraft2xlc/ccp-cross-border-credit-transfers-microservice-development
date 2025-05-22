package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Enum che rappresenta il tipo di regolamento della commissione per una transazione bancaria.<p>
 *
 * Questa enumerazione identifica se la commissione viene regolata in euro o nella divisa della transazione.<p>
 *
 * I valori possibili sono:<p>
 * - {@code EURO}: La commissione è regolata in euro.<p>
 * - {@code DIVISA}: La commissione è regolata nella divisa della transazione.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE regolamento_commissione AS ENUM (
 *     'EURO',
 *     'DIVISA'
 * );
 * }</pre>
 */
@Schema(
    title = "Regolamento commissione",
    description = """
        Rappresenta il tipo di regolamento della commissione per una transazione bancaria.
        """
)
public enum RegolamentoCommissione {

    /// La commissione è regolata in euro
    EURO,

    /// La commissione è regolata nella divisa della transazione
    DIVISA
}