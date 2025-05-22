package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.AttoreFiscale;
import com.prowidesoftware.swift.model.mx.dic.TaxParty2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(TaxParty2Mapper.Decorator.class)
public interface TaxParty2Mapper {

    TaxParty2Mapper INSTANCE = Mappers.getMapper(TaxParty2Mapper.class);

    @Mapping(target = "taxId", source = "entity.identificativoFiscale")
    @Mapping(target = "regnId", source = "entity.identificativo")
    @Mapping(target = "taxTp", source = "entity.tipoContribuente")
    @Mapping(target = "authstn.titl", source = "entity.titolo")
    @Mapping(target = "authstn.nm", source = "entity.intestazione")
    TaxParty2 map(AttoreFiscale.WithLinkedEntities attore);

    abstract class Decorator implements TaxParty2Mapper {

        private final TaxParty2Mapper delegate;

        Decorator(TaxParty2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxParty2 map(AttoreFiscale.WithLinkedEntities attore) {
            return Utils.allFieldsEmpty(delegate.map(attore));
        }
    }
}
