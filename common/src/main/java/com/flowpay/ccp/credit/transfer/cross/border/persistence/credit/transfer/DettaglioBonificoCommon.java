package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer;

import java.math.BigDecimal;

/**
 * Dati comuni ad entrambi i dettagli bonifico
 */
public interface DettaglioBonificoCommon {
    BigDecimal importo();
    String divisa();
    BigDecimal cambio();
    BigDecimal importoDiAddebito();
    String codiceCausaleTransazione();
}
