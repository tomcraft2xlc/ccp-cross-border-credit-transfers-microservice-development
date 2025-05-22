package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import org.mapstruct.*;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        SettlementInstruction7Mapper.class,
        BranchAndFinancialInstitutionIdentification6Mapper.class
},
imports = {
        TipoIntermediario.class,
        BranchAndFinancialInstitutionIdentification6Mapper.class
})
@DecoratedWith(GroupHeader93Mapper.Decorator.class)
public interface GroupHeader93Mapper {


    @Mapping(target = "msgId", source = ".", qualifiedByName = "msgId")
    @GroupHeader93Mapping
    GroupHeader93 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext bankConfig);

    @Named("msgId")
    default String msgId(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return "NONREF";
        }
        return bonifico.getEntity().tid();
    }

    abstract class Decorator implements GroupHeader93Mapper {
        private final GroupHeader93Mapper delegate;

        Decorator(GroupHeader93Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public GroupHeader93 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext bankConfig) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, bankConfig));
        }
    }
}
