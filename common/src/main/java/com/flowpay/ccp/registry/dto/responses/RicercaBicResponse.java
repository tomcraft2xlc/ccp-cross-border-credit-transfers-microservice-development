package com.flowpay.ccp.registry.dto.responses;

import com.flowpay.ccp.registry.persistence.RicercaBic;

public record RicercaBicResponse(
        String codiceBIC,
        String intestazioneBic,
        String codicePaeseIso,
        String decodificaPaese,
        Long numeroAbi,
        Boolean flagRaggiungSepa,
        Boolean flagTgt,
        RicercaBic.DirezioneTarget flagTgtDirIndir,
        String bicBancaTramitante,
        RicercaBic.TipoEmbargo flagEmbargo,
        Boolean flagCondSpecPaese,
        Long posizioneEstero,
        Boolean flagScambioChiavi,
        String indirizzo,
        String citta,
        String provincia,
        String codiceLei,
        String zipCode
) {
}
