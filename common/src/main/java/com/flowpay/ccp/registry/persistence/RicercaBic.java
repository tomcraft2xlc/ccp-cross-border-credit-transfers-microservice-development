package com.flowpay.ccp.registry.persistence;


public abstract class RicercaBic{
    public enum DirezioneTarget {
        DIRETTA,
        INDIRETTA
    }

    public enum TipoEmbargo {
        NONPRESENTE,
        PARZIALE,
        TOTALE
    }
}
