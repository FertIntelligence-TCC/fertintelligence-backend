-- Potassio em Teores de Nutrientes Diversos: quatro limites independentes,
-- compativeis com o classificador Baixo/Medio/Alto.
ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS
    ADD COLUMN IF NOT EXISTS TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION;

-- Restaura somente limites semanticamente identicos preservados pela migration
-- V20260703_06; instalacoes sem valores anteriores permanecem com valores nulos.
-- O SQL e dinamico porque as colunas LEGACY_* podem nao existir no schema.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_final_baixo_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_final_baixo_potassio =
                     COALESCE(teor_final_baixo_potassio, legacy_teor_final_baixo_potassio)';
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_inicial_medio_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_inicial_medio_potassio =
                     COALESCE(teor_inicial_medio_potassio, legacy_teor_inicial_medio_potassio)';
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_final_medio_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_final_medio_potassio =
                     COALESCE(teor_final_medio_potassio, legacy_teor_final_medio_potassio)';
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'faixas_de_teores_diversos'
          AND column_name = 'legacy_teor_inicial_alto_potassio'
    ) THEN
        EXECUTE 'UPDATE faixas_de_teores_diversos
                 SET teor_inicial_alto_potassio =
                     COALESCE(teor_inicial_alto_potassio, legacy_teor_inicial_alto_potassio)';
    END IF;
END
$$;
