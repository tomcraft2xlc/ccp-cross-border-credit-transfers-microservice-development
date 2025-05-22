package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.prowidesoftware.swift.model.mx.dic.PersonIdentificationSchemeName1Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(PersonIdentificationSchemeName1ChoiceMapper.Decorator.class)
public interface PersonIdentificationSchemeName1ChoiceMapper {

    PersonIdentificationSchemeName1ChoiceMapper INSTANCE = Mappers.getMapper(PersonIdentificationSchemeName1ChoiceMapper.class);

    PersonIdentificationSchemeName1Choice map(String cd, String prtry);

    abstract class Decorator implements PersonIdentificationSchemeName1ChoiceMapper {

        private final PersonIdentificationSchemeName1ChoiceMapper delegate;

        Decorator(PersonIdentificationSchemeName1ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PersonIdentificationSchemeName1Choice map(String cd, String prtry) {
            return Utils.allFieldsEmpty(delegate.map(cd, prtry));
        }
    }
}
