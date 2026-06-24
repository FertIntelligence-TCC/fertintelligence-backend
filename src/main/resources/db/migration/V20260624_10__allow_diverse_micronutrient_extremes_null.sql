ALTER TABLE IF EXISTS faixas_de_teores_diversos
    ALTER COLUMN menor_teor_boro DROP NOT NULL,
    ALTER COLUMN maior_teor_boro DROP NOT NULL,
    ALTER COLUMN menor_teor_cobre DROP NOT NULL,
    ALTER COLUMN maior_teor_cobre DROP NOT NULL,
    ALTER COLUMN menor_teor_ferro DROP NOT NULL,
    ALTER COLUMN maior_teor_ferro DROP NOT NULL,
    ALTER COLUMN menor_teor_manganes DROP NOT NULL,
    ALTER COLUMN maior_teor_manganes DROP NOT NULL,
    ALTER COLUMN menor_teor_zinco DROP NOT NULL,
    ALTER COLUMN maior_teor_zinco DROP NOT NULL;
