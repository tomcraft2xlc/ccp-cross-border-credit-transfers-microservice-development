package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;
import java.util.List;

public record RicercaBicOutput(
    List<Segnalazione> listaSegnalazioni,
    boolean errored,
    List<Bic> listaBic

) implements CabelOutput  {

    public record Bic(
        String codiceBIC,
        String intestazioneBic,
        String codicePaeseIso,
        String decodificaPaese,
        Integer numeroAbi,
        boolean flagTgt,
        FlagTgtDirIndir flagTgtDirIndir,
        String bicBancaTramitante,
        FlagEmbargo flagEmbargo,
        boolean condSpecPaese,
        Integer posizioneEstero,
        boolean flagScambioChiavi,
        String indirizzo,
        String citta,
        String provincia,
        String codiceLei,
        String zipCode

    ) {
        public enum FlagTgtDirIndir {
            DIRETTA,
            INDIRETTA
        }

        public enum FlagEmbargo {
            NONPRESENTE,
            PARZIALE,
            TOTALE
        }
    }
}