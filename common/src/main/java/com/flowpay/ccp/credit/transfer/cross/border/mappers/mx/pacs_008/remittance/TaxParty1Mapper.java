package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;


import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.AttoreFiscale;
import com.prowidesoftware.swift.model.mx.dic.TaxParty1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(TaxParty1Mapper.Decorator.class)
public interface TaxParty1Mapper {

    TaxParty1Mapper INSTANCE = Mappers.getMapper(TaxParty1Mapper.class);

    @Mapping(target = "taxId", source = "entity.identificativoFiscale")
    @Mapping(target = "regnId", source = "entity.identificativo")
    @Mapping(target = "taxTp", source = "entity.tipoContribuente")
    TaxParty1 map(AttoreFiscale.WithLinkedEntities attore);

    abstract class Decorator implements TaxParty1Mapper {

        private final TaxParty1Mapper delegate;

        Decorator(TaxParty1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxParty1 map(AttoreFiscale.WithLinkedEntities attore) {
            return Utils.allFieldsEmpty(delegate.map(attore));
        }
    }
}
