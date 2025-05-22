DO $$
DECLARE
canaleCCP UUID;
canaleAPI UUID;

tipologiaPACS008 UUID;
tipologiaPACS008009COV UUID;
tipologiaPACS008009COVMT999 UUID;
tipologiaPACS008MT999 UUID;

tipologiaPACS9 UUID;
tipologiaPACS9CAMT57 UUID;
tipologiaPACS9CAMT54CREDT UUID;
tipologiaPACS9CAMT54DEBT UUID;
BEGIN
canaleCCP := gen_random_uuid();
canaleAPI := gen_random_uuid();
tipologiaPACS008 := gen_random_uuid();
tipologiaPACS008009COV := gen_random_uuid();
tipologiaPACS008009COVMT999 := gen_random_uuid();
tipologiaPACS008MT999 := gen_random_uuid();
tipologiaPACS9 := gen_random_uuid();
tipologiaPACS9CAMT57 := gen_random_uuid();
tipologiaPACS9CAMT54CREDT := gen_random_uuid();
tipologiaPACS9CAMT54DEBT := gen_random_uuid();

INSERT INTO canale
(id, id_canale, utente_richiesto)
VALUES
(canaleCCP, 'ccp', false),
(canaleAPI, 'API', true);

INSERT INTO sotto_tipologia_bonifico
(id, nome, descrizione, produci_mt999, campi_dto_obbligatori, banca_a_banca, con_notifica)
VALUES
(tipologiaPACS008, 'pacs008', 'Pacs.008', false, null, false, false),
(tipologiaPACS008009COV, 'pacs008_pacs009cov', 'Pacs.008 con Pacs.009 COV', false, null, false, true),
(tipologiaPACS008009COVMT999, 'pacs008_pacs009cov_mt999', 'Pacs.008 con Pacs.009 COV e MT999', true, null, false, true),
(tipologiaPACS008MT999, 'pacs008_mt999', 'Pacs.008 con MT999', true, null, false, false),
(tipologiaPACS9, 'pacs009', 'Pacs.009', false, null, true, false),
(tipologiaPACS9CAMT57, 'pacs009_camt57', 'Pacs.009 con Camt.057', false, null, true, true),
(tipologiaPACS9CAMT54CREDT, 'pacs009_camt54credit', 'Pacs.009 con Camt.054 (Credit)', false, null, true, true),
(tipologiaPACS9CAMT54DEBT, 'pacs009_camt54debit', 'Pacs.009 con Camt.054 (Debit)', false, null, true, true);


INSERT INTO bonifico_extra_sepa_lista_canali_abilitati
(id, id_sottotipo_bonifico_extra_sepa, id_canale, stato_default)
VALUES
(gen_random_uuid(), tipologiaPACS008, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS008009COV, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS008009COVMT999, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS008MT999, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS9, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS9CAMT57, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS9CAMT54CREDT, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS9CAMT54DEBT, canaleCCP, 'INSERITO');

INSERT INTO sotto_tipologia_bonifico_mappatura
(id, id_sotto_tipologia_bonifico, id_messaggio, mappatura_classe_qualificata)
VALUES
(gen_random_uuid(), tipologiaPACS008, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),

(gen_random_uuid(), tipologiaPACS008009COV, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),
(gen_random_uuid(), tipologiaPACS008009COV, 'pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009COVISOMapper'),

(gen_random_uuid(), tipologiaPACS008009COVMT999, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),
(gen_random_uuid(), tipologiaPACS008009COVMT999, 'pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009COVISOMapper'),

(gen_random_uuid(), tipologiaPACS008MT999, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),

(gen_random_uuid(), tipologiaPACS9, 'pacs.009.001.09', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009ISOMapper'),

(gen_random_uuid(), tipologiaPACS9CAMT57, 'pacs.009.001.09', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009ISOMapper'),
-- manca l'implementazione di mappatura camt
(gen_random_uuid(), tipologiaPACS9CAMT54CREDT, 'pacs.009.001.09', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009ISOMapper'),
-- manca l'implementazione di mappatura camt
(gen_random_uuid(), tipologiaPACS9CAMT54DEBT, 'pacs.009.001.09', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009ISOMapper');
-- manca l'implementazione di mappatura camt


INSERT INTO configurazioni_autorizzative
(id, id_sotto_tipologia_bonifico, id_canale, livelli_di_autorizzazione, livello_di_autorizzazione_di_default)
VALUES
(gen_random_uuid(), tipologiaPACS008, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS008009COV, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS008009COVMT999, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS008MT999, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS9, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS9CAMT57, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS9CAMT54CREDT, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS9CAMT54DEBT, canaleCCP, 2, 0);


INSERT INTO mappatura_livelli_autorizzativi
(id, ruolo, livello)
VALUES
(gen_random_uuid(), 'base', 1),
(gen_random_uuid(), 'base', 2),
(gen_random_uuid(), 'base', 3),
(gen_random_uuid(), 'autorizzatore_master', 1),
(gen_random_uuid(), 'autorizzatore_master', 2),
(gen_random_uuid(), 'autorizzatore_master', 3);
END $$;