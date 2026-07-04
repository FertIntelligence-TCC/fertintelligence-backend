-- Remove do modelo ativo as estruturas antigas de potassio em mmolc/dm3.
-- Os dados legados sao preservados para auditoria em colunas/tabela LEGACY_*.

DO $$
BEGIN
    IF to_regclass('public.teores_trocaveis_de_potassio') IS NOT NULL
       AND to_regclass('public.legacy_teores_trocaveis_de_potassio') IS NULL THEN
        ALTER TABLE TEORES_TROCAVEIS_DE_POTASSIO
            RENAME TO LEGACY_TEORES_TROCAVEIS_DE_POTASSIO;
    ELSIF to_regclass('public.teores_trocaveis_de_potassio') IS NOT NULL THEN
        INSERT INTO LEGACY_TEORES_TROCAVEIS_DE_POTASSIO
        SELECT *
        FROM TEORES_TROCAVEIS_DE_POTASSIO active_k
        WHERE NOT EXISTS (
            SELECT 1
            FROM LEGACY_TEORES_TROCAVEIS_DE_POTASSIO legacy_k
            WHERE legacy_k.ID = active_k.ID
        );

        DROP TABLE TEORES_TROCAVEIS_DE_POTASSIO;
    END IF;
END $$;

ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS
    ADD COLUMN IF NOT EXISTS LEGACY_MENOR_TEOR_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_INICIAL_BAIXO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_TEOR_FINAL_ALTO_POTASSIO DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_MAIOR_TEOR_POTASSIO DOUBLE PRECISION;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'menor_teor_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_MENOR_TEOR_POTASSIO = MENOR_TEOR_POTASSIO
        WHERE LEGACY_MENOR_TEOR_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_inicial_baixo_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_INICIAL_BAIXO_POTASSIO = TEOR_INICIAL_BAIXO_POTASSIO
        WHERE LEGACY_TEOR_INICIAL_BAIXO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_final_baixo_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_FINAL_BAIXO_POTASSIO = TEOR_FINAL_BAIXO_POTASSIO
        WHERE LEGACY_TEOR_FINAL_BAIXO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_inicial_medio_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_INICIAL_MEDIO_POTASSIO = TEOR_INICIAL_MEDIO_POTASSIO
        WHERE LEGACY_TEOR_INICIAL_MEDIO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_final_medio_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_FINAL_MEDIO_POTASSIO = TEOR_FINAL_MEDIO_POTASSIO
        WHERE LEGACY_TEOR_FINAL_MEDIO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_inicial_alto_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_INICIAL_ALTO_POTASSIO = TEOR_INICIAL_ALTO_POTASSIO
        WHERE LEGACY_TEOR_INICIAL_ALTO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'teor_final_alto_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_TEOR_FINAL_ALTO_POTASSIO = TEOR_FINAL_ALTO_POTASSIO
        WHERE LEGACY_TEOR_FINAL_ALTO_POTASSIO IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'faixas_de_teores_diversos'
          AND column_name = 'maior_teor_potassio'
    ) THEN
        UPDATE FAIXAS_DE_TEORES_DIVERSOS
        SET LEGACY_MAIOR_TEOR_POTASSIO = MAIOR_TEOR_POTASSIO
        WHERE LEGACY_MAIOR_TEOR_POTASSIO IS NULL;
    END IF;
END $$;

ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS
    DROP COLUMN IF EXISTS MENOR_TEOR_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_INICIAL_BAIXO_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_FINAL_BAIXO_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_INICIAL_MEDIO_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_FINAL_MEDIO_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_INICIAL_ALTO_POTASSIO,
    DROP COLUMN IF EXISTS TEOR_FINAL_ALTO_POTASSIO,
    DROP COLUMN IF EXISTS MAIOR_TEOR_POTASSIO;
