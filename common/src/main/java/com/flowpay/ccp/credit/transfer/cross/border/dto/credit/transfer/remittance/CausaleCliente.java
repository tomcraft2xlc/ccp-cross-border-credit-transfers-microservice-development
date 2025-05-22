package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.Valid;

import java.util.List;

@Schema(
        description = """
                Causale di una transazione. Può essere sia descrittiva,
                e in tal caso è composta da una singola stringa non strutturata, sia
                strutturata.
                """
)
public record CausaleCliente(
        @Schema(
                description = "Se presente, la causale è descrittiva, e rappresenta l'intera causale."
        )
        String causaleDescrittiva,

        List<@Valid CausaleStrutturata> causaleStrutturata
) implements Causale {
}
