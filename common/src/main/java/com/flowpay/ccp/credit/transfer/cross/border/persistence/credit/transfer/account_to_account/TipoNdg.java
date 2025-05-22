package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account;


/**
 * Enum che rappresenta il tipo di attore coinvolto nella transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code PartyIdentification135}.<p>
 *
 * Ogni valore di questa enumerazione corrisponde a una specifica entità che può essere
 * coinvolta nel processo di trasferimento di fondi.<p>
 *
 * SQL per la creazione del tipo ENUM in PostgreSQL:<p>
 * <pre>{@code
 * CREATE TYPE tipo_ndg AS ENUM (
 *     'PRESENTATORE',
 *     'TITOLARE_EFFETTIVO'
 * );
 * }</pre>
 */
public enum TipoNdg {
    PRESENTATORE,
    TITOLARE_EFFETTIVO
}