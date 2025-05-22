package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record DettagliIdentificativiBonifico(
        String identificativoFlusso,
        String tid,
        UUID codiceUETR,
        String endToEnd,
        String idMessaggio,
        String idTransazione,
        Boolean possibileDuplicato,
        String prioritaRegolamentoBanca,
        Instant dataAddebito,
        Instant dataAccredito,
        InfoSistemaDiRegolamento infoSistemaDiRegolamento,
        String identificativoDiDefinizioneDelMessaggio,
        String servizioDiBusiness,
        Instant dataDiCreazioneFlusso,
        Long numeroTransazioni,
        String idSistemaDiClearing,
        String canaleDiClearing,
        String metodoDiRegolamento
) {
}
