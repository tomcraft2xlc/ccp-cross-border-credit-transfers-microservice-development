package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ContoBancaDiCopertura;

public interface DettagliBonificoExtraSepa {
    DettagliImporto dettagliImporto();
    default DettagliImporto getDettagliImporto() { return dettagliImporto(); }

    DettagliDate dettagliDate();
    default DettagliDate getDettagliDate() { return dettagliDate(); }

    ContoBancaDiCopertura contoBancaDiCopertura();
    default ContoBancaDiCopertura getContoBancaDiCopertura() { return contoBancaDiCopertura(); }
}
