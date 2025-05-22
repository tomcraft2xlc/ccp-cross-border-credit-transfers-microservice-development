package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

import java.util.UUID;

public record AccreditoToPersistenceContext(
        String abi,
        Long codiceFiliale,
        SistemaDiRegolamento sistemaDiRegolamento,
        UUID idMappatura
) {
}
