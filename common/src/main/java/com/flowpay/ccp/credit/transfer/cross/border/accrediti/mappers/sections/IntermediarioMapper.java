package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.prowidesoftware.swift.model.mx.dic.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.function.Function;

@Mapper(
        config = MxMappingConfig.class,
uses = {
                RapportoMapper.class,
                IndirizzoMapper.class
})
public interface IntermediarioMapper {

    IntermediarioMapper INSTANCE = Mappers.getMapper(IntermediarioMapper.class);

    @Mapping(target = "bic", source = "finInstId.BICFI")
    @Mapping(target = "intestazione", source = "finInstId.nm")
    @Mapping(target = "codiceLEI", source = "finInstId.LEI")
    @Mapping(target = "codiceSistemaClearing", source = "finInstId.clrSysMmbId.clrSysId.cd")
    @Mapping(target = "identificativoClearing", source = "finInstId.clrSysMmbId.mmbId")
    @Mapping(target = "rapporto", source = "cashAccount38")
    @Mapping(target = "indirizzo", source = "finInstId.pstlAdr")
    Intermediario map(FinancialInstitutionIdentification18 finInstId, CashAccount38 cashAccount38, @Context AccreditoMappingContext context);

    @Mapping(target = "bic", source = "finInstId.BICFI")
    @Mapping(target = "intestazione", source = "finInstId.nm")
    @Mapping(target = "codiceLEI", ignore = true)
    @Mapping(target = "codiceSistemaClearing", source = "finInstId.clrSysMmbId.clrSysId.cd")
    @Mapping(target = "identificativoClearing", source = "finInstId.clrSysMmbId.mmbId")
    @Mapping(target = "rapporto", source = "cashAccount38")
    @Mapping(target = "indirizzo", source = "finInstId.pstlAdr")
    Intermediario map(FinancialInstitutionIdentification8 finInstId, CashAccount38 cashAccount38, @Context AccreditoMappingContext context);

    default Intermediario map(BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification6, CashAccount38 cashAccount38, @Context AccreditoMappingContext context) {
        if (branchAndFinancialInstitutionIdentification6 == null && cashAccount38 == null) {
            return null;
        }
        var finInstId = Optional.ofNullable(branchAndFinancialInstitutionIdentification6).map(BranchAndFinancialInstitutionIdentification6::getFinInstnId).orElse(null);
        return map(finInstId, cashAccount38, context);
    }

    default Intermediario map(BranchAndFinancialInstitutionIdentification5 branchAndFinancialInstitutionIdentification6, CashAccount38 cashAccount38, @Context AccreditoMappingContext context) {
        if (branchAndFinancialInstitutionIdentification6 == null && cashAccount38 == null) {
            return null;
        }
        var finInstId = Optional.ofNullable(branchAndFinancialInstitutionIdentification6).map(BranchAndFinancialInstitutionIdentification5::getFinInstnId).orElse(null);
        return map(finInstId, cashAccount38, context);
    }

    default Intermediario map(BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification6) {
        return map(branchAndFinancialInstitutionIdentification6, null, null);
    }

    default Intermediario map(BranchAndFinancialInstitutionIdentification5 branchAndFinancialInstitutionIdentification5) {
        return map(branchAndFinancialInstitutionIdentification5, null, null);
    }

    default <T> Intermediario map(
            Optional<T> settlementInstruction7,
            Function<T, BranchAndFinancialInstitutionIdentification6> getInfo,
            Function<T, CashAccount38> getAccount,
            @Context AccreditoMappingContext context) {

        BranchAndFinancialInstitutionIdentification6 info = null;
        if (getInfo != null) {
            info = settlementInstruction7.map(getInfo).orElse(null);
        }
        CashAccount38 account = null;
        if (getAccount != null) {
            account = settlementInstruction7.map(getAccount).orElse(null);
        }
        return IntermediarioMapper.INSTANCE.map(info, account, context);
    }
}
