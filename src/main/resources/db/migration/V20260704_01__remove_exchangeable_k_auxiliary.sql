ALTER TABLE IF EXISTS faixas_de_teores_diversos
    DROP COLUMN IF EXISTS menor_teor_potassio,
    DROP COLUMN IF EXISTS teor_inicial_baixo_potassio,
    DROP COLUMN IF EXISTS teor_final_baixo_potassio,
    DROP COLUMN IF EXISTS teor_inicial_medio_potassio,
    DROP COLUMN IF EXISTS teor_final_medio_potassio,
    DROP COLUMN IF EXISTS teor_inicial_alto_potassio,
    DROP COLUMN IF EXISTS teor_final_alto_potassio,
    DROP COLUMN IF EXISTS maior_teor_potassio;

DROP TABLE IF EXISTS teores_trocaveis_de_potassio;
