package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.google.common.base.Splitter;
import com.prowidesoftware.swift.model.mx.dic.InstructionForNextAgent1;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(InstructionForNextAgent1Mapper.Decorator.class)
public interface InstructionForNextAgent1Mapper {


    @Mapping(target = "cd", ignore = true)
    InstructionForNextAgent1 map(String instrInf);

    default List<InstructionForNextAgent1> mapList(InformazioniAggiuntivePagamento info, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.stp())) {
            return null;
        }
        var result = new ArrayList<InstructionForNextAgent1>(6);
        if (info == null || info.istruzioniBancaRicevente1() == null) {
            return null;
        }
        Splitter.fixedLength(35).splitToList(info.istruzioniBancaRicevente1())
        .stream().limit(6).map(this::map).forEach(result::add);

        return result;
    }


    abstract class Decorator implements InstructionForNextAgent1Mapper {

        private final InstructionForNextAgent1Mapper delegate;

        Decorator(InstructionForNextAgent1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public InstructionForNextAgent1 map(String instrInf) {
            return Utils.allFieldsEmpty(delegate.map(instrInf));
        }
    }
}
