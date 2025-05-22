package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Indirizzo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import jakarta.validation.Valid;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        description = "Informazioni su un attore"
)
public record Attore(
        String intestazione,
        @Valid
        Indirizzo indirizzo,
        @Schema(description = "Paese di residenza")
        String paeseDiResidenza,
        @Valid
        InfoOrganizzazione organizzazione,
        @Valid
        InfoPrivato privato,
        @Valid
        Rapporto rapporto
) {
}
