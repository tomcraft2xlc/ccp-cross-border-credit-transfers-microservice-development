package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;


import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.GenericAccountIdentification1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
uses = AccountSchemeName1ChoiceMapper.class)
@DecoratedWith(GenericAccountIdentification1Mapper.Decorator.class)
public interface GenericAccountIdentification1Mapper {

    @Mapping(target = "id", source = "altroID")
    @Mapping(target = "schmeNm", source = ".")
    @Mapping(target = "issr", source = "emittente")
    GenericAccountIdentification1 map(InformazioniRapporto rapporto);

    abstract class Decorator implements GenericAccountIdentification1Mapper {

        private final GenericAccountIdentification1Mapper delegate;

        Decorator(GenericAccountIdentification1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public GenericAccountIdentification1 map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
