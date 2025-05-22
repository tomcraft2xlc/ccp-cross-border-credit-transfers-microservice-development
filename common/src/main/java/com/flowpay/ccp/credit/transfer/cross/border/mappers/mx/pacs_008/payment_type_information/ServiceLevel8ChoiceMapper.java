package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.ServiceLevel8Choice;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(ServiceLevel8ChoiceMapper.Decorator.class)
public interface ServiceLevel8ChoiceMapper {

    @Mapping(target = "cd", source = "codiceLivelloDiServizio", conditionExpression = "java(informazioni.dettaglioLivelloDiServizio() != null && !informazioni.dettaglioLivelloDiServizio().isBlank())")
    @Mapping(target = "prtry", source = ".")
    ServiceLevel8Choice map(InformazioniAggiuntivePagamento informazioni, @Context MappingContext context);

    default List<ServiceLevel8Choice> mapList(InformazioniAggiuntivePagamento informazioni, @Context MappingContext context) {
        var element = this.map(informazioni, context);
        if (element == null) {
            return null;
        }
        return List.of(element);
    }

    default String prtry(InformazioniAggiuntivePagamento informazioniAggiuntivePagamento, @Context MappingContext context) {
        if (Boolean.FALSE.equals(context.stp())) {
            return Optional.ofNullable(informazioniAggiuntivePagamento.dettaglioLivelloDiServizio()).orElse("SDVA");
        }
        return null;
    }

    abstract class Decorator implements ServiceLevel8ChoiceMapper {

        private final ServiceLevel8ChoiceMapper delegate;

        Decorator(ServiceLevel8ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ServiceLevel8Choice map(InformazioniAggiuntivePagamento informazioni, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(informazioni, context));
        }
    }
}
