# Investigacao: espacamento e Recomendacao Direta no backend

## Escopo

Investigacao do fluxo atual de cultura anual, espacamento e geracao da Recomendacao Direta. Nenhuma regra de negocio nova foi implementada neste prompt.

Branch inspecionada: `m-fertilization`.

## Arquivos inspecionados

### Cultura anual

- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/cropModels/CropModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/crop/CropCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/crop/CropPostRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/crop/CropResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/documentation/CropController.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/CropControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/documentation/CropService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/CropServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/CropRepository.java`

### Pasta de culturas anuais

- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/AnnualCropFolderModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/annualCropFolder/AnnualCropFolderCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/annualCropFolder/AnnualCropFolderPostRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/annualCropFolder/AnnualCropFolderResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/documentation/AnnualCropFolderController.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/AnnualCropFolderControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/documentation/AnnualCropFolderService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/AnnualCropFolderServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/AnnualCropFolderRepository.java`

### Recommendation e motor agronomico

- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/RecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/documentation/RecommendationController.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/RecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/documentation/RecommendationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/NutrientFertilizationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/FertilizationRecommendationContext.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationNarrativeService.java`

### Documentos derivados

- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/DirectRecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/directRecommendation/DirectRecommendationCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/directRecommendation/DirectRecommendationPostRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/directRecommendation/DirectRecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/documentation/DirectRecommendationController.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/DirectRecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/documentation/DirectRecommendationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/DirectRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/DirectRecommendationRepository.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/SummaryRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/ShoppingListServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/DirectRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/SummaryRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/ShoppingListReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/TechnicalRecommendationDocumentSupport.java`

Nao foram encontradas classes com os nomes literais `DirectRecommendationBuilder`, `SummaryRecommendationBuilder` ou `ShoppingListBuilder`. O papel de builder/gerador esta hoje nos services `DirectRecommendationReportService`, `SummaryRecommendationReportService` e `ShoppingListReportService`.

## Campos encontrados

### Distancia entre linhas

Existe no modelo persistente de cultura:

- `CropModel.distanceBetweenLines`
- coluna JPA: `DISTANCIA_ENTRE_LINHAS`
- `nullable = false`

DTOs atuais:

- `CropCreateRequestDto.distanceBetweenLines`, JSON `distancia_entre_linhas`, obrigatorio.
- `CropPostRequestDto.distanceBetweenLines`, JSON `novo_distancia_entre_linhas`, opcional em update.
- `CropResponseDto.distanceBetweenLines`, JSON `distancia_entre_linhas`.

Uso atual confirmado:

- `CropServiceImpl.createCrop` copia o valor do create DTO para `CropModel`.
- `CropServiceImpl.updateCrop` atualiza o campo quando `novo_distancia_entre_linhas` e informado.
- `CropModel.toDto` devolve o valor na resposta.
- `RecommendationCalculationService` carrega a cultura, mas nao usa `distanceBetweenLines` no calculo de NPK ou nos documentos atuais.

### Plantas por metro

Existe no modelo persistente de cultura:

- `CropModel.plantsPerMeter`
- coluna JPA: `N_PLANTAS_POR_METRO`
- `nullable = false`

DTOs atuais:

- `CropCreateRequestDto.plantsPerMeter`, JSON `numero_plantas_por_metro`, obrigatorio.
- `CropPostRequestDto.plantsPerMeter`, JSON `novo_numero_plantas_por_metro`, opcional em update.
- `CropResponseDto.plantsPerMeter`, JSON `numero_plantas_por_metro`.

Uso atual confirmado:

- Persistido e retornado pela camada de cultura.
- Nao participa dos calculos de `RecommendationCalculationService`, `NutrientFertilizationCalculationService`, `DirectRecommendationReportService` ou `SummaryRecommendationReportService`.

### Populacao estimada

Nao foi encontrado campo persistente nem DTO especifico para populacao estimada em `CropModel`, nos DTOs de crop ou no fluxo de recommendation.

Populacao tambem nao e calculada no motor atual. O unico uso agronomico de cultura dentro do calculo de fertilizacao NPK e para:

