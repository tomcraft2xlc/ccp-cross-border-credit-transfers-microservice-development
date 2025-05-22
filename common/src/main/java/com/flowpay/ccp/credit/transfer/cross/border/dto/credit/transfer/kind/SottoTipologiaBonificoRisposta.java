package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(
        title = "Dati sottotipologia del bonifico",
        description = """
                        Sotto-tipologia per il bonifico “Area Extra Sepa”, con dati aggiuntivi.
                        """
)
public record SottoTipologiaBonificoRisposta(
        @NotBlank
        @Schema(
            description = "Nome del tipo",
                example = "Pacs.008"
        )
        String tipo,

        @Schema(
                description = "Descrizione del tipo",
                example = "Bonifico Clientela Extra-Sepa"
        )
        String descrizione,

        @Schema(
            description = "Se causa la creazione di una notifica"
        )
        Boolean conNotifica,

        @Schema(
                description = "Se la tipologia bonifico è banca a banca o meno"
        )
        Boolean bancaABanca
) {
}
