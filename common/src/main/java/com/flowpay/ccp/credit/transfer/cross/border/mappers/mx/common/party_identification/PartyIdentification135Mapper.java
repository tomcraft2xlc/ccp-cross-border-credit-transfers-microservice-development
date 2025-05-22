package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.address.PostalAddress24Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.prowidesoftware.swift.model.mx.dic.PartyIdentification135;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        PostalAddress24Mapper.class,
        Party38ChoiceMapper.class
})
@DecoratedWith(PartyIdentification135Mapper.Decorator.class)
public interface PartyIdentification135Mapper {

    PartyIdentification135Mapper INSTANCE = Mappers.getMapper(PartyIdentification135Mapper.class);

    @Mapping(target = "nm", source = "entity.intestazione")
    @Mapping(target = "pstlAdr", source = "indirizzoPostale.entity")
    @Mapping(target = "id", source = ".")
    @Mapping(target = "ctryOfRes", source = "entity.paeseDiResidenza")
    @Mapping(target = "ctctDtls", ignore = true)
    PartyIdentification135 map(InformazioniAttore.WithLinkedEntities attore);

    default PartyIdentification135 map(BonificoExtraSepa.WithLinkedEntities bonifico, TipoAttore tipoAttore) {
        return map(
                bonifico.informazioniAttori.stream()
                .filter(attore -> attore.getEntity().tipo() == tipoAttore)
                .findFirst().orElse(null)
        );
    }

    abstract class Decorator implements PartyIdentification135Mapper {

        private final PartyIdentification135Mapper delegate;

        Decorator(PartyIdentification135Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PartyIdentification135 map(InformazioniAttore.WithLinkedEntities attore) {
            return Utils.allFieldsEmpty(delegate.map(attore));
        }
    }
}
