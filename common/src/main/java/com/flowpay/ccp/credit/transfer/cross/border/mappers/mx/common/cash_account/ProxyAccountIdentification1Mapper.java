package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.prowidesoftware.swift.model.mx.dic.ProxyAccountIdentification1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = ProxyAccountType1ChoiceMapper.class)
@DecoratedWith(ProxyAccountIdentification1Mapper.Decorator.class)
public interface ProxyAccountIdentification1Mapper {


    @Mapping(target = "tp", source = ".")
    @Mapping(target = "id", source = "dettaglioIdentificativoAlias")
    ProxyAccountIdentification1 map(InformazioniRapporto rapporto);

    abstract class Decorator implements ProxyAccountIdentification1Mapper {

        private final ProxyAccountIdentification1Mapper delegate;

        Decorator(ProxyAccountIdentification1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ProxyAccountIdentification1 map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
