CREATE OR REPLACE FUNCTION ensure_fertilizer_photo_table(
    photo_table_name TEXT,
    fertilizer_table_name TEXT,
    fk_constraint_name TEXT,
    unique_constraint_name TEXT
) RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    pk_constraint_name TEXT;
    pk_column_count INTEGER;
    pk_has_id BOOLEAN;
    id_sequence_name TEXT;
BEGIN
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I (
            id BIGINT,
            adubo_id BIGINT NOT NULL,
            ordem INTEGER NOT NULL,
            id_foto VARCHAR(255) NOT NULL
        )',
        photo_table_name
    );

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = photo_table_name
          AND column_name = 'id'
    ) THEN
        EXECUTE format('ALTER TABLE %I ADD COLUMN id BIGINT', photo_table_name);
    END IF;

    id_sequence_name := photo_table_name || '_id_seq';
    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS %I', id_sequence_name);
    EXECUTE format('ALTER SEQUENCE %I OWNED BY %I.id', id_sequence_name, photo_table_name);
    EXECUTE format('ALTER TABLE %I ALTER COLUMN id SET DEFAULT nextval(%L)', photo_table_name, id_sequence_name);
    EXECUTE format('UPDATE %I SET id = nextval(%L) WHERE id IS NULL', photo_table_name, id_sequence_name);
    EXECUTE format(
        'SELECT setval(%L, COALESCE((SELECT MAX(id) FROM %I), 0) + 1, false)',
        id_sequence_name,
        photo_table_name
    );
    EXECUTE format('ALTER TABLE %I ALTER COLUMN id SET NOT NULL', photo_table_name);

    SELECT tc.constraint_name
    INTO pk_constraint_name
    FROM information_schema.table_constraints tc
    WHERE tc.table_schema = current_schema()
      AND tc.table_name = photo_table_name
      AND tc.constraint_type = 'PRIMARY KEY'
    LIMIT 1;

    IF pk_constraint_name IS NOT NULL THEN
        SELECT COUNT(*), BOOL_OR(kcu.column_name = 'id')
        INTO pk_column_count, pk_has_id
        FROM information_schema.key_column_usage kcu
        WHERE kcu.table_schema = current_schema()
          AND kcu.table_name = photo_table_name
          AND kcu.constraint_name = pk_constraint_name;

        IF pk_column_count <> 1 OR pk_has_id IS DISTINCT FROM TRUE THEN
            EXECUTE format('ALTER TABLE %I DROP CONSTRAINT %I', photo_table_name, pk_constraint_name);
            pk_constraint_name := NULL;
        END IF;
    END IF;

    IF pk_constraint_name IS NULL THEN
        EXECUTE format('ALTER TABLE %I ADD CONSTRAINT %I PRIMARY KEY (id)', photo_table_name, photo_table_name || '_pkey');
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_schema = current_schema()
          AND table_name = photo_table_name
          AND constraint_name = unique_constraint_name
    ) THEN
        EXECUTE format(
            'ALTER TABLE %I ADD CONSTRAINT %I UNIQUE (adubo_id, ordem)',
            photo_table_name,
            unique_constraint_name
        );
    END IF;

    IF to_regclass(fertilizer_table_name) IS NOT NULL
       AND NOT EXISTS (
           SELECT 1
           FROM information_schema.table_constraints
           WHERE table_schema = current_schema()
             AND table_name = photo_table_name
             AND constraint_name = fk_constraint_name
       ) THEN
        EXECUTE format(
            'ALTER TABLE %I ADD CONSTRAINT %I FOREIGN KEY (adubo_id) REFERENCES %I(id) ON DELETE CASCADE',
            photo_table_name,
            fk_constraint_name,
            fertilizer_table_name
        );
    END IF;

    EXECUTE format(
        'CREATE INDEX IF NOT EXISTS %I ON %I (adubo_id)',
        'idx_' || photo_table_name || '_adubo_id',
        photo_table_name
    );
END;
$$;

SELECT ensure_fertilizer_photo_table(
    'adubos_minerais_simples_fotos',
    'adubos_minerais_simples',
    'fk_adubos_minerais_simples_fotos_adubo',
    'uk_adubos_minerais_simples_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_minerais_formulados_fotos',
    'adubos_minerais_formulados',
    'fk_adubos_minerais_formulados_fotos_adubo',
    'uk_adubos_minerais_formulados_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_organo_minerais_fotos',
    'adubos_organo_minerais',
    'fk_adubos_organo_minerais_fotos_adubo',
    'uk_adubos_organo_minerais_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_verdes_fotos',
    'adubos_verdes',
    'fk_adubos_verdes_fotos_adubo',
    'uk_adubos_verdes_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_organicos_fotos',
    'adubos_organicos',
    'fk_adubos_organicos_fotos_adubo',
    'uk_adubos_organicos_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_minerais_fotos',
    'adubos_minerais',
    'fk_adubos_minerais_fotos_adubo',
    'uk_adubos_minerais_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'adubos_quelatados_fotos',
    'adubos_quelatados',
    'fk_adubos_quelatados_fotos_adubo',
    'uk_adubos_quelatados_fotos_adubo_ordem'
);

SELECT ensure_fertilizer_photo_table(
    'bio_fertilizantes_fotos',
    'bio_fertilizantes',
    'fk_bio_fertilizantes_fotos_adubo',
    'uk_bio_fertilizantes_fotos_adubo_ordem'
);

DROP FUNCTION ensure_fertilizer_photo_table(TEXT, TEXT, TEXT, TEXT);
