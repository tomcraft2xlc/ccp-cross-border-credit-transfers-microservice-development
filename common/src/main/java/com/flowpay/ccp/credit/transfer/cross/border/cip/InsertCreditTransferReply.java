package com.flowpay.ccp.credit.transfer.cross.border.cip;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.status.InsertedOutcomeCode;

import java.math.BigDecimal;

public record InsertCreditTransferReply(
        String esito,
        //TODO: use the correct enum
        InsertedOutcomeCode codiceEsito,
        Integer idRichiesta,
        Boolean flagDisponibilita,
        BigDecimal saldoRapporto,
        Boolean flagEmbargo
) {
}
