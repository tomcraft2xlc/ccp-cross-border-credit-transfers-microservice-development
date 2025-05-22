package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.AttoreIdentificato;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.IdentificazioneAttore;
import com.prowidesoftware.swift.model.mx.dic.CashAccount38;
import com.prowidesoftware.swift.model.mx.dic.Party38Choice;
import com.prowidesoftware.swift.model.mx.dic.PartyIdentification135;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.function.Function;

@Mapper(imports = AttoreMapper.class)
public interface AttoreIdentificatoMapper {

    AttoreIdentificatoMapper INSTANCE = Mappers.getMapper(AttoreIdentificatoMapper.class);

    @Mapping(target = "identificazione", source = "party.id")
    @Mapping(target = "attore", expression = "java(AttoreMapper.INSTANCE.map(party, account))")
    AttoreIdentificato map(PartyIdentification135 party, CashAccount38 account);

    default IdentificazioneAttore map(Party38Choice id) {
        if (id == null) {
            return null;
        }
        if (id.getOrgId() != null) {
            return IdentificazioneAttore.ORGANIZZAZIONE;
        }
        return IdentificazioneAttore.INDIVIDUO;
    }

    default <T> AttoreIdentificato map(Optional<T> value, Function<T, PartyIdentification135> getParty, Function<T, CashAccount38> getAccount) {
        PartyIdentification135 party = null;
        if (getParty != null) {
            party = value.map(getParty).orElse(null);
        }
        CashAccount38 account = null;
        if (getAccount != null) {
            account = value.map(getAccount).orElse(null);
        }

        return this.map(party, account);
    }
}