- validar cultura versus tabela de adubacao;
- montar identificacao/resumo;
- usar datas fenologicas no texto de aplicacao de cobertura.

### Espacamento

Ha dois conceitos separados no codigo atual:

1. Espacamento da cultura registrada:
   - `distanceBetweenLines`
   - `plantsPerMeter`

2. Espacamento sugerido/usado da tabela de adubacao de culturas:
   - `CropFertilizationTableCreateRequestDto.suggested_spacing`
   - `CropFertilizationTableCreateRequestDto.used_spacing`
   - `CropFertilizationTableCreateRequestDto.used_spacing_value`
   - `CropFertilizationTableCreateRequestDto.used_spacing_maximum_value`
   - campos equivalentes em `CropFertilizationTablePostRequestDto` e `CropFertilizationTableResponseDto`
   - enum `SpacingType`

Esses campos da tabela de adubacao nao resolvem a demanda de `g/m linear` e `g/cova`, porque a regra solicitada deve usar a cultura registrada dentro da pasta anual do talhao. A cultura hoje nao possui modo de espacamento, distancia entre covas ou plantas por cova.

### Cultura da pasta anual

A relacao esta em:

- `CropModel.folder`
- `@ManyToOne`
- coluna JPA: `ID_PASTA_PROPRIEDADES_ANUAL`
- tipo: `AnnualCropFolderModel`

`AnnualCropFolderModel` contem:

- `id`
- `plot`
- `cropsYear`, coluna `ANO_CULURAS`

`CropRepository` possui consultas por pasta:

- `findByNameAndVarietyAndFolder`
- `findTopByFolderAndNameOrderByIdDesc`
- `findAllByFolder`
- `findAllByFolderId`

## Onde a Recommendation recupera a cultura da pasta anual

O request de geracao exige simultaneamente:

- `RecommendationCreateRequestDto.annualCropFolderId`, JSON `id_pasta_cultura_anual`
- `RecommendationCreateRequestDto.cropId`, JSON `id_cultura`

O carregamento acontece em `RecommendationCalculationService.loadRecommendationInputs`:

- `findAnnualCropFolderByIdOrThrow(dto.getAnnualCropFolderId())`
- `findCropByIdOrThrow(dto.getCropId())`

A validacao de associacao acontece em `RecommendationCalculationService.validateRecommendationInputs`:

- valida que a pasta anual pertence ao talhao informado;
- valida que `inputs.crop().getFolder().getId()` e igual a `inputs.annualCropFolder().getId()`;
- chama `validateCropCompatibleWithFertilizationTable`.

A validacao Cultura x Tabela esta preservada em `validateCropCompatibleWithFertilizationTable`, comparando:

- `crop.getName()`
- `cropFertilizationTable.getCrop_common_name()`

## Onde o kg/ha de plantio e cobertura e calculado

O calculo NPK principal esta em `NutrientFertilizationCalculationService.calculate`.

### Necessidades de N, P2O5 e K2O

As necessidades-base de plantio saem de `ContentRangeModel.application`:

- `requiredN = nRange.map(ContentRangeModel::getApplication).orElse(null)`
- `requiredP2O5 = pRange.map(ContentRangeModel::getApplication).orElse(null)`
- `requiredK2O = kRange.map(ContentRangeModel::getApplication).orElse(null)`

Selecao das faixas:

- N: `selectNitrogenRange(table)`, pega o primeiro intervalo de `NITROGENIO`.
- P: `selectNutrientRange(..., Nutriente.FOSFORO, extractPhosphorusValue(...))`.
- K: `selectNutrientRange(..., Nutriente.POTASSIO, extractPotassiumValue(...))`.

### Dose comercial de plantio

O adubo de plantio e escolhido em `selectBestPlantingFertilizer`.

O metodo tenta primeiro formulados e depois simples:

- formulado: `selectFormulatedFertilizers`
- simples: `selectSimpleFertilizers`

A dose comercial em `kg/ha` e calculada por `calculateByGreatestFactor`, que adiciona candidatos por nutriente:

- `required / concentration * 100`

Depois `buildSelection` preenche:

