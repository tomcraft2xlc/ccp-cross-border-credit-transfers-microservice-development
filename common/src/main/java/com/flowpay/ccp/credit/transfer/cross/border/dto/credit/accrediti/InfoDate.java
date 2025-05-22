package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import java.time.Instant;
import java.time.LocalDate;

public record InfoDate(
        Instant dataCreazione,
        LocalDate dataEsecuzione,
        LocalDate dataRegolamentoBancaBeneficiario
) {
}
