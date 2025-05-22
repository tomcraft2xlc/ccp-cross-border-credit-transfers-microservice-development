package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header.GroupHeader93COVMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.Purpose2Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(Purpose2ChoiceMapper.Decorator.class)
public interface Purpose2ChoiceMapper {

    @Mapping(target = "cd", source = "classificazionePagamento")
    @Mapping(target = "prtry", source = "dettaglioClassificazionePagamento")
    Purpose2Choice map(InformazioniAggiuntivePagamento info);

    abstract class Decorator implements Purpose2ChoiceMapper {

        private final Purpose2ChoiceMapper delegate;

        Decorator(Purpose2ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Purpose2Choice map(InformazioniAggiuntivePagamento info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
