package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.IdentificativoLineaDocumento;
import com.prowidesoftware.swift.model.mx.dic.DocumentLineType1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(DocumentLineType1Mapper.Decorator.class)
public interface DocumentLineType1Mapper {

    @Mapping(target = "cdOrPrtry.cd", source = "entity.codiceVoce")
    @Mapping(target = "cdOrPrtry.prtry", source = "entity.codiceProprietarioVoce")
    @Mapping(target = "issr", source = "entity.emittente")
    DocumentLineType1 map(IdentificativoLineaDocumento.WithLinkedEntities info);

    abstract class Decorator implements DocumentLineType1Mapper {

        private final DocumentLineType1Mapper delegate;

        Decorator(DocumentLineType1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DocumentLineType1 map(IdentificativoLineaDocumento.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
