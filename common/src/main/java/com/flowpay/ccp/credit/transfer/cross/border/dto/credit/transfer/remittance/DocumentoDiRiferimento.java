package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    title = "Documento di riferimento",
    description = """
            Un documento fornito all'interno di una causale.
            """
)
public record DocumentoDiRiferimento(
        @Schema(description = "Tipo del documento")
        String tipo,
        @Schema(description = "Descrizione del documento")
        String descrizione,
        @Schema(description = "Ente emittente il documento")
        String emittente,
        @Schema(description = "Numento documento")
        String numero,
        @Schema(description = "Data del documento")
        LocalDate data,

        // TODO: Descrivere le linee del documento
        List<@Valid LineaDocumentoDiRiferimento> linee
) {

    public record LineaDocumentoDiRiferimento(
        List<@Valid IdLineaDocumento> identificativi,
        String descrizione,
        @Valid
        ImportiCausale importi
    ) {

    }

    public record IdLineaDocumento(
            String codice,
            String codiceProprietario,
            String emittente,
            String numero,
            LocalDate data
    ) { }
}
