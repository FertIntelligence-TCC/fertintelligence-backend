ALTER TABLE EXTRATOS_ANALISES_FERTILIDADE
    ADD COLUMN IF NOT EXISTS UNIDADE_CALCIO VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_MAGNESIO VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_POTASSIO VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_SODIO VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_ALUMINIO VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_ALUMINIO_MAIS_HIDROGENIO VARCHAR(40) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_SOMA_BASES VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_CTC_EFETIVA VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³',
    ADD COLUMN IF NOT EXISTS UNIDADE_CTC_PH_7 VARCHAR(20) NOT NULL DEFAULT 'mmolc/dm³';

-- Esta migration padroniza somente o rótulo/unidade persistida dos campos do complexo de troca.
-- O repositório não traz uma evidência confiável do padrão dos valores já gravados no banco real:
-- modelos e engine já tratavam os campos como mmolc/dm3, enquanto alguns comentários legados citavam cmolc/dm3.
-- Por isso, não há conversão automática de valores aqui; conversão por fator 10 deve ser feita apenas após auditoria
-- dos dados produtivos confirmar que os números armazenados estão em cmolc/dm³ reais.
UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_CALCIO = 'mmolc/dm³'
WHERE UNIDADE_CALCIO IS NULL
   OR LOWER(REPLACE(UNIDADE_CALCIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_MAGNESIO = 'mmolc/dm³'
WHERE UNIDADE_MAGNESIO IS NULL
   OR LOWER(REPLACE(UNIDADE_MAGNESIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_POTASSIO = 'mmolc/dm³'
WHERE UNIDADE_POTASSIO IS NULL
   OR LOWER(REPLACE(UNIDADE_POTASSIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_SODIO = 'mmolc/dm³'
WHERE UNIDADE_SODIO IS NULL
   OR LOWER(REPLACE(UNIDADE_SODIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_ALUMINIO = 'mmolc/dm³'
WHERE UNIDADE_ALUMINIO IS NULL
   OR LOWER(REPLACE(UNIDADE_ALUMINIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_ALUMINIO_MAIS_HIDROGENIO = 'mmolc/dm³'
WHERE UNIDADE_ALUMINIO_MAIS_HIDROGENIO IS NULL
   OR LOWER(REPLACE(UNIDADE_ALUMINIO_MAIS_HIDROGENIO, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_SOMA_BASES = 'mmolc/dm³'
WHERE UNIDADE_SOMA_BASES IS NULL
   OR LOWER(REPLACE(UNIDADE_SOMA_BASES, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_CTC_EFETIVA = 'mmolc/dm³'
WHERE UNIDADE_CTC_EFETIVA IS NULL
   OR LOWER(REPLACE(UNIDADE_CTC_EFETIVA, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');

UPDATE EXTRATOS_ANALISES_FERTILIDADE
SET UNIDADE_CTC_PH_7 = 'mmolc/dm³'
WHERE UNIDADE_CTC_PH_7 IS NULL
   OR LOWER(REPLACE(UNIDADE_CTC_PH_7, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3');
