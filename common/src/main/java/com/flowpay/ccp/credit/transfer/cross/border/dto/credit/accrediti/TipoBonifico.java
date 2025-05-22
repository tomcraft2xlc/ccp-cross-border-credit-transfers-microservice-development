package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;

public record TipoBonifico(
        SottoTipologiaBonifico sottoTipologiaBonifico,
        Boolean cov
) {
}
