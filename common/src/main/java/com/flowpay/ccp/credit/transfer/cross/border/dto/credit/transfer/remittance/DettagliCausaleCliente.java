package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import jakarta.validation.Valid;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        title = "Dettagli causale",
        description = """
                Informazioni dettagliate sulla causale di un bonifico.        
                """
)
public record DettagliCausaleCliente(
        @Schema(title = "Codice causale")
        String codiceCausaleTransazione,
        @Valid
        CausaleCliente causale
) implements DettagliCausale {
}
