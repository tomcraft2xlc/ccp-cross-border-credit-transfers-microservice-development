package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.CashAccountType2Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class
)
@DecoratedWith(CashAccountType2ChoiceMapper.Decorator.class)
public interface CashAccountType2ChoiceMapper {

    @Mapping(target = "cd", source = "codiceTipoConto")
    @Mapping(target = "prtry", source = "dettaglioTipoConto")
    CashAccountType2Choice map(InformazioniRapporto rapporto);

    abstract class Decorator implements CashAccountType2ChoiceMapper {

        private final CashAccountType2ChoiceMapper delegate;

        Decorator(CashAccountType2ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CashAccountType2Choice map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
