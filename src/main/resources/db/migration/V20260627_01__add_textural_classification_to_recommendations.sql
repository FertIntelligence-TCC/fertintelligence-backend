ALTER TABLE recommendations
    ADD COLUMN IF NOT EXISTS textural_classification VARCHAR(20);

UPDATE recommendations
SET textural_classification = 'BRASILEIRO'
WHERE textural_classification IS NULL;

ALTER TABLE recommendations
    ALTER COLUMN textural_classification SET DEFAULT 'BRASILEIRO',
    ALTER COLUMN textural_classification SET NOT NULL;

ALTER TABLE recommendations
    DROP CONSTRAINT IF EXISTS recommendations_textural_classification_check;

ALTER TABLE recommendations
    ADD CONSTRAINT recommendations_textural_classification_check
        CHECK (textural_classification IN ('BRASILEIRO', 'AMERICANO'));
