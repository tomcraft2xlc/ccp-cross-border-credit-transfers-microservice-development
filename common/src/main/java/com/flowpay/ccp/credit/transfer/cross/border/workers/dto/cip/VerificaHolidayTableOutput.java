package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;
import java.util.List;   
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record VerificaHolidayTableOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    boolean festivo,
    List<GiornoFestivo> listaGiorniFestivi
)  implements CabelOutput {
    public record GiornoFestivo(
        String tipoRecord,
        String codiceIso,

        @Schema(
            description = "Year the holiday definition is valid for.",
            example = "2025"
        )
        Integer annoValidita,

        @Schema(
            description = "Date of the holiday."
        )
        String dataFestivita
    ) {
    }
}