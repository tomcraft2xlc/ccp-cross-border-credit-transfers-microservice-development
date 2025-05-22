package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor;

public record InfoOrganizzazione(
        String bic,
        String codiceLEI,
        String identificativoOrganizzazione1,
        String codiceIdentificativoOrganizzazione1,
        String codiceProprietarioIdentificativoOrganizzazione1,
        String emittente1,
        String identificativoOrganizzazione2,
        String codiceIdentificativoOrganizzazione2,
        String codiceProprietarioIdentificativoOrganizzazione2,
        String emittente2
) {
}
