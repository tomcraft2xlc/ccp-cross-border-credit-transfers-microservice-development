package com.flowpay.ccp.registry.dto.responses;


public record VerificaCambioResponse(
//        Il batch di cip corrispondente non restiuisce granché di dati
//        restituisce soltanto errored: false quando il controllo di valore di cambio passa
//        errored: true quando il controllo non passa.
        Boolean errored
) {
}
