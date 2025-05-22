package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class,
        uses = FIToFICustomerCreditTransferV08Mapper.class)
@DecoratedWith(MxPacs00800108Mapper.Decorator.class)
public interface MxPacs00800108Mapper extends MxMapper {

    @Override
    @Mapping(target = "FIToFICstmrCdtTrf", source = ".")
    @Mapping(target = "appHdr", ignore = true)
    MxPacs00800108 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    abstract class Decorator implements MxPacs00800108Mapper {

        private final MxPacs00800108Mapper delegate;

        Decorator(MxPacs00800108Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public MxPacs00800108 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
