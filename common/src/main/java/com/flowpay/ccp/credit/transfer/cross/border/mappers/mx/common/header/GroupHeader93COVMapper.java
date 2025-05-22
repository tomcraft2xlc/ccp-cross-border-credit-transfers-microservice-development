package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class,
        uses = {
        SettlementInstruction7COVEMapper.class
},
imports = {
        BranchAndFinancialInstitutionIdentification6Mapper.class,
        TipoIntermediario.class
})
@DecoratedWith(GroupHeader93COVMapper.Decorator.class)
public interface GroupHeader93COVMapper {

    @GroupHeader93Mapping
    @Mapping(target = "msgId", source = ".")
    GroupHeader93 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext bankConfig);

    default String msgId(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return "NONREF";
        }
        return bonifico.getEntity().tidDocumentoCollegato();
    }


    abstract class Decorator implements GroupHeader93COVMapper {

        private final GroupHeader93COVMapper delegate;

        Decorator(GroupHeader93COVMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public GroupHeader93 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext bankConfig) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, bankConfig));
        }
    }
}
