package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.instructions;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.Instruction3Code;
import com.prowidesoftware.swift.model.mx.dic.InstructionForCreditorAgent1;
import org.jboss.logging.Logger;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(InstructionForCreditorAgent1Mapper.Decorator.class)
public interface InstructionForCreditorAgent1Mapper {

    Logger logger = Logger.getLogger(InstructionForCreditorAgent1Mapper.class);

    InstructionForCreditorAgent1 map(String cd, String instrInf);

    default Instruction3Code map(String cd) {
        if (cd != null) {
            return Instruction3Code.fromValue(cd);
        }
        return null;
    }

    default String trim(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() > 140) {
            return value.substring(0, 140);
        }
        return value;
    }

    default List<InstructionForCreditorAgent1> mapList(InformazioniAggiuntivePagamento info, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.stp())) {
            return null;
        }
        if (info == null) {
            return null;
        }
        var result = new ArrayList<InstructionForCreditorAgent1>(2);
        result.add(
                this.map(
                        info.codiceIstruzioneBancaDelBeneficiario1(),
                        info.istruzioneBancaDelBeneficiario1())
        );

        result.add(
                this.map(
                        info.codiceIstruzioneBancaDelBeneficiario2(),
                        info.istruzioneBancaDelBeneficiario2())
        );

        return result;
    }

    abstract class Decorator implements InstructionForCreditorAgent1Mapper {

        private final InstructionForCreditorAgent1Mapper delegate;

        Decorator(InstructionForCreditorAgent1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public InstructionForCreditorAgent1 map(String cd, String instrInf) {
            return Utils.allFieldsEmpty(delegate.map(cd, instrInf));
        }

        @Override
        public Instruction3Code map(String cd) {
            return Utils.allFieldsEmpty(delegate.map(cd));
        }
    }
}
