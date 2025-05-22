package com.flowpay.ccp.credit.transfer.cross.border.mapping.authorization;

import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.DettaglioAutorizzazioniBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.ListaAutorizzazioniBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.Autorizzazione;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(config = MappingCommonConfig.class)
public interface ListaAutorizzazioniBonificoMapper {

    default ListaAutorizzazioniBonifico map(List<Autorizzazione> autorizzazioni, @Context Boolean conNotifica) {
        return new ListaAutorizzazioniBonifico(
            isNotificaNegata(autorizzazioni, conNotifica),
            mapDettaglio(autorizzazioni, conNotifica)
        );
    }

    default Boolean isNotificaNegata(List<Autorizzazione> autorizzazioni, @Context Boolean conNotifica) {
        if (Boolean.FALSE.equals(conNotifica)) {
            return null;
        }

        return autorizzazioni.stream().anyMatch(autorizzazione -> !Optional.ofNullable(autorizzazione.autorizzazioneNotifica()).orElse(true));
    }

    List<DettaglioAutorizzazioniBonifico> mapDettaglio(List<Autorizzazione> autorizzazioni, @Context Boolean conNotifica);


    @Mapping(target = "profilo", source = "utente")
    @Mapping(target = "nomeCompleto", source = "nomeUtente")
    @Mapping(target = "azioneEseguitaAlle", source = "createdAt")
    @Mapping(target = "notificaAutorizzata", source = "autorizzazioneNotifica")
    @Mapping(target = "messaggioAutorizzato", source = "autorizzazioneMessaggio")
    @Mapping(target = "dataRegolamentoPrecedente", source = "dataDiRegolamentoPrecedente")
    @Mapping(target = "nota", source = "note")
    DettaglioAutorizzazioniBonifico mapDettaglio(Autorizzazione autorizzazione, @Context Boolean conNotifica);
}
