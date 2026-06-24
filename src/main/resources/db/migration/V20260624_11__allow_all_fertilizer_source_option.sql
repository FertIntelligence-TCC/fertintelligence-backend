DO $$
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
        SET fertilizer_source_option = 'TODOS'
        WHERE UPPER(TRIM(fertilizer_source_option)) = 'ALL';

        ALTER TABLE recommendations
            DROP CONSTRAINT IF EXISTS recommendations_fertilizer_source_option_check;

        ALTER TABLE recommendations
            ADD CONSTRAINT recommendations_fertilizer_source_option_check
                CHECK (fertilizer_source_option IS NULL OR fertilizer_source_option IN (
                    'PRIVATE', 'PUBLIC', 'DEFAULT', 'BOTH', 'ALL',
                    'PRIVADAS', 'PUBLICAS', 'PADRAO', 'AMBAS', 'TODOS'
                ));
    END IF;
END $$;
