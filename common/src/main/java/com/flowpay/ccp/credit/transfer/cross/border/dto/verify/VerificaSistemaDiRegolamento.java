package com.flowpay.ccp.credit.transfer.cross.border.dto.verify;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

import java.util.List;

public record VerificaSistemaDiRegolamento(
        Boolean controlloPassato,
        List<SistemaDiRegolamento> sistemaDiRegolamentoCompatibili
) {
}
