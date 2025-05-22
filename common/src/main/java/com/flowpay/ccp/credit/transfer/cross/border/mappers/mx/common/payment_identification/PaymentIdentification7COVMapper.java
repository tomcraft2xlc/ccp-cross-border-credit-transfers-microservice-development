package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.payment_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.PaymentIdentification7;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(PaymentIdentification7COVMapper.Decorator.class)
public interface PaymentIdentification7COVMapper {

    @Mapping(target = "instrId", source = "tidDocumentoCollegato")
    @Mapping(target = "endToEndId", source = "tid")
    @Mapping(target = "txId", source = "tidDocumentoCollegato")
    @Mapping(target = "UETR", source = "id")
    PaymentIdentification7 map(BonificoExtraSepa bonifico);

    abstract class Decorator implements PaymentIdentification7COVMapper {
        private final PaymentIdentification7COVMapper delegate;

        Decorator(PaymentIdentification7COVMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PaymentIdentification7 map(BonificoExtraSepa bonifico) {
            return Utils.allFieldsEmpty(delegate.map(bonifico));
        }
    }
}
