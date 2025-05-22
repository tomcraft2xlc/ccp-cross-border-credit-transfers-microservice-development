package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.TipoAttoreFiscale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliFiscali;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.TaxInformation7;
import com.prowidesoftware.swift.model.mx.dic.TaxParty1;
import com.prowidesoftware.swift.model.mx.dic.TaxParty2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        config = MxMappingConfig.class,
        uses = TaxRecord2Mapper.class,
        imports = TipoAttoreFiscale.class
)
@DecoratedWith(TaxInformation7Mapper.Decorator.class)
public interface TaxInformation7Mapper {

    @Mapping(target = "cdtr", expression = "java(cdtr(info))")
    @Mapping(target = "dbtr", expression = "java(attore(info, TipoAttoreFiscale.DEBITORE))")
    @Mapping(target = "ultmtDbtr", expression = "java(attore(info, TipoAttoreFiscale.DEBITORE_FINALE))")
    @Mapping(target = "admstnZone", source = "entity.amministratoreDiRiferimento")
    @Mapping(target = "refNb", source = "entity.dettaglioImpostaRiferimento")
    @Mapping(target = "mtd", source = "entity.metodo")
    @Mapping(target = "ttlTaxblBaseAmt", expression = "java(amt(info.getEntity().importoImponibile(), info.getEntity().divisaImportoImponibile()))")
    @Mapping(target = "ttlTaxAmt", expression = "java(amt(info.getEntity().importoImposta(), info.getEntity().divisaImportoImposta()))")
    @Mapping(target = "dt", source = "entity.scadenza")
    @Mapping(target = "seqNb", source = "entity.numeroProgressivoDichiarazione")
    @Mapping(target = "rcrd", source = "recordsDettagliFiscali")
    TaxInformation7 map(DettagliFiscali.WithLinkedEntities info);

    default TaxParty1 cdtr(DettagliFiscali.WithLinkedEntities info) {
        return TaxParty1Mapper.INSTANCE.map(
                info.attoriFiscali.stream().filter(a -> a.getEntity().tipoAttoreFiscale() == TipoAttoreFiscale.CREDITORE)
                        .findFirst()
                        .orElse(null)
        );
    }

    default TaxParty2 attore(DettagliFiscali.WithLinkedEntities info, TipoAttoreFiscale tipo) {
        return TaxParty2Mapper.INSTANCE.map(
                info.attoriFiscali.stream().filter(a -> a.getEntity().tipoAttoreFiscale() == tipo)
                        .findFirst()
                        .orElse(null)
        );
    }

    default ActiveOrHistoricCurrencyAndAmount amt(BigDecimal amt, String ccy) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(amt, ccy);
    }

    abstract class Decorator implements TaxInformation7Mapper {

        private final TaxInformation7Mapper delegate;

        Decorator(TaxInformation7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxInformation7 map(DettagliFiscali.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}

