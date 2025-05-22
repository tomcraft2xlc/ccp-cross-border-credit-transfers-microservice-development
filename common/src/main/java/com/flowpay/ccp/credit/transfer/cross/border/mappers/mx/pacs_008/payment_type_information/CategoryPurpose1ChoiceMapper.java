package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.CategoryPurpose1Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(CategoryPurpose1ChoiceMapper.Decorator.class)
public interface CategoryPurpose1ChoiceMapper {

    @Mapping(target = "cd", source = "codiceTransazione")
    @Mapping(target = "prtry", source = "dettaglioIdentificativoTransazione")
    CategoryPurpose1Choice map(InformazioniAggiuntivePagamento informazioni);

    abstract class Decorator implements CategoryPurpose1ChoiceMapper {

        private final CategoryPurpose1ChoiceMapper delegate;

        Decorator(CategoryPurpose1ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CategoryPurpose1Choice map(InformazioniAggiuntivePagamento informazioni) {
            return Utils.allFieldsEmpty(delegate.map(informazioni));
        }
    }
}
