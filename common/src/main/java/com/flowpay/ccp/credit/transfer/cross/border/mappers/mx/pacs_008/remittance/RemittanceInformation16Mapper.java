package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.prowidesoftware.swift.model.mx.dic.RemittanceInformation16;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Objects;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(RemittanceInformation16Mapper.Decorator.class)
public interface RemittanceInformation16Mapper {


    default RemittanceInformation16 map(Collection<InformazioniCausale.WithLinkedEntities> infos) {
        var unstrucuted = infos.stream().map(InformazioniCausale.WithLinkedEntities::getEntity)
        .map(InformazioniCausale::causaleDescrittiva).filter(Objects::nonNull).toList();
        var structured = infos.stream()
                .filter(info -> info.getEntity().causaleDescrittiva() == null)
                .map(StructuredRemittanceInformation16Mapper.INSTANCE::map)
                .toList();

        var result = new RemittanceInformation16();

        result.getUstrd().addAll(unstrucuted);
        result.getStrd().addAll(structured);

        return result;
    }

    abstract class Decorator implements RemittanceInformation16Mapper {

        private final RemittanceInformation16Mapper delegate;

        Decorator(RemittanceInformation16Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RemittanceInformation16 map(Collection<InformazioniCausale.WithLinkedEntities> infos) {
            return Utils.allFieldsEmpty(delegate.map(infos));
        }
    }
}