- `fertilizerQuantityKgHa`
- `providedN`
- `providedP2O5`
- `providedK2O`
- saldos e memoria de calculo.

A linha final e adicionada como `FertilizationRecommendationRow` com `phase("Plantio")`.

### Dose de cobertura

As coberturas sao montadas por `buildCoverageRows`.

Entrada:

- `CoverageModel` obtidos por `coverageRepository.findAllByRangeOrderByOrderAsc(range)`.
- `targetApplication = c.getApplication()` representa a dose recomendada de cobertura em `kg/ha` do nutriente alvo.

Quando ha fonte simples compativel:

- `q = targetApplication / pct * 100`
- `q` e a quantidade comercial em `kg/ha`.
- A linha de cobertura e adicionada como `FertilizationRecommendationRow`.

Quando nao ha fonte ou concentracao valida, a linha permanece sem quantidade comercial calculada e recebe aviso tecnico.

## Onde `g/m linear` e `g/cova` sao gerados ou deixados como nao calculados

O ponto atual e `DirectRecommendationReportService`.

Constante usada:

- `TechnicalRecommendationDocumentSupport.LINEAR_CONVERSION_UNAVAILABLE`
- valor: `Não calculado por falta de dados.`

Tabela de micronutrientes:

- `DirectRecommendationReportService.appendMicronutrientTable`
- colunas: `Nutriente/Adubo | kg/ha | g/m linear | g/cova`
- para cada linha encontrada, `g/m linear` e `g/cova` recebem sempre `LINEAR_CONVERSION_UNAVAILABLE`.
- se nao houver linha, tambem recebe `LINEAR_CONVERSION_UNAVAILABLE`.

Tabela de N, P2O5 e K2O:

- `DirectRecommendationReportService.appendNpkTable`
- colunas: `Adubação | Adubos simples/formulados | kg/ha | g/m linear | g/cova`
- `appendFertilizationRows` le as secoes `10. Adubação de plantio` e `11. Adubação de cobertura` do `technicalReport` persistido.
- quando encontra fase, adubo e quantidade, imprime `kg/ha` da fonte textual e preenche `g/m linear` e `g/cova` sempre com `LINEAR_CONVERSION_UNAVAILABLE`.

Tambem ha mencao em:

- `SummaryRecommendationReportService.build`, que adiciona `Conversões g/m linear e g/cova: Não calculado por falta de dados.`

Conclusao: a informacao nao esta sendo calculada em nenhum ponto; o documento direto apenas transforma linhas do laudo legado/geral em Markdown e usa fallback fixo para as conversoes.

## Persistencia da DirectRecommendation

`DirectRecommendationModel` persiste documento unico em tabela propria:

- tabela: `DIRECT_RECOMMENDATIONS`
- relacao: `@OneToOne` com `RecommendationModel`
- `DOCUMENT_NAME`
- `TECHNICAL_REPORT` como `@Lob`
- timestamps

Nao existem linhas normalizadas de Recomendacao Direta em tabela relacional propria. A resposta e montada por `DirectRecommendationServiceImpl.toDto`, expondo:

- `technicalReport`
- `content`
- `contentFormat = markdown`
- metadados de fonte/tamanho/gerado

O conteudo inicial vem de:

- `DirectRecommendationServiceImpl.getByRecommendation`
- se nao existir registro, chama `directRecommendationReportService.build(recommendation)`
- salva via `createInitial`.

## Fluxo de print

`RecommendationControllerImpl` expoe:

- `POST /recommendation/generate`
- `GET /recommendation/print`

`/recommendation/print` chama `RecommendationServiceImpl.preparePrint`, que:

- valida permissao de leitura e permissao de impressao;
- retorna `RecommendationResponseDto`;
- inclui `technicalReport` de `RecommendationModel`.

Esse print nao usa diretamente `DirectRecommendationReportService` nem a tabela da Recomendacao Direta. Ele retorna o laudo tecnico geral persistido em `RecommendationModel.technicalReport`, gerado por `RecommendationReportService.buildTechnicalReport` e eventualmente passado por `RecommendationNarrativeService`.

Portanto:

- Recomendacao Direta: documento derivado em Markdown salvo em `DIRECT_RECOMMENDATIONS`.
- Print de recommendation: estrutura legada/geral via `RecommendationModel.technicalReport`.

