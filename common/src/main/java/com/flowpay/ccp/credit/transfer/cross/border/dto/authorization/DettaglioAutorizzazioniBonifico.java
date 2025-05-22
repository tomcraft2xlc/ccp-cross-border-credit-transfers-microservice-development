package com.flowpay.ccp.credit.transfer.cross.border.dto.authorization;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.AutorizzazioneActionEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record DettaglioAutorizzazioniBonifico(
        String profilo,
        String nomeCompleto,
        String ruolo,
        AutorizzazioneActionEnum azione,
        Instant azioneEseguitaAlle,
        Long livelloAutorizzazione,
        Boolean messaggioAutorizzato,
        Boolean notificaAutorizzata,
        LocalDate dataRegolamentoPrecedente,
        String nota
) {
}
