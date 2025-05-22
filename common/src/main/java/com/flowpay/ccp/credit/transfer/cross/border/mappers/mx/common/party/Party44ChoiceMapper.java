package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification6;
import com.prowidesoftware.swift.model.mx.dic.Party44Choice;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        imports = {
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        TipoIntermediario.class
})
@DecoratedWith(Party44ChoiceMapper.Decorator.class)
public interface Party44ChoiceMapper {

    Party44ChoiceMapper INSTANCE = Mappers.getMapper(Party44ChoiceMapper.class);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bankConfig, context, false))")
    Party44Choice map(BanksConfig.BankConfig bankConfig, @Context MappingContext context);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(map(bonifico, context, TipoIntermediario.BANCA_DESTINATARIA))")
    Party44Choice map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", source = ".")
    Party44Choice map(BranchAndFinancialInstitutionIdentification6 value);


    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(map(bonifico, context, tipoIntermediario))")
    Party44Choice mapWithIntermediario(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context, TipoIntermediario tipoIntermediario);

    default BranchAndFinancialInstitutionIdentification6 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context, TipoIntermediario tipoIntermediario) {
        return BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(bonifico, tipoIntermediario, context, false);
    }

    abstract class Decorator implements Party44ChoiceMapper {
        private final Party44ChoiceMapper delegate;

        Decorator(Party44ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Party44Choice map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }

        @Override
        public Party44Choice map(BanksConfig.BankConfig bankConfig, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bankConfig, context));
        }

        @Override
        public Party44Choice mapWithIntermediario(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context, TipoIntermediario tipoIntermediario) {
            return Utils.allFieldsEmpty(delegate.mapWithIntermediario(bonifico, context, tipoIntermediario));
        }
    }
}
