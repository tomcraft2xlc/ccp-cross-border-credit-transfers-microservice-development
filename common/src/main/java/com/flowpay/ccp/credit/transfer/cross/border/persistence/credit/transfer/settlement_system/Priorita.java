package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system;

import com.prowidesoftware.swift.model.mx.dic.Priority3Code;

/**
 * Definisce il livello di priorità del regolamento del pagamento,<p>
 * in conformità con lo standard ISO 20022 {@code Priority3Code}.<p>
 *
 * Questa enumerazione rappresenta i livelli di priorità che possono essere
 * assegnati a un pagamento per determinarne l'urgenza nella regolazione.<p>
 *
 * Mappatura con {@code Priority3Code} di ISO 20022:<p>
 * <ul>
 *     <li>{@code URGENTE} → {@code HIGH}</li>
 *     <li>{@code ALTA} → {@code NORM}</li>
 *     <li>{@code NORMALE} → {@code LOW}</li>
 * </ul><p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE priorita AS ENUM (
 *     'URGENTE',
 *     'ALTA',
 *     'NORMALE'
 * );
 * }</pre>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 * @see Priority3Code
 */
public enum Priorita {

    /// Priorità alta per pagamenti urgenti.
    /// <p>Mappato in `HIGH` di Priority3Code.</p>
    URGENTE,

    /// Priorità elevata ma non urgente.
    /// <p>Mappato in `NORM` di Priority3Code.</p>
    ALTA,

    /// Priorità normale per pagamenti standard.
    /// <p>Mappato in `LOW` di Priority3Code.</p>
    NORMALE;

    public Priority3Code asPriority3Code() {
        return switch (this) {
            case URGENTE -> Priority3Code.URGT;
            case ALTA -> Priority3Code.HIGH;
            case NORMALE -> Priority3Code.NORM;
        };
    }

    public static Priorita fromPriority3Code(Priority3Code code) {
        if (code == null) {
            return null;
        }
        return switch (code) {
            case URGT -> Priorita.URGENTE;
            case HIGH -> Priorita.ALTA;
            case NORM -> Priorita.NORMALE;
        };
    }
}