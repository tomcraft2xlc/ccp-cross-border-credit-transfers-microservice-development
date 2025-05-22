package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        title = "Conto banca di copertura",
        description = """
                Dati definenti il conto della banca coprente il bonifico.
                """
)
public record ContoBancaDiCopertura(
        @Schema(description = "IBAN del conto della banca di copertura", example = "BY86AKBB10100000002966000000")
        String rapportoBanca,
        @Schema(description = "Divisa della banca di copertura", example = "euro")
        String divisa,
        @Schema(description = "BIC della banca di copertura", example = "PRACIT31XXX")
        String bic,
        @Schema(description = "Intestazione conto della banca di copertura")
        String intestazione
) {
}
