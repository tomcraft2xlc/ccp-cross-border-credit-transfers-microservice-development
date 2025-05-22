DO $$
DECLARE
canaleCCP UUID;
-- id canali codici da comunicare --
-- START --
canaleDELEGE UUID;
canaleCashPoolingAutoSweeping UUID;
canaleBOEsteroAS400 UUID;
-- END --

-- id sottotipologie bonifici --
-- START --
tipologiaPACS008 UUID;
tipologiaPACS009COV UUID;
tipologiaPACS008EPACS009COV UUID;

tipologiaPACS009 UUID;
-- END --
BEGIN
canaleCCP := gen_random_uuid();
canaleDELEGE := gen_random_uuid();
canaleCashPoolingAutoSweeping := gen_random_uuid();
canaleBOEsteroAS400 := gen_random_uuid();
tipologiaPACS008 := gen_random_uuid();
tipologiaPACS009COV := gen_random_uuid();
tipologiaPACS008EPACS009COV := gen_random_uuid();
tipologiaPACS009 := gen_random_uuid();
INSERT INTO canale
(id, id_canale, utente_richiesto)
VALUES
(canaleCCP, 'ccp', false),
(canaleDELEGE, 'deleghe', true),
(canaleCashPoolingAutoSweeping, 'cash_pooling', true),
(canaleBOEsteroAS400, 'bo_estero_as_400', true);

INSERT INTO sotto_tipologia_bonifico
(id, nome, descrizione, produci_mt999, campi_dto_obbligatori, banca_a_banca, con_notifica)
VALUES
-- pacs 008
(tipologiaPACS008, 'pacs008', 'Pacs.008', false, null, false, false),
(tipologiaPACS009COV, 'pacs009cov', 'Pacs.009 COV', false, null, false, false),
(tipologiaPACS008EPACS009COV, 'pacs008_pacs009cov', 'Pacs.008 con Pacs.009 COV', false, null, false, false),
(tipologiaPACS009, 'pacs009', 'Pacs.009', false, null, true, false);


INSERT INTO bonifico_extra_sepa_lista_canali_abilitati
(id, id_sottotipo_bonifico_extra_sepa, id_canale, stato_default)
VALUES
(gen_random_uuid(), tipologiaPACS008, canaleCCP, 'INSERITO'),
(gen_random_uuid(), tipologiaPACS009, canaleCCP, 'INSERITO'),

(gen_random_uuid(), tipologiaPACS008, canaleDELEGE, 'DA_AUTORIZZARE'),
(gen_random_uuid(), tipologiaPACS009COV, canaleCashPoolingAutoSweeping, 'AUTORIZZATO'),
(gen_random_uuid(), tipologiaPACS008EPACS009COV, canaleBOEsteroAS400, 'DA_AUTORIZZARE'),
(gen_random_uuid(), tipologiaPACS009, canaleBOEsteroAS400, 'DA_AUTORIZZARE'),
(gen_random_uuid(), tipologiaPACS008, canaleBOEsteroAS400, 'DA_AUTORIZZARE');


INSERT INTO sotto_tipologia_bonifico_mappatura
(id, id_sotto_tipologia_bonifico, id_messaggio, mappatura_classe_qualificata)
VALUES
(gen_random_uuid(), tipologiaPACS008, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),

(gen_random_uuid(), tipologiaPACS008EPACS009COV, 'pacs.008.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs008ISOMapper'),
(gen_random_uuid(), tipologiaPACS008EPACS009COV, 'pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009COVISOMapper'),

(gen_random_uuid(), tipologiaPACS009, 'pacs.009.001.08', 'com.flowpay.ccp.credit.transfer.cross.border.mappers.Pacs009ISOMapper');


-- livelli autorizzativi di default
INSERT INTO configurazioni_autorizzative
(id, id_sotto_tipologia_bonifico, id_canale, livelli_di_autorizzazione, livello_di_autorizzazione_di_default)
VALUES
(gen_random_uuid(), tipologiaPACS008, canaleCCP, 2, 0),
(gen_random_uuid(), tipologiaPACS009, canaleCCP, 2, 0),

(gen_random_uuid(), tipologiaPACS008, canaleDELEGE, 2, 1),

(gen_random_uuid(), tipologiaPACS008EPACS009COV, canaleBOEsteroAS400, 2, 0),
(gen_random_uuid(), tipologiaPACS009, canaleBOEsteroAS400, 2, 0),
(gen_random_uuid(), tipologiaPACS008, canaleBOEsteroAS400, 2, 0);


INSERT INTO mappatura_livelli_autorizzativi
(id, ruolo, livello)
VALUES
(gen_random_uuid(), 'base', 1),
(gen_random_uuid(), 'base', 2),
(gen_random_uuid(), 'base', 3),
(gen_random_uuid(), 'autorizzatore_master', 1),
(gen_random_uuid(), 'autorizzatore_master', 2),
(gen_random_uuid(), 'autorizzatore_master', 3);

INSERT INTO bic_banca_destinataria
(id, bic)
VALUES
(gen_random_uuid(), 'BCITITMMXXX');
END $$;