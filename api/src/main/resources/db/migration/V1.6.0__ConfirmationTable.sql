-- DATI VERIFICA

ALTER TYPE credit_transfer_status ADD VALUE 'CONFERMATO_STEP_1_COMPLETO';
ALTER TYPE credit_transfer_status ADD VALUE 'CONFERMATO_STEP_2';

CREATE TYPE stato_conferma AS ENUM (
    'ATTENDE_RISPOSTE',
    'CONFERMATO',
    'DA_CONFERMARE',
    'ATTENDE_RISPOSTA_STEP_2',
    'FALLITO'
);
CREATE TYPE stato_conferma_generico AS ENUM (
    'ATTENDE_RISPOSTA',
    'CONFERMATO',
    'FALLITO',
    'ERRORE'
);
CREATE TYPE stato_conferma_bonifico AS ENUM (
    'ATTENDE_RISPOSTA',
    'CONFERMATO',
    'FALLITO',
    'ERRORE'
    'NON_INVIATA'
);
CREATE TYPE stato_conferma_avvertenze AS ENUM (
    'ATTENDE_RISPOSTA',
    'CONFERMATO',
    'CONFERMATO_STEP_VERIFICA',
    'BLOCCO_DARE',
    'BLOCCO_TOTALE',
    'FALLITO',
    'ERRORE'
);
CREATE TYPE stato_conferma_cambio AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_MODIFICA_CAMBIO',
    'CONFERMATO',
    'ERRORE'
);
CREATE TYPE stato_conferma_embargo AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_FORZATURA_EMBARGO_PARZIALE',
    'CONFERMATO_STEP_VERIFICA',
    'CONFERMATO',
    'FALLITO',
    'ERRORE'
);
CREATE TYPE stato_conferma_saldo_rapporto AS ENUM (
    'ATTENDE_RISPOSTA',
    'NECESSITA_FORZATURA_SCONFINAMENTO',
    'CONFERMATO',
    'CONFERMATO_STEP_VERIFICA',
    'FALLITO',
    'ERRORE'
);

CREATE TABLE dati_conferma_bonifico (
     id UUID PRIMARY KEY,
     id_bonifico_extra_sepa UUID NOT NULL,
     inizio_conferma TIMESTAMP WITH TIME ZONE NOT NULL,
     stato_conferma stato_conferma NOT NULL,
     stato_conferma_saldo_rapporto stato_conferma_saldo_rapporto NOT NULL,
     importo_sconfinamento NUMERIC(18,2),
     stato_conferma_avvertenze_rapporto stato_conferma_avvertenze NOT NULL,
     stato_conferma_embargo stato_conferma_embargo NOT NULL,
     stato_conferma_cambio stato_conferma_cambio NOT NULL,
     stato_conferma_holiday_table_paese stato_conferma_generico NOT NULL,
     stato_conferma_holiday_table_divisa stato_conferma_generico NOT NULL,
     stato_conferma_bonifico stato_conferma_bonifico NOT NULL,
 
     -- Vincolo di chiave esterna
     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
         REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

CREATE TABLE dati_conferma_bonifico_avvertenze (
    id UUID PRIMARY KEY,
    id_dati_conferma_bonifico UUID NOT NULL,
    codice VARCHAR(255) NOT NULL,
    descrizione VARCHAR(255) NOT NULL,
 
    CONSTRAINT fk_dati_conferma_bonifico FOREIGN KEY (id_dati_conferma_bonifico)
        REFERENCES dati_conferma_bonifico(id)
);

CREATE TABLE dati_conferma_bonifico_errore_tecnico (
    id UUID PRIMARY KEY,
    id_dati_conferma_bonifico UUID NOT NULL,
    codice VARCHAR(255) NOT NULL,
    descrizione VARCHAR(255) NOT NULL,

    CONSTRAINT fk_dati_conferma_bonifico FOREIGN KEY (id_dati_conferma_bonifico)
        REFERENCES dati_conferma_bonifico(id)
);

ALTER TABLE bonifico_extra_sepa
ADD COLUMN numero_transazione INTEGER;