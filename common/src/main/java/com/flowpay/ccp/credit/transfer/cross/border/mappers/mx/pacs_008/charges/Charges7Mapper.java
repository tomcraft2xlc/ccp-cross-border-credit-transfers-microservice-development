package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.charges;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification.BranchAndFinancialInstitutionIdentification6Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification6;
import com.prowidesoftware.swift.model.mx.dic.Charges7;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(Charges7Mapper.Decorator.class)
public interface Charges7Mapper {


    @Mapping(target = "amt", source = ".")
    @Mapping(target = "agt", source = ".")
    Charges7 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    default List<Charges7> mapList(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        var element = this.map(bonifico, context);
        if (element == null) {
            return null;
        }
        return List.of(element);
    }

    default ActiveOrHistoricCurrencyAndAmount amount(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (bonifico.commissioneBanca == null) {
            return null;
        }
        var commissione = bonifico.commissioneBanca.getEntity();
        if (bonifico.sottoTipologiaBonifico.getEntity().bancaABanca()) {
            return null;
        }
        var tipologia = bonifico.dettaglioBonificoAccountToAccount.getEntity().tipologiaCommissioni();
        return switch (tipologia) {
            case DEBTOR, CREDITOR -> ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(
                    commissione.importo(),
                    commissione.divisa() != null ? commissione.divisa() : bonifico.dettaglioBonificoAccountToAccount.getEntity().divisa()
            );
            case SHARED -> null;
        };
    }

    default BranchAndFinancialInstitutionIdentification6 agent(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(bonifico.sottoTipologiaBonifico.getEntity().bancaABanca())) {
            return null;
        }
        return BranchAndFinancialInstitutionIdentification6Mapper.INSTANCE.map(
                switch (bonifico.dettaglioBonificoAccountToAccount.getEntity().tipologiaCommissioni()) {
                    case CREDITOR -> bonifico.informazioniIntermediari.stream().filter(intermediary -> intermediary.getEntity().tipoIntermediario() == TipoIntermediario.BANCA_DELL_ORDINANTE)
                        .findFirst().orElseThrow();
                    case DEBTOR -> bonifico.informazioniIntermediari.stream().filter(intermediary -> intermediary.getEntity().tipoIntermediario() == TipoIntermediario.BANCA_DESTINATARIA)
                            .findFirst().orElseThrow();
                    case SHARED -> null;
                },
                context
        );

    }

    abstract class Decorator implements Charges7Mapper {
        private final Charges7Mapper delegate;

        Decorator(Charges7Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public Charges7 map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
            return Utils.allFieldsEmpty(delegate.map(bonifico, context));
        }
    }
}
