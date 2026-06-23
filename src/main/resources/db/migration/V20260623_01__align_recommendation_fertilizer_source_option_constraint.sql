ALTER TABLE recommendations
    DROP CONSTRAINT IF EXISTS recommendations_fertilizer_source_option_check;

ALTER TABLE recommendations
    ADD CONSTRAINT recommendations_fertilizer_source_option_check
        CHECK (fertilizer_source_option IS NULL OR fertilizer_source_option IN (
            'PRIVATE', 'PUBLIC', 'DEFAULT', 'BOTH',
            'PRIVADAS', 'PUBLICAS', 'PADRAO', 'AMBAS'
        ));
