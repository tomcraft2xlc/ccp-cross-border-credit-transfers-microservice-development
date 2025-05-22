package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.pagination.dto.PaginatedResponse;
import com.flowpay.ccp.pagination.dto.PaginationRequest;
import com.flowpay.ccp.pagination.persistence.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BonificoDaAutorizzare(
        UUID id,
        String tid,
        SistemaDiRegolamento sistemaDiRegolamento,
        String sottoTipologiaBonifico,
        String divisa,
        BigDecimal importo,
        String intestazioneOrdinante,
        String bicOrdinante,
        String rapportoOrdinante,
        String intestazioneBeneficiario,
        String bicBancaBeneficiaria,
        String rapportoBeneficiario,
        String bicBancaDelBeneficiario,
        LocalDate dataRegolamentoBancaBeneficiario,
        Long filiale,
        CreditTransferStatus stato,
        Long maxLivelloAutorizzazione
) {

    public static PaginatedResponse<BonificoDaAutorizzare> toPaginatedResponse(Page<BonificoDaAutorizzare> page, PaginationRequest request) {
        return new PaginatedResponse<>(page, request.getPage(), request.getPageSize());
    }

}
