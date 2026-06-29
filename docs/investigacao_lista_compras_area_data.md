# Investigacao da area e da data da Lista de Compras

## Objetivo

Investigar de onde a Lista de Compras obtem a area usada nos totais de compra e a data de plantio exibida no relatorio.

Esta investigacao nao altera codigo.

## Fluxo encontrado

### Geracao automatica por recomendacao

1. O endpoint `GET /shopping-list/get-by-recommendation` chega em `ShoppingListControllerImpl.getByRecommendation(...)`.
2. `ShoppingListServiceImpl.getByRecommendation(...)` carrega a `RecommendationModel` por `RecommendationRepository.findByIdForUpdate(...)`, valida permissao de leitura sobre `recommendation.getPlot()` e procura uma `ShoppingListModel` ja existente.
3. Se a Lista de Compras ainda nao existe, o service chama `shoppingListReportService.build(recommendation)`.
4. `ShoppingListReportService.build(...)` monta o texto do documento e persiste esse texto por `ShoppingListServiceImpl.createInitial(...)`.
5. Depois de persistida, a Lista de Compras nao e recalculada automaticamente no mesmo caminho; chamadas futuras retornam o `technicalReport` ja salvo em `ShoppingListModel`.

### Criacao manual

1. O endpoint `POST /shopping-list/register` recebe `ShoppingListCreateRequestDto`.
2. O DTO exige `id_recomendacao` e `laudo_tecnico`.
3. `ShoppingListServiceImpl.create(...)` valida a recomendacao e salva o texto recebido pelo cliente usando `createInitial(recommendation, dto.getTechnicalReport())`.
4. Nesse caminho, a area e a data exibidas no documento dependem exclusivamente do conteudo textual enviado no request. O backend nao recalcula nem valida esses campos contra talhao ou cultura.

## Classes envolvidas

- `ShoppingListControllerImpl`: expõe os endpoints `/shopping-list/register`, `/shopping-list/get`, `/shopping-list/get-by-recommendation`, `/shopping-list/update` e `/shopping-list/delete`.
- `ShoppingListServiceImpl`: coordena criacao, consulta, atualizacao e remocao da `ShoppingListModel`.
- `ShoppingListReportService`: constroi automaticamente o texto da Lista de Compras quando ela ainda nao existe para uma recomendacao.
- `TechnicalRecommendationDocumentSupport`: fornece o cabecalho compartilhado de identificacao, coleta itens de compra do laudo tecnico e das linhas estruturadas da Recomendacao Direta, e calcula totais por area.
- `ShoppingListModel`: persiste o texto final em `TECHNICAL_REPORT` e se relaciona 1:1 com `RecommendationModel`.
- `RecommendationModel`: fornece `property`, `plot`, `cropName`, `cropYear`, `creator`, `createdAt` e o `technicalReport` usado como fonte parcial de itens.
- `PlotModel`: fornece a area usada atualmente pela Lista de Compras.
- `CropModel`: possui `usedAreaInThePlot` e `plantingDate`, mas esses campos nao sao usados pela Lista de Compras automatica.
- Repositorios de linhas da Recomendacao Direta: `DirectRecommendationMicronutrientFertilizerLineRepository`, `DirectRecommendationPlantingFormulatedFertilizerLineRepository` e `DirectRecommendationCoverageFormulatedFertilizerLineRepository`, usados para coletar insumos estruturados.

## Origem da area

Na geracao automatica, `ShoppingListReportService.build(...)` faz:

```java
PlotModel plot = recommendation.getPlot();
Double area = plot != null ? plot.getArea() : null;
```

Essa area e exibida como `Área usada para totalização` e tambem e usada em `TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area)` para calcular `kg/ha * area`.

O mesmo cabecalho compartilhado tambem exibe `Área avaliada` usando `recommendation.getPlot().getArea()`.

Conclusao: a area efetivamente usada pela Lista de Compras automatica vem de `RecommendationModel.plot.area`, isto e, de `PlotModel.area` (`TALHOES.AREA`).

## Origem da data de plantio

Na Lista de Compras automatica, a data de plantio vem do cabecalho compartilhado `TechnicalRecommendationDocumentSupport.appendIdentification(...)`.

O metodo nao consulta cultura, pasta anual ou DTO de criacao. Ele escreve a data de plantio como constante:

```java
report.append("- Data de plantio: ").append(NOT_INFORMED).append("\n");
```

Conclusao: a data de plantio exibida na Lista de Compras automatica nao vem de entidade persistida. Ela e sempre `Não informado.` no cabecalho gerado pelo backend.

No caminho manual (`POST /shopping-list/register`) e no caminho de atualizacao (`PUT /shopping-list/update`), qualquer data exibida vem do texto enviado pelo cliente em `technicalReport` ou `newTechnicalReport`, sem verificacao contra `CropModel.plantingDate`.

