-- Descontinua campos obsoletos do CRUD de tabelas de adubação de culturas.
-- Os dados legados são preservados em colunas/tabela LEGACY_* para auditoria,
-- mas deixam de fazer parte do modelo ativo da aplicação.

DO $$
BEGIN
    IF to_regclass('public.doses_micronutrientes_tabelas_adubacao_culturas') IS NOT NULL
       AND to_regclass('public.legacy_doses_micronutrientes_tabelas_adubacao_culturas') IS NULL THEN
        ALTER TABLE DOSES_MICRONUTRIENTES_TABELAS_ADUBACAO_CULTURAS
            RENAME TO LEGACY_DOSES_MICRONUTRIENTES_TABELAS_ADUBACAO_CULTURAS;
    ELSIF to_regclass('public.doses_micronutrientes_tabelas_adubacao_culturas') IS NOT NULL THEN
        DROP TABLE DOSES_MICRONUTRIENTES_TABELAS_ADUBACAO_CULTURAS;
    END IF;
END $$;

ALTER TABLE TABELAS_ADUBACAO_CULTURAS
    ADD COLUMN IF NOT EXISTS LEGACY_SUGESTAO_GESSAGEM DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS LEGACY_SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES VARCHAR(1000);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tabelas_adubacao_culturas'
          AND column_name = 'sugestao_gessagem'
    ) THEN
        UPDATE TABELAS_ADUBACAO_CULTURAS
        SET LEGACY_SUGESTAO_GESSAGEM = SUGESTAO_GESSAGEM
        WHERE LEGACY_SUGESTAO_GESSAGEM IS NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tabelas_adubacao_culturas'
          AND column_name = 'sugestao_de_adubacao_com_micronutrientes'
    ) THEN
        UPDATE TABELAS_ADUBACAO_CULTURAS
        SET LEGACY_SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES = SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES
        WHERE LEGACY_SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES IS NULL;
    END IF;
END $$;

ALTER TABLE TABELAS_ADUBACAO_CULTURAS
    DROP COLUMN IF EXISTS SUGESTAO_GESSAGEM,
    DROP COLUMN IF EXISTS SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES;
