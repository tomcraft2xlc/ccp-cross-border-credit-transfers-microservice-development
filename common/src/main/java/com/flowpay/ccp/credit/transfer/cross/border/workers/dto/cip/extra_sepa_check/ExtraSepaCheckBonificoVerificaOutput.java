package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check;

import java.util.List;

import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.CabelOutput;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.Segnalazione;

public record ExtraSepaCheckBonificoVerificaOutput(
        List<Segnalazione> listaSegnalazioni,
        boolean errored,
        Boolean ibanObbligatorio) implements CabelOutput {
}