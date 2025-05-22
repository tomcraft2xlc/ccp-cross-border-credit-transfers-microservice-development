package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.dic.ClearingSystemIdentification3Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class
)
@DecoratedWith(ClearingSystemIdentification3ChoiceMapper.Decorator.class)
public interface ClearingSystemIdentification3ChoiceMapper {

    @Mapping(target = "cd", source = ".")
    @Mapping(target = "prtry", ignore = true)
    ClearingSystemIdentification3Choice map(BonificoExtraSepa.WithLinkedEntities bonifico);

    default String mapCd(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return "TGT";
        }
        return null;
    }

    abstract class Decorator implements ClearingSystemIdentification3ChoiceMapper {

        private final ClearingSystemIdentification3ChoiceMapper delegate;

        Decorator(ClearingSystemIdentification3ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ClearingSystemIdentification3Choice map(BonificoExtraSepa.WithLinkedEntities bonifico) {
            return Utils.allFieldsEmpty(delegate.map(bonifico));
        }
    }
}
