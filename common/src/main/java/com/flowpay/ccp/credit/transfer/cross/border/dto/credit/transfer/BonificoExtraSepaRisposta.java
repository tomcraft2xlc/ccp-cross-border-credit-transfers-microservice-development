package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(
        title = "Dati di un bonifico nel sistema",
        description = "Contiene tutti i campi relativi a un bonifico nel sistema"
)
public record BonificoExtraSepaRisposta(
    @Schema(description = "Identificativo univoco del bonifico")
    @NotNull UUID id,
    @Schema(description = "Identificativo della transazione")
    @NotNull String tid,
    @NotNull String identificativoFlusso,
    @NotNull String idMessaggio,
    @NotNull String idTransazione,
    @NotNull String identificativoDiDefinizioneDelMessaggio,
    Long numeroTransazione,
    String servizioDiBusiness,
    Long numeroDiTransazioni,
    String idSistemaDiClearing,
    String canaleDiClearing,
    
    @Valid DatiBonificoExtraSepaClienteRisposta bonificoCliente,
    @Valid DatiBonificoExtraSepaBancaRisposta bonificoBanca,

    @Schema(description = "Informazioni sullo stato del bonifico")
    @Valid @NotNull InfoStato infoStato
) {


    public record InfoStato(
        @Schema(description = "Stato del bonifico")
        @Valid @NotNull CreditTransferStatus stato,
        Boolean inGestione
    ) {
    }

}
