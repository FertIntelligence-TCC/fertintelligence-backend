# Investigacao: selecao de formulados NPK

Esta investigacao cobre somente o backend Spring Boot/Java. Nenhum algoritmo, DTO, entidade, endpoint ou service foi alterado.

## Arquivos inspecionados

- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/NutrientFertilizationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/FormulatedFertilizerSelectionService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/FormulatedFertilizerRatioService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/PlantingFormulatedFertilizerRecommendationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/CoverageFormulatedFertilizerRecommendationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/FertilizationRecommendationContext.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/soilFertilizerModels/FormulatedMineralFertilizerModel.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/FormulatedMineralFertilizerRepository.java`
- `src/test/java/com/migueltcc/fertintelligence/service/FormulatedFertilizerSelectionServiceTest.java`
- `src/test/java/com/migueltcc/fertintelligence/service/FormulatedFertilizerRatioServiceTest.java`

## Fluxo completo encontrado

1. `RecommendationCalculationService.calculate(...)` recebe a requisicao, resolve `FertilizerSourceOption` a partir de `dto.getOrigemAdubos()` e usa `BOTH` quando nao informado.
2. O mesmo metodo carrega entradas tecnicas em `loadRecommendationInputs(...)`, valida o contexto e monta diagnosticos.
3. A selecao de fertilizacao NPK inicia, do ponto de vista do orquestrador, em `RecommendationCalculationService.buildFertilizationRecommendations(...)`.
4. `buildFertilizationRecommendations(...)` delega para `NutrientFertilizationCalculationService.calculate(...)`.
5. `NutrientFertilizationCalculationService.calculate(...)` seleciona os intervalos tecnicos de N, P e K:
   - N: primeiro intervalo de `ContentRangeModel` para `Nutriente.NITROGENIO`.
   - P: intervalo de `Nutriente.FOSFORO` classificado pelo valor de fosforo do extrato de fertilidade.
   - K: intervalo de `Nutriente.POTASSIO` classificado pelo valor de potassio do extrato de fertilidade.
6. As necessidades principais usadas no plantio sao extraidas de `ContentRangeModel.getApplication()`:
   - `requiredN`
   - `requiredP2O5`
   - `requiredK2O`
7. O service monta dois tipos de resultado relacionados a formulados:
   - linha principal/legada de plantio em `FertilizationRecommendationRow`, por `selectBestPlantingFertilizer(...)`;
   - linhas estruturadas de formulados NPK em `plantingFormulatedFertilizerRows` e `coverageFormulatedFertilizerRows`.

## Chamadas do RecommendationCalculationService relacionadas a formulados

`RecommendationCalculationService` nao chama diretamente `FormulatedFertilizerSelectionService`, `PlantingFormulatedFertilizerRecommendationService` nem `CoverageFormulatedFertilizerRecommendationService`.

A cadeia real e:

```text
RecommendationCalculationService.calculate
  -> buildFertilizationRecommendations
    -> NutrientFertilizationCalculationService.calculate
      -> selectBestPlantingFertilizer
        -> selectFormulatedFertilizers
        -> compareScore
        -> calculateByGreatestFactor
      -> PlantingFormulatedFertilizerRecommendationService.calculate
        -> FormulatedFertilizerSelectionService.selectCandidates
          -> FormulatedFertilizerRatioService.calculateRecommendedRatio
          -> FormulatedFertilizerRatioService.calculateFormulatedRatio
          -> direct match ou fallback aproximado
      -> CoverageFormulatedFertilizerRecommendationService.calculate
        -> FormulatedFertilizerSelectionService.selectCandidates
          -> mesmo algoritmo de relacao NPK
