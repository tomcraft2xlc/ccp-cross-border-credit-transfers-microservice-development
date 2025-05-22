CREATE TYPE tipo_rapporto AS ENUM (
    'RAPPORTO',
    'SOTTO_CONTO'
);

CREATE TABLE canale (
    id UUID PRIMARY KEY,
    id_canale VARCHAR(255) NOT NULL,
    utente_richiesto BOOLEAN NOT NULL
);
-- Questa tabella è mappata dalla classe Java "InformazioniRapporto"
CREATE TABLE informazioni_rapporto (
    id UUID PRIMARY KEY,
    numero VARCHAR(255),
    tipo_rapporto tipo_rapporto,
    ndg VARCHAR(50),
    iban VARCHAR(34),
    altro_id VARCHAR(255),
    divisa VARCHAR(3),
    codice_tipo_conto VARCHAR(255),
    dettaglio_tipo_conto VARCHAR(255),
    intestazione_conto VARCHAR(255),
    codice_tipo_alias VARCHAR(255),
    descrizione_alias VARCHAR(255),
    dettaglio_identificativo_alias VARCHAR(255),
    codice_identificativo_conto VARCHAR(255),
    descrizione_identificativo_conto VARCHAR(255),
    emittente VARCHAR(255),
    codice_filiale INT,
    denominazione_filiale VARCHAR(255)
);

-- Creazione del tipo ENUM per PostgreSQL basato su TipoInformazioniRapporto
CREATE TYPE tipo_informazioni_rapporto AS ENUM (
    'CORRISPONDENTE_MITTENTE',
    'CORRISPONDENTE_MITTENTE_DOCUMENTO_COLLEGATO',
    'BANCA_CORRISPONDENTE_MITTENTE',
    'BANCA_CORRISPONDENTE_RICEVENTE',
    'ISTITUTO_TERZO_DI_RIMBORSO'
);

-- Creazione del tipo ENUM PostgreSQL basato sull'enum Java CreditTransferStatus
CREATE TYPE credit_transfer_status AS ENUM (
    'TO_BE_MANAGED',
    'INSERITO',
    'DA_CONFERMARE',
    'CONFERMATO',
    'ELIMINATO',
    'WCL_NON_PASSATA',
    'WCL_NEGATA',
    'DA_AUTORIZZARE',
    'AUTORIZZATO',
    'INVIATO',
    'REGOLATO',
    'IN_ERRORE',
    'NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO',
    'NON_AUTORIZZATO',
    'RIFIUTATO_RIMBORSO_PROGRAMMATO',
    'RIFIUTATO',
    'STORNO_RICHIESTO',
    'STORNO_INVIATO',
    'STORNO_ACCETTATO',
    'STORNO_RIFIUTATO',
    'STORNO_RIMBORSO_PROGRAMMATO',
    'RIMBORSATO'
);

-- Creazione del tipo ENUM per il sistema di regolamento
CREATE TYPE sistema_di_regolamento AS ENUM (
    'TARGET',
    'NO_TARGET'
);

-- Creazione della tabella bonifico_extra_sepa
CREATE TABLE bonifico_extra_sepa (
    id UUID PRIMARY KEY,
    tid VARCHAR(255) NOT NULL,
    tid_documento_collegato VARCHAR(255),
    id_canale UUID NOT NULL,
    id_sotto_tipologia_bonifico UUID NOT NULL,
    sistema_di_regolamento sistema_di_regolamento NOT NULL,
    codice_filiale INT,
    denominazione_filiale VARCHAR(255),
    utente VARCHAR(255),
    iban_conto_banca_di_copertura VARCHAR(34),
    divisa_conto_banca_di_copertura VARCHAR(3),
    bic_banca_di_copertura VARCHAR(11),
    intestazione_banca_di_copertura VARCHAR(255),
    data_di_creazione DATE NOT NULL,
    data_di_esecuzione DATE NOT NULL,
    data_valuta_ordinante DATE NOT NULL,
    data_regolamento_banca_beneficiario DATE NOT NULL,
    stato credit_transfer_status NOT NULL,
    in_gestione BOOLEAN NOT NULL,
    istante_creazione_messaggio TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_canale FOREIGN KEY (id_canale)
        REFERENCES canale(id) DEFERRABLE
);

