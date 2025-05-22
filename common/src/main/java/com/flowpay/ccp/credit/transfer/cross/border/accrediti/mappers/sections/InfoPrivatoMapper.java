package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.InfoPrivato;
import com.prowidesoftware.swift.model.mx.dic.*;
import org.javatuples.Quartet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InfoPrivatoMapper {

    default InfoPrivato map(Party38Choice party) {
        if (party == null) {
            return null;
        }
        var privtID = party.getPrvtId();
        if (privtID == null) {
            return null;
        }
        var result = map(privtID.getDtAndPlcOfBirth());
        result = update(result, privtID.getOthr());
        return result;
    }

    @Mapping(target = "dataDiNascita", source = "birthDt")
    @Mapping(target = "provinciaDiNascita", source = "prvcOfBirth")
    @Mapping(target = "cittaDiNascita", source = "cityOfBirth")
    @Mapping(target = "paeseDiNascita", source = "ctryOfBirth")
    @Mapping(target = "identificativoSoggetto1", ignore = true)
    @Mapping(target = "codiceIdentificativoSoggetto1", ignore = true)
    @Mapping(target = "codiceProprietarioIdentificativoSoggetto1", ignore = true)
    @Mapping(target = "emittente1", ignore = true)
    @Mapping(target = "identificativoSoggetto2", ignore = true)
    @Mapping(target = "codiceIdentificativoSoggetto2", ignore = true)
    @Mapping(target = "codiceProprietarioIdentificativoSoggetto2", ignore = true)
    @Mapping(target = "emittente2", ignore = true)
    InfoPrivato map(DateAndPlaceOfBirth1 info);

    default InfoPrivato update(InfoPrivato privato, List<GenericPersonIdentification1> othr) {
        String identificativoSoggetto1 = null;
        String codiceIdentificativoSoggetto1 = null;
        String codiceProprietarioIdentificativoSoggetto1 = null;
        String emittente1 = null;
        String identificativoSoggetto2 = null;
        String codiceIdentificativoSoggetto2 = null;
        String codiceProprietarioIdentificativoSoggetto2 = null;
        String emittente2 = null;
        if (othr.isEmpty()) {
            return privato;
        }
        var info = extract(othr.get(0));
        identificativoSoggetto1 = info.getValue0();
        codiceIdentificativoSoggetto1 = info.getValue1();
        codiceProprietarioIdentificativoSoggetto1 = info.getValue2();
        emittente1 = info.getValue3();
        if (othr.size() >= 2) {
            info = extract(othr.get(1));
            identificativoSoggetto2 = info.getValue0();
            codiceIdentificativoSoggetto2 = info.getValue1();
            codiceProprietarioIdentificativoSoggetto2 = info.getValue2();
            emittente2 = info.getValue3();
        }

        return update(
                privato,
                identificativoSoggetto1,
                codiceIdentificativoSoggetto1,
                codiceProprietarioIdentificativoSoggetto1,
                emittente1,
                identificativoSoggetto2,
                codiceIdentificativoSoggetto2,
                codiceProprietarioIdentificativoSoggetto2,
                emittente2);
    }

    default Quartet<String, String, String, String> extract(GenericPersonIdentification1 identification) {
        if (identification == null) {
            return null;
        }
        var identificativo = identification.getId();
        var codice = Optional.ofNullable(identification.getSchmeNm()).map(PersonIdentificationSchemeName1Choice::getCd).orElse(null);
        var proprietario = Optional.ofNullable(identification.getSchmeNm()).map(PersonIdentificationSchemeName1Choice::getPrtry).orElse(null);
        var emittente = identification.getIssr();
        return Quartet.with(identificativo, codice, proprietario, emittente);
    }

    InfoPrivato update(InfoPrivato privato,
                       String identificativoSoggetto1,
                       String codiceIdentificativoSoggetto1,
                       String codiceProprietarioIdentificativoSoggetto1,
                       String emittente1,
                       String identificativoSoggetto2,
                       String codiceIdentificativoSoggetto2,
                       String codiceProprietarioIdentificativoSoggetto2,
                       String emittente2);
}
