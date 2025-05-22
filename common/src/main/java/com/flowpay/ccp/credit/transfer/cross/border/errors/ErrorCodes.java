package com.flowpay.ccp.credit.transfer.cross.border.errors;

import jakarta.ws.rs.core.Response.Status;

public class ErrorCodes extends RuntimeException {

    public final Codes code;

    public enum Codes {
        BFF_REQUIRED,
        CBPR_REQUIRED,
        OTHER_BANK_CBPR_NOT_SUPPORTED,
        TARGET_REQUIRED,
        OTHER_BANK_TARGET_NOT_SUPPORTED,
        SETTLMENT_INCOMPATIBLE,
        DST_CRD_INCOMPATIBLE,
        DST_INCOMPATIBLE,
        INVALID_STATUS_TRANSITION,
        CRD_REQUIRED,
        
        // Un capo essenziale manca dal dto
        MISSING_REQUIRED_FIELD,

        // Un bonifico è stato inviato su un canale che non supporta la sua tipologia
        TRANSFER_KIND_NOT_ALLOWED,

        // Un bonifico di tipologia errata è stato inviato
        WRONG_TRANSFER_TARGETS, 
        
        // La combinazione di banche ha vietato tutti i tipi di sistema di regolamento
        CONFIGURAZIONE_BANCHE_INCOMPATIBILE, 
        
        // Il sistema di regolamento non è supportato dalle banche intermediarie
        SISTEMA_DI_REGOLAMENTO_NON_SUPPORTATO, 
        
        SCONFINAMENTO_NON_PERMESSO, 
        EMBARGO_TOTALE, 
        CAMBIO_SUPERIORE_AL_SCARTO,
        CABEL_CALL_TECHNICAL_ERROR, 
        AVVERTENZE_BLOCCO_DARE, 
        AVVERTENZE_BLOCCO_TOTALE, 
        RICHIESTO_IBAN_BENEFICIARIO,
        DATA_FESTIVA;

        
        /**
         * Get the HTTP status that best matches the error code.
         * 
         * @return the HTTP status that best matches the error code
         */
        public Status responseCode() {
            switch (this) {
                // TODO: fill this with more precise codes
                case TRANSFER_KIND_NOT_ALLOWED, INVALID_STATUS_TRANSITION:
                    return Status.FORBIDDEN;
                // Defaults to `400 Bad Request`
                default:
                    return Status.BAD_REQUEST;
            }
        }

    }

    public ErrorCodes(Codes code) {
        super();
        this.code = code;
    }

    public ErrorCodes(Codes code, String message) {
        super(message);
        this.code = code;
    }

}
