package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification5Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification5;
import com.prowidesoftware.swift.model.mx.dic.Party9Choice;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        imports = {
        BranchAndFinancialInstitutionIdentification5Mapper.class,
        TipoIntermediario.class
})
@DecoratedWith(Party9ChoiceMapper.Decorator.class)
public interface Party9ChoiceMapper {

    Party9ChoiceMapper INSTANCE = Mappers.getMapper(Party9ChoiceMapper.class);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(BranchAndFinancialInstitutionIdentification5Mapper.INSTANCE.map(bankConfig, context, false))")
    Party9Choice map(BanksConfig.BankConfig bankConfig, @Context MappingContext context);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(BranchAndFinancialInstitutionIdentification5Mapper.INSTANCE.map(bonifico, TipoIntermediario.BANCA_DESTINATARIA, context, false))")
    Party9Choice map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "FIId", expression = "java(map(bonifico, context, tipoIntermediario))")
    Party9Choice mapWithIntermediario(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context, TipoIntermediario tipoIntermediario);

    default BranchAndFinancialInstitutionIdentification5 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context, TipoIntermediario tipoIntermediario) {
        return BranchAndFinancialInstitutionIdentification5Mapper.INSTANCE.map(bonifico, tipoIntermediario, context, false);
    }

    abstract class Decorator implements Party9ChoiceMapper {
        private final Party9ChoiceMapper delegate;

        Decorator(Party9ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Party9Choice map(BanksConfig.BankConfig bankConfig, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bankConfig, context));
        }

        @Override
        public Party9Choice map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }

        @Override
        public Party9Choice mapWithIntermediario(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context, TipoIntermediario tipoIntermediario) {
            return Utils.allFieldsEmpty(delegate.mapWithIntermediario(bonifico, context, tipoIntermediario));
        }
    }
}