```

O resultado retorna para `RecommendationCalculationService.buildCalculationResult(...)` por `FertilizationRecommendationContext`, nos campos:

- `recommendationRows`
- `fertilizerSuggestions`
- `nutrientBalanceRows`
- `plantingFormulatedFertilizerRows`
- `coverageFormulatedFertilizerRows`
- `requiredN`
- `requiredP2O5`
- `requiredK2O`

## Classes envolvidas

### RecommendationCalculationService

Atua como orquestrador da recomendacao. Ele carrega entradas, valida entidades, monta diagnosticos e delega a fertilizacao NPK para `NutrientFertilizationCalculationService`.

Tambem define as classes internas de saida usadas pelas linhas de formulados:

- `PlantingFormulatedFertilizerRecommendationRow`
- `CoverageFormulatedFertilizerRecommendationRow`
- `FertilizationRecommendationRow`
- `FertilizerSuggestion`
- `NutrientBalanceRow`

### NutrientFertilizationCalculationService

E o ponto onde a recomendacao NPK e calculada. Ele:

- resolve `requiredN`, `requiredP2O5` e `requiredK2O`;
- seleciona o adubo de plantio da linha principal;
- calcula cobertura com adubos simples;
- acumula NPK das coberturas para posterior selecao estruturada de formulados;
- chama os services especificos de linhas estruturadas de formulados.

### FormulatedFertilizerSelectionService

E o algoritmo comum de selecao estruturada de formulados NPK. Ele recebe necessidades N/P2O5/K2O e retorna candidatos ordenados, com indicacao se a selecao foi direta ou por fallback.

Tambem resolve a lista de formulados conforme `FertilizerSourceOption`.

### FormulatedFertilizerRatioService

Calcula e compara relacoes N-P2O5-K2O. Ele normaliza uma tripla dividindo cada componente pelo menor valor positivo da propria tripla.

Exemplo coberto em teste: `20, 80, 40` vira relacao `1, 4, 2`.

### PlantingFormulatedFertilizerRecommendationService

Monta as linhas estruturadas de formulados para plantio. Usa `FormulatedFertilizerSelectionService.selectCandidates(...)`, limita a apresentacao a 2 candidatos e gera metadados como relacao usada, tipo de selecao, dose em kg/ha e conversoes por espacamento.

### CoverageFormulatedFertilizerRecommendationService

Monta linhas estruturadas de formulados para cobertura. Recebe uma lista de `CoverageNpkRecommendation`, exige que cada cobertura tenha N, P2O5 e K2O positivos para forcar selecao de formulado NPK e usa o mesmo seletor comum de formulados.

### FormulatedMineralFertilizerRepository

Fornece as listas de formulados por origem:

- privados do usuario;
- publicos de usuarios que nao sao `USUARIO_SUPREMO`;
- padrao do criador `USUARIO_SUPREMO`;
- combinacoes deduplicadas para `BOTH` e `ALL`.

### FormulatedMineralFertilizerModel

Contem as garantias usadas pelo algoritmo:

- `N`
- `P2O5`
- `K2O`

O model tambem possui `formulate` e `relation`, mas a selecao estruturada atual recalcula a relacao a partir de `N`, `P2O5` e `K2O` via `FormulatedFertilizerRatioService`.

## Algoritmo atual: linha principal/legada de plantio

Este fluxo acontece em `NutrientFertilizationCalculationService.selectBestPlantingFertilizer(...)` e alimenta `FertilizationRecommendationRow` e `FertilizerSuggestion`.

1. Busca formulados conforme `FertilizerSourceOption`.
2. Filtra produtos em que pelo menos um dos tres macronutrientes primarios seja maior que zero.
3. Escolhe um unico produto por `max(...)` usando `compareScore(...)`.
4. `compareScore(...)` calcula quantos nutrientes requeridos positivos o produto consegue cobrir com concentracao positiva.
5. Se a pontuacao empatar e houver necessidade positiva de P, usa maior concentracao de P2O5 como criterio secundario.
6. Se ainda empatar, usa o ID de forma invertida no comparador usado por `max`, favorecendo o menor ID.
7. A dose e calculada por `calculateByGreatestFactor(...)`:
   - monta candidatos de dose para cada nutriente com necessidade positiva e concentracao positiva;
   - cada dose e `necessidade / concentracao * 100`;
   - seleciona a maior dose calculada.
8. Os nutrientes fornecidos sao calculados pela dose escolhida multiplicada pelas concentracoes do produto.
9. Os saldos sao `fornecido - requerido`, deixando excedentes e deficits explicitos.
10. Se nao houver formulado, tenta adubo mineral simples com a mesma estrategia geral.

Este caminho pode gerar excedentes porque a dose comercial e definida pelo maior fator necessario entre os nutrientes cobertos pelo produto selecionado.

## Algoritmo atual: selecao estruturada de formulados NPK

Este fluxo acontece em `FormulatedFertilizerSelectionService` e e usado por:

- `PlantingFormulatedFertilizerRecommendationService`;
- `CoverageFormulatedFertilizerRecommendationService`.

### Calculo da relacao recomendada

`FormulatedFertilizerRatioService.calculateRecommendedRatio(...)`:

1. trata valores nulos, invalidos, infinitos ou negativos como zero e gera mensagem tecnica;
2. encontra o menor valor positivo entre N, P2O5 e K2O;
3. divide cada componente pelo menor valor positivo;
4. arredonda cada componente para 2 casas decimais;
5. se nao houver nenhum valor positivo, a relacao nao e calculada.

### Calculo da relacao do formulado

`FormulatedFertilizerRatioService.calculateFormulatedRatio(...)` aplica a mesma regra sobre `fertilizer.getN()`, `fertilizer.getP2O5()` e `fertilizer.getK2O()`.

### Filtro de produto aplicavel

`FormulatedFertilizerSelectionService.hasValidNpkConcentrations(...)` exige:

- fertilizante nao nulo;
- N, P2O5 e K2O nao nulos;
- valores finitos;
- valores maiores ou iguais a zero;
- soma N + P2O5 + K2O maior que zero.

Observacao: concentracao zero em um componente pode ser aceita quando a relacao recomendada tambem comporta zero naquele componente. O teste `acceptsFormulatedFertilizerWithZeroComponentWhenNpkSumIsValid` cobre esse comportamento.

### Dose do candidato estruturado

Para cada candidato valido, a dose e:

```text
dose kg/ha = 100 * (requiredN positivo + requiredP2O5 positivo + requiredK2O positivo)
             / (N% + P2O5% + K2O% do formulado)
