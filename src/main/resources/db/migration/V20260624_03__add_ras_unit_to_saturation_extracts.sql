ALTER TABLE EXTRATOS_ANALISES_EXTRATO_SATURACAO
    ADD COLUMN IF NOT EXISTS UNIDADE_RAS VARCHAR(30) NOT NULL DEFAULT '(mmolc)**0.5';

UPDATE EXTRATOS_ANALISES_EXTRATO_SATURACAO
SET UNIDADE_RAS = '(mmolc)**0.5'
WHERE UNIDADE_RAS IS NULL
   OR LOWER(REPLACE(REPLACE(UNIDADE_RAS, ' ', ''), '^', '**')) IN (
        '(mmolc)**0.5',
        'mmolc**0.5',
        'mmolc/mmolc**0.5'
   );

-- DUREZA_CACO3 e sua coluna permanecem no banco por compatibilidade com dados legados.
-- O contrato novo de API não lê nem escreve esse campo.
