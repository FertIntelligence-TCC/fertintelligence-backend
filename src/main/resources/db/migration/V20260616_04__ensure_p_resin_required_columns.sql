ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_resina_troca_anionica
    ADD COLUMN IF NOT EXISTS unidade VARCHAR(20),
    ADD COLUMN IF NOT EXISTS muito_baixo DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS baixo_menor DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS baixo_maior DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS medio_menor DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS medio_maior DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS alto_menor DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS alto_maior DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS muito_alto DOUBLE PRECISION;

DO $$
BEGIN
    IF to_regclass('fosforo_disponivel_com_extrator_resina_troca_anionica') IS NOT NULL THEN
        UPDATE fosforo_disponivel_com_extrator_resina_troca_anionica
        SET
            unidade = COALESCE(unidade, 'g/dm3'),
            muito_baixo = COALESCE(muito_baixo, 0),
            baixo_menor = COALESCE(baixo_menor, 5),
            baixo_maior = COALESCE(baixo_maior, 10),
            medio_menor = COALESCE(medio_menor, 10),
            medio_maior = COALESCE(medio_maior, 20),
            alto_menor = COALESCE(alto_menor, 20),
            alto_maior = COALESCE(alto_maior, 999),
            muito_alto = COALESCE(muito_alto, 999);
    END IF;
END $$;

ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_resina_troca_anionica
    ALTER COLUMN unidade SET DEFAULT 'g/dm3',
    ALTER COLUMN muito_baixo SET DEFAULT 0,
    ALTER COLUMN baixo_menor SET DEFAULT 5,
    ALTER COLUMN baixo_maior SET DEFAULT 10,
    ALTER COLUMN medio_menor SET DEFAULT 10,
    ALTER COLUMN medio_maior SET DEFAULT 20,
    ALTER COLUMN alto_menor SET DEFAULT 20,
    ALTER COLUMN alto_maior SET DEFAULT 999,
    ALTER COLUMN muito_alto SET DEFAULT 999,
    ALTER COLUMN unidade SET NOT NULL,
    ALTER COLUMN muito_baixo SET NOT NULL,
    ALTER COLUMN baixo_menor SET NOT NULL,
    ALTER COLUMN baixo_maior SET NOT NULL,
    ALTER COLUMN medio_menor SET NOT NULL,
    ALTER COLUMN medio_maior SET NOT NULL,
    ALTER COLUMN alto_menor SET NOT NULL,
    ALTER COLUMN alto_maior SET NOT NULL,
    ALTER COLUMN muito_alto SET NOT NULL;
