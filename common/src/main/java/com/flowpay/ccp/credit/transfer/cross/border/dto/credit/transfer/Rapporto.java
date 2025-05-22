package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoRapporto;

import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        description = "Rapporto tra la banca e un attore. Usualmente corrisponde a un conto aperto."
)
public record Rapporto(


        String numero,

        @Schema(description = "Identificativo del rapporto", oneOf = {
            Rapporto.NumeroRapportoIban.class,
            Rapporto.NumeroRapportoAltroIdentificativo.class
        })
        @NotNull
        String identificativo,

        @Schema(description = "Divisa del conto")
        String divisa,

        @Schema(description = "Codice tipo conto")
        String codiceTipoConto,

        @Schema(description = "Descrizione tipo conto")
        String dettaglioTipoConto,

        @Schema(description = "Intestazione del conto")
        String intestazioneConto,

        @Schema(description = "Codice tipo identificativo alias")
        String codiceTipoAlias,

        @Schema(description = "Descrizione tipo identificativo alias")
        String descrizioneAlias,

        @Schema(description = "Dettaglio tipo identificativo alias")
        String dettaglioIdentificativoAlias,

        @Schema(description = "Codice identificativo conto")
        String codiceIdentificativoConto,

        @Schema(description = "Descrizione identificativo conto")
        String descrizioneIdentificativoConto,

        @Schema(description = "Emittente del conto")
        String emittente,

        @Schema(description = "Codice filiale della banca")
        Long codiceFiliale,

        @Schema(description = "Denominazione filiale della banca")
        String denominazioneFiliale,

        TipoRapporto tipo,

        String ndg
) {
    @Schema(
        title = "Identificativo IBAN",
        description = "Rapporto rappresentato da un IBAN",
        type = SchemaType.STRING,
        pattern = "^[A-Z]{2}.*$",
        example = "AD1400080001001234567890"
    )
    private static final class NumeroRapportoIban {}
    @Schema(
        title = "Altro identificativo",
        description = "Rapporto rappresentato da un altro identificativo",
        type = SchemaType.STRING,
        pattern = "^[^A-Z]{2}.*$"
    )
    private static final class NumeroRapportoAltroIdentificativo {}
}

