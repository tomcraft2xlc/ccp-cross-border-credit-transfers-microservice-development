package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.BonificoInIngressoResult;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.pagination.dto.PaginatedResponse;
import com.flowpay.ccp.pagination.dto.PaginationRequest;
import com.flowpay.ccp.pagination.persistence.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccreditiInquiryMapper {

    AccreditiInquiryMapper INSTANCE = Mappers.getMapper(AccreditiInquiryMapper.class);

    @Mapping(target = ".", source = "persistedRecord")
    @Mapping(target = "rapportoBeneficiario", source = "persistedRecord.numeroRapportoBeneficiario")
    @Mapping(target = "rapportoOrdinante", source = "persistedRecord.numeroRapportoOrdinante")
    @Mapping(target = "filiale", source = "persistedRecord.codiceFiliale")
    BonificoInIngressoResult toDTO(BonificoInIngresso persistedRecord);

    default PaginatedResponse<BonificoInIngressoResult> paginatedResponse(
            Page<BonificoInIngresso> page,
            PaginationRequest request
    ) {
        return new PaginatedResponse<>(new Page<>(page.totalElements(), page.data().stream().map(this::toDTO).toList()), request.getPage(), request.getPageSize());
    }

}