```

A dose e arredondada para 2 casas decimais.

Essa dose nao e calculada por nutriente limitante individual. Ela usa o somatorio das necessidades positivas e o somatorio das concentracoes NPK do produto.

### Selecao direta

`selectDirectMatches(...)` seleciona todos os produtos cuja relacao calculada bate completamente com a relacao recomendada.

A comparacao usa tolerancia de `0.01` por componente:

```text
abs(recomendado - formulado) <= 0.01
```

Os candidatos diretos sao ordenados por:

1. maior `fertilizerDoseKgHa`;
2. maior soma de concentracoes N + P2O5 + K2O;
3. nome formatado do produto;
4. ID do produto.

Na apresentacao de plantio e cobertura, os consumers limitam a 2 candidatos.

### Fallback por aproximacao

Se nao houver correspondencia direta, `selectFallbackByApproximation(...)`:

1. exige que a soma da relacao recomendada seja positiva;
2. calcula a distancia absoluta entre a soma da relacao do formulado e a soma da relacao recomendada;
3. ordena os produtos por menor distancia;
4. desempata por maior soma de concentracoes;
5. desempata por nome;
6. desempata por ID;
7. limita internamente a 2 candidatos;
8. marca os candidatos como fallback aproximado;
9. reordena os 2 candidatos finais pela ordenacao de dose usada tambem na selecao direta.

Mensagem tecnica atual do fallback:

```text
Sem correspondencia direta da relacao N-P2O5-K2O recomendada; foi usado fallback por aproximacao pelo somatorio da relacao ou das concentracoes normalizadas.
```

## Cobertura estruturada

As linhas estruturadas de cobertura dependem de `CoverageNpkAccumulator`, interno em `NutrientFertilizationCalculationService`.

1. Durante `buildCoverageRows(...)`, cada `CoverageModel` cadastrado em cada intervalo selecionado adiciona sua dose a um acumulador por ordem de cobertura.
2. O acumulador soma N, P2O5 e K2O por `coverageOrder`.
3. `CoverageFormulatedFertilizerRecommendationService.calculate(...)` recebe a lista acumulada.
4. O service so tenta selecionar formulado NPK quando a cobertura tem recomendacao positiva completa de N, P2O5 e K2O.
5. Quando algum nutriente esta ausente, zero ou invalido, adiciona warning e nao forca a selecao de formulado NPK para aquela cobertura.

## Dependencias

- `ContentRangeRepository`: fornece os intervalos de N, P e K da tabela de adubacao.
- `CoverageRepository`: fornece as coberturas por intervalo selecionado.
- `FormulatedMineralFertilizerRepository`: fornece formulados por origem.
- `SimpleMineralFertilizerRepository`: usado no fluxo de plantio legado como fallback e nas coberturas principais com simples.
- `AlternativeFertilizationCalculationService`: executado no mesmo calculo NPK, mas nao e o seletor de formulados NPK investigado aqui.
- `CropSpacingCalculationService`: converte a dose dos formulados estruturados para unidades operacionais quando ha dados suficientes de espacamento.
- `FertilizerSourceOption`: controla a origem dos produtos considerados (`PRIVATE`, `PUBLIC`, `DEFAULT`, `BOTH`, `ALL`).
- `Cargo.USUARIO_SUPREMO`: usado para diferenciar registros padrao.

## Criterios atualmente utilizados

### Para a linha principal/legada de plantio

- Quantidade de nutrientes requeridos positivos que o produto cobre com concentracao positiva.
- Maior concentracao de P2O5 como desempate quando P e requerido.
- ID como desempate final.
- Dose pelo maior fator individual entre N, P2O5 e K2O.
- Fallback para adubo simples quando nao ha formulado.

### Para linhas estruturadas de formulados

- Relacao N-P2O5-K2O recomendada normalizada pelo menor valor positivo.
- Relacao N-P2O5-K2O do formulado normalizada pelo menor valor positivo.
- Correspondencia direta componente a componente com tolerancia `0.01`.
- Fallback pela menor distancia entre soma das relacoes normalizadas.
- Desempate por maior soma de concentracoes NPK.
- Desempate por nome e ID.
- Dose pelo somatorio das necessidades positivas dividido pelo somatorio das concentracoes NPK.
- Limite de apresentacao de 2 candidatos.
- Respeito a origem dos adubos selecionada na recomendacao.

## Pontos recomendados para substituicao

1. `NutrientFertilizationCalculationService.selectBestPlantingFertilizer(...)`
   - Ponto central da selecao de produto da linha principal/legada.
   - E onde hoje existem `compareScore(...)` e dose por maior fator, comportamento diretamente associado a excedentes.

2. `NutrientFertilizationCalculationService.compareScore(...)`
   - Concentra a regra de comparacao da linha principal.
   - E um ponto pequeno para substituir criterio de ranking sem mexer no restante do fluxo.

3. `NutrientFertilizationCalculationService.calculateByGreatestFactor(...)`
   - Concentra o calculo de dose por maior fator individual.
   - Deve ser revisto se a nova regra precisar evitar excedentes ou otimizar balanco.

4. `FormulatedFertilizerSelectionService.selectCandidates(...)`
   - Ponto central da selecao estruturada de formulados.
   - Permite substituir ou expandir a estrategia de relacao direta/fallback sem alterar os services de apresentacao.

5. `FormulatedFertilizerSelectionService.selectDirectMatches(...)`
   - Ponto especifico para alterar o conceito de correspondencia direta.

6. `FormulatedFertilizerSelectionService.selectFallbackByApproximation(...)`
   - Ponto especifico para substituir a aproximacao por soma de relacoes normalizadas.

7. `FormulatedFertilizerSelectionService.calculateDoseKgHa(...)`
   - Ponto especifico para substituir a dose por somatorio de nutrientes e concentracoes.

8. `CoverageFormulatedFertilizerRecommendationService.hasCompletePositiveNpk(...)`
   - Ponto de decisao que hoje impede formulado em coberturas sem N, P2O5 e K2O positivos completos.

9. `CoverageNpkAccumulator` dentro de `NutrientFertilizationCalculationService`
   - Ponto onde as doses de cobertura sao agregadas por ordem antes de entrar na selecao estruturada.

## Limitacoes e avisos tecnicos

- A implementacao possui dois mecanismos de selecao de formulados coexistindo: um para a linha principal/legada e outro para as linhas estruturadas de formulados. Eles nao usam os mesmos criterios.
- A selecao estruturada recalcula relacao a partir das garantias `N`, `P2O5` e `K2O`; nao usa diretamente o campo persistido `relation` do model.
- A dose estruturada por somatorio NPK pode nao representar atendimento exato nutriente a nutriente.
- O fallback estruturado por soma de relacoes pode aproximar produtos com distribuicoes diferentes, desde que a soma da relacao normalizada fique proxima.
- As linhas estruturadas de cobertura so aparecem quando a cobertura acumulada tem N, P2O5 e K2O positivos.
- Este documento nao recomenda uma nova regra agronomica; apenas aponta pontos tecnicos para substituicao em prompt futuro.
