# Investigacao: ausencia das coberturas na Recomendacao Direta

## Objetivo

Identificar por que a Recomendacao Direta pode apresentar apenas a adubacao de plantio, mesmo quando a engine agronomica calcula recomendacoes de cobertura.

Esta investigacao nao altera codigo de aplicacao.

## Fluxo encontrado

### 1. Geracao da recomendacao principal

O fluxo de geracao passa por `RecommendationServiceImpl.generate`.

1. `RecommendationCalculationService.calculate(...)` executa a engine agronomica e retorna um `RecommendationCalculationResult`.
2. `RecommendationReportService.buildTechnicalReport(calculationResult)` gera o laudo tecnico completo.
3. `RecommendationNarrativeService.improveNarrative(technicalReport)` ajusta a narrativa.
4. `RecommendationModel.technicalReport` recebe o laudo melhorado.
5. `GeneralRecommendationService.createInitial(savedRecommendation, improvedReport)` salva a Recomendacao Geral com o proprio laudo completo.
6. `DirectRecommendationService.createInitial(...)` cria a Recomendacao Direta com:
   - texto inicial de `DirectRecommendationReportService.build(savedRecommendation)`;
   - `calculationResult.getMicronutrientFertilizerRows()`;
   - `calculationResult.getPlantingFormulatedFertilizerRows()`;
   - `calculationResult.getCoverageFormulatedFertilizerRows()`.

### 2. Calculo de plantio e cobertura na engine

O calculo de N, P2O5 e K2O ocorre em `NutrientFertilizationCalculationService.calculate`.

O service:

1. seleciona faixas de N, P e K em `ContentRangeModel`;
2. calcula a linha de plantio e adiciona uma `FertilizationRecommendationRow` com `phase = "Plantio"`;
3. percorre as faixas selecionadas de N, P e K;
4. chama `buildCoverageRows(...)` para cada faixa;
5. em `buildCoverageRows(...)`, consulta `CoverageRepository.findAllByRangeOrderByOrderAsc(range)`;
6. para cada `CoverageModel` com `application` preenchida, adiciona uma `FertilizationRecommendationRow` com fase no formato `Cobertura {ordem} - {nutriente}`;
7. acumula as doses por ordem em `CoverageNpkAccumulator`;
8. monta o balanco nutricional consolidado;
9. tenta montar linhas estruturadas de formulados de plantio;
10. tenta montar linhas estruturadas de formulados de cobertura.

Portanto, as coberturas simples existem no resultado principal em `RecommendationCalculationResult.fertilizationRecommendationRows`.

### 3. Recomendacao Geral

A Recomendacao Geral usa o laudo tecnico completo gerado por `RecommendationReportService`.

Na tabela de plantio e cobertura:

- `appendPlantingFertilization(...)` filtra `fertilizationRecommendationRows` por `phase = "Plantio"`;
- `appendCoverageFertilization(...)` chama `filterCoverageRows(...)`;
- `filterCoverageRows(...)` inclui linhas cuja fase contem `cobertura`, excluindo apenas `Balanço global NPK`.

Com isso, qualquer linha calculada por `buildCoverageRows(...)` entra na secao `11. Adubação de cobertura` da Recomendacao Geral, mesmo quando a cobertura e uma recomendacao de adubo simples e nao um formulado NPK.

### 4. Recomendacao Direta

A Recomendacao Direta nao persiste nem recebe a lista completa `fertilizationRecommendationRows`.

Ela possui dois caminhos para montar a `Tabela de N, P2O5 e K2O` em `DirectRecommendationReportService.appendNpkTable(...)`.

#### Caminho A: linhas estruturadas de formulados

Quando `hasFormulatedLines(...)` retorna verdadeiro, a Direta monta a tabela apenas com:

- `DirectRecommendationPlantingFormulatedFertilizerLineModel`;
- `DirectRecommendationCoverageFormulatedFertilizerLineModel`.

Nesse caminho:

- `appendPlantingFormulatedRows(...)` adiciona as linhas estruturadas de plantio;
- `appendCoverageFormulatedRows(...)` adiciona as linhas estruturadas de cobertura formulada;
- o metodo retorna imediatamente;
- as secoes `10. Adubação de plantio` e `11. Adubação de cobertura` do laudo fonte nao sao lidas.

#### Caminho B: parse do laudo tecnico fonte

Quando nao ha linhas estruturadas de formulados, a Direta faz fallback para o texto de origem:

- `appendFertilizationRows(..., "10. Adubação de plantio", ...)`;
- `appendFertilizationRows(..., "11. Adubação de cobertura", ...)`.

Nesse caminho, a Direta consegue reaproveitar a secao de cobertura do `RecommendationModel.technicalReport`, desde que ela esteja presente e parseavel.

## Onde as coberturas deixam de ser propagadas

A perda acontece na entrada do caminho estruturado da Recomendacao Direta.

`DirectRecommendationServiceImpl.createInitial(...)` recebe as linhas estruturadas de plantio e cobertura formulada. Quando existe linha estruturada de plantio, `hasStructuredLines(...)` e verdadeiro, o service sincroniza as tabelas estruturadas disponiveis e recalcula o texto da Direta com `directRecommendationReportService.build(recommendation)`.

