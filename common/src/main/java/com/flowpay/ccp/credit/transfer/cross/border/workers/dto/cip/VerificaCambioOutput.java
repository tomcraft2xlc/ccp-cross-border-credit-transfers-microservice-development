package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.util.List;

public record VerificaCambioOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored

)  implements CabelOutput {
}