## DTOs afetados em prompts futuros

Para suportar os dois modos de espacamento na cultura, os DTOs naturalmente afetados sao:

- `CropCreateRequestDto`
- `CropPostRequestDto`
- `CropResponseDto`

Campos futuros provaveis, a confirmar no prompt de implementacao:

- modo de espacamento da cultura;
- distancia entre covas;
- numero de plantas por cova.

Nao ha indicio de que `RecommendationCreateRequestDto` precise receber esses dados, porque a recomendacao ja recebe `annualCropFolderId` e `cropId` e valida que a cultura pertence a pasta anual. O ponto correto e ler os dados da cultura carregada.

## Services e geradores afetados em prompts futuros

### Cultura

- `CropModel`
- `CropServiceImpl`
- `CropRepository`, se for necessaria consulta adicional. A principio, nao parece necessaria.
- Controllers e interfaces de Crop apenas se a assinatura de DTO mudar por validacao/documentacao.

### Motor agronomico

- `RecommendationCalculationService` deve continuar como coordenador.
- Novo algoritmo deve ficar em classe especializada no pacote `RecommendationEngine`, por exemplo um calculador de conversao operacional por espacamento.
- `NutrientFertilizationCalculationService` ja recebe `CropModel` e monta as linhas NPK. E um candidato para anexar resultados estruturados as linhas, caso o modelo de resultado seja expandido.

### Documento direto/resumido

- `DirectRecommendationReportService` e o ponto exato onde hoje `g/m linear` e `g/cova` viram `Não calculado por falta de dados`.
- `SummaryRecommendationReportService` tem a observacao resumida das conversoes.
- `TechnicalRecommendationDocumentSupport` contem a constante e parsers de tabela/quantidade.

## Migrations provavelmente necessarias

Como os dados devem ser persistidos na cultura, deve haver migration Flyway para a tabela `CULTURAS`.

Provaveis colunas novas:

- modo de espacamento da cultura, como enum/string;
- distancia entre covas em metros;
- numero de plantas por cova.

Cuidados:

- `DISTANCIA_ENTRE_LINHAS` e `N_PLANTAS_POR_METRO` ja existem e hoje sao `nullable = false` no modelo.
- Para compatibilidade com dados existentes, campos novos devem nascer nullable ou com defaults tecnicamente defensaveis. Como nao ha modo novo nos dados atuais, default automatico pode mascarar dado antigo; o tratamento honesto deve preservar ausencia e reportar impossibilidade de conversao quando faltar dado.
- Evitar `@ElementCollection`, listas persistidas ou JSON persistido.
- Nao usar `FetchType.EAGER`.

As migrations iniciais de `CULTURAS`, `PASTAS_CULTURAS_ANUAIS`, `RECOMMENDATIONS`, `DIRECT_RECOMMENDATIONS`, `SUMMARY_RECOMMENDATIONS` e `SHOPPING_LISTS` nao apareceram no `rg` por `CREATE TABLE` dentro de `src/main/resources/db/migration`; os nomes de tabela/coluna acima foram confirmados pelos modelos JPA e migrations incrementais existentes.

## Ponto exato de integracao recomendado

Ponto de entrada:

1. `RecommendationCalculationService.loadRecommendationInputs` ja carrega `CropModel`.
2. `RecommendationCalculationService.validateRecommendationInputs` ja garante que essa cultura pertence a pasta anual informada.
3. `NutrientFertilizationCalculationService.calculate` ja recebe `CropModel` e gera as linhas estruturadas de plantio/cobertura.

Ponto recomendado para conversao:

- criar uma classe especializada em `RecommendationEngine` para converter `kg/ha` em `g/m linear` e `g/cova` a partir de `CropModel`;
- acoplar o resultado estruturado a `FertilizationRecommendationRow` ou a um DTO interno equivalente, para evitar reparsear Markdown;
- `DirectRecommendationReportService` deve preferir dados estruturados quando disponiveis. Se continuar lendo apenas `RecommendationModel.technicalReport`, ele nao tera acesso ao `CropModel` completo nem aos campos novos de cultura, porque `RecommendationModel` persiste apenas `cropName` e `cropYear`, nao `cropId`/`annualCropFolderId`.

