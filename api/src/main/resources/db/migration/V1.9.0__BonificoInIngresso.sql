CREATE TABLE mappatura_bonifico_in_ingresso (
    id uuid PRIMARY KEY NOT NULL,
    namespace VARCHAR(255) NOT NULL,
    classe_qualificata_nome_completo TEXT NOT NULL
);

INSERT INTO mappatura_bonifico_in_ingresso (id, namespace, classe_qualificata_nome_completo) VALUES
(gen_random_uuid(), 'urn:iso:std:iso:20022:tech:xsd:pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.accrediti.Pacs009AccreditoMapper'),
(gen_random_uuid(), 'urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.accrediti.Pacs008AccreditoMapper');

-- bonifico_in_ingresso definition
CREATE TABLE bonifico_in_ingresso (
	id uuid PRIMARY KEY NOT NULL,
	id_mappatura_bonifico_in_ingresso uuid NOT NULL,
	id_bonifico_collegato uuid NULL,
	is_banca_a_banca BOOLEAN NULL,
	raw_xml TEXT NULL,
	tid varchar(255) NULL,
	uetr uuid NULL,
	bic_ordinante varchar(11) NULL,
	data_ricezione timestamptz NULL,
	sotto_tipologia_bonifico varchar(255) NULL,
	sistema_di_regolamento sistema_di_regolamento NULL,
	divisa varchar(3) NULL,
	cambio numeric(18, 2) NULL,
	importo numeric(18, 2) NULL,
	intestazione_beneficiario varchar(255) NULL,
	numero_rapporto_beneficiario varchar(255) NULL,
	bic_beneficiario varchar(11) NULL,
	bic_banca_emittente varchar(11) NULL,
	data_regolamento_banca_beneficiario date NULL,
	codice_filiale int4 NULL,
	intestazione_ordinante varchar(255) NULL,
	numero_rapporto_ordinante varchar(255) NULL,
	id_rapporto_di_copertura uuid NULL,
	id_rapporto_beneficiario uuid NULL,
	stato varchar(255) NULL,
	codice_causale_transazione varchar(255) NULL,
	tipologia_commissioni_banca tipologia_commissioni NULL,
    regolamento_commissioni_banca regolamento_commissione NULL,
    regolamento_commissioni_clientela regolamento_commissione NULL,
	created_at timestamptz NOT NULL,
	CONSTRAINT id_mappatura_bonifico_in_ingresso_fk FOREIGN KEY (id_mappatura_bonifico_in_ingresso) REFERENCES mappatura_bonifico_in_ingresso(id) DEFERRABLE,
	CONSTRAINT bonifico_in_ingresso_bonifico_in_ingresso_fk FOREIGN KEY (id_bonifico_collegato) REFERENCES bonifico_in_ingresso(id) DEFERRABLE,
	CONSTRAINT bonifico_in_ingresso_informazioni_rapporto_copertura_fk FOREIGN KEY (id_rapporto_di_copertura) REFERENCES informazioni_rapporto(id) DEFERRABLE,
	CONSTRAINT bonifico_in_ingresso_informazioni_rapporto_beneficiario_fk FOREIGN KEY (id_rapporto_beneficiario) REFERENCES informazioni_rapporto(id) DEFERRABLE
);

-- Tabella di contorno per le commissioni clientela
CREATE TABLE bonifico_in_ingresso_commissione_clientela (
    id uuid PRIMARY KEY NOT NULL,
    id_bonifico_in_ingresso uuid NOT NULL,
	codice varchar(255) NULL,
	descrizione varchar(255) NULL,
	importo numeric(18, 2) NULL,
	percentuale numeric(5, 2) NULL,
	max numeric(18, 2) NULL,
	min numeric(18, 2) NULL,
	divisa varchar(3) NULL,
	CONSTRAINT fk_bonifico_in_ingresso FOREIGN KEY (id_bonifico_in_ingresso) REFERENCES bonifico_in_ingresso(id) DEFERRABLE
);

-- Tabella di contorno per le commissioni banca
CREATE TABLE bonifico_in_ingresso_commissione_banca (
    id uuid PRIMARY KEY NOT NULL,
    id_bonifico_in_ingresso uuid NOT NULL,
	descrizione varchar(255) NULL,
	divisa varchar(3) NULL,
	importo numeric(18, 2) NULL,
	CONSTRAINT fk_bonifico_in_ingresso FOREIGN KEY (id_bonifico_in_ingresso) REFERENCES bonifico_in_ingresso(id) DEFERRABLE
);