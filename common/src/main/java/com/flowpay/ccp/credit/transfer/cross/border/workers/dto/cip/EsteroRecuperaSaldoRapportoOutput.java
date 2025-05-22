package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.math.BigDecimal;
import java.util.List;

public record EsteroRecuperaSaldoRapportoOutput(

    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    BigDecimal saldoRapporto,
    BigDecimal importoSconfinamento,
    FlagSiNo flagDisponibilita,
    FlagSiNo flagForzaturaSconfinamento

) implements CabelOutput  {
}