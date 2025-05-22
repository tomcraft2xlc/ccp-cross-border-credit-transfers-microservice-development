package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepaBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausaleBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


@Schema(
    description = """
            Dati necessari per inserire un bonifico banca a banca nel sistema.
            """
)
public record InserisciBonificoExtraSepaBancaRichiesta(

        String tid,
        @Valid
        @NotNull
        SottoTipologiaBonificoRichiesta sottoTipologiaBonifico,

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
        Intermediario bancaDelBeneficiario,

        @Valid
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
        RiferimentiAggiuntivi riferimentiAggiuntivi,

        String user
) implements InserisciBonificoExtraSepaRichiesta {

    @Override
    public boolean isBancaABanca() {
        return true;
    }

        @Override
        public DettagliDocumentoDiCopertura documentoDiCopertura() {
                return null;
        }
}
