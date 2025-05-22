package com.flowpay.ccp.credit.transfer.cross.border.dto.bank;

public record ConfigurazioneBanca(
        String bic,
        InfoCanale target,
        InfoCanale cbpr,
        Boolean listinoCommissioni,
        Boolean sconfinamento,
        String codiceDivisaDefault,
        String descrizioneDivisaDefault,
        String descrizioneCompletaDivisa
) {

    public record InfoCanale(
            Boolean attivo,
            String tramitatoDa,
            Integer oraCutOff,
            Integer minutoCutOff
    ) { }
}
