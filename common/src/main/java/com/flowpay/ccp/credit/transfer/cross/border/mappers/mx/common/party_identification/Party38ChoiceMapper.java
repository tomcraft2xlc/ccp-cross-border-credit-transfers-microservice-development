package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.prowidesoftware.swift.model.mx.dic.Party38Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        OrganisationIdentification29Mapper.class,
        PersonIdentification13Mapper.class
})
@DecoratedWith(Party38ChoiceMapper.Decorator.class)
public interface Party38ChoiceMapper {

    @Mapping(target = "orgId", source = "organizzazione.entity")
    @Mapping(target = "prvtId", source = "privato.entity")
    Party38Choice map(InformazioniAttore.WithLinkedEntities attore);

    abstract class Decorator implements Party38ChoiceMapper {

        private final Party38ChoiceMapper delegate;

        Decorator(Party38ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Party38Choice map(InformazioniAttore.WithLinkedEntities attore) {
            return Utils.allFieldsEmpty(delegate.map(attore));
        }
    }
}
