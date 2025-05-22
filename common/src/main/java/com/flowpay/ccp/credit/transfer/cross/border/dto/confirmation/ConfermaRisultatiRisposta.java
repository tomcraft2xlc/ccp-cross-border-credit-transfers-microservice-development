package com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation;

import com.flowpay.ccp.credit.transfer.cross.border.dto.verify.*;

import java.util.List;

public record ConfermaRisultatiRisposta(
        RisultatiSaldo saldo,
        RisultatiAvvertenze avvertenze,
        RisultatiEmbargo embargo,
        RisultatiCambio cambio,
        RisultatiHolidayTable festivitaPaese,
        RisultatiHolidayTable festivitaDivisa,

        List<ErroreTecnico> erroriTecnici
) {
}
