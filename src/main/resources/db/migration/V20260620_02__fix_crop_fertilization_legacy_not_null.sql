-- Corrige falha de criação/edição em TABELAS_ADUBACAO_CULTURAS causada por drift de schema
-- em bancos legados (Render), onde restrições/colunas antigas não correspondem mais à
-- entidade atual (CropFertilizationTableModel), fazendo todo INSERT violar NOT NULL.
--
-- 1) SUGESTAO_DE_ADUBACAO_COM_N_P2O5_K2O: coluna removida do projeto nas mudanças recentes,
--    mas ainda presente (NOT NULL, sem default) em bancos legados. Como a entidade atual
--    não a preenche, todo INSERT falha. Removemos a coluna definitivamente.
-- 2) OBSERVACOES: a entidade trata como opcional, mas o DROP NOT NULL original
--    (V20260604_01) foi pulado pelo baseline Flyway 20260618 e nunca chegou ao prod;
--    salvar sem observações falha. Reaplicamos o DROP NOT NULL de forma idempotente.

ALTER TABLE TABELAS_ADUBACAO_CULTURAS
    DROP COLUMN IF EXISTS SUGESTAO_DE_ADUBACAO_COM_N_P2O5_K2O;

ALTER TABLE TABELAS_ADUBACAO_CULTURAS
    ALTER COLUMN OBSERVACOES DROP NOT NULL;
