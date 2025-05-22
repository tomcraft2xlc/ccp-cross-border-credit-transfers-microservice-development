package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification.PartyIdentification135Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.DettagliPignoramento;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.Garnishment3;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        config = MxMappingConfig.class,
        uses = PartyIdentification135Mapper.class)
@DecoratedWith(Garnishment3Mapper.Decorator.class)
public interface Garnishment3Mapper {

    @Mapping(target = "tp.cdOrPrtry.cd", source = "entity.codice")
    @Mapping(target = "tp.cdOrPrtry.prtry", source = "entity.codiceProprietario")
    @Mapping(target = "tp.issr", source = "entity.emittente")
    @Mapping(target = "grnshee", source = "terzoPignorato")
    @Mapping(target = "grnshmtAdmstr", source = "gestorePignoramento")
    @Mapping(target = "refNb", source = "entity.identificativoPignoramento")
    @Mapping(target = "dt", source = "entity.dataPignoramento")
    @Mapping(target = "rmtdAmt", expression = "java(amt(dettagli.getEntity().importoPignoramento(), dettagli.getEntity().divisaImportoPignoramento()))")
    @Mapping(target = "fmlyMdclInsrncInd", source = "entity.assicurazioneSanitaria")
    @Mapping(target = "mplyeeTermntnInd", source = "entity.disoccupato")
    Garnishment3 map(DettagliPignoramento.WithLinkedEntities dettagli);

    default ActiveOrHistoricCurrencyAndAmount amt(BigDecimal amt, String ccy) {
        return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(amt, ccy);
    }

    abstract class Decorator implements Garnishment3Mapper {

        private final Garnishment3Mapper delegate;

        Decorator(Garnishment3Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Garnishment3 map(DettagliPignoramento.WithLinkedEntities dettagli) {
            return Utils.allFieldsEmpty(delegate.map(dettagli));
        }
    }
}
