package com.flowpay.ccp.credit.transfer.cross.border.dto.authorization;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    title = "Informazioni riguardo l'autorizzazione di un bonifico", 
    description = "Contiene i dati riguardo all'autorizzazione di uno specifico bonifico."
)
public record InserisciAutorizzazioneBonifico(
        @Schema(description = "Indica se viene fornita l'autorizzazione alla disposizione del messaggio.", required = true) 
        boolean autorizzazioneMessaggio,

        @Schema(description = """
                Indica se viene fornita l'autorizzazione alla disposizione del messaggio di notifica.

                A seconda dei messaggi da generare in rete oltre al messaggio potrebbe essere necessario l'invio di un messaggio __notifica__
                In questo caso l'operatore deve autorizzare oltre al messaggio anche la notifica stessa.""") 
        Boolean autorizzazioneNotifica,

        @Schema(description = "Note di testo libero") 
        String note) {
}