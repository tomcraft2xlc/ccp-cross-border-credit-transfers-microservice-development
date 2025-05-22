package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details;

import java.time.Instant;
import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(title = "Dettagli date", description = "Informazioni sulle tempistiche del bonifico")
public record DettagliDate(
        LocalDate dataCreazione,
        LocalDate dataEsecuzione,
        LocalDate dataValutaOrdinante,
        @NotNull LocalDate dataRegolamentoBancaBeneficiario) {
}