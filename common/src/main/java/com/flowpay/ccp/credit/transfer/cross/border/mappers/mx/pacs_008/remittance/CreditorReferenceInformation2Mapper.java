package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.prowidesoftware.swift.model.mx.dic.CreditorReferenceInformation2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(CreditorReferenceInformation2Mapper.Decorator.class)
public interface CreditorReferenceInformation2Mapper {

    @Mapping(target = "tp.cdOrPrtry.cd", source = "tipoRiferimentoCreditore")
    @Mapping(target = "tp.cdOrPrtry.prtry", source = "descrizioneRiferimentoCreditore")
    @Mapping(target = "tp.issr", source = "emittenteRiferimentoCreditore")
    @Mapping(target = "ref", source = "riferimentoUnivocoCreditore")
    CreditorReferenceInformation2 map(InformazioniCausale info);

    abstract class Decorator implements CreditorReferenceInformation2Mapper {

        private final CreditorReferenceInformation2Mapper delegate;

        Decorator(CreditorReferenceInformation2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CreditorReferenceInformation2 map(InformazioniCausale info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
