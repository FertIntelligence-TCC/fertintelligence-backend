CREATE TABLE IF NOT EXISTS direct_recommendation_micronutrient_fertilizer_lines (
    id BIGSERIAL PRIMARY KEY,
    id_direct_recommendation BIGINT NOT NULL,
    micronutrient VARCHAR(20) NOT NULL,
    micronutrient_dose_kg_ha DOUBLE PRECISION,
    fertilizer_id BIGINT,
    fertilizer_name VARCHAR(255),
    micronutrient_concentration_percent DOUBLE PRECISION,
    fertilizer_dose_kg_ha DOUBLE PRECISION,
    dose_unit_mode VARCHAR(50),
    dose_unit_label VARCHAR(100),
    grams_per_linear_meter DOUBLE PRECISION,
    grams_per_pit DOUBLE PRECISION,
    technical_observation VARCHAR(1000),
    CONSTRAINT fk_direct_rec_micro_lines_direct_recommendation
        FOREIGN KEY (id_direct_recommendation)
        REFERENCES direct_recommendations (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_direct_rec_micro_lines_direct_recommendation
    ON direct_recommendation_micronutrient_fertilizer_lines (id_direct_recommendation);
