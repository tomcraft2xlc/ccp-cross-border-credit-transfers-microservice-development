package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(title = "Commissioni cliente", description = """
        Rappresenta una commissione associata a un bonifico account-to-account nel sistema di pagamento.
        """)
public record CommissioniCliente(
        String codice,
        @Schema(description = "Descrizione della commissione")
        String descrizione,
        @Schema(description = "Importo della commissione")
        BigDecimal importo,
        @Schema(description = "Percentuale del totale")
        BigDecimal percentuale,
        @Schema(description = "Importo minimo della commissione")
        BigDecimal min,
        @Schema(description = "Importo massimo della commissione")
        BigDecimal max,
        @Schema(description = "Divisa della commissione")
        String divisa) {
}
