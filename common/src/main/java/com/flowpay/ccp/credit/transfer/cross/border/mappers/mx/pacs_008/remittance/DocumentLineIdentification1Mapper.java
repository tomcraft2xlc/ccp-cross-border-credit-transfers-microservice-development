package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.IdentificativoLineaDocumento;
import com.prowidesoftware.swift.model.mx.dic.DocumentLineIdentification1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = DocumentLineType1Mapper.class)
@DecoratedWith(DocumentLineIdentification1Mapper.Decorator.class)
public interface DocumentLineIdentification1Mapper {

    @Mapping(target = "tp", source = ".")
    @Mapping(target = "nb", source = "entity.numero")
    @Mapping(target = "rltdDt", source = "entity.data")
    DocumentLineIdentification1 map(IdentificativoLineaDocumento.WithLinkedEntities info);

    abstract class Decorator implements DocumentLineIdentification1Mapper {

        private final DocumentLineIdentification1Mapper delegate;

        Decorator(DocumentLineIdentification1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DocumentLineIdentification1 map(IdentificativoLineaDocumento.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
