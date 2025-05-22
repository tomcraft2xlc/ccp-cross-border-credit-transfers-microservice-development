package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.ProxyAccountType1Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(ProxyAccountType1ChoiceMapper.Decorator.class)
public interface ProxyAccountType1ChoiceMapper {

    @Mapping(target = "cd", source = "codiceTipoAlias")
    @Mapping(target = "prtry", source = "descrizioneAlias")
    ProxyAccountType1Choice map(InformazioniRapporto rapporto);


    abstract class Decorator implements ProxyAccountType1ChoiceMapper {

        private final ProxyAccountType1ChoiceMapper delegate;

        Decorator(ProxyAccountType1ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ProxyAccountType1Choice map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
