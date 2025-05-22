package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.address.NameAndAddress16Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RiferimentiAggiuntiviPagamento;
import com.prowidesoftware.swift.model.mx.dic.RemittanceLocationData1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        imports = NameAndAddress16Mapper.class)
@DecoratedWith(RemittanceLocationData1Mapper.Decorator.class)
public interface RemittanceLocationData1Mapper {

    @Mapping(target = "mtd", source = "entity.modalitaAvvisoPagamento")
    @Mapping(target = "elctrncAdr", source = "entity.emailDestinatarioReporting")
    @Mapping(target = "pstlAdr", expression = "java(NameAndAddress16Mapper.INSTANCE.map(riferimenti.getEntity().intestazioneDestinatarioReporting(), riferimenti.indirizzoPostale.getEntity()))")
    RemittanceLocationData1 map(RiferimentiAggiuntiviPagamento.WithLinkedEntities riferimenti);

    abstract class Decorator implements RemittanceLocationData1Mapper {

        private final RemittanceLocationData1Mapper delegate;

        Decorator(RemittanceLocationData1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RemittanceLocationData1 map(RiferimentiAggiuntiviPagamento.WithLinkedEntities riferimenti) {
            return Utils.allFieldsEmpty(delegate.map(riferimenti));
        }
    }
}