Risco importante: depois de salvar uma `RecommendationModel`, o documento direto atual e gerado a partir do texto `technicalReport`, nao do `RecommendationCalculationResult`. Para calcular conversoes de forma confiavel na Recomendacao Direta, os dados estruturados precisam estar disponiveis no momento da geracao do documento ou persistidos de forma relacional/compatível no documento da recomendacao.

## Riscos de compatibilidade

- Campos atuais de cultura sao obrigatorios no create. Alterar sem cuidado pode quebrar clientes que enviam apenas `numero_plantas_por_metro`.
- Dados antigos nao terao distancia entre covas nem plantas por cova.
- Se o novo modo for obrigatorio sem migration/backfill, registros antigos podem falhar ao carregar ou gerar recomendacao.
- `DirectRecommendationReportService` le Markdown do laudo tecnico. Mudancas em headings/tabelas de `RecommendationReportService` podem quebrar a extracao textual.
- `RecommendationNarrativeService` pode alterar o texto final via Fert-AI. Isso torna mais fragil qualquer regra baseada em parsing do `technicalReport`.
- `/recommendation/print` nao usa a Recomendacao Direta, entao corrigir apenas `DirectRecommendationReportService` nao altera necessariamente o print geral.
- `RecommendationModel` nao persiste `cropId` nem `annualCropFolderId`, apenas `cropName` e `cropYear`. Isso limita recalculo posterior a partir de recomendacoes ja salvas.
- `plantsPerMeter` atual e obrigatorio e pode ter unidade/semantica historica ambigua em dados ja gravados.

## Plano recomendado para os proximos prompts

1. Modelar o novo modo de espacamento da cultura em backend:
   - enum simples para os dois modos;
   - campos escalares em `CropModel`;
   - DTOs de create/update/response;
   - migration Flyway em `CULTURAS`.

2. Implementar validacao honesta em `CropServiceImpl`:
   - modo plantas por metro exige `distanceBetweenLines` e `plantsPerMeter`;
   - modo covas exige `distanceBetweenLines`, distancia entre covas e plantas por cova;
   - manter compatibilidade com dados antigos sempre que possivel, sem inferir covas.

3. Criar classe especializada no pacote `RecommendationEngine` para conversoes operacionais:
   - entrada: `CropModel` e dose `kg/ha`;
   - saida: valores opcionais e mensagem tecnica quando faltar dado;
   - formulas:
     - metros lineares/ha = `10000 / distanciaEntreLinhas`;
     - `g/m linear = kg/ha * 1000 / metrosLinearesHa`;
     - covas/ha = `10000 / (distanciaEntreLinhas * distanciaEntreCovas)`;
     - populacao por covas = `covasHa * plantasPorCova`;
     - `g/cova = kg/ha * 1000 / covasHa`.

4. Integrar sem aumentar `RecommendationCalculationService`:
   - manter `RecommendationCalculationService` como orquestrador;
   - usar `NutrientFertilizationCalculationService` ou classe auxiliar para enriquecer linhas NPK com conversoes;
   - evitar parsing de Markdown como fonte primaria.

5. Ajustar documentos:
   - `RecommendationReportService` deve emitir dados suficientes de conversao ou estrutura clara;
   - `DirectRecommendationReportService` deve preencher `g/m linear` e `g/cova` quando houver dados;
   - manter fallback `Não calculado por falta de dados.` com motivo tecnico quando faltar dado.

6. Definir estrategia para recomendacoes antigas:
   - nao recalcular silenciosamente se `RecommendationModel` nao tem `cropId`;
   - exibir fallback honesto nos documentos ja salvos;
   - aplicar conversao completa apenas em novas geracoes ou onde a cultura puder ser recuperada com seguranca.

7. Cobrir com testes:
   - create/update de cultura para os dois modos;
   - conversor operacional com entradas validas e ausentes;
   - geracao de Recomendacao Direta com `g/m linear` e `g/cova`;
   - regressao do fallback `Não calculado por falta de dados.`.
