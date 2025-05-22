package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(title = "Dettagli importo", description = "Informazioni sull'importo del bonifico")
public record DettagliImporto(
        @NotNull BigDecimal importo,
        @NotNull String divisa,
        @NotNull BigDecimal cambio) {
}