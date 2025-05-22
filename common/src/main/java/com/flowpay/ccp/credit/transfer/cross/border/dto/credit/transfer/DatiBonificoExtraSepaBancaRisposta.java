package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


@Schema(
    description = "Dati di un bonifico ExtraSepa tra banche già inserito nel sistema"
)
public record DatiBonificoExtraSepaBancaRisposta(

        @Valid
        @NotNull
        SottoTipologiaBonificoRisposta sottoTipologiaBonifico,

        @Valid
        @NotNull
        Intermediario ordinante,

        @Valid
        @NotNull
        Intermediario bancaOrdinante,

        @Valid
        @NotNull
        Intermediario beneficiario,

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
        DettagliBonificoExtraSepaBanca dettagliBonifico,

        @Valid
        @NotNull
        DettagliCausaleBanca dettagliCausale,

        @Valid
        @NotNull
        RiferimentiAggiuntivi riferimentiAggiuntivi,

        @Valid
        DettagliDocumentoDiCopertura documentoDiCopertura,

        String user
) {
}
