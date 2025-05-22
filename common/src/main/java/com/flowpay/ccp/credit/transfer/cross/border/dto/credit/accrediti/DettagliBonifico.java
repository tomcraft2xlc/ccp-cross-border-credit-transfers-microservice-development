package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliImporto;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.fee.CommissioniBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.CausaleCliente;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;

import java.math.BigDecimal;
import java.util.List;

public record DettagliBonifico(
        Importo importo,
        DettagliImporto importoIstruito,
        Importo importoDiAccredito,
        InfoDate date,
        String codiceCausaleTransazione,
        List<InformazioneAggiuntivaCommissione> informazioniAggiuntiveCommissioni,
        CausaleCliente causaleCliente, // (causale descrittiva e causale descrittiva strutturata)
        RegolamentoCommissione regolamentoCommissioniBanca,
        TipologiaCommissioni tipologiaCommissioniBanca,
        List<CommissioneBanca> commissioniBanca,
        RegolamentoCommissione regolamentoCommissioniClientela,
        List<CommissioneClientela> commissioniClientela
) {
    public record InformazioneAggiuntivaCommissione(
            Importo importo,
            Intermediario intermediario
    ) {
    }
}