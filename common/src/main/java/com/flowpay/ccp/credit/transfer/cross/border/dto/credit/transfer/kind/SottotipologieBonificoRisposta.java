package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        title = "Elenco di sottotipi disponibili",
        description = """
                Elenco di tutti i sottotipi disponibili per un dato tipo di bonifico
                e una specifica banca.
                """
)
public record SottotipologieBonificoRisposta(
        @Schema(title = "Elenco dei sottotipi")
        List<SottoTipologiaBonificoRisposta> sottoTipologiaBonifici
) {
}
