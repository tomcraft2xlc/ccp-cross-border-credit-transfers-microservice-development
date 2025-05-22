package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        title = "Dettagli causale",
        description = """
                Informazioni dettagliate sulla causale di un bonifico.        
                """
)
public record DettagliCausaleBanca(
        @Schema(title = "Codice causale")
        String codiceCausaleTransazione,
        @Valid
        CausaleBanca causale
) implements DettagliCausale {
}
