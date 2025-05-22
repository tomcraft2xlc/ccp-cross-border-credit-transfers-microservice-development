package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.util.List;

public interface CabelOutput {
    List<Segnalazione> listaSegnalazioni();

    boolean errored();
}