ALTER TABLE faixas_de_teores_diversos
    ADD COLUMN IF NOT EXISTS unidade_carbono_organico VARCHAR(20) NOT NULL DEFAULT 'g/dm3';

ALTER TABLE faixas_de_teores_diversos
    ADD COLUMN IF NOT EXISTS unidade_materia_organica VARCHAR(20) NOT NULL DEFAULT 'g/dm3';

UPDATE faixas_de_teores_diversos
SET unidade_carbono_organico = 'g/dm3',
    unidade_materia_organica = 'g/dm3';
