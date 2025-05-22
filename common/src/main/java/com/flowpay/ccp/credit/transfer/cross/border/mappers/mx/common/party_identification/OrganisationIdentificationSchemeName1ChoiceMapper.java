package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party44ChoiceMapper;
import com.prowidesoftware.swift.model.mx.dic.OrganisationIdentificationSchemeName1Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(OrganisationIdentificationSchemeName1ChoiceMapper.Decorator.class)
public interface OrganisationIdentificationSchemeName1ChoiceMapper {

    OrganisationIdentificationSchemeName1ChoiceMapper INSTANCE = Mappers.getMapper(OrganisationIdentificationSchemeName1ChoiceMapper.class);

    OrganisationIdentificationSchemeName1Choice map(String cd, String prtry);

    abstract class Decorator implements OrganisationIdentificationSchemeName1ChoiceMapper {

        private final OrganisationIdentificationSchemeName1ChoiceMapper delegate;

        Decorator(OrganisationIdentificationSchemeName1ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public OrganisationIdentificationSchemeName1Choice map(String cd, String prtry) {
            return Utils.allFieldsEmpty(delegate.map(cd, prtry));
        }
    }
}
