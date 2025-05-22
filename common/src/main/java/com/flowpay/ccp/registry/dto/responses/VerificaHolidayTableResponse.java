package com.flowpay.ccp.registry.dto.responses;

public record VerificaHolidayTableResponse(
        String tipoRecord,
        String codiceIso,
        String annoValidita,
        String dataFestivita
) {

}
