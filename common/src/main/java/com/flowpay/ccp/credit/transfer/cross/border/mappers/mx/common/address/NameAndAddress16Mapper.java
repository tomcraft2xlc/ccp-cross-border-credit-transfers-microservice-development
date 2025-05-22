package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.address;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.prowidesoftware.swift.model.mx.dic.NameAndAddress16;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        uses = PostalAddress24Mapper.class)
@DecoratedWith(NameAndAddress16Mapper.Decorator.class)
public interface NameAndAddress16Mapper {

    NameAndAddress16Mapper INSTANCE = Mappers.getMapper(NameAndAddress16Mapper.class);

    NameAndAddress16 map(String nm, IndirizzoPostale adr);

    abstract class Decorator implements NameAndAddress16Mapper {

        private final NameAndAddress16Mapper delegate;

        Decorator(NameAndAddress16Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public NameAndAddress16 map(String nm, IndirizzoPostale adr) {
            return Utils.allFieldsEmpty(delegate.map(nm, adr));
        }
    }
}
