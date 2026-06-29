# Investigacao do fluxo de micronutrientes na Recomendacao Resumida

Esta investigacao cobre somente o backend Spring Boot/Java. Nenhuma funcionalidade, endpoint, DTO, entidade, regra agronomica ou algoritmo foi alterado.

## Objetivo

Identificar por que a Recomendacao Resumida apresenta micronutrientes como "Nao calculado", mesmo quando a engine agronomica calcula micronutrientes e a Recomendacao Geral os apresenta.

## Fluxo encontrado

### 1. Geracao da recomendacao principal

Arquivo: `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationServiceImpl.java`

O metodo `generate` executa a engine por `recommendationCalculationService.calculate(...)`, monta o laudo geral por `recommendationReportService.buildTechnicalReport(calculationResult)`, aplica a narrativa e persiste o `RecommendationModel` com `technicalReport`.

Depois disso:

- cria a Recomendacao Geral com o `improvedReport`;
- cria a Recomendacao Direta com o relatorio direto e com listas estruturadas vindas de `calculationResult`;
- nao cria a Recomendacao Resumida nesse fluxo inicial.

Trecho relevante:

- `RecommendationServiceImpl.java:96-103`
  - `generalRecommendationService.createInitial(savedRecommendation, improvedReport)`
  - `directRecommendationService.createInitial(..., calculationResult.getMicronutrientFertilizerRows(), ...)`

### 2. Calculo dos micronutrientes

Arquivo: `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/AlternativeFertilizationCalculationService.java`

O fluxo de micronutrientes parte de:

- diagnostico quimico do solo;
- tabela `MicronutrientDoseModel`;
- faixas de `DiverseContentRangeModel`;
- selecao de fonte por `MicronutrientFertilizerSelectionService`.

Quando existe dose cadastrada, o servico monta dois tipos de saida:

- linhas alternativas para o laudo geral: `AlternativeFertilizationRecommendationRow`;
- linhas estruturadas para a recomendacao direta: `MicronutrientFertilizerRecommendationRow`.

Trechos relevantes:

- `AlternativeFertilizationCalculationService.java:229-256`
  - identifica micronutrientes do diagnostico, carrega dose e faixa, seleciona fontes.
- `AlternativeFertilizationCalculationService.java:258-270`
  - quando calculado, adiciona uma linha `MICRONUTRIENTE` em `alternativeFertilizationRows`.
- `AlternativeFertilizationCalculationService.java:259`
  - tambem adiciona a linha estruturada em `directRows`.
- `AlternativeFertilizationCalculationService.java:314-341`
  - converte a selecao em `MicronutrientFertilizerRecommendationRow`, incluindo micronutriente, dose do elemento, fonte, concentracao, dose do adubo e conversoes por espacamento.

### 3. Resultado de calculo

Arquivo: `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationCalculationService.java`

O resultado final inclui:

- `alternativeFertilizationRows`;
- `micronutrientFertilizerRows`;
- linhas de formulados de plantio e cobertura.

Trechos relevantes:

- `RecommendationCalculationService.java:280`
  - propaga `recommendations.micronutrientFertilizerRows()` para `RecommendationCalculationResult`.
- `RecommendationCalculationService.java:1615`
  - `RecommendationCalculationResult` possui `List<MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows`.

### 4. Recomendacao Geral

Arquivos:

- `src/main/java/com/migueltcc/fertintelligence/service/implementation/GeneralRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationReportService.java`

A Recomendacao Geral persiste o proprio laudo tecnico completo gerado a partir de `RecommendationCalculationResult`.

No laudo geral, os micronutrientes calculados entram na secao:

`13.1. Fontes organicas, organominerais e micronutrientes`

Essa secao percorre `result.getAlternativeFertilizationRows()`. Como os micronutrientes calculados sao adicionados a essa lista como linhas `MICRONUTRIENTE`, eles aparecem no documento geral.

Trechos relevantes:

- `RecommendationReportService.java:286-303`
  - monta a tabela "Fontes organicas, organominerais e micronutrientes" a partir de `alternativeFertilizationRows`.
- `GeneralRecommendationServiceImpl.java:40-58`
  - persiste o relatorio recebido em `GeneralRecommendationModel.technicalReport`.

### 5. Recomendacao Direta

Arquivos:

- `src/main/java/com/migueltcc/fertintelligence/service/implementation/DirectRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/DirectRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/DirectRecommendationDtoMapper.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/DirectRecommendationMicronutrientFertilizerLineModel.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/DirectRecommendationMicronutrientFertilizerLineRepository.java`

