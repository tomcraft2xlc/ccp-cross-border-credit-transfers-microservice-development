package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

import java.util.List;

public record EsteroElencoPaesiOutput(
        List<Segnalazione> listaSegnalazioni,
        boolean errored,
        List<Paese> listaPaesi
) implements CabelOutput {

    public record Paese(
            FlagEmbargo embargo
    ) {
    }

    public enum FlagEmbargo {
        NONSOTTOEMBARGO,
        SOTTOEMBARGOPARZIALE,
        SOTTOEMBARGOTOTALE
    }
}
