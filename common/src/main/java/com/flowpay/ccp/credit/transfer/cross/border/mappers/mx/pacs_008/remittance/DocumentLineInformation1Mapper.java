package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioLineaDocumentoDiRiferimento;
import com.prowidesoftware.swift.model.mx.dic.DocumentLineInformation1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        DocumentLineIdentification1Mapper.class,
        RemittanceAmount3Mapper.class
})
@DecoratedWith(DocumentLineInformation1Mapper.Decorator.class)
public interface DocumentLineInformation1Mapper {


    @Mapping(target = "id", source = "identificativiLinea")
    @Mapping(target = "desc", source = "entity.descrizioneVoce")
    @Mapping(target = "amt", source = "dettaglioLineaDocumentoDiRiferimentoDettaglioImporti")
    DocumentLineInformation1 map(DettaglioLineaDocumentoDiRiferimento.WithLinkedEntities dettaglio);

    abstract class Decorator implements DocumentLineInformation1Mapper {

        private final DocumentLineInformation1Mapper delegate;

        Decorator(DocumentLineInformation1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DocumentLineInformation1 map(DettaglioLineaDocumentoDiRiferimento.WithLinkedEntities dettaglio) {
            return Utils.allFieldsEmpty(delegate.map(dettaglio));
        }
    }
}