A Recomendacao Direta recebe explicitamente `calculationResult.getMicronutrientFertilizerRows()` no momento em que a recomendacao e gerada.

O servico direto:

- sincroniza linhas em `DIRECT_RECOMMENDATION_MICRONUTRIENT_FERTILIZER_LINES`;
- recria o relatorio direto quando existem linhas estruturadas;
- expoe as linhas estruturadas no DTO direto.

Trechos relevantes:

- `DirectRecommendationServiceImpl.java:82-90`
  - sincroniza linhas estruturadas e reconstroi o relatorio direto.
- `DirectRecommendationServiceImpl.java:120-130`
  - salva as linhas de micronutrientes.
- `DirectRecommendationServiceImpl.java:136-149`
  - mapeia `MicronutrientFertilizerRecommendationRow` para `DirectRecommendationMicronutrientFertilizerLineModel`.
- `DirectRecommendationReportService.java:114-134`
  - se existirem linhas diretas, monta a "Tabela de micronutrientes" com micronutriente, adubo, dose do elemento, dose do adubo e conversao por espacamento.
- `DirectRecommendationDtoMapper.java:48-88`
  - expoe `micronutrientFertilizerLines` no DTO de resposta da Recomendacao Direta.

### 6. Recomendacao Resumida

Arquivos:

- `src/main/java/com/migueltcc/fertintelligence/service/implementation/SummaryRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/SummaryRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/SummaryRecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/summaryRecommendation/SummaryRecommendationResponseDto.java`

A Recomendacao Resumida nao e criada no fluxo inicial de `RecommendationServiceImpl.generate`. Ela e criada sob demanda em `SummaryRecommendationServiceImpl.getByRecommendation(...)`, caso ainda nao exista.

Quando e criada automaticamente, o conteudo vem de:

`summaryRecommendationReportService.build(recommendation)`

Esse builder recebe apenas `RecommendationModel`, le `recommendation.getTechnicalReport()` como fonte textual e extrai algumas secoes do Markdown. Ele nao recebe `RecommendationCalculationResult`, nao consulta `DirectRecommendationMicronutrientFertilizerLineRepository` e nao utiliza `DirectRecommendationModel`.

Trechos relevantes:

- `SummaryRecommendationServiceImpl.java:81-87`
  - cria a resumida sob demanda usando `summaryRecommendationReportService.build(recommendation)`.
- `SummaryRecommendationServiceImpl.java:53-58`
  - persiste somente `technicalReport` textual no `SummaryRecommendationModel`.
- `SummaryRecommendationReportService.java:16-18`
  - usa somente `RecommendationModel` e `recommendation.getTechnicalReport()` como fonte.
- `SummaryRecommendationReportService.java:48-52`
  - copia a subsecao "Fontes organicas, organominerais e micronutrientes" para a parte de adubacao organica.
- `SummaryRecommendationReportService.java:54-60`
  - escreve Boro, Cobre, Ferro, Manganes e Zinco com `NOT_CALCULATED` de forma fixa.
- `SummaryRecommendationResponseDto.java`
  - expoe apenas `technicalReport`/`content`; nao ha campos estruturados de micronutrientes na resposta resumida.

## Onde os micronutrientes deixam de ser propagados

Os micronutrientes nao se perdem na engine agronomica. Eles sao calculados em `AlternativeFertilizationCalculationService` e propagados para:

- a Recomendacao Geral, por `alternativeFertilizationRows`;
- a Recomendacao Direta, por `micronutrientFertilizerRows` e pela tabela `DIRECT_RECOMMENDATION_MICRONUTRIENT_FERTILIZER_LINES`.

A perda ocorre na montagem da Recomendacao Resumida:

1. `SummaryRecommendationReportService.build(...)` nao recebe as listas estruturadas calculadas.
2. O builder nao busca as linhas estruturadas ja persistidas da Recomendacao Direta.
3. A secao "Recomendacao de micronutrientes" e hardcoded com `NOT_CALCULATED` para B, Cu, Fe, Mn e Zn.
4. A unica informacao relacionada a micronutrientes que a resumida reaproveita fica misturada na secao "Recomendacao de adubacao organica", pois ela copia a subsecao geral "Fontes organicas, organominerais e micronutrientes".

## Comparacao entre Geral e Resumida

