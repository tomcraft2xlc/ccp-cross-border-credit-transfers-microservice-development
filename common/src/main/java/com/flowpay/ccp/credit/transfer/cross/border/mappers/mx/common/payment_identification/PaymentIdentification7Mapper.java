package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.payment_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.PaymentIdentification7;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(PaymentIdentification7Mapper.Decorator.class)
public interface PaymentIdentification7Mapper {


    @Mapping(target = "instrId", source = "tid")
    @Mapping(target = "endToEndId", source = "tid")
    @Mapping(target = "UETR", source = "id")
    PaymentIdentification7 map(BonificoExtraSepa bonifico);

    abstract class Decorator implements PaymentIdentification7Mapper {
        private final PaymentIdentification7Mapper delegate;

        Decorator(PaymentIdentification7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PaymentIdentification7 map(BonificoExtraSepa bonifico) {
            return Utils.allFieldsEmpty(delegate.map(bonifico));
        }
    }
}
