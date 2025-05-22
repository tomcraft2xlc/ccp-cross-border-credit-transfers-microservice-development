package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import jakarta.validation.Valid;

import java.util.List;

public record ImportiCausale(
        @Valid
        Importo dovuto,
        List<@Valid IdentificativoEImporto> sconti,
        @Valid
        Importo notaDiCredito,
        List<@Valid IdentificativoEImporto> imposte,
        List<@Valid Rettifica> rettifiche,
        @Valid
        Importo disposto
) {

    public record IdentificativoEImporto(
            String codice,
            String codiceProprietario,
            Importo importo
    ) { }

    public record Rettifica(
            Importo importo,
            String verso,
            String motivo,
            String informazioniAggiuntive
    ) { }
}
