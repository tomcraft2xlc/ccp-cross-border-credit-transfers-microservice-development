package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
    description = """
            Rappresenta una commissione bancaria associata a 
            un bonifico nel sistema di pagamento.
            """
)
public record CommissioniBanca(
        String codice,
        @Schema(description = "Descrizione della commissione")
        String descrizione,
        @Schema(description = "Importo della commissione")
        BigDecimal importo,
        @Schema(description = "Divisa della commissione")
        String divisa
) {
}
