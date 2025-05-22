package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO representing the input for the holiday verification service.
 */
@Schema(name = "VerificaHolidayTableInput", description = "Input structure for the holiday verification service.")
public record VerificaHolidayTableInput(

        @Schema(description = "The type of request being made.", enumeration = {
                "LISTA", "CONTROLLO" }, required = true) 
        TipoRichiesta tipoRichiesta,

        @Schema(description = "The type of code provided in 'codiceIso'.", enumeration = { "PAESE",
                "DIVISA" }, required = true) 
        TipoCodice tipoCodice,

        @Schema(description = "The ISO code value (e.g., 'IT' for PAESE, 'EUR' for DIVISA).", example = "IT", required = true) 
        String codiceIso,

        @Schema(description = "Request timestamp in local date-time format (YYYY-MM-DD'T'HH:mm:ss).", type = SchemaType.STRING, format = "date-time", pattern = "yyyy-MM-dd'T'HH:mm:ss", example = "2025-04-10T17:57:00", required = true) 
        LocalDateTime dataRichiesta

    ){

    @Schema(description = "Possible types for TipoRichiesta.")
    public enum TipoRichiesta {
        LISTA,
        CONTROLLO
    }

    @Schema(description = "Possible types for TipoCodice.")
    public enum TipoCodice {
        PAESE,
        DIVISA
    }
}