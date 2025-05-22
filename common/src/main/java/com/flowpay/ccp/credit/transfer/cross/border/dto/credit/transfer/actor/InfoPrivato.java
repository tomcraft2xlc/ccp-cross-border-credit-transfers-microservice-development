package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor;

import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(title = "Informazioni Privato", description = "Informazioni su un privato cittadino")
public record InfoPrivato(
        LocalDate dataDiNascita,
        String provinciaDiNascita,
        String cittaDiNascita,
        String paeseDiNascita,
        String identificativoSoggetto1,
        String codiceIdentificativoSoggetto1,
        String codiceProprietarioIdentificativoSoggetto1,
        String emittente1,
        String identificativoSoggetto2,
        String codiceIdentificativoSoggetto2,
        String codiceProprietarioIdentificativoSoggetto2,
        String emittente2
) {
}
