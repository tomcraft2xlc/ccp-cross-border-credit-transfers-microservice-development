package com.flowpay.ccp.registry.dto.responses;

/**
 * DTO di risposta della ricerca voce contabile
 */
public record VoceContabile(
        Long voceContabile,
        String decodificaVoce,
        Long sottoContoContabilitaGenerale,
        String decodificaSottoContoContabilitaGenerale
) { }
