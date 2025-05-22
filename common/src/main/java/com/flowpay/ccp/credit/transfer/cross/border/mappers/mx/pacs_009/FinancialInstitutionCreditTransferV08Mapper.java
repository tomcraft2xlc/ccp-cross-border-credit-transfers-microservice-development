package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header.GroupHeader93Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer.CreditTransferTransaction36Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionCreditTransferV08;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        GroupHeader93Mapper.class,
        CreditTransferTransaction36Mapper.class
})
@DecoratedWith(FinancialInstitutionCreditTransferV08Mapper.Decorator.class)
public interface FinancialInstitutionCreditTransferV08Mapper {

    @Mapping(target = "grpHdr", source = ".")
    @Mapping(target = "cdtTrfTxInf", source = ".")
    @Mapping(target = "splmtryData", ignore = true)
    FinancialInstitutionCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    abstract class Decorator implements FinancialInstitutionCreditTransferV08Mapper {

        private final FinancialInstitutionCreditTransferV08Mapper delegate;

        Decorator(FinancialInstitutionCreditTransferV08Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public FinancialInstitutionCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
