package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check;

import java.util.List;

import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.CabelOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.Segnalazione;

public record ExtraSepaCheckBonificoConfermaOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    Long numeroTransazione

) implements CabelOutput {
}