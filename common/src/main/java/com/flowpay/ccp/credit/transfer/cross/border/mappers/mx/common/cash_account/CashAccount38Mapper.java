package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.cash_account;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapportoBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.TipoInformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.TipoAttore;
import com.prowidesoftware.swift.model.mx.dic.CashAccount38;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Objects;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        AccountIdentification4ChoiceMapper.class,
        ProxyAccountIdentification1Mapper.class,
        CashAccountType2ChoiceMapper.class
})
@DecoratedWith(CashAccount38Mapper.Decorator.class)
public interface CashAccount38Mapper {

    CashAccount38Mapper INSTANCE = Mappers.getMapper(CashAccount38Mapper.class);

    @Mapping(target = "id", source = ".")
    @Mapping(target = "tp", source = ".")
    @Mapping(target = "ccy", source = "divisa")
    @Mapping(target = "nm", source = "intestazioneConto")
    @Mapping(target = "prxy", source = ".")
    CashAccount38 map(InformazioniRapporto rapporto);


    default CashAccount38 map(Collection<InformazioniAttore.WithLinkedEntities> attori, TipoAttore tipoAttore) {
        return CashAccount38Mapper.INSTANCE.map(
                attori.stream().filter(attore -> attore.getEntity().tipo() == tipoAttore)
                        .findFirst().map(attore -> attore.informazioniRapporto).map(InformazioniRapporto.WithLinkedEntities::getEntity).orElse(null)
        );
    }

    default CashAccount38 map(Collection<InformazioniIntermediario.WithLinkedEntities> attori, TipoIntermediario tipoIntermediario) {
        return map(attori, tipoIntermediario, false);
    }

    default CashAccount38 map(Collection<InformazioniIntermediario.WithLinkedEntities> attori, TipoIntermediario tipoIntermediario, Boolean documentoCollegato) {
        return CashAccount38Mapper.INSTANCE.map(
                attori.stream().filter(intermediario -> intermediario.getEntity().tipoIntermediario() == tipoIntermediario && Objects.equals(intermediario.getEntity().intermediarioDocumentoCollegato(), documentoCollegato))
                        .findFirst().map(intermediario -> intermediario.informazioniRapporto).map(InformazioniRapporto.WithLinkedEntities::getEntity).orElse(null)
        );
    }

    default CashAccount38 map(Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> rapporti, TipoInformazioniRapporto tipoInformazioniRapporto) {
        return CashAccount38Mapper.INSTANCE.map(
                rapporti.stream().filter(rapporto -> rapporto.getEntity().tipoInformazioniRapporto() == tipoInformazioniRapporto)
                        .findFirst().map(rapporto -> rapporto.informazioniRapporto).map(InformazioniRapporto.WithLinkedEntities::getEntity).orElse(null)
        );
    }

    abstract class Decorator implements CashAccount38Mapper {
        private final CashAccount38Mapper delegate;

        Decorator(CashAccount38Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public CashAccount38 map(InformazioniRapporto rapporto) {
            return Utils.allFieldsEmpty(delegate.map(rapporto));
        }
    }
}
