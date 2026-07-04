DO $$
DECLARE
    invalid_source_options BIGINT;
BEGIN
    IF to_regclass('public.recommendations') IS NOT NULL
       AND EXISTS (
           SELECT 1
           FROM information_schema.columns
           WHERE table_schema = 'public'
             AND table_name = 'recommendations'
             AND column_name = 'fertilizer_source_option'
       ) THEN
        UPDATE recommendations
        SET fertilizer_source_option = CASE UPPER(TRIM(fertilizer_source_option))
            WHEN 'PRIVATE' THEN 'PRIVADAS'
            WHEN 'PRIVADAS' THEN 'PRIVADAS'
            WHEN 'PUBLIC' THEN 'PUBLICAS'
            WHEN 'PUBLICAS' THEN 'PUBLICAS'
            WHEN 'DEFAULT' THEN 'PADRAO'
            WHEN 'PADRAO' THEN 'PADRAO'
            WHEN 'BOTH' THEN 'AMBAS'
            WHEN 'AMBAS' THEN 'AMBAS'
            ELSE fertilizer_source_option
        END
        WHERE fertilizer_source_option IS NOT NULL;

        SELECT COUNT(*)
        INTO invalid_source_options
        FROM recommendations
        WHERE fertilizer_source_option IS NOT NULL
          AND UPPER(TRIM(fertilizer_source_option)) NOT IN (
              'PRIVATE', 'PRIVADAS',
              'PUBLIC', 'PUBLICAS',
              'DEFAULT', 'PADRAO',
              'BOTH', 'AMBAS'
          );

        IF invalid_source_options > 0 THEN
            RAISE EXCEPTION
                'recommendations.fertilizer_source_option contém % valor(es) incompatível(is) com FertilizerSourceOption. Corrija para PRIVADAS, PUBLICAS, PADRAO ou AMBAS antes de aplicar a constraint.',
                invalid_source_options;
        END IF;

        ALTER TABLE recommendations
            DROP CONSTRAINT IF EXISTS recommendations_fertilizer_source_option_check;

        ALTER TABLE recommendations
            ADD CONSTRAINT recommendations_fertilizer_source_option_check
                CHECK (fertilizer_source_option IS NULL OR fertilizer_source_option IN (
                    'PRIVATE', 'PUBLIC', 'DEFAULT', 'BOTH',
                    'PRIVADAS', 'PUBLICAS', 'PADRAO', 'AMBAS'
                ));
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.extratos_analises_fisicas') IS NOT NULL THEN
        ALTER TABLE EXTRATOS_ANALISES_FISICAS
            ADD COLUMN IF NOT EXISTS UNIDADE_TEOR_DE_AREIA VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_TEOR_DE_SILTE VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_TEOR_DE_ARGILA VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_DENSIDADE_APARENTE VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_DENSIDADE_REAL VARCHAR(20);

        ALTER TABLE EXTRATOS_ANALISES_FISICAS
            ALTER COLUMN UNIDADE_TEOR_DE_AREIA SET DEFAULT 'g/dm3',
            ALTER COLUMN UNIDADE_TEOR_DE_SILTE SET DEFAULT 'g/dm3',
            ALTER COLUMN UNIDADE_TEOR_DE_ARGILA SET DEFAULT 'g/dm3',
            ALTER COLUMN UNIDADE_DENSIDADE_APARENTE SET DEFAULT 'g/dm3',
            ALTER COLUMN UNIDADE_DENSIDADE_REAL SET DEFAULT 'g/dm3';

        UPDATE EXTRATOS_ANALISES_FISICAS
        SET UNIDADE_TEOR_DE_AREIA = 'g/dm3'
        WHERE UNIDADE_TEOR_DE_AREIA IS NULL
           OR LOWER(REPLACE(UNIDADE_TEOR_DE_AREIA, '³', '3')) IN ('g/dm3', 'g_per_dm3', 'g/kg', 'g_per_kg');

        UPDATE EXTRATOS_ANALISES_FISICAS
        SET UNIDADE_TEOR_DE_SILTE = 'g/dm3'
        WHERE UNIDADE_TEOR_DE_SILTE IS NULL
           OR LOWER(REPLACE(UNIDADE_TEOR_DE_SILTE, '³', '3')) IN ('g/dm3', 'g_per_dm3', 'g/kg', 'g_per_kg');

        UPDATE EXTRATOS_ANALISES_FISICAS
        SET UNIDADE_TEOR_DE_ARGILA = 'g/dm3'
        WHERE UNIDADE_TEOR_DE_ARGILA IS NULL
           OR LOWER(REPLACE(UNIDADE_TEOR_DE_ARGILA, '³', '3')) IN ('g/dm3', 'g_per_dm3', 'g/kg', 'g_per_kg');

        UPDATE EXTRATOS_ANALISES_FISICAS
        SET UNIDADE_DENSIDADE_APARENTE = 'g/dm3'
        WHERE UNIDADE_DENSIDADE_APARENTE IS NULL
           OR LOWER(REPLACE(UNIDADE_DENSIDADE_APARENTE, '³', '3')) IN ('g/dm3', 'g_per_dm3', 'g/kg', 'g_per_kg');

        UPDATE EXTRATOS_ANALISES_FISICAS
        SET UNIDADE_DENSIDADE_REAL = 'g/dm3'
        WHERE UNIDADE_DENSIDADE_REAL IS NULL
           OR LOWER(REPLACE(UNIDADE_DENSIDADE_REAL, '³', '3')) IN ('g/dm3', 'g_per_dm3', 'g/kg', 'g_per_kg');

        ALTER TABLE EXTRATOS_ANALISES_FISICAS
            ALTER COLUMN UNIDADE_TEOR_DE_AREIA SET NOT NULL,
            ALTER COLUMN UNIDADE_TEOR_DE_SILTE SET NOT NULL,
            ALTER COLUMN UNIDADE_TEOR_DE_ARGILA SET NOT NULL,
            ALTER COLUMN UNIDADE_DENSIDADE_APARENTE SET NOT NULL,
            ALTER COLUMN UNIDADE_DENSIDADE_REAL SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.extratos_analises_fertilidade') IS NOT NULL THEN
        ALTER TABLE EXTRATOS_ANALISES_FERTILIDADE
            ADD COLUMN IF NOT EXISTS UNIDADE_CALCIO VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_MAGNESIO VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_POTASSIO VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_SODIO VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_ALUMINIO VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_ALUMINIO_MAIS_HIDROGENIO VARCHAR(40),
            ADD COLUMN IF NOT EXISTS UNIDADE_SOMA_BASES VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_CTC_EFETIVA VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_CTC_PH_7 VARCHAR(20);

        ALTER TABLE EXTRATOS_ANALISES_FERTILIDADE
            ALTER COLUMN UNIDADE_CALCIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_MAGNESIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_POTASSIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_SODIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_ALUMINIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_ALUMINIO_MAIS_HIDROGENIO SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_SOMA_BASES SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_CTC_EFETIVA SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN UNIDADE_CTC_PH_7 SET DEFAULT 'mmolc/dm³';

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

        ALTER TABLE EXTRATOS_ANALISES_FERTILIDADE
            ALTER COLUMN UNIDADE_CALCIO SET NOT NULL,
            ALTER COLUMN UNIDADE_MAGNESIO SET NOT NULL,
            ALTER COLUMN UNIDADE_POTASSIO SET NOT NULL,
            ALTER COLUMN UNIDADE_SODIO SET NOT NULL,
            ALTER COLUMN UNIDADE_ALUMINIO SET NOT NULL,
            ALTER COLUMN UNIDADE_ALUMINIO_MAIS_HIDROGENIO SET NOT NULL,
            ALTER COLUMN UNIDADE_SOMA_BASES SET NOT NULL,
            ALTER COLUMN UNIDADE_CTC_EFETIVA SET NOT NULL,
            ALTER COLUMN UNIDADE_CTC_PH_7 SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.extratos_analises_extrato_saturacao') IS NOT NULL THEN
        ALTER TABLE EXTRATOS_ANALISES_EXTRATO_SATURACAO
            ADD COLUMN IF NOT EXISTS UNIDADE_RAS VARCHAR(30);

        ALTER TABLE EXTRATOS_ANALISES_EXTRATO_SATURACAO
            ALTER COLUMN UNIDADE_RAS SET DEFAULT '(mmolc)**0.5';

        UPDATE EXTRATOS_ANALISES_EXTRATO_SATURACAO
        SET UNIDADE_RAS = '(mmolc)**0.5'
        WHERE UNIDADE_RAS IS NULL
           OR LOWER(REPLACE(REPLACE(UNIDADE_RAS, ' ', ''), '^', '**')) IN (
                '(mmolc)**0.5',
                'mmolc**0.5',
                'mmolc/mmolc**0.5'
           );

        ALTER TABLE EXTRATOS_ANALISES_EXTRATO_SATURACAO
            ALTER COLUMN UNIDADE_RAS SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.intervalo_teor_nutriente') IS NOT NULL THEN
        ALTER TABLE INTERVALO_TEOR_NUTRIENTE
            ADD COLUMN IF NOT EXISTS UNIDADE_TEOR VARCHAR(20),
            ADD COLUMN IF NOT EXISTS UNIDADE_APLICACAO_PLANTIO VARCHAR(20);

        ALTER TABLE INTERVALO_TEOR_NUTRIENTE
            ALTER COLUMN UNIDADE_APLICACAO_PLANTIO SET DEFAULT 'kg/ha';

        UPDATE INTERVALO_TEOR_NUTRIENTE
        SET UNIDADE_TEOR = 'mg/dm³'
        WHERE NUTRIENTE = 1
          AND (UNIDADE_TEOR IS NULL OR LOWER(REPLACE(UNIDADE_TEOR, '³', '3')) IN ('mg/dm3', 'mg_per_dm3'));

        UPDATE INTERVALO_TEOR_NUTRIENTE
        SET UNIDADE_TEOR = 'mmolc/dm³'
        WHERE NUTRIENTE = 2
          AND (UNIDADE_TEOR IS NULL OR LOWER(REPLACE(UNIDADE_TEOR, '³', '3')) IN ('cmolc/dm3', 'cmolc_per_dm3', 'mmolc/dm3', 'mmolc_per_dm3'));

        UPDATE INTERVALO_TEOR_NUTRIENTE
        SET UNIDADE_APLICACAO_PLANTIO = 'kg/ha'
        WHERE UNIDADE_APLICACAO_PLANTIO IS NULL
           OR LOWER(UNIDADE_APLICACAO_PLANTIO) IN ('kg/ha', 'kg_per_ha', 'kg per ha');

        ALTER TABLE INTERVALO_TEOR_NUTRIENTE
            ALTER COLUMN UNIDADE_APLICACAO_PLANTIO SET NOT NULL;
    END IF;

    IF to_regclass('public.cobertura') IS NOT NULL THEN
        ALTER TABLE COBERTURA
            ADD COLUMN IF NOT EXISTS UNIDADE_APLICACAO_COBERTURA VARCHAR(20);

        ALTER TABLE COBERTURA
            ALTER COLUMN UNIDADE_APLICACAO_COBERTURA SET DEFAULT 'kg/ha';

        UPDATE COBERTURA
        SET UNIDADE_APLICACAO_COBERTURA = 'kg/ha'
        WHERE UNIDADE_APLICACAO_COBERTURA IS NULL
           OR LOWER(UNIDADE_APLICACAO_COBERTURA) IN ('kg/ha', 'kg_per_ha', 'kg per ha');

        ALTER TABLE COBERTURA
            ALTER COLUMN UNIDADE_APLICACAO_COBERTURA SET NOT NULL;
    END IF;

    IF to_regclass('public.adubacao_cobertura') IS NOT NULL THEN
        ALTER TABLE ADUBACAO_COBERTURA
            ADD COLUMN IF NOT EXISTS UNIDADE_DOSE VARCHAR(20);

        ALTER TABLE ADUBACAO_COBERTURA
            ALTER COLUMN UNIDADE_DOSE SET DEFAULT 'kg/ha';

        UPDATE ADUBACAO_COBERTURA
        SET UNIDADE_DOSE = 'kg/ha'
        WHERE UNIDADE_DOSE IS NULL
           OR LOWER(UNIDADE_DOSE) IN ('kg/ha', 'kg_per_ha', 'kg per ha');

        ALTER TABLE ADUBACAO_COBERTURA
            ALTER COLUMN UNIDADE_DOSE SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.fosforo_disponivel_com_extrator_resina_troca_anionica') IS NOT NULL THEN
        ALTER TABLE fosforo_disponivel_com_extrator_resina_troca_anionica
            ADD COLUMN IF NOT EXISTS unidade VARCHAR(20);

        ALTER TABLE fosforo_disponivel_com_extrator_resina_troca_anionica
            ALTER COLUMN unidade SET DEFAULT 'mg/dm³';

        UPDATE fosforo_disponivel_com_extrator_resina_troca_anionica
        SET unidade = 'mg/dm³'
        WHERE unidade IS NULL
           OR LOWER(REPLACE(unidade, '³', '3')) IN ('g/dm3', 'mg/dm3', 'mg_per_dm3');

        ALTER TABLE fosforo_disponivel_com_extrator_resina_troca_anionica
            ALTER COLUMN unidade SET NOT NULL;
    END IF;

    IF to_regclass('public.criterios_de_interpretacao_da_salinidade_do_solo') IS NOT NULL THEN
        ALTER TABLE criterios_de_interpretacao_da_salinidade_do_solo
            ADD COLUMN IF NOT EXISTS unidade_ras VARCHAR(30);

        ALTER TABLE criterios_de_interpretacao_da_salinidade_do_solo
            ALTER COLUMN unidade_ras SET DEFAULT '(mmolc)**0.5';

        UPDATE criterios_de_interpretacao_da_salinidade_do_solo
        SET unidade_ras = '(mmolc)**0.5'
        WHERE unidade_ras IS NULL
           OR LOWER(REPLACE(REPLACE(unidade_ras, ' ', ''), '^', '**')) IN (
                '(mmolc)**0.5',
                'mmolc**0.5',
                'mmolc/mmolc**0.5'
           );

        ALTER TABLE criterios_de_interpretacao_da_salinidade_do_solo
            ALTER COLUMN unidade_ras SET NOT NULL;
    END IF;

    IF to_regclass('public.faixas_de_teores_diversos') IS NOT NULL THEN
        ALTER TABLE faixas_de_teores_diversos
            ADD COLUMN IF NOT EXISTS unidade_carbono_organico VARCHAR(20),
            ADD COLUMN IF NOT EXISTS unidade_materia_organica VARCHAR(20);

        ALTER TABLE faixas_de_teores_diversos
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

        ALTER TABLE faixas_de_teores_diversos
            ALTER COLUMN unidade_carbono_organico SET NOT NULL,
            ALTER COLUMN unidade_materia_organica SET NOT NULL;
    END IF;

    IF to_regclass('public.sodio_trocavel') IS NOT NULL THEN
        ALTER TABLE sodio_trocavel
            ADD COLUMN IF NOT EXISTS unidade_sodio VARCHAR(20),
            ADD COLUMN IF NOT EXISTS unidade_ctc VARCHAR(20);

        ALTER TABLE sodio_trocavel
            ALTER COLUMN unidade_sodio SET DEFAULT 'mmolc/dm³',
            ALTER COLUMN unidade_ctc SET DEFAULT 'mmolc/dm³';

        UPDATE sodio_trocavel
        SET unidade_sodio = 'mmolc/dm³'
        WHERE unidade_sodio IS NULL
           OR LOWER(REPLACE(unidade_sodio, '³', '3')) IN ('cmolc/dm3', 'mmolc/dm3', 'mmolc_per_dm3');

        UPDATE sodio_trocavel
        SET unidade_ctc = 'mmolc/dm³'
        WHERE unidade_ctc IS NULL
           OR LOWER(REPLACE(unidade_ctc, '³', '3')) IN ('cmolc/dm3', 'mmolc/dm3', 'mmolc_per_dm3');

        ALTER TABLE sodio_trocavel
            ALTER COLUMN unidade_sodio SET NOT NULL,
            ALTER COLUMN unidade_ctc SET NOT NULL;
    END IF;
END $$;
