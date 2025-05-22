package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.prowidesoftware.swift.model.mx.dic.RemittanceInformation2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Objects;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(RemittanceInformation2Mapper.Decorator.class)
public interface RemittanceInformation2Mapper {

    default RemittanceInformation2 map(Collection<InformazioniCausale.WithLinkedEntities> infos) {
        var unstrucuted = infos.stream().map(InformazioniCausale.WithLinkedEntities::getEntity)
                .map(InformazioniCausale::causaleDescrittiva).filter(Objects::nonNull).toList();

        var result = new RemittanceInformation2();

        result.getUstrd().addAll(unstrucuted);

        return result;
    }

    abstract class Decorator implements RemittanceInformation2Mapper {

        private final RemittanceInformation2Mapper delegate;

        Decorator(RemittanceInformation2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RemittanceInformation2 map(Collection<InformazioniCausale.WithLinkedEntities> infos) {
            return Utils.allFieldsEmpty(delegate.map(infos));
        }
    }
}
