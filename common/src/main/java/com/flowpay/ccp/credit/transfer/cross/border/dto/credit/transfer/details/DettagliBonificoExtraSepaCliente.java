package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ContoBancaDiCopertura;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniCliente;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

public record DettagliBonificoExtraSepaCliente(
        @Valid @NotNull DettagliImporto dettagliImporto,
        @Valid @NotNull DettagliDate dettagliDate,
        @Valid ContoBancaDiCopertura contoBancaDiCopertura,
        @Valid DettagliCommissioni dettagliCommissioni) implements DettagliBonificoExtraSepa {

    public record DettagliCommissioni(
            @Schema(description = "Tipologia del regolamento della commissione")
            @NotNull RegolamentoCommissione regolamentoClientela,
            @NotNull RegolamentoCommissione regolamentoBanca,
            @Schema(description = "Tipologia della commissione")
            @NotNull TipologiaCommissioni tipologiaCommissioni,
            List<@Valid CommissioniCliente> commissioniCliente,
            CommissioniBanca commissioniBanca) {
    }

}
