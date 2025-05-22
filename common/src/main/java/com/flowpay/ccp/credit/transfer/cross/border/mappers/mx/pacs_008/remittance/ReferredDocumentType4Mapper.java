package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.InformazioniDocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.ReferredDocumentType4;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(ReferredDocumentType4Mapper.Decorator.class)
public interface ReferredDocumentType4Mapper {

    @Mapping(target = "cdOrPrtry.cd", source = "entity.tipo")
    @Mapping(target = "cdOrPrtry.prtry", source = "entity.descrizione")
    @Mapping(target = "issr", source = "entity.emittente")
    ReferredDocumentType4 map(InformazioniDocumentoDiRiferimento.WithLinkedEntities info);

    abstract class Decorator implements ReferredDocumentType4Mapper {

        private final ReferredDocumentType4Mapper delegate;

        Decorator(ReferredDocumentType4Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ReferredDocumentType4 map(InformazioniDocumentoDiRiferimento.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