### Recomendacao Geral

- Fonte de dados: `RecommendationCalculationResult`.
- Usa linhas calculadas de `alternativeFertilizationRows`.
- Os micronutrientes entram como linhas `MICRONUTRIENTE`.
- Documento persistido: `GeneralRecommendationModel.technicalReport`.

### Recomendacao Resumida

- Fonte de dados: `RecommendationModel.technicalReport` ja renderizado em texto.
- Nao usa `RecommendationCalculationResult`.
- Nao usa `micronutrientFertilizerRows`.
- Nao consulta `DirectRecommendationMicronutrientFertilizerLineRepository`.
- Escreve a secao especifica de micronutrientes com valores fixos "Nao calculado por falta de dados."
- Documento persistido: `SummaryRecommendationModel.technicalReport`.

## Objetos, DTOs, builders e mapeadores envolvidos

### Objetos de calculo

- `RecommendationCalculationService.RecommendationCalculationResult`
- `RecommendationCalculationService.AlternativeFertilizationRecommendationRow`
- `RecommendationCalculationService.MicronutrientFertilizerRecommendationRow`
- `FertilizationRecommendationContext`
- `MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult`

### Modelos persistidos

- `RecommendationModel`
- `GeneralRecommendationModel`
- `SummaryRecommendationModel`
- `DirectRecommendationModel`
- `DirectRecommendationMicronutrientFertilizerLineModel`

### DTOs

- `RecommendationResponseDto`
- `GeneralRecommendationResponseDto`
- `SummaryRecommendationResponseDto`
- `DirectRecommendationResponseDto`
- `DirectRecommendationMicronutrientFertilizerLineResponseDto`

### Builders/servicos de documento

- `RecommendationReportService`
- `SummaryRecommendationReportService`
- `DirectRecommendationReportService`
- `TechnicalRecommendationDocumentSupport`

### Mapeadores

- `DirectRecommendationDtoMapper`
- mapeamento interno `SummaryRecommendationServiceImpl.toDto(...)`
- mapeamento interno `GeneralRecommendationServiceImpl.toDto(...)`

## Causa provavel da inconsistencia

A causa provavel e uma falha de montagem/mapeamento da Recomendacao Resumida, nao uma falha do calculo agronomico.

O builder da resumida possui uma secao fixa:

- Boro: `NOT_CALCULATED`
- Cobre: `NOT_CALCULATED`
- Ferro: `NOT_CALCULATED`
- Manganes: `NOT_CALCULATED`
- Zinco: `NOT_CALCULATED`

Essa secao nao e alimentada por nenhuma das fontes onde os micronutrientes calculados existem:

- `RecommendationCalculationResult.alternativeFertilizationRows`;
- `RecommendationCalculationResult.micronutrientFertilizerRows`;
- `DirectRecommendationMicronutrientFertilizerLineModel`;
- tabela "13.1. Fontes organicas, organominerais e micronutrientes" do laudo geral.

Por isso a Recomendacao Geral pode mostrar os micronutrientes corretamente enquanto a Recomendacao Resumida mostra "Nao calculado".

## Recomendacao tecnica para correcao

Manter a correcao concentrada no backend e evitar recalcular a agronomia dentro da Recomendacao Resumida.

Opcoes tecnicas, em ordem de menor risco:

1. Fazer `SummaryRecommendationReportService` consultar as linhas estruturadas ja persistidas da Recomendacao Direta (`DirectRecommendationMicronutrientFertilizerLineRepository`) e renderizar a secao "Recomendacao de micronutrientes" a partir delas.
2. Caso nao existam linhas estruturadas, usar fallback honesto extraindo linhas `MICRONUTRIENTE` da subsecao "13.1. Fontes organicas, organominerais e micronutrientes" do laudo geral.
3. Se nenhuma das duas fontes existir, manter `NOT_CALCULATED` com aviso tecnico de ausencia de dados estruturados.

A primeira opcao reaproveita o dado mais confiavel atualmente persistido: micronutriente, dose do elemento, fonte, concentracao, dose do adubo e conversoes por espacamento ja calculadas para a Recomendacao Direta.

## Conclusao

Os micronutrientes calculados chegam ao laudo geral e a Recomendacao Direta. A Recomendacao Resumida deixa de propaga-los porque seu builder nao consome a lista estruturada nem parseia as linhas calculadas; em vez disso, grava a secao especifica de micronutrientes com valores fixos `NOT_CALCULATED`.
