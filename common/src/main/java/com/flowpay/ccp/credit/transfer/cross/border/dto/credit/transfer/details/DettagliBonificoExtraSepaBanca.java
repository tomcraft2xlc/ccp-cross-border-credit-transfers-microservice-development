package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ContoBancaDiCopertura;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniBanca;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DettagliBonificoExtraSepaBanca(
        @Valid @NotNull DettagliImporto dettagliImporto,
        @Valid @NotNull DettagliDate dettagliDate,
        @Valid ContoBancaDiCopertura contoBancaDiCopertura,
        @Valid DettagliCommissioni dettagliCommissioni,
        @Valid DettagliNotifica dettagliNotifica) implements DettagliBonificoExtraSepa {

    public record DettagliCommissioni(
            @NotNull RegolamentoCommissione regolamentoBanca,
            @Valid CommissioniBanca commissioniBanca) {
    }

    public record DettagliNotifica(
            String notifica) {
    }
}
