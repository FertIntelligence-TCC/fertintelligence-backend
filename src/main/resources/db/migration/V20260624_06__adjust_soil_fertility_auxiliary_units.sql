ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_resina_troca_anionica
    ALTER COLUMN unidade SET DEFAULT 'mg/dm³';

UPDATE fosforo_disponivel_com_extrator_resina_troca_anionica
SET unidade = 'mg/dm³'
WHERE unidade IS NULL
   OR LOWER(REPLACE(unidade, '³', '3')) IN ('g/dm3', 'mg/dm3', 'mg_per_dm3');

ALTER TABLE IF EXISTS teores_trocaveis_de_potassio
    ADD COLUMN IF NOT EXISTS unidade VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³';

UPDATE teores_trocaveis_de_potassio
SET unidade = 'mmolc/dm³'
WHERE unidade IS NULL
   OR LOWER(REPLACE(unidade, '³', '3')) IN ('mg/dm3', 'cmolc/dm3', 'mmolc/dm3', 'mmolc_per_dm3');

ALTER TABLE IF EXISTS criterios_de_interpretacao_da_salinidade_do_solo
    ADD COLUMN IF NOT EXISTS unidade_ras VARCHAR(30) NOT NULL DEFAULT '(mmolc)**0.5';

UPDATE criterios_de_interpretacao_da_salinidade_do_solo
SET unidade_ras = '(mmolc)**0.5'
WHERE unidade_ras IS NULL
   OR LOWER(REPLACE(REPLACE(unidade_ras, ' ', ''), '^', '**')) IN (
        '(mmolc)**0.5',
        'mmolc**0.5',
        'mmolc/mmolc**0.5'
   );

ALTER TABLE IF EXISTS faixas_de_teores_diversos
    ALTER COLUMN unidade_carbono_organico SET DEFAULT 'g/dm³',
    ALTER COLUMN unidade_materia_organica SET DEFAULT 'g/dm³';

UPDATE faixas_de_teores_diversos
SET unidade_carbono_organico = 'g/dm³'
WHERE unidade_carbono_organico IS NULL
   OR LOWER(REPLACE(unidade_carbono_organico, '³', '3')) = 'g/dm3';

UPDATE faixas_de_teores_diversos
SET unidade_materia_organica = 'g/dm³'
WHERE unidade_materia_organica IS NULL
   OR LOWER(REPLACE(unidade_materia_organica, '³', '3')) = 'g/dm3';

ALTER TABLE IF EXISTS sodio_trocavel
    ADD COLUMN IF NOT EXISTS unidade_sodio VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS unidade_ctc VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³';

UPDATE sodio_trocavel
SET unidade_sodio = 'mmolc/dm³'
WHERE unidade_sodio IS NULL
   OR LOWER(REPLACE(unidade_sodio, '³', '3')) IN ('cmolc/dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE sodio_trocavel
SET unidade_ctc = 'mmolc/dm³'
WHERE unidade_ctc IS NULL
   OR LOWER(REPLACE(unidade_ctc, '³', '3')) IN ('cmolc/dm3', 'mmolc/dm3', 'mmolc_per_dm3');

-- As colunas legadas continuam com nomes CTC_4_3/8_6/15_0 por compatibilidade,
-- mas a engine e o DTO passam a interpretar os limites em mmolc/dm³:
-- < 43, 43 a 86, 87 a 150 e > 150.
