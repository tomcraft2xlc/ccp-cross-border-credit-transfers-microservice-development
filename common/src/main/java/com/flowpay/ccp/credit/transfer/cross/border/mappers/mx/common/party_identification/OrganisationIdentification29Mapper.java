package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Organizzazione;
import com.prowidesoftware.swift.model.mx.dic.OrganisationIdentification29;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        GenericOrganisationIdentification1Mapper.class
})
@DecoratedWith(OrganisationIdentification29Mapper.Decorator.class)
public interface OrganisationIdentification29Mapper {

    @Mapping(target = "anyBIC", source = "bic")
    @Mapping(target = "LEI", source = "codiceLEI")
    @Mapping(target = "othr", source = ".")
    OrganisationIdentification29 map(Organizzazione organizzazione);

    abstract class Decorator implements OrganisationIdentification29Mapper {

        private final OrganisationIdentification29Mapper delegate;

        Decorator(OrganisationIdentification29Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public OrganisationIdentification29 map(Organizzazione organizzazione) {
            return Utils.allFieldsEmpty(delegate.map(organizzazione));
        }
    }
}
