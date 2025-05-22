package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction.CreditTransferTransaction39Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header.GroupHeader93Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.FIToFICustomerCreditTransferV08;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class,
        uses = {
        GroupHeader93Mapper.class,
        CreditTransferTransaction39Mapper.class
})
@DecoratedWith(FIToFICustomerCreditTransferV08Mapper.Decorator.class)
public interface FIToFICustomerCreditTransferV08Mapper {


    @Mapping(target = "grpHdr", source = ".")
    @Mapping(target = "cdtTrfTxInf", source = ".")
    @Mapping(target = "splmtryData", ignore = true)
    FIToFICustomerCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    abstract class Decorator implements FIToFICustomerCreditTransferV08Mapper {

        private final FIToFICustomerCreditTransferV08Mapper delegate;

        Decorator(FIToFICustomerCreditTransferV08Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public FIToFICustomerCreditTransferV08 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
