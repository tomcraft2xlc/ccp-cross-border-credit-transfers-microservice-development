package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;
import java.util.List;

public record EsteroControlloRapportoOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    boolean bloccoDare,
    boolean bloccoAvere,
    boolean bloccoTotale,

    List<Avvertenza> avvertenze,
    Avvertenza esitoAvvertenze,

    boolean abiDiAltraBanca,

    EsitoControlloGenerale esitoControlloGenerale

) implements CabelOutput {

    public record Avvertenza(
        String codice,
        String descrizione
    ) {}

    public enum EsitoControlloGenerale {
        VALIDO,
        BLOCCATO
    }
}