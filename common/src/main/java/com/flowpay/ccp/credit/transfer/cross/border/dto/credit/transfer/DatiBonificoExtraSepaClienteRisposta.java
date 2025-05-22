package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante.InfoOrdinante;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


@Schema(
    description = "Dati di un bonifico ExtraSepa tra account già inserito nel sistema"
)
public record DatiBonificoExtraSepaClienteRisposta(

        @Valid
        @NotNull
        SottoTipologiaBonificoRisposta sottoTipologiaBonifico,

        @Valid
        @NotNull
        InfoOrdinante ordinante,

        @Valid
        @NotNull
        Attore soggettoIstruttore,

        @Valid
        @NotNull
        Attore debitoreEffettivo,

        @Valid
        @NotNull
        Intermediario bancaOrdinante,

        @Valid
        @NotNull
        Attore beneficiario,

        @Valid
        @NotNull
        Attore beneficiarioEffettivo,

        @Valid
        @NotNull
        Intermediario bancaDestinataria,

        @Valid
        @NotNull
        Intermediario bancaDelBeneficiario,

        @Valid
        @NotNull
        AltriIntermediari altriIntermediari,

        @Valid
        @NotNull
        InfoSistemaDiRegolamento sistemaDiRegolamento,

        @Valid
        @NotNull
        DettagliBonificoExtraSepaCliente dettagliBonifico,

        @Valid
        @NotNull
        DettagliCausaleCliente dettagliCausale,

        @Valid
        @NotNull
        RiferimentiAggiuntivi riferimentiAggiuntivi,

        @Valid
        DettagliDocumentoDiCopertura documentoDiCopertura,

        String user
) {
}