## Entidades que efetivamente fornecem as informacoes

### Area usada hoje

- Entidade efetiva: `PlotModel`.
- Campo efetivo: `area`.
- Coluna: `TALHOES.AREA`.
- Caminho: `ShoppingListReportService.build(...) -> recommendation.getPlot().getArea()`.

### Data de plantio usada hoje

- Entidade efetiva: nenhuma, no caminho automatico.
- Valor efetivo: constante `TechnicalRecommendationDocumentSupport.NOT_INFORMED`.
- Caminho: `ShoppingListReportService.build(...) -> TechnicalRecommendationDocumentSupport.appendIdentification(...)`.

### Dados disponiveis, mas nao usados pela Lista de Compras automatica

- `CropModel.usedAreaInThePlot`, coluna `CULTURAS.AREA_USADA_NO_TALHAO`.
- `CropModel.plantingDate`, colunas `CULTURAS.DATA_PLANTIO_DIA`, `CULTURAS.DATA_PLANTIO_MES`, `CULTURAS.DATA_PLANTIO_ANO`.

Esses dados sao carregados durante o calculo da recomendacao em `RecommendationCalculationService`, mas o `RecommendationCalculationResult` persistido em `RecommendationModel` guarda apenas `cropName`, `cropYear` e referencias de tabela/criterios. Ele nao persiste `cropId`, area da cultura nem data de plantio.

## Divergencia entre area da cultura e area do talhao

Existe divergencia potencial e ela e estrutural:

- `PlotModel.area` representa a area total cadastrada do talhao.
- `CropModel.usedAreaInThePlot` representa a area ocupada pela cultura dentro do talhao.
- A Lista de Compras usa `PlotModel.area`.
- A Lista de Compras nao usa `CropModel.usedAreaInThePlot`.

Portanto, se a cultura recomendada ocupa apenas parte do talhao, os totais de compra da Lista de Compras serao calculados sobre a area total do talhao, nao sobre a area efetivamente usada pela cultura na recomendacao.

Exemplo de impacto: para uma dose de 100 kg/ha, um talhao de 50 ha e uma cultura ocupando 20 ha, a Lista de Compras automatica totalizaria 5.000 kg, enquanto a totalizacao pela area da cultura seria 2.000 kg.

## Inconsistencias identificadas

1. A Lista de Compras automatica usa area do talhao para totalizacao, embora exista area especifica da cultura no modelo.
2. O cabecalho da Lista de Compras chama a area de `Área avaliada`, mas tambem a obtem de `PlotModel.area`, nao da cultura usada na recomendacao.
3. A data de plantio e sempre `Não informado.` no caminho automatico, apesar de existir `CropModel.plantingDate`.
4. `RecommendationModel` nao guarda referencia direta para `CropModel` nem copia os campos `usedAreaInThePlot` e `plantingDate`; apos a recomendacao ser persistida, a Lista de Compras automatica nao tem acesso simples e imutavel aos dados da cultura originalmente usada.
5. O caminho manual permite persistir texto com area/data arbitrarias, sem validacao de coerencia com `RecommendationModel`, `PlotModel` ou `CropModel`.

## Recomendacao para correcao

Para corrigir sem ambiguidade, a origem da area e da data deve ser definida como parte do contrato da recomendacao:

1. Persistir na recomendacao os dados da cultura efetivamente usada no calculo, ao menos `cropId`, `cropUsedAreaInThePlot` e `cropPlantingDate`, ou criar relacionamento persistido entre `RecommendationModel` e `CropModel`.
2. Alterar a Lista de Compras automatica para totalizar pela area da cultura usada na recomendacao quando esse dado estiver disponivel.
3. Manter fallback honesto para `PlotModel.area` apenas quando a recomendacao antiga nao possuir area de cultura persistida, exibindo aviso tecnico no documento.
4. Exibir a data de plantio a partir da cultura usada na recomendacao quando disponivel.
5. Diferenciar no texto do relatorio `Área do talhão` e `Área da cultura considerada`, evitando que `Área avaliada` oculte a origem real.
6. Para documentos criados/atualizados manualmente, considerar validacao ou regeneracao controlada para evitar que o texto persistido divirja dos dados oficiais.

## Referencias de codigo

- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/ShoppingListReportService.java`: linhas 27-42 e 49-55.
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/TechnicalRecommendationDocumentSupport.java`: linhas 72-84.
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/ShoppingListServiceImpl.java`: linhas 32-37, 42-50 e 81-87.
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/PlotModel.java`: campo `area`.
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/cropModels/CropModel.java`: campos `usedAreaInThePlot` e `plantingDate`.
