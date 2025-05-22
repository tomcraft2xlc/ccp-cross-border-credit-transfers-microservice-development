ALTER TABLE dettaglio_bonifico_account_to_account
ADD COLUMN regolamento_commissioni_clientela regolamento_commissione,
ADD COLUMN regolamento_commissioni_banca regolamento_commissione,
ADD COLUMN tipologia_commissioni tipologia_commissioni;

UPDATE dettaglio_bonifico_account_to_account AS dbata
SET
    regolamento_commissioni_clientela = COALESCE(cata.regolamento_commissione, 'EURO'),
    regolamento_commissioni_banca = COALESCE(cbab.regolamento, 'EURO'),
    tipologia_commissioni = COALESCE(cbab.tipologia, 'SHARED')
FROM
    commissione_account_to_account cata,
    commissione_banca_a_banca cbab
WHERE
    dbata.id_bonifico_extra_sepa = cata.id_bonifico_extra_sepa AND
    dbata.id_bonifico_extra_sepa = cbab.id_bonifico_extra_sepa;

UPDATE dettaglio_bonifico_account_to_account
SET
    regolamento_commissioni_clientela = 'EURO'
WHERE
    regolamento_commissioni_clientela IS NULL;
UPDATE dettaglio_bonifico_account_to_account
SET
    regolamento_commissioni_banca = 'EURO'
WHERE
    regolamento_commissioni_banca IS NULL;

UPDATE dettaglio_bonifico_account_to_account
SET
    tipologia_commissioni = 'SHARED'
WHERE
    tipologia_commissioni IS NULL;


ALTER TABLE dettaglio_bonifico_account_to_account
ALTER COLUMN regolamento_commissioni_clientela SET NOT NULL,
ALTER COLUMN regolamento_commissioni_banca SET NOT NULL,
ALTER COLUMN tipologia_commissioni SET NOT NULL;

ALTER TABLE dettaglio_bonifico_banca_a_banca
ADD COLUMN regolamento_commissioni_banca regolamento_commissione;


UPDATE dettaglio_bonifico_banca_a_banca AS dbbab
SET regolamento_commissioni_banca = COALESCE(cbab.regolamento, 'EURO')
FROM
    commissione_banca_a_banca cbab
WHERE
    dbbab.id_bonifico_extra_sepa = cbab.id_bonifico_extra_sepa;

UPDATE dettaglio_bonifico_banca_a_banca
SET regolamento_commissioni_banca = 'EURO'
WHERE
    regolamento_commissioni_banca IS NULL;

ALTER TABLE dettaglio_bonifico_banca_a_banca
ALTER COLUMN regolamento_commissioni_banca SET NOT NULL;

ALTER TABLE commissione_account_to_account
DROP COLUMN regolamento_commissione;

ALTER TABLE commissione_account_to_account
ADD COLUMN codice VARCHAR(50);

UPDATE commissione_account_to_account
SET codice = 'not-provided';

ALTER TABLE commissione_account_to_account
ALTER COLUMN codice SET NOT NULL;

ALTER TABLE commissione_banca_a_banca
DROP COLUMN tipologia,
DROP COLUMN regolamento;

ALTER TABLE commissione_banca_a_banca
ADD COLUMN codice VARCHAR(50);

UPDATE commissione_banca_a_banca
SET codice = 'not-provided';

ALTER TABLE commissione_banca_a_banca
ALTER COLUMN codice SET NOT NULL;
