package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Privato;
import com.prowidesoftware.swift.model.mx.dic.DateAndPlaceOfBirth1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(DateAndPlaceOfBirth1Mapper.Decorator.class)
public interface DateAndPlaceOfBirth1Mapper {

    @Mapping(target = "birthDt", source = "dataDiNascita")
    @Mapping(target = "prvcOfBirth", source = "provinciaDiNascita")
    @Mapping(target = "cityOfBirth", source = "cittaDiNascita")
    @Mapping(target = "ctryOfBirth", source = "paeseDiNascita")
    DateAndPlaceOfBirth1 map(Privato privato);

    abstract class Decorator implements DateAndPlaceOfBirth1Mapper {

        private final DateAndPlaceOfBirth1Mapper delegate;

        Decorator(DateAndPlaceOfBirth1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DateAndPlaceOfBirth1 map(Privato privato) {
            return Utils.allFieldsEmpty(delegate.map(privato));
        }
    }
}
