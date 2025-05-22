package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.dic.RemittanceLocation7;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        config = MxMappingConfig.class,
        uses = RemittanceLocationData1Mapper.class)
@DecoratedWith(RemittanceLocation7Mapper.Decorator.class)
public interface RemittanceLocation7Mapper {

    @Mapping(target = "rmtId", source = "dettaglioBonificoAccountToAccount.entity.altroIdentificativoPagamento")
    @Mapping(target = "rmtLctnDtls", source = "riferimentiAggiuntiviPagamento")
    RemittanceLocation7 map(BonificoExtraSepa.WithLinkedEntities bonifico);

    default List<RemittanceLocation7> mapList(BonificoExtraSepa.WithLinkedEntities bonifico) {
        var element = this.map(bonifico);
        if (element == null) {
            return null;
        }
        return List.of(element);
    }

    abstract class Decorator implements RemittanceLocation7Mapper {

        private final RemittanceLocation7Mapper delegate;

        Decorator(RemittanceLocation7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RemittanceLocation7 map(BonificoExtraSepa.WithLinkedEntities bonifico) {
            return Utils.allFieldsEmpty(delegate.map(bonifico));
        }
    }
}
