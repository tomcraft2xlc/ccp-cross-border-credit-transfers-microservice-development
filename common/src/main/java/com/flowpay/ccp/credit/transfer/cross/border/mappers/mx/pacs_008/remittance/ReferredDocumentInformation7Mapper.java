package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.InformazioniDocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.ReferredDocumentInformation7;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
                ReferredDocumentType4Mapper.class,
                DocumentLineInformation1Mapper.class
        }
)
@DecoratedWith(ReferredDocumentInformation7Mapper.Decorator.class)
public interface ReferredDocumentInformation7Mapper {

    @Mapping(target = "tp", source = ".")
    @Mapping(target = "nb", source = "entity.numero")
    @Mapping(target = "rltdDt", source = "entity.data")
    @Mapping(target = "lineDtls", source = "dettagliLineeDocumentoDiRiferimento")
    ReferredDocumentInformation7 map(InformazioniDocumentoDiRiferimento.WithLinkedEntities info);

    abstract class Decorator implements ReferredDocumentInformation7Mapper {

        private final ReferredDocumentInformation7Mapper delegate;

        Decorator(ReferredDocumentInformation7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ReferredDocumentInformation7 map(InformazioniDocumentoDiRiferimento.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
