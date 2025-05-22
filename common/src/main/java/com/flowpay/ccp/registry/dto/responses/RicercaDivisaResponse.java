package com.flowpay.ccp.registry.dto.responses;

import java.math.BigDecimal;

public record RicercaDivisaResponse(
        BigDecimal valoreCambioAttuale,
        BigDecimal percentualeScartoDivisa,
        String descrizioneDivisa,
        BigDecimal codiceDivisaUic,
        String codiceDivisaIso
) { }