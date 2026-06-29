ALTER TABLE recommendations
    ADD COLUMN IF NOT EXISTS id_crop BIGINT,
    ADD COLUMN IF NOT EXISTS crop_used_area_in_the_plot DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS crop_planting_date_day INTEGER,
    ADD COLUMN IF NOT EXISTS crop_planting_date_month INTEGER,
    ADD COLUMN IF NOT EXISTS crop_planting_date_year INTEGER;

CREATE INDEX IF NOT EXISTS idx_recommendations_crop
    ON recommendations (id_crop);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE lower(conname) = lower('fk_recommendations_crop')
    ) THEN
        ALTER TABLE recommendations
            ADD CONSTRAINT fk_recommendations_crop
            FOREIGN KEY (id_crop) REFERENCES culturas(id);
    END IF;
END $$;
