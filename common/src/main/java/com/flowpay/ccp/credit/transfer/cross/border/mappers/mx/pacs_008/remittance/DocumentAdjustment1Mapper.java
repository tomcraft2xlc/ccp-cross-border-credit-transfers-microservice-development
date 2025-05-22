package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioImporto;
import com.prowidesoftware.swift.model.mx.dic.CreditDebitCode;
import com.prowidesoftware.swift.model.mx.dic.DocumentAdjustment1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        imports = ActiveOrHistoricCurrencyAndAmountMapper.class
)
@DecoratedWith(DocumentAdjustment1Mapper.Decorator.class)
public interface DocumentAdjustment1Mapper {

    DocumentAdjustment1Mapper INSTANCE = Mappers.getMapper(DocumentAdjustment1Mapper.class);

    @Mapping(target = "amt", expression = "java(ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(importo.importo(), importo.divisa()))")
    @Mapping(target = "cdtDbtInd", expression = "java(mapCreditDebitCode(importo))")
    @Mapping(target = "rsn", source = "motivo")
    @Mapping(target = "addtlInf", source = "informazioniAggiuntive")
    DocumentAdjustment1 map(DettaglioImporto importo);

    default CreditDebitCode mapCreditDebitCode(DettaglioImporto importo) {
        if (importo.verso() != null) {
            return importo.verso().asCreditDebitCode();
        }
        return null;
    }

    abstract class Decorator implements DocumentAdjustment1Mapper {

        private final DocumentAdjustment1Mapper delegate;

        Decorator(DocumentAdjustment1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public DocumentAdjustment1 map(DettaglioImporto importo) {
            return Utils.allFieldsEmpty(delegate.map(importo));
        }
    }
}
