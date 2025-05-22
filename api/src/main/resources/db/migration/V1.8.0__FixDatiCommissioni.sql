ALTER TABLE dettaglio_bonifico_account_to_account
ALTER COLUMN regolamento_commissioni_clientela DROP NOT NULL,
ALTER COLUMN regolamento_commissioni_banca DROP NOT NULL,
ALTER COLUMN tipologia_commissioni DROP NOT NULL;

ALTER TABLE dettaglio_bonifico_banca_a_banca
ALTER COLUMN regolamento_commissioni_banca DROP NOT NULL;