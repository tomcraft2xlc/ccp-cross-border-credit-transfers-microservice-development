package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DettagliDocumentoDiCopertura(
        @Valid
        Rapporto rapportoCorrispondenteMittente,

        String tid,

        @Valid
        InfoSistemaDiRegolamento infoSistemaDiRegolamento,

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
        Intermediario bancaIntermediaria3,

        @Valid
        RiferimentiAggiuntivi riferimentiAggiuntivi,

        String causaleDescrittiva
) {
}
