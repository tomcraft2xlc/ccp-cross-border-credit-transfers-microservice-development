package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.instructions;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.prowidesoftware.swift.model.mx.dic.Instruction5Code;
import com.prowidesoftware.swift.model.mx.dic.InstructionForCreditorAgent2;
import org.jboss.logging.Logger;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(InstructionForCreditorAgent2Mapper.Decorator.class)
public interface InstructionForCreditorAgent2Mapper {


    Logger logger = Logger.getLogger(InstructionForCreditorAgent2Mapper.class);



    InstructionForCreditorAgent2 map(String cd, String instrInf);


    default Instruction5Code map(String cd) {
        if (cd != null) {
            return Instruction5Code.fromValue(cd);
        }
        return null;
    }

    default List<InstructionForCreditorAgent2> mapList(InformazioniAggiuntivePagamento info, @Context MappingContext context) {
        if (info == null) {
            return null;
        }
        var result = new ArrayList<InstructionForCreditorAgent2>(2);

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

    abstract class Decorator implements InstructionForCreditorAgent2Mapper {

        private final InstructionForCreditorAgent2Mapper delegate;

        Decorator(InstructionForCreditorAgent2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public InstructionForCreditorAgent2 map(String cd, String instrInf) {
            return Utils.allFieldsEmpty(delegate.map(cd, instrInf));
        }
    }
}
