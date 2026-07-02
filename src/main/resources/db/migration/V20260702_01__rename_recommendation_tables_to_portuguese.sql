DO $$
BEGIN
    IF to_regclass('public.recommendations') IS NOT NULL AND to_regclass('public.recomendacoes') IS NULL THEN
        ALTER TABLE recommendations RENAME TO recomendacoes;
    END IF;
    IF to_regclass('public.general_recommendations') IS NOT NULL AND to_regclass('public.recomendacoes_gerais') IS NULL THEN
        ALTER TABLE general_recommendations RENAME TO recomendacoes_gerais;
    END IF;
    IF to_regclass('public.summary_recommendations') IS NOT NULL AND to_regclass('public.recomendacoes_resumidas') IS NULL THEN
        ALTER TABLE summary_recommendations RENAME TO recomendacoes_resumidas;
    END IF;
    IF to_regclass('public.direct_recommendations') IS NOT NULL AND to_regclass('public.recomendacoes_diretas') IS NULL THEN
        ALTER TABLE direct_recommendations RENAME TO recomendacoes_diretas;
    END IF;
    IF to_regclass('public.shopping_lists') IS NOT NULL AND to_regclass('public.listas_de_compras') IS NULL THEN
        ALTER TABLE shopping_lists RENAME TO listas_de_compras;
    END IF;
    IF to_regclass('public.direct_recommendation_micronutrient_fertilizer_lines') IS NOT NULL
       AND to_regclass('public.linhas_micronutrientes_recomendacao_direta') IS NULL THEN
        ALTER TABLE direct_recommendation_micronutrient_fertilizer_lines
            RENAME TO linhas_micronutrientes_recomendacao_direta;
    END IF;
    IF to_regclass('public.direct_recommendation_planting_formulated_fertilizer_lines') IS NOT NULL
       AND to_regclass('public.linhas_adubacao_plantio_recomendacao_direta') IS NULL THEN
        ALTER TABLE direct_recommendation_planting_formulated_fertilizer_lines
            RENAME TO linhas_adubacao_plantio_recomendacao_direta;
    END IF;
    IF to_regclass('public.direct_recommendation_coverage_formulated_fertilizer_lines') IS NOT NULL
       AND to_regclass('public.linhas_adubacao_cobertura_recomendacao_direta') IS NULL THEN
        ALTER TABLE direct_recommendation_coverage_formulated_fertilizer_lines
            RENAME TO linhas_adubacao_cobertura_recomendacao_direta;
    END IF;
END $$;

DO $$
DECLARE
    constraint_pair text[];
    index_pair text[];
BEGIN
    FOREACH constraint_pair SLICE 1 IN ARRAY ARRAY[
        ARRAY['recomendacoes_gerais', 'uk_general_recommendations_recommendation', 'uk_recomendacoes_gerais_recomendacao'],
        ARRAY['recomendacoes_gerais', 'fk_general_recommendations_recommendation', 'fk_recomendacoes_gerais_recomendacao'],
        ARRAY['recomendacoes_resumidas', 'uk_summary_recommendations_recommendation', 'uk_recomendacoes_resumidas_recomendacao'],
        ARRAY['recomendacoes_resumidas', 'fk_summary_recommendations_recommendation', 'fk_recomendacoes_resumidas_recomendacao'],
        ARRAY['recomendacoes_diretas', 'uk_direct_recommendations_recommendation', 'uk_recomendacoes_diretas_recomendacao'],
        ARRAY['recomendacoes_diretas', 'fk_direct_recommendations_recommendation', 'fk_recomendacoes_diretas_recomendacao'],
        ARRAY['listas_de_compras', 'uk_shopping_lists_recommendation', 'uk_listas_de_compras_recomendacao'],
        ARRAY['listas_de_compras', 'fk_shopping_lists_recommendation', 'fk_listas_de_compras_recomendacao'],
        ARRAY['recomendacoes', 'recommendations_fertilizer_source_option_check', 'recomendacoes_fertilizer_source_option_check'],
        ARRAY['recomendacoes', 'recommendations_textural_classification_check', 'recomendacoes_textural_classification_check'],
        ARRAY['recomendacoes', 'fk_recommendations_crop', 'fk_recomendacoes_cultura']
    ] LOOP
        IF to_regclass('public.' || constraint_pair[1]) IS NOT NULL
           AND EXISTS (
               SELECT 1
               FROM pg_constraint c
               JOIN pg_class t ON t.oid = c.conrelid
               JOIN pg_namespace n ON n.oid = t.relnamespace
               WHERE n.nspname = 'public'
                 AND t.relname = constraint_pair[1]
                 AND c.conname = constraint_pair[2]
           )
           AND NOT EXISTS (
               SELECT 1
               FROM pg_constraint c
               JOIN pg_class t ON t.oid = c.conrelid
               JOIN pg_namespace n ON n.oid = t.relnamespace
               WHERE n.nspname = 'public'
                 AND t.relname = constraint_pair[1]
                 AND c.conname = constraint_pair[3]
           ) THEN
            EXECUTE format('ALTER TABLE %I RENAME CONSTRAINT %I TO %I', constraint_pair[1], constraint_pair[2], constraint_pair[3]);
        END IF;
    END LOOP;

    FOREACH index_pair SLICE 1 IN ARRAY ARRAY[
        ARRAY['idx_recommendations_crop', 'idx_recomendacoes_cultura'],
        ARRAY['idx_direct_recommendation_micronutrient_fertilizer_lines_direct_recommendation', 'idx_linhas_micronutrientes_recomendacao_direta'],
        ARRAY['idx_direct_recommendation_planting_formulated_lines_direct_recommendation', 'idx_linhas_plantio_recomendacao_direta'],
        ARRAY['idx_direct_recommendation_coverage_formulated_lines_direct_recommendation', 'idx_linhas_cobertura_recomendacao_direta'],
        ARRAY['idx_direct_recommendation_coverage_formulated_lines_coverage_order', 'idx_linhas_cobertura_recomendacao_direta_ordem']
    ] LOOP
        IF to_regclass('public.' || index_pair[1]) IS NOT NULL AND to_regclass('public.' || index_pair[2]) IS NULL THEN
            EXECUTE format('ALTER INDEX %I RENAME TO %I', index_pair[1], index_pair[2]);
        END IF;
    END LOOP;
END $$;
