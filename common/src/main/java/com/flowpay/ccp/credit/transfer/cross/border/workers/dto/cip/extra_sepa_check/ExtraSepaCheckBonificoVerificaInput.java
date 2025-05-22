package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip.extra_sepa_check;

public record ExtraSepaCheckBonificoVerificaInput(
        TipoRichiesta tipoRichiesta,
        TipoMessaggio tipoMessaggio,
        String numeroRapportoDare,
        Integer voceContabileDare,
        String divisaDare,
        String numeroRapportoAvere,
        Integer voceContabileAvere,
        String divisaAvere,
        SistemaRegolamento sistemaRegolamento,
        String divisaBonifico,
        Boolean regolamentoCommissioniClienteCc,
        String causaleBonifico,
        String ibanBeneficiario,
        String bicBancaBeneficiario,
        String paeseBancaBeneficiario

) {
}