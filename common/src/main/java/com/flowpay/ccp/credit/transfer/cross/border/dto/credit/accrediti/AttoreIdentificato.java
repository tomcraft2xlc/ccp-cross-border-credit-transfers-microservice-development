package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;

public record AttoreIdentificato(
        Attore attore,
        IdentificazioneAttore identificazione
) {
}
