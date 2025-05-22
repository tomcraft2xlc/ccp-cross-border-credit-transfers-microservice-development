package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    title = "Tipo bonifico",
    description = """
        Tipi di bonifico gestiti dal sistema.
        
        I valori possibili sono:
        - `BANCA`: Bonifico banca-banca
        - `CLIENTE`: Bonifico cliente-cliente
        - `ENTRAMBI`: Entrambe le tipologie
        """
)
public enum WireTransferType {

    BANCA,
    CLIENTE,
    ENTRAMBI;
}
