package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.address.PostalAddress24Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionIdentification18;
import com.prowidesoftware.swift.model.mx.dic.PostalAddress24;
import org.mapstruct.*;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        ClearingSystemMemberIdentification2Mapper.class,
        PostalAddress24Mapper.class
})
@DecoratedWith(FinancialInstitutionIdentification18Mapper.Decorator.class)
public interface FinancialInstitutionIdentification18Mapper {

    @Mapping(target = "BICFI", source = "entity.bic")
    @Mapping(target = "clrSysMmbId", source = "entity", conditionQualifiedByName = "checkInclude")
    @Mapping(target = "LEI", source = "entity.codiceLEI", conditionQualifiedByName = "checkInclude")
    @Mapping(target = "nm", source = "entity.intestazione", conditionQualifiedByName = "checkIncludeSTP")
    @Mapping(target = "pstlAdr", source = "indirizzoPostale.entity", conditionQualifiedByName = "checkIncludeSTP")
    @Mapping(target = "othr", ignore = true)
    FinancialInstitutionIdentification18 map(InformazioniIntermediario.WithLinkedEntities informazioni, @Context MappingContext context, @Context Boolean fullInfo);


    @Mapping(target = "BICFI", expression = "java(bankConfig.bic())")
    @Mapping(target = "clrSysMmbId", ignore = true)
    @Mapping(target = "LEI", expression = "java(stringIfFullInfo(bankConfig.lei(), fullInfo))")
    @Mapping(target = "nm", expression = "java(stringIfNotSTPAndFullInfo(bankConfig.name(), context, fullInfo))")
    @Mapping(target = "pstlAdr", expression = "java(includePostalAddress(bankConfig, context, fullInfo))")
    @Mapping(target = "othr", ignore = true)
    FinancialInstitutionIdentification18 map(BanksConfig.BankConfig bankConfig, @Context MappingContext context, @Context Boolean fullInfo);

    @Named("checkInclude")
    @Condition
    default Boolean include(InformazioniIntermediario.WithLinkedEntities informazioni, @Context Boolean fullInfo) {
        return fullInfo;
    }

    @Named("checkIncludeSTP")
    @Condition
    default Boolean include(InformazioniIntermediario.WithLinkedEntities informazioni, @Context MappingContext context, @Context Boolean fullInfo) {
        return !context.stp() && fullInfo;
    }

    default PostalAddress24 includePostalAddress(BanksConfig.BankConfig bankConfig, @Context MappingContext context, @Context Boolean fullInfo) {
        if (Boolean.FALSE.equals(context.stp()) && Boolean.TRUE.equals(fullInfo)) {
            return PostalAddress24Mapper.INSTANCE.map(bankConfig.address());
        }
        return null;
    }

    default String stringIfFullInfo(String value, Boolean fullInfo) {
        if (Boolean.TRUE.equals(fullInfo)) {
            return value;
        }
        return null;
    }

    default String stringIfNotSTPAndFullInfo(String value, MappingContext context, Boolean fullInfo) {
        if (Boolean.FALSE.equals(context.stp()) && Boolean.TRUE.equals(fullInfo)) {
            return value;
        }
        return null;
    }

    abstract class Decorator implements FinancialInstitutionIdentification18Mapper {

        private final FinancialInstitutionIdentification18Mapper delegate;

        Decorator(FinancialInstitutionIdentification18Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public FinancialInstitutionIdentification18 map(InformazioniIntermediario.WithLinkedEntities informazioni, MappingContext context, Boolean fullInfo) {
            return Utils.allFieldsEmpty(delegate.map(informazioni, context, fullInfo));
        }

        @Override
        public FinancialInstitutionIdentification18 map(BanksConfig.BankConfig bankConfig, MappingContext context, Boolean fullInfo) {
            return Utils.allFieldsEmpty(delegate.map(bankConfig, context, fullInfo));
        }
    }
}