-- Creazione della tabella informazioni_rapporto_bonifico_extra_sepa
CREATE TABLE informazioni_rapporto_bonifico_extra_sepa (
    id UUID PRIMARY KEY,
    id_info_rapporto UUID NOT NULL,
    id_bonifico_extra_sepa UUID NOT NULL,
    tipo_informazioni_rapporto tipo_informazioni_rapporto NOT NULL,

    -- Vincoli di chiave esterna
    CONSTRAINT fk_info_rapporto FOREIGN KEY (id_info_rapporto)
        REFERENCES informazioni_rapporto(id) DEFERRABLE,

    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella dettaglio_bonifico_account_to_account
CREATE TABLE dettaglio_bonifico_account_to_account (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    importo NUMERIC(18,2) NOT NULL,
    divisa VARCHAR(3) NOT NULL,
    valore_cambio NUMERIC(18,6),
    importo_di_addebito NUMERIC(18,2),
    codice_causale_transazione VARCHAR(255),
    altro_identificativo_pagamento VARCHAR(255),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione del tipo ENUM PostgreSQL basato sull'enum Java TipoNdg
CREATE TYPE tipo_ndg AS ENUM (
     'PRESENTATORE',
     'TITOLARE_EFFETTIVO'
);

-- Creazione della tabella informazioni_ndg
CREATE TABLE informazioni_ndg (
    id UUID PRIMARY KEY,
    id_dettaglio_bonifico UUID NOT NULL,
    tipo tipo_ndg NOT NULL,
    ndg VARCHAR(255),
    nome VARCHAR(255),
    codice_fiscale VARCHAR(16),

    CONSTRAINT fk_dettaglio_bonifico FOREIGN KEY (id_dettaglio_bonifico)
        REFERENCES dettaglio_bonifico_account_to_account(id) DEFERRABLE
);


-- Creazione della tabella dettaglio_bonifico_banca_a_banca
CREATE TABLE dettaglio_bonifico_banca_a_banca (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    importo NUMERIC(18,2) NOT NULL,
    divisa VARCHAR(3) NOT NULL,
    cambio NUMERIC(18,6),
    importo_di_addebito NUMERIC(18,2),
    codice_causale_transazione VARCHAR(255),
    informazioni_aggiuntive_notifica VARCHAR(255),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella indirizzo_postale
CREATE TABLE indirizzo_postale (
    id UUID PRIMARY KEY,
    indirizzo VARCHAR(255),
    citta VARCHAR(255),
    cap VARCHAR(10),
    paese VARCHAR(2),
    divisione VARCHAR(255),
    sotto_divisione VARCHAR(255),
    numero_civico VARCHAR(10),
    edificio VARCHAR(255),
    piano VARCHAR(10),
    cassetta_postale VARCHAR(50),
    stanza VARCHAR(50),
    localita VARCHAR(255),
    distretto VARCHAR(255),
    provincia VARCHAR(255),
    linea_indirizzo VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Creazione del tipo ENUM per il regolamento della commissione
CREATE TYPE regolamento_commissione AS ENUM (
    'EURO',
    'DIVISA'
);

-- Creazione del tipo ENUM per la tipologia di commissioni
CREATE TYPE tipologia_commissioni AS ENUM (
    'SHARED',
    'CREDITOR',
    'DEBTOR'
);

-- Creazione della tabella commissione_account_to_account
CREATE TABLE commissione_account_to_account (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    regolamento_commissione regolamento_commissione NOT NULL,
    descrizione VARCHAR(255),
    importo NUMERIC(18,2),
    percentuale NUMERIC(5,2),
    max NUMERIC(18,2),
    min NUMERIC(18,2),
    divisa VARCHAR(3),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella commissione_banca_a_banca
CREATE TABLE commissione_banca_a_banca (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    tipologia tipologia_commissioni NOT NULL,
    regolamento regolamento_commissione NOT NULL,
    descrizione VARCHAR(255),
    divisa VARCHAR(3),
    importo NUMERIC(18,2),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione del tipo ENUM per i tipi di intermediari finanziari
CREATE TYPE tipo_intermediario AS ENUM (
    'ORDINANTE',
    'BANCA_DELL_ORDINANTE',
    'BANCA_DESTINATARIA',
    'BANCA_BENEFICIARIA',
    'BANCA_DEL_BENEFICIARIO',
    'BANCA_ISTRUTTRICE_1',
    'BANCA_ISTRUTTRICE_2',
    'BANCA_ISTRUTTRICE_3',
    'BANCA_INTERMEDIARIA_1',
    'BANCA_INTERMEDIARIA_2',
    'BANCA_INTERMEDIARIA_3',
    'BANCA_CORRISPONDENTE_MITTENTE',
    'BANCA_CORRISPONDENTE_RICEVENTE',
    'ISTITUTO_TERZO_DI_RIMBORSO'
);

-- Creazione della tabella informazioni_intermediario con chiavi esterne
CREATE TABLE informazioni_intermediario (
    id UUID PRIMARY KEY,
    intermediario_documento_collegato BOOLEAN NOT NULL,
    id_bonifico_extra_sepa UUID NOT NULL,
    id_info_rapporto UUID,
    id_indirizzo_postale UUID,
    tipo_intermediario tipo_intermediario NOT NULL,
    bic VARCHAR(11),
    intestazione VARCHAR(255),
    codice_lei VARCHAR(20),
    codice_sistema_clearing VARCHAR(255),
    identificativo_clearing VARCHAR(255),

    -- Vincoli di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE,

    CONSTRAINT fk_info_rapporto FOREIGN KEY (id_info_rapporto)
        REFERENCES informazioni_rapporto(id) DEFERRABLE,

    CONSTRAINT fk_indirizzo_postale FOREIGN KEY (id_indirizzo_postale)
        REFERENCES indirizzo_postale(id) DEFERRABLE
);

-- Creazione della tabella sotto_tipologia_bonifico
CREATE TABLE sotto_tipologia_bonifico (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    produci_mt999 BOOLEAN NOT NULL,
    campi_dto_obbligatori TEXT,
    banca_a_banca BOOLEAN NOT NULL,
    con_notifica BOOLEAN NOT NULL
);

-- Creazione della tabella bonifico_extra_sepa_lista_canali_abilitati con chiavi esterne
CREATE TABLE bonifico_extra_sepa_lista_canali_abilitati (
    id UUID PRIMARY KEY,
    id_sottotipo_bonifico_extra_sepa UUID NOT NULL,
    id_canale UUID NOT NULL,
    stato_default credit_transfer_status NOT NULL,
    CONSTRAINT fk_sottotipo_bonifico FOREIGN KEY (id_sottotipo_bonifico_extra_sepa)
        REFERENCES sotto_tipologia_bonifico(id) DEFERRABLE,
    CONSTRAINT fk_canale FOREIGN KEY (id_canale)
        REFERENCES canale(id) DEFERRABLE
);

-- Creazione della tabella sotto_tipologia_bonifico_mappatura con chiave esterna
CREATE TABLE sotto_tipologia_bonifico_mappatura (
    id UUID PRIMARY KEY,
    id_sotto_tipologia_bonifico UUID NOT NULL,
    id_messaggio VARCHAR(255) NOT NULL,
    mappatura_classe_qualificata VARCHAR(255) NOT NULL,
    CONSTRAINT fk_sotto_tipologia_bonifico FOREIGN KEY (id_sotto_tipologia_bonifico)
        REFERENCES sotto_tipologia_bonifico(id) DEFERRABLE
);

-- Creazione del tipo ENUM per il tipo di attore nella transazione
CREATE TYPE tipo_attore AS ENUM (
    'ORDINANTE',
    'SOGGETTO_ISTRUTTORE',
    'ORDINANTE_EFFETTIVO',
    'BENEFICIARIO',
    'BENEFICIARIO_EFFETTIVO'
);

-- Creazione della tabella informazioni_attore con chiavi esterne
CREATE TABLE informazioni_attore (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID,
    id_info_rapporto UUID,
    id_indirizzo_postale UUID,
    tipo_attore tipo_attore NOT NULL,
    intestazione VARCHAR(255),
    paese_di_residenza VARCHAR(2),

    -- Vincoli di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE,

    CONSTRAINT fk_info_rapporto FOREIGN KEY (id_info_rapporto)
        REFERENCES informazioni_rapporto(id) DEFERRABLE,

    CONSTRAINT fk_indirizzo_postale FOREIGN KEY (id_indirizzo_postale)
        REFERENCES indirizzo_postale(id) DEFERRABLE
);

-- Creazione della tabella organizzazione con chiave esterna
CREATE TABLE organizzazione (
    id UUID PRIMARY KEY,
    id_informazioni_attore UUID NOT NULL,
    bic VARCHAR(11),
    codice_lei VARCHAR(20),
    identificativo_organizzazione_1 VARCHAR(255),
    codice_identificativo_organizzazione_1 VARCHAR(255),
    codice_proprietario_identificativo_organizzazione_1 VARCHAR(255),
    emittente_1 VARCHAR(255),
    identificativo_organizzazione_2 VARCHAR(255),
    codice_identificativo_organizzazione_2 VARCHAR(255),
    codice_proprietario_identificativo_organizzazione_2 VARCHAR(255),
    emittente_2 VARCHAR(255),
    CONSTRAINT fk_informazioni_attore FOREIGN KEY (id_informazioni_attore)
        REFERENCES informazioni_attore(id) DEFERRABLE
);

-- Creazione della tabella privato con chiave esterna
CREATE TABLE privato (
    id UUID PRIMARY KEY,
    id_informazioni_attore UUID NOT NULL,
    data_di_nascita DATE,
    provincia_di_nascita VARCHAR(255),
    citta_di_nascita VARCHAR(255),
    paese_di_nascita VARCHAR(2),
    identificativo_soggetto_1 VARCHAR(255),
    codice_identificativo_soggetto_1 VARCHAR(255),
    codice_proprietario_identificativo_soggetto_1 VARCHAR(255),
    emittente_1 VARCHAR(255),
    identificativo_soggetto_2 VARCHAR(255),
    codice_identificativo_soggetto_2 VARCHAR(255),
    codice_proprietario_identificativo_soggetto_2 VARCHAR(255),
    emittente_2 VARCHAR(255),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_informazioni_attore FOREIGN KEY (id_informazioni_attore)
        REFERENCES informazioni_attore(id) DEFERRABLE
);

-- Creazione della tabella regulatory_reporting con chiave esterna
CREATE TABLE regulatory_reporting (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    tipo VARCHAR(255),
    autorita_richiedente VARCHAR(255),
    paese_autorita_richiedente VARCHAR(2),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);


-- Creazione della tabella dettagli_regulatory_reporting con chiave esterna
CREATE TABLE dettagli_regulatory_reporting (
    id UUID PRIMARY KEY,
    id_regulatory_reporting UUID NOT NULL,
    dettaglio VARCHAR(255),
    data DATE,
    importo NUMERIC(18,2),
    divisa VARCHAR(3),
    paese VARCHAR(2),
    informazioni_aggiuntive TEXT,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_regulatory_reporting FOREIGN KEY (id_regulatory_reporting)
        REFERENCES regulatory_reporting(id) DEFERRABLE
);

-- Creazione del tipo ENUM per la priorità della transazione
CREATE TYPE priorita_transazione AS ENUM (
    'NORMALE',
    'ALTA'
);

-- Creazione della tabella informazioni_aggiuntive_pagamento con chiave esterna
CREATE TABLE informazioni_aggiuntive_pagamento (
    id UUID PRIMARY KEY,
    informazioni_documento_collegato BOOLEAN NOT NULL,
    id_bonifico_extra_sepa UUID NOT NULL,
    istruzioni_banca_ricevente_1 VARCHAR(255),
    istruzioni_banca_ricevente_2 VARCHAR(255),
    istruzioni_banca_ricevente_3 VARCHAR(255),
    istruzioni_banca_ricevente_4 VARCHAR(255),
    istruzioni_banca_ricevente_5 VARCHAR(255),
    istruzioni_banca_ricevente_6 VARCHAR(255),
    codice_istruzione_banca_del_beneficiario_1 VARCHAR(255),
    istruzione_banca_del_beneficiario_1 VARCHAR(255),
    codice_istruzione_banca_del_beneficiario_2 VARCHAR(255),
    istruzione_banca_del_beneficiario_2 VARCHAR(255),
    priorita_transazione priorita_transazione,
    codice_livello_di_servizio VARCHAR(255),
    dettaglio_livello_di_servizio VARCHAR(255),
    classificazione_pagamento VARCHAR(255),
    dettaglio_classificazione_pagamento VARCHAR(255),
    codice_transazione VARCHAR(255),
    dettaglio_identificativo_transazione VARCHAR(255),
    codice_tipo_servizio VARCHAR(255),
    dettaglio_tipo_servizio VARCHAR(255),
    valore_cambio_istruito NUMERIC(18,6),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella riferimenti_aggiuntivi_pagamento con chiavi esterne
CREATE TABLE riferimenti_aggiuntivi_pagamento (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    modalita_avviso_pagamento VARCHAR(255),
    email_destinatario_reporting VARCHAR(255),
    intestazione_destinatario_reporting VARCHAR(255),
    id_indirizzo_postale UUID,

    -- Vincoli di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE,

    CONSTRAINT fk_indirizzo_postale FOREIGN KEY (id_indirizzo_postale)
        REFERENCES indirizzo_postale(id) DEFERRABLE
);

-- Creazione della tabella informazioni_causale con chiavi esterne
CREATE TABLE informazioni_causale (
    id UUID PRIMARY KEY,
    causale_documento_collegato BOOLEAN NOT NULL,
    id_bonifico_extra_sepa UUID NOT NULL,
    causale_descrittiva VARCHAR(255),
    tipo_riferimento_creditore VARCHAR(255),
    descrizione_riferimento_creditore VARCHAR(255),
    emittente_riferimento_creditore VARCHAR(255),
    riferimento_univoco_creditore VARCHAR(255),
    id_attore_emittente_documento UUID,
    id_attore_ricevente_documento UUID,
    ulteriori_informazioni TEXT,

    -- Vincoli di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE,

    CONSTRAINT fk_attore_emittente_documento FOREIGN KEY (id_attore_emittente_documento)
        REFERENCES informazioni_attore(id) DEFERRABLE,

    CONSTRAINT fk_attore_ricevente_documento FOREIGN KEY (id_attore_ricevente_documento)
        REFERENCES informazioni_attore(id) DEFERRABLE
);

-- Creazione del tipo ENUM per rappresentare i tipi di attore fiscale
CREATE TYPE tipo_attore_fiscale AS ENUM (
    'DEBITORE',
    'CREDITORE',
    'DEBITORE_FINALE'
);

-- Creazione della tabella dettagli_fiscali con chiave esterna
CREATE TABLE dettagli_fiscali (
    id UUID PRIMARY KEY,
    id_informazioni_causale UUID NOT NULL,
    amministrazione_di_riferimento VARCHAR(255),
    dettaglio_imposta_riferimento VARCHAR(255),
    metodo VARCHAR(255),
    importo_imponibile NUMERIC(18,2),
    divisa_importo_imponibile VARCHAR(3),
    importo_imposta NUMERIC(18,2),
    divisa_importo_imposta VARCHAR(3),
    scadenza DATE,
    numero_progressivo_dichiarazione NUMERIC(18,2),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
        REFERENCES informazioni_causale(id) DEFERRABLE
);

-- Creazione della tabella attore_fiscale con chiave esterna
CREATE TABLE attore_fiscale (
    id UUID PRIMARY KEY,
    id_dettagli_fiscali UUID NOT NULL,
    tipo_attore_fiscale tipo_attore_fiscale NOT NULL,
    identificativo_fiscale VARCHAR(255),
    identificativo VARCHAR(255),
    tipo_contribuente VARCHAR(255),
    titolo VARCHAR(255),
    intestazione VARCHAR(255),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_dettagli_fiscali FOREIGN KEY (id_dettagli_fiscali)
        REFERENCES dettagli_fiscali(id) DEFERRABLE
);

-- Creazione del tipo ENUM per i dettagli dell'importo nel documento di riferimento
CREATE TYPE tipo_dettaglio_importo_documento_di_riferimento AS ENUM (
    'IMPORTO_DOVUTO',
    'SCONTO',
    'NOTA_DI_CREDITO',
    'TASSE',
    'RETTIFICA',
    'IMPORTO_DISPOSTO'
);

-- Creazione della tabella dettaglio_importo con chiave esterna
CREATE TABLE dettaglio_importo (
    id UUID PRIMARY KEY,
    tipo_dettaglio_importo_documento_di_riferimento tipo_dettaglio_importo_documento_di_riferimento NOT NULL,
    tipo VARCHAR(255),
    dettagli VARCHAR(255),
    importo NUMERIC(18,2),
    divisa VARCHAR(3),
    verso VARCHAR(255),
    motivo VARCHAR(255),
    informazioni_aggiuntive TEXT
);


-- Creazione della tabella informazioni_causale_dettaglio_importo con chiave esterna
CREATE TABLE informazioni_causale_dettaglio_importo (
    id UUID PRIMARY KEY,
    id_informazioni_causale UUID NOT NULL,
    id_dettaglio_importo UUID NOT NULL,
    CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
        REFERENCES informazioni_causale(id) DEFERRABLE,
    CONSTRAINT fk_dettaglio_importo FOREIGN KEY (id_dettaglio_importo)
        REFERENCES dettaglio_importo(id) DEFERRABLE
);


-- Creazione della tabella informazioni_documento_di_riferimento con chiave esterna
CREATE TABLE informazioni_documento_di_riferimento (
    id UUID PRIMARY KEY,
    id_informazioni_causale UUID NOT NULL,
    tipo VARCHAR(255),
    descrizione VARCHAR(255),
    emittente VARCHAR(255),
    numero VARCHAR(255),
    data_documento_di_riferimento DATE,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
        REFERENCES informazioni_causale(id) DEFERRABLE
);

-- Creazione della tabella dettaglio_linea_documento_di_riferimento con chiave esterna
CREATE TABLE dettaglio_linea_documento_di_riferimento (
    id UUID PRIMARY KEY,
    id_informazioni_documento_di_riferimento UUID NOT NULL,
    descrizione_voce VARCHAR(255),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_informazioni_documento_di_riferimento FOREIGN KEY (id_informazioni_documento_di_riferimento)
        REFERENCES informazioni_documento_di_riferimento(id) DEFERRABLE
);

-- Creazione della tabella dettaglio_linea_documento_di_riferimento_dettaglio_importo con chiavi esterne
CREATE TABLE dettaglio_linea_documento_di_riferimento_dettaglio_importo (
    id UUID PRIMARY KEY,
    id_dettaglio_linea UUID NOT NULL,
    id_dettaglio_importo UUID NOT NULL,

    -- Vincoli di chiave esterna
    CONSTRAINT fk_dettaglio_linea FOREIGN KEY (id_dettaglio_linea)
        REFERENCES dettaglio_linea_documento_di_riferimento(id) DEFERRABLE,

    CONSTRAINT fk_dettaglio_importo FOREIGN KEY (id_dettaglio_importo)
        REFERENCES dettaglio_importo(id) DEFERRABLE
);

-- Creazione della tabella dettagli_pignoramento con chiavi esterne
CREATE TABLE dettagli_pignoramento (
    id UUID PRIMARY KEY,
    id_informazioni_causale UUID NOT NULL,
    codice VARCHAR(255),
    codice_proprietario VARCHAR(255),
    emittente VARCHAR(255),
    id_terzo_pignorato UUID,
    id_gestore_pignoramento UUID,
    identificativo_pignoramento VARCHAR(255),
    data_pignoramento DATE,
    importo_pignoramento NUMERIC(18,2),
    divisa_importo_pignoramento VARCHAR(3),
    assicurazione_sanitaria BOOLEAN,
    disoccupato BOOLEAN,

    -- Vincoli di chiave esterna
    CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
        REFERENCES informazioni_causale(id) DEFERRABLE,

    CONSTRAINT fk_terzo_pignorato FOREIGN KEY (id_terzo_pignorato)
        REFERENCES informazioni_attore(id) DEFERRABLE,

    CONSTRAINT fk_gestore_pignoramento FOREIGN KEY (id_gestore_pignoramento)
        REFERENCES informazioni_attore(id) DEFERRABLE
);

-- Creazione della tabella record_dettagli_fiscali con chiave esterna
CREATE TABLE record_dettagli_fiscali (
    id UUID PRIMARY KEY,
    id_dettagli_fiscali UUID NOT NULL,
    codice_tipo VARCHAR(255),
    categoria_tassazione VARCHAR(255),
    dettagli_categoria_tassazione VARCHAR(255),
    status_contribuente_debitore VARCHAR(255),
    identificativo_dichiarazione VARCHAR(255),
    codice_modello_dichiarazione VARCHAR(255),
    anno_riferimento_dichiarazione VARCHAR(255),
    periodo_riferimento_dichiarazione VARCHAR(255),
    periodo_riferimento_dichiarazione_da DATE,
    periodo_riferimento_dichiarazione_a DATE,
    percentuale_imposta NUMERIC(5,2),
    importo_imponibile NUMERIC(18,2),
    divisa_importo_imponibile VARCHAR(3),
    importo_imposta NUMERIC(18,2),
    divisa_importo_imposta VARCHAR(3),
    informazioni_aggiuntive TEXT,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_dettagli_fiscali FOREIGN KEY (id_dettagli_fiscali)
        REFERENCES dettagli_fiscali(id) DEFERRABLE
);

-- Creazione della tabella dettagli_record_dettagli_fiscali con chiave esterna
CREATE TABLE dettagli_record_dettagli_fiscali (
    id UUID PRIMARY KEY,
    id_record_dettagli_fiscali UUID NOT NULL,
    anno_riferimento VARCHAR(255),
    periodo_riferimento VARCHAR(255),
    periodo_riferimento_da DATE,
    periodo_riferimento_a DATE,
    importo NUMERIC(18,2),
    divisa VARCHAR(3),

    -- Vincolo di chiave esterna
    CONSTRAINT fk_record_dettagli_fiscali FOREIGN KEY (id_record_dettagli_fiscali)
        REFERENCES record_dettagli_fiscali(id) DEFERRABLE
);

-- Creazione della tabella identificativo_linea_documento con chiave esterna
CREATE TABLE identificativo_linea_documento (
    id UUID PRIMARY KEY,
    id_dettaglio_linea_documento_di_riferimento UUID NOT NULL,
    codice_voce VARCHAR(255),
    codice_proprietario_voce VARCHAR(255),
    emittente VARCHAR(255),
    numero VARCHAR(255),
    data DATE,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_dettaglio_linea_documento_di_riferimento FOREIGN KEY (id_dettaglio_linea_documento_di_riferimento)
        REFERENCES dettaglio_linea_documento_di_riferimento(id) DEFERRABLE
);

-- Creazione del tipo ENUM per i livelli di priorità del regolamento
CREATE TYPE priorita AS ENUM (
    'URGENTE',
    'ALTA',
    'NORMALE'
);

-- Creazione della tabella informazioni_sistema_di_regolamento
CREATE TABLE informazioni_sistema_di_regolamento (
    id UUID PRIMARY KEY,
    informazioni_documento_collegato BOOLEAN NOT NULL,
    id_bonifico_extra_sepa UUID NOT NULL,
    priorita VARCHAR(255),
    orario_accredito TIMESTAMP WITH TIME ZONE,
    da TIMESTAMP WITH TIME ZONE,
    a TIMESTAMP WITH TIME ZONE,
    scadenza_ultima TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    stp BOOLEAN NOT NULL,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella bonifico_extra_sepa_info_stato_inserito con chiave esterna
CREATE TABLE bonifico_extra_sepa_info_stato_inserito (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    outcome VARCHAR(255),
    codice_risultato VARCHAR(255),
    id_richiesta INTEGER,
    disponibile BOOLEAN,
    saldo NUMERIC(18,2),
    embargo BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella storia_stati_bonifico_extra_sepa con chiave esterna
CREATE TABLE storia_stati_bonifico_extra_sepa (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    stato_attuale VARCHAR(255) NOT NULL,
    nuovo_stato VARCHAR(255) NOT NULL,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

-- Creazione della tabella bonifico_extra_sepa_info_stato_errore con chiave esterna
CREATE TABLE bonifico_extra_sepa_info_stato_errore (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    codice_errore VARCHAR(255),
    descrizione_errore TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Vincolo di chiave esterna
    CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
        REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

CREATE TYPE autorizzazione_action_enum AS ENUM (
    'AUTORIZZAZIONE',
    'MODIFICA_DATA_REGOLAMENTO_BANCA'
);

CREATE TABLE autorizzazione (
    id UUID PRIMARY KEY,
    id_bonifico_extra_sepa UUID NOT NULL,
    utente VARCHAR(255) NOT NULL,
    nome_utente VARCHAR(255) NOT NULL,
    ruolo VARCHAR(255) NOT NULL,
    profilo VARCHAR(255) NOT NULL,
    azione autorizzazione_action_enum NOT NULL,
    livello_autorizzazione INT NOT NULL,
    data_di_regolamento_precedente DATE,
    autorizzazione_messaggio BOOLEAN,
    autorizzazione_notifica BOOLEAN,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Vincolo di chiave esterna
        CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
            REFERENCES bonifico_extra_sepa(id) DEFERRABLE
);

CREATE TABLE configurazioni_autorizzative (
    id UUID PRIMARY KEY,
    id_sotto_tipologia_bonifico UUID NOT NULL,
    id_canale UUID NOT NULL,
    livelli_di_autorizzazione INT NOT NULL,
    livello_di_autorizzazione_di_default INT NOT NULL,

    -- Vincolo di chiave esterna
            CONSTRAINT fk_sotto_tipologia_bonifico FOREIGN KEY (id_sotto_tipologia_bonifico)
                REFERENCES sotto_tipologia_bonifico(id) DEFERRABLE,
            CONSTRAINT fk_canale FOREIGN KEY (id_canale)
                            REFERENCES canale(id) DEFERRABLE
);

CREATE TABLE mappatura_livelli_autorizzativi (
    id UUID PRIMARY KEY,
    ruolo VARCHAR(255) NOT NULL,
    livello INT NOT NULL
);