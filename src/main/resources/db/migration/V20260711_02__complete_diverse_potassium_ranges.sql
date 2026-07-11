-- Completa o contrato de oito limites de Potassio em Teores de Nutrientes Diversos.
-- As quatro colunas introduzidas por V20260711_01 sao preservadas.
ALTER TABLE faixas_de_teores_diversos
    ADD COLUMN IF NOT EXISTS menor_teor_potassio DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS teor_inicial_baixo_potassio DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS teor_final_alto_potassio DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS maior_teor_potassio DOUBLE PRECISION;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_menor_teor_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET menor_teor_potassio =
                     COALESCE(menor_teor_potassio, legacy_menor_teor_potassio)';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_inicial_baixo_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_inicial_baixo_potassio =
                     COALESCE(teor_inicial_baixo_potassio, legacy_teor_inicial_baixo_potassio)';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_final_alto_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_final_alto_potassio =
                     COALESCE(teor_final_alto_potassio, legacy_teor_final_alto_potassio)';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_maior_teor_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET maior_teor_potassio =
                     COALESCE(maior_teor_potassio, legacy_maior_teor_potassio)';
    END IF;
END
$$;
