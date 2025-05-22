package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;

import java.time.LocalDate;

public record InformazioniPignoramento(
    String codice,
    String codiceProprietario,
    String emittente,
    Attore terzoPignorato,
    Attore gestorePignoramento,
    String identificativo,
    LocalDate data,
    Importo importo,
    Boolean assicurazioneSanitaria,
    Boolean disoccupato
) {
}
