package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account;

/**
 * Definisce il tipo di rapporto che un {@code InformazioniRapporto} può assumere all'interno di un file ISO 20022.
 * <p>
 * Ogni valore di questa enum rappresenta una specifica posizione nel messaggio ISO 20022,
 * identificando il ruolo del conto o dell'agente nella transazione.
 * <p>
 * SQL:
 * <pre>{@code
 * CREATE TYPE tipo_informazioni_rapporto AS ENUM (
 *     'CORRISPONDENTE_MITTENTE',
 *     'BANCA_CORRISPONDENTE_MITTENTE',
 *     'BANCA_CORRISPONDENTE_RICEVENTE',
 *     'ISTITUTO_TERZO_DI_RIMBORSO'
 * );
 * }</pre>
 */
public enum TipoInformazioniRapporto {

    /// Rappresenta il conto corrispondente del mittente.
    /// <p>Tipicamente mappato nel campo `GrpHdr/SttlmInf/SttlmnAcct`.
    CORRISPONDENTE_MITTENTE, // SttlmnAcct

    /// Rappresenta il rapporto del corrispondente mittente del documento collegato al pacs originale.
    CORRISPONDENTE_MITTENTE_DOCUMENTO_COLLEGATO,

    /// InstgRmbrsmntAgtAcct
    BANCA_CORRISPONDENTE_MITTENTE,

    /// InstdRmbrsmntAgt
    BANCA_CORRISPONDENTE_RICEVENTE,

    /// ThrdRmbrsmntAgtAcct
    ISTITUTO_TERZO_DI_RIMBORSO,

}