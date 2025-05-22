package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

public record AccreditoMappingContext(
        SistemaDiRegolamento sistemaDiRegolamento,
        SottoTipologiaBonifico sottoTipologiaBonifico
) {

}
