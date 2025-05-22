package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = """
        Causale di una transazione banca a banca.
        """)
@JsonIgnoreProperties({"causaleStrutturata"})
public record CausaleBanca(
        @Schema(description = "Rappresenta l'intera causale.") 
        @NotBlank 
        String causaleDescrittiva
) implements Causale {

    @Override
    public List<CausaleStrutturata> causaleStrutturata() {
        return null;
    }
}
