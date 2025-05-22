package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

public interface DettagliCausale {
    String codiceCausaleTransazione();
    default String getCodiceCausaleTransazione() { return codiceCausaleTransazione(); }

    Causale causale();

    default Causale getCausale() { return  causale(); }
}
