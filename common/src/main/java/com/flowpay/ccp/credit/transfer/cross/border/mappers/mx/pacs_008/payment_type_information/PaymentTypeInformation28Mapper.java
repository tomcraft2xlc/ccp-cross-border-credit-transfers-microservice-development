package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.payment_type_information;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.PrioritaTransazione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.PaymentTypeInformation28;
import com.prowidesoftware.swift.model.mx.dic.Priority2Code;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        ServiceLevel8ChoiceMapper.class,
        CategoryPurpose1ChoiceMapper.class,
        LocalInstrument2ChoiceMapper.class
})
@DecoratedWith(PaymentTypeInformation28Mapper.Decorator.class)
public interface PaymentTypeInformation28Mapper {

    @Mapping(target = "instrPrty", source = "prioritaTransazione")
    @Mapping(target = "svcLvl", source = "informazioni")
    @Mapping(target = "lclInstrm", source = "informazioni")
    @Mapping(target = "ctgyPurp", source = "informazioni")
    @Mapping(target = "clrChanl", ignore = true)
    PaymentTypeInformation28 map(InformazioniAggiuntivePagamento informazioni, @Context MappingContext context);

    default Priority2Code map(PrioritaTransazione priorita) {
        if (priorita == null) {
            return null;
        }
        return priorita.asPriority2Code();
    }

    abstract class Decorator implements PaymentTypeInformation28Mapper {

        private final PaymentTypeInformation28Mapper delegate;

        Decorator(PaymentTypeInformation28Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public PaymentTypeInformation28 map(InformazioniAggiuntivePagamento informazioni, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(informazioni, context));
        }
    }
}
