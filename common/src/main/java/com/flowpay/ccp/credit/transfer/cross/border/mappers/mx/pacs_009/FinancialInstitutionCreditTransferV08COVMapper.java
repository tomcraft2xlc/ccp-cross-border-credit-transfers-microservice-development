package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header.GroupHeader93COVMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer.CreditTransferTransaction36COVMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionCreditTransferV08;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        GroupHeader93COVMapper.class,
        CreditTransferTransaction36COVMapper.class
})
@DecoratedWith(FinancialInstitutionCreditTransferV08COVMapper.Decorator.class)
public interface FinancialInstitutionCreditTransferV08COVMapper {

    @Mapping(target = "grpHdr", source = ".")
    @Mapping(target = "cdtTrfTxInf", source = ".")
    @Mapping(target = "splmtryData", ignore = true)
    FinancialInstitutionCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    abstract class Decorator implements FinancialInstitutionCreditTransferV08COVMapper {

        private final FinancialInstitutionCreditTransferV08COVMapper delegate;

        Decorator(FinancialInstitutionCreditTransferV08COVMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public FinancialInstitutionCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
