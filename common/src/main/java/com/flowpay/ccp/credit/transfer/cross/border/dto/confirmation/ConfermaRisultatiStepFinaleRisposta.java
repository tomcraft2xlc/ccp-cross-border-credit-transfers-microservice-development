package com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation;

import com.flowpay.ccp.credit.transfer.cross.border.dto.verify.ErroreTecnico;

import java.util.List;

public record ConfermaRisultatiStepFinaleRisposta(
        RisultatiConfermaBonifico confermaBonifico,
        List<ErroreTecnico> erroriTecnici
) {
}
