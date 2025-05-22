package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Indirizzo;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress24;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress6;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IndirizzoMapper {

    @Mapping(target = "indirizzo", source = "strtNm")
    @Mapping(target = "citta", source = "twnNm")
    @Mapping(target = "cap", source = "pstCd")
    @Mapping(target = "paese", source = "ctry")
    @Mapping(target = "divisione", source = "dept")
    @Mapping(target = "sottoDivisione", source = "subDept")
    @Mapping(target = "numeroCivico", source = "bldgNb")
    @Mapping(target = "edificio", source = "bldgNb")
    @Mapping(target = "piano", source = "flr")
    @Mapping(target = "cassettaPostale", source = "pstBx")
    @Mapping(target = "stanza", source = "room")
    @Mapping(target = "localita", source = "twnLctnNm")
    @Mapping(target = "distretto", source = "dstrctNm")
    @Mapping(target = "provincia", source = "ctrySubDvsn")
    @Mapping(target = "lineaIndirizzo", source = "adrLine")
    Indirizzo map(PostalAddress24 address);

    @Mapping(target = "indirizzo", source = "strtNm")
    @Mapping(target = "citta", source = "twnNm")
    @Mapping(target = "cap", source = "pstCd")
    @Mapping(target = "paese", source = "ctry")
    @Mapping(target = "divisione", source = "dept")
    @Mapping(target = "sottoDivisione", source = "subDept")
    @Mapping(target = "numeroCivico", source = "bldgNb")
    @Mapping(target = "edificio", source = "bldgNb")
    @Mapping(target = "piano", ignore = true)
    @Mapping(target = "cassettaPostale", ignore = true)
    @Mapping(target = "stanza", ignore = true)
    @Mapping(target = "localita", ignore = true)
    @Mapping(target = "distretto", ignore = true)
    @Mapping(target = "provincia", source = "ctrySubDvsn")
    @Mapping(target = "lineaIndirizzo", source = "adrLine")
    Indirizzo map(PostalAddress6 address);

    default String map(List<String> list) {
       return String.join("", list);
    }
}
