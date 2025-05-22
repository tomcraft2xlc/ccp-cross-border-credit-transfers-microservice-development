package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;
import com.prowidesoftware.swift.model.mx.dic.CashAccount38;
import com.prowidesoftware.swift.model.mx.dic.PartyIdentification135;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        IndirizzoMapper.class,
        RapportoMapper.class,
        InfoOrganizzazioneMapper.class,
        InfoPrivatoMapper.class
})
public interface AttoreMapper {

    AttoreMapper INSTANCE = Mappers.getMapper(AttoreMapper.class);

    @Mapping(target = "intestazione", source = "party.nm")
    @Mapping(target = "indirizzo", source = "party.pstlAdr")
    @Mapping(target = "paeseDiResidenza", source = "party.ctryOfRes")
    @Mapping(target = "organizzazione", source = "party.id")
    @Mapping(target = "privato", source = "party.id")
    @Mapping(target = "rapporto", source = "account")
    Attore map(PartyIdentification135 party, CashAccount38 account);
}
