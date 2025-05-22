package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;


import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.InfoOrganizzazione;
import com.prowidesoftware.swift.model.mx.dic.GenericOrganisationIdentification1;
import com.prowidesoftware.swift.model.mx.dic.OrganisationIdentification29;
import com.prowidesoftware.swift.model.mx.dic.OrganisationIdentificationSchemeName1Choice;
import com.prowidesoftware.swift.model.mx.dic.Party38Choice;
import org.javatuples.Quartet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InfoOrganizzazioneMapper {

    default InfoOrganizzazione map(Party38Choice party) {
        if (party == null) {
            return null;
        }
        var orgID = party.getOrgId();
        if (orgID == null) {
            return null;
        }
        var result = map(party.getOrgId());
        result = update(result, orgID.getOthr());
        return result;
    }

    @Mapping(target = "bic", source = "anyBIC")
    @Mapping(target = "codiceLEI", source = "LEI")
    @Mapping(target = "identificativoOrganizzazione1", ignore = true)
    @Mapping(target = "codiceIdentificativoOrganizzazione1", ignore = true)
    @Mapping(target = "codiceProprietarioIdentificativoOrganizzazione1", ignore = true)
    @Mapping(target = "emittente1", ignore = true)
    @Mapping(target = "identificativoOrganizzazione2", ignore = true)
    @Mapping(target = "codiceIdentificativoOrganizzazione2", ignore = true)
    @Mapping(target = "codiceProprietarioIdentificativoOrganizzazione2", ignore = true)
    @Mapping(target = "emittente2", ignore = true)
    InfoOrganizzazione map(OrganisationIdentification29 organisation);

    default InfoOrganizzazione update(InfoOrganizzazione organizzazione, List<GenericOrganisationIdentification1> othr) {
        String identificativoOrganizzazione1 = null;
        String codiceIdentificativoOrganizzazione1 = null;
        String codiceProprietarioIdentificativoOrganizzazione1 = null;
        String emittente1 = null;
        String identificativoOrganizzazione2 = null;
        String codiceIdentificativoOrganizzazione2 = null;
        String codiceProprietarioIdentificativoOrganizzazione2 = null;
        String emittente2 = null;
        if (othr.isEmpty()) {
            return organizzazione;
        }
        var info = extract(othr.get(0));
        identificativoOrganizzazione1 = info.getValue0();
        codiceIdentificativoOrganizzazione1 = info.getValue1();
        codiceProprietarioIdentificativoOrganizzazione1 = info.getValue2();
        emittente1 = info.getValue3();
        if (othr.size() >= 2) {
            info = extract(othr.get(1));
            identificativoOrganizzazione2 = info.getValue0();
            codiceIdentificativoOrganizzazione2 = info.getValue1();
            codiceProprietarioIdentificativoOrganizzazione2 = info.getValue2();
            emittente2 = info.getValue3();
        }

        return update(
                organizzazione,
                identificativoOrganizzazione1,
                codiceIdentificativoOrganizzazione1,
                codiceProprietarioIdentificativoOrganizzazione1,
                emittente1,
                identificativoOrganizzazione2,
                codiceIdentificativoOrganizzazione2,
                codiceProprietarioIdentificativoOrganizzazione2,
                emittente2);
    }

    default Quartet<String, String, String, String> extract(GenericOrganisationIdentification1 identification) {
        if (identification == null) {
            return null;
        }
        var identificativo = identification.getId();
        var codice = Optional.ofNullable(identification.getSchmeNm()).map(OrganisationIdentificationSchemeName1Choice::getCd).orElse(null);
        var proprietario = Optional.ofNullable(identification.getSchmeNm()).map(OrganisationIdentificationSchemeName1Choice::getPrtry).orElse(null);
        var emittente = identification.getIssr();
        return Quartet.with(identificativo, codice, proprietario, emittente);
    }

    InfoOrganizzazione update(InfoOrganizzazione organizzazione,
                              String identificativoOrganizzazione1,
                              String codiceIdentificativoOrganizzazione1,
                              String codiceProprietarioIdentificativoOrganizzazione1,
                              String emittente1,
                              String identificativoOrganizzazione2,
                              String codiceIdentificativoOrganizzazione2,
                              String codiceProprietarioIdentificativoOrganizzazione2,
                              String emittente2);
}
