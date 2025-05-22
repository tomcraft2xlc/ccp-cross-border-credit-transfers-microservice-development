package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary;

/**
 * Definisce il tipo di intermediario finanziario, indicando quale parte del messaggio
 * ISO 20022 {@code FinancialInstitutionIdentification18} deve essere popolata con i dati
 * presenti in {@code InformazioniIntermediario}.<p>
 *
 * Ogni valore di questa enumerazione rappresenta una specifica entità finanziaria
 * coinvolta nella transazione di pagamento, come istituzioni finanziarie ordinanti,
 * beneficiarie o intermediarie.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipo_intermediario AS ENUM (
 *     'ORDINANTE',
 *     'BANCA_DELL_ORDINANTE',
 *     'BANCA_DESTINATARIA',
 *     'BANCA_BENEFICIARIA',
 *     'BANCA_DEL_BENEFICIARIO',
 *     'BANCA_ISTRUTTRICE_1',
 *     'BANCA_ISTRUTTRICE_2',
 *     'BANCA_ISTRUTTRICE_3',
 *     'BANCA_INTERMEDIARIA_1',
 *     'BANCA_INTERMEDIARIA_2',
 *     'BANCA_INTERMEDIARIA_3',
 *     'BANCA_CORRISPONDENTE_MITTENTE',
 *     'BANCA_CORRISPONDENTE_RICEVENTE',
 *     'ISTITUTO_TERZO_DI_RIMBORSO'
 * );
 * }</pre>
 */
public enum TipoIntermediario {

    /// L'ordinante del pagamento.
    /// <p>Mappato nel campo `Dbtr` del documento xml.</p>
    ORDINANTE,

    /// La banca dell'ordinante.
    /// <p>Mappato nel campo `DbtrAgt` del documento xml.</p>
    /// <p>In caso di pacs008 con tipologia di commissioni `CRED` viene valorizzato anche il campo `ChrgsInf/Agt`.</p>
    BANCA_DELL_ORDINANTE,

    /// La banca destinataria.
    /// <p>Mappato nel campo `InstdAgt` del documento xml.</p>
    /// <p>In caso di pacs008 con tipologia di commissioni `DEBT` viene valorizzato anche il campo `ChrgsInf/Agt`.</p>
    BANCA_DESTINATARIA,

    /// Il beneficiario del pagamento.
    /// <p>Mappato nel campo `Cdtr` del documento xml.</p>
    BANCA_BENEFICIARIA,

    /// La banca del beneficiario.
    /// <p>Mappato nel campo `CdtrAgt` del documento xml.</p>
    BANCA_DEL_BENEFICIARIO,

    /// Prima banca istruttrice nella catena del pagamento.
    /// <p>Mappato nel campo `PrvsInstgAgt1` del documento xml.</p>
    BANCA_ISTRUTTRICE_1,

    /// Seconda banca istruttrice nella catena del pagamento.
    /// <p>Mappato nel campo `PrvsInstgAgt2` del documento xml.</p>
    BANCA_ISTRUTTRICE_2,

    /// Terza banca istruttrice nella catena del pagamento.
    /// <p>Mappato nel campo `PrvsInstgAgt3` del documento xml.</p>
    BANCA_ISTRUTTRICE_3,

    /// Primo intermediario finanziario coinvolto nella transazione.
    /// <p>Mappato nel campo `IntrmyAgt1` del documento xml.</p>
    BANCA_INTERMEDIARIA_1,

    /// Secondo intermediario finanziario coinvolto nella transazione.
    /// <p>Mappato nel campo `IntrmyAgt2` del documento xml.</p>
    BANCA_INTERMEDIARIA_2,

    /// Terzo intermediario finanziario coinvolto nella transazione.
    /// <p>Mappato nel campo `IntrmyAgt3` del documento xml.</p>
    BANCA_INTERMEDIARIA_3,

    /// Banca corrispondente dell'ordinante, responsabile della gestione del pagamento lato mittente.
    /// <p>Mappato nel campo `InstgRmbrsmntAgt` del documento xml.</p>
    BANCA_CORRISPONDENTE_MITTENTE,

    /// Banca corrispondente del destinatario, responsabile della ricezione del pagamento lato beneficiario.
    /// <p>Mappato nel campo `InstdRmbrsmntAgt` del documento xml.</p>
    BANCA_CORRISPONDENTE_RICEVENTE,

    /// Istituto finanziario terzo responsabile del rimborso della transazione.
    /// <p>Mappato nel campo `ThrdRmbrsmntAgt` del documento xml.</p>
    ISTITUTO_TERZO_DI_RIMBORSO
}