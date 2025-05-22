package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Indirizzo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;

import jakarta.validation.Valid;

public record Intermediario(
        String bic,
        String intestazione,
        String codiceLEI,
        String codiceSistemaClearing,
        String identificativoClearing,
        @Valid
        Rapporto rapporto,
        @Valid
        Indirizzo indirizzo
) {
}
