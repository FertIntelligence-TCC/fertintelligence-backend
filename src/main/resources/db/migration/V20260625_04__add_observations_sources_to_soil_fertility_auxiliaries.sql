ALTER TABLE IF EXISTS criterios_de_interpretacao_da_salinidade_do_solo
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS fontes VARCHAR(1000);

ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_resina_troca_anionica
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS fontes VARCHAR(1000);

ALTER TABLE IF EXISTS fosforo_disponivel_com_extrator_mehlich_1
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS fontes VARCHAR(1000);

ALTER TABLE IF EXISTS sodio_trocavel
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS fontes VARCHAR(1000);

ALTER TABLE IF EXISTS faixas_de_teores_diversos
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS fontes VARCHAR(1000);
