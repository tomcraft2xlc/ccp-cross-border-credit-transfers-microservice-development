package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.RecordDettagliFiscali;
import com.prowidesoftware.swift.model.mx.dic.TaxRecord2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        TaxPeriod2Mapper.class,
        TaxAmount2Mapper.class
})
@DecoratedWith(TaxRecord2Mapper.Decorator.class)
public interface TaxRecord2Mapper {

    @Mapping(target = "tp", source = "entity.codiceTipo")
    @Mapping(target = "ctgy", source = "entity.categoriaTassazione")
    @Mapping(target = "ctgyDtls", source = "entity.dettagliCategoriaTassazione")
    @Mapping(target = "dbtrSts", source = "entity.statusContribuenteDebitore")
    @Mapping(target = "certId", source = "entity.identificativoDichiarazione")
    @Mapping(target = "frmsCd", source = "entity.codiceModelloDichiarazione")
    @Mapping(target = "prd", source = ".")
    @Mapping(target = "taxAmt", source = ".")
    @Mapping(target = "addtlInf", source = "entity.informazioniAggiuntive")
    TaxRecord2 map(RecordDettagliFiscali.WithLinkedEntities dettagli);

    abstract class Decorator implements TaxRecord2Mapper {

        private final TaxRecord2Mapper delegate;

        Decorator(TaxRecord2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxRecord2 map(RecordDettagliFiscali.WithLinkedEntities dettagli) {
            return Utils.allFieldsEmpty(delegate.map(dettagli));
        }
    }
}
