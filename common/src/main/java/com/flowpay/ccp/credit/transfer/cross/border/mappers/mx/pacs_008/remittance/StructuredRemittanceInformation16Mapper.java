package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification.PartyIdentification135Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.google.common.base.Splitter;
import com.prowidesoftware.swift.model.mx.dic.StructuredRemittanceInformation16;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        ReferredDocumentInformation7Mapper.class,
        RemittanceAmount2Mapper.class,
        CreditorReferenceInformation2Mapper.class,
        PartyIdentification135Mapper.class,
        TaxInformation7Mapper.class,
        Garnishment3Mapper.class
})
@DecoratedWith(StructuredRemittanceInformation16Mapper.Decorator.class)
public interface StructuredRemittanceInformation16Mapper {

    StructuredRemittanceInformation16Mapper INSTANCE = Mappers.getMapper(StructuredRemittanceInformation16Mapper.class);

    @Mapping(target = "rfrdDocInf", source = "informazioniDocumentiDiRiferimento")
    @Mapping(target = "rfrdDocAmt", source = "informazioniCausaleDettaglioImporti")
    @Mapping(target = "cdtrRefInf", source = "entity")
    @Mapping(target = "invcr", source = "attoreEmittenteDocumento")
    @Mapping(target = "invcee", source = "attoreRiceventeDocumento")
    @Mapping(target = "taxRmt", source = "dettagliFiscali")
    @Mapping(target = "grnshmtRmt", source = "dettagliPignoramento")
    @Mapping(target = "addtlRmtInf", source = "entity.ulterioriInformazioni")
    StructuredRemittanceInformation16 map(InformazioniCausale.WithLinkedEntities info);

    default List<String> format(String info) {
        return Splitter.fixedLength(130).splitToList(info);
    }

    abstract class Decorator implements StructuredRemittanceInformation16Mapper {

        private final StructuredRemittanceInformation16Mapper delegate;

        Decorator(StructuredRemittanceInformation16Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public StructuredRemittanceInformation16 map(InformazioniCausale.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
