package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import jakarta.validation.Valid;

public record AltriIntermediari(

        @Valid
        Rapporto riferimentiCorrispondenteMittente,
        @Valid
        Intermediario bancaCorrispondenteMittente,
        @Valid
        Intermediario bancaCorrispondenteRicevente,
        @Valid
        Intermediario istitutoTerzoDiRimborso,
        @Valid
        Intermediario bancaIstruttrice1,
        @Valid
        Intermediario bancaIstruttrice2,
        @Valid
        Intermediario bancaIstruttrice3,
        @Valid
        Intermediario bancaIntermediaria1,
        @Valid
        Intermediario bancaIntermediaria2,
        @Valid
        Intermediario bancaIntermediaria3
) {
}
