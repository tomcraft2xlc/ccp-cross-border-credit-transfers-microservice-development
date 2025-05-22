package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;
import java.util.List;

public record EsteroValidaRapportoCtrParteOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    FlagSiNo flagRaggiungibilitaSepa

)  implements CabelOutput {
}