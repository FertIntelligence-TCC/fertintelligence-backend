ALTER TABLE IF EXISTS linhas_adubacao_plantio_recomendacao_direta
    ADD COLUMN IF NOT EXISTS planting_option VARCHAR(50);

ALTER TABLE IF EXISTS linhas_adubacao_plantio_recomendacao_direta
    ADD COLUMN IF NOT EXISTS component_type VARCHAR(50);

ALTER TABLE IF EXISTS direct_recommendation_planting_formulated_fertilizer_lines
    ADD COLUMN IF NOT EXISTS planting_option VARCHAR(50);

ALTER TABLE IF EXISTS direct_recommendation_planting_formulated_fertilizer_lines
    ADD COLUMN IF NOT EXISTS component_type VARCHAR(50);
