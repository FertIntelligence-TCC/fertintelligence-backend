-- Descontinua os campos de esterco da tabela de adubação de culturas no contrato funcional.
-- As colunas legadas são preservadas para dados produtivos antigos, mas deixam de ser
-- obrigatórias porque a aplicação não grava mais esses valores.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tabelas_adubacao_culturas'
          AND column_name = 'tipo_de_esterco'
    ) THEN
        ALTER TABLE TABELAS_ADUBACAO_CULTURAS
            ALTER COLUMN TIPO_DE_ESTERCO DROP NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tabelas_adubacao_culturas'
          AND column_name = 'quantidade_de_esterco'
    ) THEN
        ALTER TABLE TABELAS_ADUBACAO_CULTURAS
            ALTER COLUMN QUANTIDADE_DE_ESTERCO DROP NOT NULL;
    END IF;
END $$;
