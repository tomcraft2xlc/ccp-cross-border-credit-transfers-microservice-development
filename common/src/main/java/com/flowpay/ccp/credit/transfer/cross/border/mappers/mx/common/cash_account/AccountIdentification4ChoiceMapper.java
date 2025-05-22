package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.AccountIdentification4Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = GenericAccountIdentification1Mapper.class)
@DecoratedWith(AccountIdentification4ChoiceMapper.Decorator.class)
public interface AccountIdentification4ChoiceMapper {

    @Mapping(target = "IBAN", source = "iban")
    @Mapping(target = "othr", source = ".")
    AccountIdentification4Choice map(InformazioniRapporto rapporto);

    abstract class Decorator implements AccountIdentification4ChoiceMapper {

        private final AccountIdentification4ChoiceMapper delegate;

        Decorator(AccountIdentification4ChoiceMapper mapper) {
            this.delegate = mapper;
        }

        @Override
        public AccountIdentification4Choice map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
