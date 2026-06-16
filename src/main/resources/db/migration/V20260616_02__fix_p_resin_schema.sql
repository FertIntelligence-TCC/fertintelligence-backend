ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_resina_troca_anionica
    ADD COLUMN IF NOT EXISTS unidade VARCHAR(20) DEFAULT 'g/dm3',
    ADD COLUMN IF NOT EXISTS muito_baixo DOUBLE PRECISION DEFAULT 0,
    ADD COLUMN IF NOT EXISTS baixo_menor DOUBLE PRECISION DEFAULT 5,
    ADD COLUMN IF NOT EXISTS baixo_maior DOUBLE PRECISION DEFAULT 10,
    ADD COLUMN IF NOT EXISTS medio_menor DOUBLE PRECISION DEFAULT 10,
    ADD COLUMN IF NOT EXISTS medio_maior DOUBLE PRECISION DEFAULT 20,
    ADD COLUMN IF NOT EXISTS alto_menor DOUBLE PRECISION DEFAULT 20,
    ADD COLUMN IF NOT EXISTS alto_maior DOUBLE PRECISION DEFAULT 999,
    ADD COLUMN IF NOT EXISTS muito_alto DOUBLE PRECISION DEFAULT 999;

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

ALTER TABLE fosforo_disponivel_com_extrator_resina_troca_anionica
    ALTER COLUMN unidade SET NOT NULL,
    ALTER COLUMN muito_baixo SET NOT NULL,
    ALTER COLUMN baixo_menor SET NOT NULL,
    ALTER COLUMN baixo_maior SET NOT NULL,
    ALTER COLUMN medio_menor SET NOT NULL,
    ALTER COLUMN medio_maior SET NOT NULL,
    ALTER COLUMN alto_menor SET NOT NULL,
    ALTER COLUMN alto_maior SET NOT NULL,
    ALTER COLUMN muito_alto SET NOT NULL;
