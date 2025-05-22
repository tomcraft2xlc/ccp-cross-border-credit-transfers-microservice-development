package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Organizzazione;
import com.prowidesoftware.swift.model.mx.dic.GenericOrganisationIdentification1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(GenericOrganisationIdentification1Mapper.Decorator.class)
public interface GenericOrganisationIdentification1Mapper {


    @Mapping(target = "id", source = "identificativo")
    @Mapping(target = "schmeNm", expression = "java(OrganisationIdentificationSchemeName1ChoiceMapper.INSTANCE.map(codiceIdentificativo, codiceProprietarioIdentificativo))")
    @Mapping(target = "issr", source = "emittente")
    GenericOrganisationIdentification1 map(
            String identificativo,
            String codiceIdentificativo,
            String codiceProprietarioIdentificativo,
            String emittente);

    default List<GenericOrganisationIdentification1> createList(Organizzazione organizzazione) {
        var result = new ArrayList<GenericOrganisationIdentification1>(2);
        result.add(
                this.map(
                        organizzazione.identificativoOrganizzazione1(),
                        organizzazione.codiceIdentificativoOrganizzazione1(),
                        organizzazione.codiceProprietarioIdentificativoOrganizzazione1(),
                        organizzazione.emittente1()));
        result.add(
                this.map(
                        organizzazione.identificativoOrganizzazione2(),
                        organizzazione.codiceIdentificativoOrganizzazione2(),
                        organizzazione.codiceProprietarioIdentificativoOrganizzazione2(),
                        organizzazione.emittente2()));
        return result;
    }

    abstract class Decorator implements GenericOrganisationIdentification1Mapper {

        private final GenericOrganisationIdentification1Mapper delegate;

        Decorator(GenericOrganisationIdentification1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public GenericOrganisationIdentification1 map(String identificativo, String codiceIdentificativo, String codiceProprietarioIdentificativo, String emittente) {
            return Utils.allFieldsEmpty(delegate.map(identificativo, codiceIdentificativo, codiceProprietarioIdentificativo, emittente));
        }
    }
}
