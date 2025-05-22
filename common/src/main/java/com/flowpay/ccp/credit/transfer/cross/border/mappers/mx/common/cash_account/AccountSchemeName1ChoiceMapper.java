package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;


import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.AccountSchemeName1Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(AccountSchemeName1ChoiceMapper.Decorator.class)
public interface AccountSchemeName1ChoiceMapper {

    @Mapping(target = "cd", source = "codiceIdentificativoConto")
    @Mapping(target = "prtry", source = "descrizioneIdentificativoConto")
    AccountSchemeName1Choice map(InformazioniRapporto rapporto);

    abstract class Decorator implements AccountSchemeName1ChoiceMapper {

        private final AccountSchemeName1ChoiceMapper delegate;

        Decorator(AccountSchemeName1ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public AccountSchemeName1Choice map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
