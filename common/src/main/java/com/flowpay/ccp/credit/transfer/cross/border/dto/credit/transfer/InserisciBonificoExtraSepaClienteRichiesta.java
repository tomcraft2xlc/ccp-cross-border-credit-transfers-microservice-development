package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante.InfoOrdinante;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleCliente;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


@Schema(
    description = """
            Dati necessari per inserire un bonifico account to account nel sistema.
            """
)
public record InserisciBonificoExtraSepaClienteRichiesta(

        String tid,
        @Valid
        @NotNull
        SottoTipologiaBonificoRichiesta sottoTipologiaBonifico,

        @Valid
        @NotNull
        InfoOrdinante ordinante,

        @Valid
        Attore soggettoIstruttore,

        @Valid
        Attore debitoreEffettivo,

        @Valid
        @NotNull
        Intermediario bancaOrdinante,

        @Valid
        @NotNull
        Attore beneficiario,

        @Valid
        Attore beneficiarioEffettivo,

        @Valid
        Intermediario bancaDestinataria,

        @Valid
        Intermediario bancaDelBeneficiario,

        @Valid
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
        RiferimentiAggiuntivi riferimentiAggiuntivi,

        String user,

        @Valid
        DettagliDocumentoDiCopertura documentoDiCopertura
) implements InserisciBonificoExtraSepaRichiesta {

    @Override
    public boolean isBancaABanca() {
        return false;
    }
}
