package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification;


import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.dic.BranchAndFinancialInstitutionIdentification6;
import java.util.Objects;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MxMappingConfig.class,
        uses = FinancialInstitutionIdentification18Mapper.class)
@DecoratedWith(BranchAndFinancialInstitutionIdentification6Mapper.Decorator.class)
public interface BranchAndFinancialInstitutionIdentification6Mapper {

    BranchAndFinancialInstitutionIdentification6Mapper INSTANCE = Mappers.getMapper(BranchAndFinancialInstitutionIdentification6Mapper.class);

    @Mapping(target = "finInstnId", source = ".")
    @Mapping(target = "brnchId", ignore = true)
    BranchAndFinancialInstitutionIdentification6 map(InformazioniIntermediario.WithLinkedEntities informazioni, @Context MappingContext context, @Context Boolean fullInfo);

    default BranchAndFinancialInstitutionIdentification6 map(InformazioniIntermediario.WithLinkedEntities informazioni, @Context MappingContext context) {
        return map(informazioni, context, true);
    }

    @Mapping(target = "finInstnId", source = ".")
    @Mapping(target = "brnchId", ignore = true)
    BranchAndFinancialInstitutionIdentification6 map(BanksConfig.BankConfig bankConfig, @Context MappingContext context, @Context Boolean fullInfo);

    default BranchAndFinancialInstitutionIdentification6 map(BanksConfig.BankConfig bankConfig, @Context MappingContext context) {
        return map(bankConfig, context, true);
    }

    default BranchAndFinancialInstitutionIdentification6 map(BonificoExtraSepa.WithLinkedEntities bonifico, TipoIntermediario tipoIntermediario,  @Context MappingContext context) {
        return map(bonifico, tipoIntermediario, false, context);
    }


        default BranchAndFinancialInstitutionIdentification6 map(BonificoExtraSepa.WithLinkedEntities bonifico, TipoIntermediario tipoIntermediario, Boolean documentoCollegato, @Context MappingContext context) {
        return map(
                bonifico.informazioniIntermediari.stream().filter(intermediario -> intermediario.getEntity().tipoIntermediario() == tipoIntermediario && intermediario.getEntity().intermediarioDocumentoCollegato() == documentoCollegato)
                .findFirst().orElse(null),
                context
        );
    }

    default BranchAndFinancialInstitutionIdentification6 map(BonificoExtraSepa.WithLinkedEntities bonifico, TipoIntermediario tipoIntermediario,  @Context MappingContext context, Boolean fullInfo) {
        return map(bonifico, tipoIntermediario, false, context, fullInfo);
    }

    default BranchAndFinancialInstitutionIdentification6 map(BonificoExtraSepa.WithLinkedEntities bonifico, TipoIntermediario tipoIntermediario, Boolean documentoCollegato, @Context MappingContext context, Boolean fullInfo) {
        return map(
                bonifico.informazioniIntermediari.stream().filter(intermediario -> intermediario.getEntity().tipoIntermediario() == tipoIntermediario && Objects.equals(intermediario.getEntity().intermediarioDocumentoCollegato(), documentoCollegato))
                        .findFirst().orElse(null),
                context,
                fullInfo
        );
    }

    abstract class Decorator implements BranchAndFinancialInstitutionIdentification6Mapper {

        private final BranchAndFinancialInstitutionIdentification6Mapper delegate;

        Decorator(BranchAndFinancialInstitutionIdentification6Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public BranchAndFinancialInstitutionIdentification6 map(InformazioniIntermediario.WithLinkedEntities informazioni, MappingContext context, Boolean fullInfo) {
            return Utils.allFieldsEmpty(delegate.map(informazioni, context, fullInfo));
        }

        @Override
        public BranchAndFinancialInstitutionIdentification6 map(BanksConfig.BankConfig bankConfig, MappingContext context, Boolean fullInfo) {
            return Utils.allFieldsEmpty(delegate.map(bankConfig, context, fullInfo));
        }

    }
}
