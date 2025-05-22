package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

public record Segnalazione(
    String codice,
    String descrizione,
    Livello livello

) {
    public enum Livello {
        ERRORE,
        WARNING,
        INFORMAZIONE
    }
}