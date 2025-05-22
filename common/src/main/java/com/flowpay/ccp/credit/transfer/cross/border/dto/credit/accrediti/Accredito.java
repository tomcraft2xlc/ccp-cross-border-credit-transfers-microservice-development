package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record Accredito(
        @Valid
        @NotNull
        TipoBonifico tipoBonifico,
        @Valid
        @NotNull
        Intermediario bancaDestinataria,
        @Valid
        @NotNull
        Intermediario bancaRegolanteRicevente,
        @Valid
        @NotNull
        DettagliIdentificativiBonifico dettagliIdentificativiBonifico,
        AttoreIdentificato beneficiario,
        AttoreIdentificato beneficiarioEffettivo,
        Intermediario bancaDelBeneficiario,
        AttoreIdentificato ordinante,
        AttoreIdentificato soggettoIstruttore,
        AttoreIdentificato ordinanteEffettivo,
        Intermediario bancaMittente,
        Intermediario bancaRegolanteMittente,
        Intermediario bancaDellOrdinante,
        AltriIntermediari altriIntermediari,
        DettagliBonifico dettagliBonifico,
        InfoStatoAccredito infoStato
) {

}
