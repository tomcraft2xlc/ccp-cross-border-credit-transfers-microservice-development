package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(
        title = "Sottotipologia del bonifico",
        description = """
                        Sotto-tipologia per il bonifico “Area Extra Sepa”.
                        
                        Le sotto-tipologie sono configurabili per banca ma, al massimo, sono le seguenti:
                        - Pacs.008
                        - Pacs.008 con Pacs.009 COV
                        - Pacs.008 con Pacs.009 COV e MT999
                        - Pacs.008 con MT999
                        """
)
public record SottoTipologiaBonificoRichiesta(
        @NotBlank
        @Schema(example = "Pacs.008")
        String tipo
) {
}