Em seguida, `DirectRecommendationReportService.appendNpkTable(...)` detecta que ha linhas formuladas por `hasFormulatedLines(...)`. Como a lista de plantio formulado normalmente existe, o metodo usa o caminho estruturado e nao faz fallback para a secao `11. Adubação de cobertura` do laudo geral.

O problema ocorre quando a engine calculou coberturas simples em `fertilizationRecommendationRows`, mas nao gerou `coverageFormulatedFertilizerRows`.

Isso e esperado em varios cenarios, porque `CoverageFormulatedFertilizerRecommendationService.calculate(...)` so gera linha de cobertura formulada se a cobertura por ordem tiver recomendacao positiva completa de N, P2O5 e K2O. Se uma cobertura tiver apenas N, apenas K2O, apenas P2O5, ou qualquer combinacao incompleta, o service registra aviso tecnico e nao cria linha formulada:

`Cobertura {ordem} sem recomendação positiva completa de N, P2O5 e K2O; seleção de formulado NPK não foi forçada.`

Assim:

1. a cobertura simples e calculada pela engine;
2. a Recomendacao Geral exibe a cobertura porque le `fertilizationRecommendationRows`;
3. a Recomendacao Direta entra no caminho estruturado por causa das linhas de plantio formulado;
4. a Direta nao tem linhas estruturadas equivalentes para coberturas simples;
5. a Direta tambem nao faz fallback parcial para a secao de cobertura do laudo fonte;
6. o resultado visivel fica restrito ao plantio formulado.

## Classes envolvidas

- `RecommendationServiceImpl`
  - Orquestra geracao, salva `RecommendationModel`, cria Recomendacao Geral e Recomendacao Direta.
- `RecommendationCalculationService`
  - Consolida `RecommendationCalculationResult` com `fertilizationRecommendationRows`, `nutrientBalanceRows`, `plantingFormulatedFertilizerRows` e `coverageFormulatedFertilizerRows`.
- `NutrientFertilizationCalculationService`
  - Calcula plantio, coberturas simples, acumulador NPK de cobertura e listas estruturadas de formulados.
- `CoverageFormulatedFertilizerRecommendationService`
  - Gera apenas coberturas formuladas com N, P2O5 e K2O positivos completos.
- `RecommendationReportService`
  - Gera a Recomendacao Geral e inclui coberturas simples via `filterCoverageRows(...)`.
- `GeneralRecommendationServiceImpl`
  - Persiste a Recomendacao Geral com o laudo completo.
- `DirectRecommendationServiceImpl`
  - Persiste a Recomendacao Direta e sincroniza somente micronutrientes, formulados de plantio e formulados de cobertura.
- `DirectRecommendationReportService`
  - Monta a tabela NPK da Direta por linhas estruturadas ou por fallback textual; nao mistura os dois caminhos.
- `DirectRecommendationDtoMapper`
  - Expoe no DTO as linhas estruturadas persistidas, incluindo `coverageFormulatedFertilizerLines`, mas nao possui lista para coberturas simples.
- `DirectRecommendationCoverageFormulatedFertilizerLineModel`
  - Modelo relacional apenas para coberturas formuladas.
- `DirectRecommendationCoverageFormulatedFertilizerLineRepository`
  - Consulta e remove linhas estruturadas de cobertura formulada.

## Causa da inconsistencia

A Recomendacao Geral consome a lista completa de linhas de adubacao (`fertilizationRecommendationRows`), que inclui plantio e coberturas simples.

A Recomendacao Direta, quando existem linhas estruturadas de formulado, consome somente as tabelas estruturadas de formulados. Como nao existe persistencia/DTO equivalente para linhas de cobertura simples, e como o fallback textual da secao `11. Adubação de cobertura` e desativado nesse caminho, as coberturas simples deixam de ser propagadas para o documento direto.

Em termos praticos, a condicao `hasFormulatedLines(...)` em `DirectRecommendationReportService.appendNpkTable(...)` transforma a presenca de plantio formulado em uma chave para ignorar as coberturas do laudo fonte, mesmo que `coverageFormulatedFertilizerLines` esteja vazia.

## Recomendacao para correcao

A correcao deve preservar os dados estruturados ja existentes e tratar honestamente o caso das coberturas simples.

Opcoes tecnicas:

1. Persistir linhas estruturadas genericas de NPK da Recomendacao Direta, cobrindo plantio e cobertura, a partir de `fertilizationRecommendationRows`.
2. Criar uma estrutura especifica para coberturas simples da Recomendacao Direta e renderiza-la junto das linhas formuladas.
3. Como ajuste menor, alterar `DirectRecommendationReportService.appendNpkTable(...)` para fazer fallback parcial da secao `11. Adubação de cobertura` quando houver plantio formulado, mas `coverageFormulatedFertilizerLines` estiver vazia.

A opcao 1 e a mais consistente com a Recomendacao Geral, porque usa o mesmo resultado agronomico completo e evita depender de parse textual do laudo. A opcao 3 e mais localizada, mas mantem a Direta dependente da estrutura textual do `technicalReport`.

## Conclusao

As coberturas nao somem no calculo. Elas existem em `fertilizationRecommendationRows` e sao renderizadas pela Recomendacao Geral.

Elas deixam de aparecer na Recomendacao Direta porque o documento direto prioriza as linhas estruturadas de formulados. Quando ha plantio formulado e nao ha cobertura formulada completa, a tabela direta renderiza apenas o plantio estruturado e nao reaproveita as coberturas simples calculadas no laudo geral.
