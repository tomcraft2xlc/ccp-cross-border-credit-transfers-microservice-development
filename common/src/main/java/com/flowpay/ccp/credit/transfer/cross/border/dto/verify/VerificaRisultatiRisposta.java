package com.flowpay.ccp.credit.transfer.cross.border.dto.verify;

import java.util.List;

public record VerificaRisultatiRisposta(
        VerificaSistemaDiRegolamento verificaSistemaDiRegolamento,
        RisultatiSaldo saldo,
        RisultatiAvvertenze avvertenze,
        RisultatiEmbargo embargo,
        RisultatiCambio cambio,
        RisultatiHolidayTable festivitaPaese,
        RisultatiHolidayTable festivitaDivisa,
        RisultatiVerificaBonifico verificaBonifico,

        List<ErroreTecnico> erroriTecnici
) {
}
