package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Size;

@Schema(description = "Un indirizzo fisico")
public record Indirizzo(
        String indirizzo,
        String citta,
        String cap,
        @Size(max = 2) String paese,
        String divisione,
        String sottoDivisione,
        String numeroCivico,
        String edificio,
        String piano,
        String cassettaPostale,
        String stanza,
        String localita,
        String distretto,
        String provincia,
        String lineaIndirizzo
) {
}
