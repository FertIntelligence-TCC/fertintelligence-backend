ALTER TABLE recommendations
    ADD COLUMN IF NOT EXISTS fertilizer_source_option VARCHAR(255);

UPDATE recommendations
SET fertilizer_source_option = 'BOTH'
WHERE fertilizer_source_option IS NULL;
