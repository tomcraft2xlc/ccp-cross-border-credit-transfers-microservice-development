package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BonificoInIngressoResult(
        UUID id,
        String tid,
        String sottoTipologiaBonifico,
        SistemaDiRegolamento sistemaDiRegolamento,
        String divisa,
        BigDecimal importo,
        String intestazioneBeneficiario,
        String bicBeneficiario,
        String rapportoBeneficiario,
        String intestazioneOrdinante,
        String bicOrdinante,
        String rapportoOrdinante,
        LocalDate dataRegolamentoBancaBeneficiario,
        Long filiale,
        String stato
) {
}
