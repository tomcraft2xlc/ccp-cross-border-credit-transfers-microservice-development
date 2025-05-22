package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;
import jakarta.validation.Valid;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(title = "Causale strutturata", description = """
        Rappresenta una causale strutturata, contentent informazioni complete e accessibili
        meccanicamente sul motivo per cui un determinato pagamento è stato effettuato.
        """)
public record CausaleStrutturata(
        @Schema(title = "Documenti di riferimento", description = "Lista dei documenti di riferimento") 
        List<@Valid DocumentoDiRiferimento> documentoDiRiferimento,

        @Valid 
        ImportiCausale importi,

        @Valid 
        InformazioniCreditore informazioniCreditore,

        @Schema(description = "Informazioni sull'emittente del bonifico") 
        @Valid 
        Attore informazioniEmittente,

        @Schema(description = "Informazioni sul ricevente del bonifico") 
        @Valid 
        Attore informazioniRicevente,

        @Valid 
        InformazioniFiscali informazioniFiscali,

        @Valid 
        InformazioniPignoramento dettagliPignoramento,

        @Schema(description = "Ulteriori informazioni sulla causale") 
        String ulterioriInformazioni

) {
}
