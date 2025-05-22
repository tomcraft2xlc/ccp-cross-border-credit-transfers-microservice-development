package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Enum che rappresenta il tipo di attore coinvolto nella transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code PartyIdentification135}.<p>
 *
 * Ogni valore di questa enumerazione corrisponde a una specifica entità che può essere
 * coinvolta nel processo di trasferimento di fondi.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipo_attore AS ENUM (
 *     'ORDINANTE',
 *     'SOGGETTO_ISTRUTTORE',
 *     'ORDINANTE_EFFETTIVO',
 *     'BENEFICIARIO',
 *     'BENEFICIARIO_EFFETTIVO'
 * );
 * }</pre>
 */
@Schema(
    title = "Tipo attore",
    description = """
        Rappresenta il tipo di attore coinvolto nella transazione di pagamento,
        secondo lo schema ISO 20022 `PartyIdentification135`.

        Ogni valore di questa enumerazione corrisponde a una specifica entità che può essere
        coinvolta nel processo di trasferimento di fondi.
        """
)
public enum TipoAttore {

    /// L'ordinante del pagamento.
    /// <p>Mappato in `Dbtr` di PartyIdentification135.</p>
    ORDINANTE,

    /// Il soggetto che istruisce il pagamento.
    /// <p>Mappato in `InitgPty` di PartyIdentification135.</p>
    SOGGETTO_ISTRUTTORE,

    /// L'ordinante effettivo del pagamento.
    /// <p>Mappato in `UltmtDbtr` di PartyIdentification135.</p>
    ORDINANTE_EFFETTIVO,

    /// Il beneficiario del pagamento.
    /// <p>Mappato in `Cdtr` di PartyIdentification135.</p>
    BENEFICIARIO,

    /// Il beneficiario effettivo del pagamento.
    /// <p>Mappato in `UltmtCdtr` di PartyIdentification135.</p>
    BENEFICIARIO_EFFETTIVO
}