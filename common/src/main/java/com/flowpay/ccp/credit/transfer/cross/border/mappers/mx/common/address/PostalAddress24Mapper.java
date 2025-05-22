package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.address;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.OptionalMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.google.common.base.Splitter;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress24;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        config = MxMappingConfig.class,
        imports = OptionalMapper.class)
@DecoratedWith(PostalAddress24Mapper.Decorator.class)
public interface PostalAddress24Mapper {

    PostalAddress24Mapper INSTANCE = Mappers.getMapper(PostalAddress24Mapper.class);

    @Mapping(target = "dept", source = "divisione")
    @Mapping(target = "subDept", source = "sottoDivisione")
    @Mapping(target = "strtNm", source = "indirizzo")
    @Mapping(target = "bldgNb", source = "numeroCivico")
    @Mapping(target = "bldgNm", source = "edificio")
    @Mapping(target = "flr", source = "piano")
    @Mapping(target = "pstBx", source = "cassettaPostale")
    @Mapping(target = "room", source = "stanza")
    @Mapping(target = "pstCd", source = "cap")
    @Mapping(target = "twnNm", source = "citta")
    @Mapping(target = "twnLctnNm", source = "localita")
    @Mapping(target = "dstrctNm", source = "distretto")
    @Mapping(target = "ctrySubDvsn", source = "provincia")
    @Mapping(target = "ctry", source = "paese")
    @Mapping(target = "adrLine", source = "lineaIndirizzo")
    @Mapping(target = "adrTp", ignore = true)
    PostalAddress24 map(IndirizzoPostale indirizzoPostale);

    default List<String> formatAddresLine(String lineaIndirizzo) {
        if (lineaIndirizzo != null && !lineaIndirizzo.isBlank()) {
            return Splitter.fixedLength(70).splitToList(lineaIndirizzo);
        }
        return null;
    }

    @Mapping(target = "adrTp", ignore = true)
    @Mapping(target = "dept", ignore = true)
    @Mapping(target = "subDept", ignore = true)
    @Mapping(target = "strtNm", expression = "java(OptionalMapper.INSTANCE.unwrap(postalAddressInfo.strtNm()))")
    @Mapping(target = "bldgNb", ignore = true)
    @Mapping(target = "bldgNm", ignore = true)
    @Mapping(target = "flr", ignore = true)
    @Mapping(target = "pstBx", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "pstCd", expression = "java(OptionalMapper.INSTANCE.unwrap(postalAddressInfo.cap()))")
    @Mapping(target = "twnNm", expression = "java(OptionalMapper.INSTANCE.unwrap(postalAddressInfo.twnNm()))")
    @Mapping(target = "twnLctnNm", ignore = true)
    @Mapping(target = "dstrctNm", ignore = true)
    @Mapping(target = "ctrySubDvsn", ignore = true)
    @Mapping(target = "ctry", expression = "java(OptionalMapper.INSTANCE.unwrap(postalAddressInfo.ctry()))")
    @Mapping(target = "adrLine", ignore = true)
    PostalAddress24 map(BanksConfig.BankConfig.PostalAddressInfo postalAddressInfo);

    abstract class Decorator implements PostalAddress24Mapper {

        private final PostalAddress24Mapper delegate;

        Decorator(PostalAddress24Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PostalAddress24 map(BanksConfig.BankConfig.PostalAddressInfo postalAddressInfo) {
            return Utils.allFieldsEmpty(delegate.map(postalAddressInfo));
        }

        @Override
        public PostalAddress24 map(IndirizzoPostale indirizzoPostale) {
            return Utils.allFieldsEmpty(delegate.map(indirizzoPostale));
        }
    }
}
