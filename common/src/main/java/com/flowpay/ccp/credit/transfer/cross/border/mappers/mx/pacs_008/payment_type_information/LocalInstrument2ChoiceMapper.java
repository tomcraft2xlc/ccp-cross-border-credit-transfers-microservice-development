package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.LocalInstrument2Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(LocalInstrument2ChoiceMapper.Decorator.class)
public interface LocalInstrument2ChoiceMapper {

    @Mapping(target = "cd", source = "codiceTipoServizio")
    @Mapping(target = "prtry", source = "dettaglioTipoServizio")
    LocalInstrument2Choice map(InformazioniAggiuntivePagamento informazioni);

    abstract class Decorator implements LocalInstrument2ChoiceMapper {

        private final LocalInstrument2ChoiceMapper delegate;

        Decorator(LocalInstrument2ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public LocalInstrument2Choice map(InformazioniAggiuntivePagamento informazioni) {
            return Utils.allFieldsEmpty(delegate.map(informazioni));
        }
    }
}
