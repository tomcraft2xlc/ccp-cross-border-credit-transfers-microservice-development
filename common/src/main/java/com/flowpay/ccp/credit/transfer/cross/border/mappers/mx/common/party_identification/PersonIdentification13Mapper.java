package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Privato;
import com.prowidesoftware.swift.model.mx.dic.PersonIdentification13;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        DateAndPlaceOfBirth1Mapper.class,
        GenericPersonIdentification1Mapper.class
})
@DecoratedWith(PersonIdentification13Mapper.Decorator.class)
public interface PersonIdentification13Mapper {

    @Mapping(target = "dtAndPlcOfBirth", source = ".")
    @Mapping(target = "othr", source = ".")
    PersonIdentification13 map(Privato privato);

    abstract class Decorator implements PersonIdentification13Mapper {

        private final PersonIdentification13Mapper delegate;

        Decorator(PersonIdentification13Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PersonIdentification13 map(Privato privato) {
            return Utils.allFieldsEmpty(delegate.map(privato));
        }
    }
}
