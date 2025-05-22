package com.flowpay.ccp.credit.transfer.cross.border.dto.amount;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        description = "Importo di una transazione"
)
public record Importo(
        @NotNull
        @Positive
        @JsonFormat(shape=JsonFormat.Shape.STRING)
        @Schema(
                title = "Importo",
                description = "Il valore numerico dell'importo nella valuta impostata."
        )
        BigDecimal importo,
        @NotBlank
        @Size(max = 3)
        @Schema(
                title = "Divisa",
                description = "La valuta dell'importo.",
                example = "EUR"
        )
        String divisa
) {
}
