-- DATI VERIFICA

CREATE TYPE stato_verifica AS ENUM (
    'ATTENDE_RISPOSTE',
    'VERIFICATO',
    'DA_CONFERMARE',
    'FALLITO'
);
CREATE TYPE stato_verifica_generico AS ENUM (
    'ATTENDE_RISPOSTA',
    'VERIFICATO',
    'FALLITO'
);
CREATE TYPE stato_verifica_bonifico AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_MODIFICA_IBAN',
    'VERIFICATO',
    'FALLITO'
);
CREATE TYPE stato_verifica_avvertenze AS ENUM (
    'ATTENDE_RISPOSTA',
    'VERIFICATO',
    'BLOCCO_DARE',
    'BLOCCO_TOTALE',
    'FALLITO'
);
CREATE TYPE stato_verifica_cambio AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_MODIFICA_CAMBIO',
    'VERIFICATO',
    'FALLITO'
);
CREATE TYPE stato_verifica_embargo AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_FORZATURA_EMBARGO_PARZIALE',
    'VERIFICATO',
    'FALLITO'
);
CREATE TYPE stato_verifica_saldo_rapporto AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_FORZATURA_SCONFINAMENTO',
    'VERIFICATO',
    'FALLITO'
);

CREATE TABLE dati_verifica_bonifico (
     id UUID PRIMARY KEY,
     id_bonifico_extra_sepa UUID NOT NULL,
     inizio_verifica TIMESTAMP WITH TIME ZONE NOT NULL,
     stato_verifica stato_verifica NOT NULL,
     stato_verifica_saldo_rapporto stato_verifica_saldo_rapporto NOT NULL,
     importo_sconfinamento NUMERIC(18,2),
     stato_verifica_avvertenze_rapporto stato_verifica_avvertenze NOT NULL,
     stato_verifica_embargo stato_verifica_embargo NOT NULL,
     stato_verifica_cambio stato_verifica_cambio NOT NULL,
     stato_verifica_holiday_table_paese stato_verifica_generico NOT NULL,
     stato_verifica_holiday_table_divisa stato_verifica_generico NOT NULL,
     stato_verifica_bonifico stato_verifica_bonifico NOT NULL,
 
     -- Vincolo di chiave esterna
     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
         REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

CREATE TABLE dati_verifica_bonifico_avvertenze (
    id UUID PRIMARY KEY,
    id_dati_verifica_bonifico UUID NOT NULL,
    codice VARCHAR(255) NOT NULL,
    descrizione VARCHAR(255) NOT NULL,
 
    CONSTRAINT fk_dati_verifica_bonifico FOREIGN KEY (id_dati_verifica_bonifico)
        REFERENCES dati_verifica_bonifico(id)
);

CREATE TABLE dati_verifica_bonifico_errore_tecnico (
    id UUID PRIMARY KEY,
    id_dati_verifica_bonifico UUID NOT NULL,
    codice VARCHAR(255) NOT NULL,
    descrizione VARCHAR(255) NOT NULL,

    CONSTRAINT fk_dati_verifica_bonifico FOREIGN KEY (id_dati_verifica_bonifico)
        REFERENCES dati_verifica_bonifico(id)
);