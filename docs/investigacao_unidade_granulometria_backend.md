# Investigacao: unidade granulometrica da analise fisica

## Escopo

Prompt 25. Esta investigacao cobre somente o backend Spring Boot/Java e nao implementa mudanca funcional.

## Arquivos inspecionados

- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/extractAnalysisModels/PhysicalAnalysisExtractModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/extractAnalysis/physical/PhysicalAnalysisExtractCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/extractAnalysis/physical/PhysicalAnalysisExtractPostRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/extractAnalysis/physical/PhysicalAnalysisExtractResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/composedAttributes/physicalAnalysis/PhysicalAnalysisUnit.java`
- `src/main/java/com/migueltcc/fertintelligence/repository/PhysicalAnalysisExtractRepository.java`
- `src/main/java/com/migueltcc/fertintelligence/service/documentation/PhysicalAnalysisExtractService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/PhysicalAnalysisExtractServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/documentation/PhysicalAnalysisExtractController.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/PhysicalAnalysisExtractControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/SoilTextureClassificationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/LimingRequirementCalculator.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/GypsumCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/CropFertilizationTableServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationCreateRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationPostRequestDto.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/RecommendationModel.java`
- `src/main/resources/db/migration/V20260624_01__add_units_to_physical_analysis_extracts.sql`
- `src/main/resources/db/migration/V20260624_09__harden_unit_defaults_and_constraints.sql`
- `src/main/resources/db/migration/V20260627_01__add_textural_classification_to_recommendations.sql`
- `src/main/java/com/migueltcc/fertintelligence/config/ExtractsDataSeeder.java`
- `src/test/java/com/migueltcc/fertintelligence/service/SoilTextureClassificationServiceTest.java`
- `src/test/java/com/migueltcc/fertintelligence/controller/PhysicalAnalysisExtractControllerImplTest.java`

## Campos de areia, silte e argila

No model `PhysicalAnalysisExtractModel`, os valores granulometricos sao:

- `teorAreia`, persistido em `TEOR_DE_AREIA`
- `teorSilte`, persistido em `TEOR_DE_SILTE`
- `teorArgila`, persistido em `TEOR_DE_ARGILA`

Cada valor tem uma unidade separada:

- `unidadeTeorAreia`, persistida em `UNIDADE_TEOR_DE_AREIA`
- `unidadeTeorSilte`, persistida em `UNIDADE_TEOR_DE_SILTE`
- `unidadeTeorArgila`, persistida em `UNIDADE_TEOR_DE_ARGILA`

O enum `PhysicalAnalysisUnit` aceita `G_PER_DM3` (`g/dm3`) e `G_PER_KG` (`g/kg`). O default do model e do service e `G_PER_DM3`.

Observacao: os comentarios do model ainda descrevem `teorAreia`, `teorSilte` e `teorArgila` como `g/dm3`, mas a estrutura atual do backend tambem possui campos de unidade por fracao.

## Cadastro, persistencia e retorno

O controller expõe os endpoints existentes em `/physical-analysis-extract`:

- `POST /register`
- `GET /get`
- `GET /get-by-range`
- `GET /get-by-layer`
- `GET /get-by-plot`
- `PUT /update`
- `DELETE /delete`

Na criacao, `PhysicalAnalysisExtractServiceImpl.createPhysicalAnalysisExtract` grava o valor informado em cada `teor*` e normaliza a unidade informada. Quando a unidade vem nula, usa `G_PER_DM3`. O service tambem converte valores numericos nulos para `0.0` durante a criacao.

Na atualizacao, `updatePhysicalAnalysisExtract` altera somente campos nao nulos. As unidades tambem podem ser alteradas individualmente e sao normalizadas pelo mesmo enum.

No retorno, `PhysicalAnalysisExtractModel.toDto` devolve os valores brutos e as unidades normalizadas. Os nomes JSON sao:

- `teor_areia` e `unidade_teor_areia`
- `teor_silte` e `unidade_teor_silte`
- `teor_argila` e `unidade_teor_argila`

## Migrations de unidade

`V20260624_01__add_units_to_physical_analysis_extracts.sql` adiciona as colunas de unidade com default `g/dm3` e atualiza valores nulos ou alguns valores `g/kg` para `g/dm3`.

`V20260624_09__harden_unit_defaults_and_constraints.sql` reforca os defaults e tambem atualiza unidades nulas ou reconhecidas como `g/dm3`, `g_per_dm3`, `g/kg` e `g_per_kg` para `g/dm3`.

Conclusao tecnica: embora o enum e os DTOs aceitem `g/kg`, as migrations existentes tendem a normalizar registros fisicos legados para `g/dm3`. Isso preserva historico assumido como volumetrico, mas tambem impede inferir apenas pelo banco se um valor antigo era originalmente `g/kg`.

## Classificacao granulometrica

`SoilTextureClassificationService` e o servico responsavel pela classificacao textural brasileira ou americana.

O seletor vem da Recommendation:

- `RecommendationCreateRequestDto.classificacao_textural`
- `RecommendationModel.texturalClassification`
- default `BRASILEIRO` em `RecommendationServiceImpl.resolveTexturalClassification`
- coluna `recommendations.textural_classification`, criada por `V20260627_01__add_textural_classification_to_recommendations.sql`

Tanto `classifyBrazilian` quanto `classifyAmerican` leem `teorAreia`, `teorSilte` e `teorArgila` como gramas por kg. As duas estrategias exigem explicitamente que as tres unidades sejam `PhysicalAnalysisUnit.G_PER_KG`.

Se alguma fracao estiver ausente, invalida ou com unidade diferente de `g/kg`, o servico nao classifica e retorna warnings. Portanto, com analise fisica em `g/dm3`, a classificacao granulometrica da Recommendation fica honestamente nao calculada.

Os testes de `SoilTextureClassificationServiceTest` confirmam esse contrato: classificacao americana e validada com valores como `600,300,100` em `G_PER_KG`, e o caso `G_PER_DM3` nao classifica.

## Uso na Recommendation

`RecommendationCalculationService` continua como orquestrador. Ele carrega a analise fisica selecionada por `id_extrato_analise_fisica`, valida associacao com o talhao e passa o model para os calculos e diagnosticos.

Pontos encontrados:

- Diagnostico fisico: `buildSoilPhysicalDiagnosis` chama `SoilTextureClassificationService.classify(texturalClassification, physicalAnalysis)`, depois exibe Areia, Silte e Argila com `physicalUnit(unidadeTeor*)`.
- Resumo fisico: quando a classificacao nao ocorre, a mensagem informa que ha fracoes granulometricas, mas a classificacao nao foi calculada por dados ou unidade insuficientes.
- Fosforo Mehlich-1: `classifyPhosphorus` usa `physicalAnalysis.getTeorArgila()` diretamente para escolher faixas `<150`, `<=350`, `<=600` e `>600`. A variavel local se chama `clayGdm3`; nao ha verificacao de `unidadeTeorArgila`.
- Enxofre: `classifySulfur` usa `getTeorArgila()` diretamente para escolher a faixa `<400` ou `>=400`; nao ha verificacao de `unidadeTeorArgila`.
- Dose corretiva de S: `buildSulfurCorrectiveRow` usa `getTeorArgila()` diretamente para escolher `less400`, mas registra a unidade atual no texto de memoria de calculo.
- Calagem: `LimingRequirementCalculator` usa `getTeorArgila()` para selecionar fator por argila (`<150`, `<=350`, `>350`) e inclui a unidade atual nos inputs, mas nao valida se a unidade e compativel com os limites.
- Indicacao de criterio de calagem em `CropFertilizationTableServiceImpl.resolveIndicatedLimingCriterion` usa `physical.getTeorArgila()` diretamente para o fator de calagem.
- Gessagem: `GypsumCalculationService` apenas inclui Argila nos `inputValues`, com unidade, mas a necessidade de gessagem atual e classificada por calcio, aluminio e saturacao por aluminio.

## Pontos de exibicao e dados de exemplo

No backend, a exibicao/retorno de Areia, Silte e Argila ocorre principalmente por:

- `PhysicalAnalysisExtractResponseDto`, para consultas de analise fisica.
- Itens de diagnostico fisico em `RecommendationCalculationService`, com unidade derivada de `PhysicalAnalysisUnit`.
- Memorias de calculo de S, calagem e gessagem, quando incluem Argila.

`ExtractsDataSeeder` cria dados fisicos com `areia + silte + argila = 1000`, mas nao informa unidades. Pelo default do model/service, esses registros ficam como `g/dm3`, embora os totais sejam numericamente compativeis com granulometria em `g/kg`.

## Conclusoes

1. O backend armazena os valores de Areia, Silte e Argila como numeros brutos e tem colunas separadas para unidade.
2. Os nomes internos `teorAreia`, `teorSilte` e `teorArgila` nao codificam unidade. Comentarios antigos do model indicam `g/dm3`; os campos `unidadeTeor*` definem a unidade efetiva retornada.
3. A classificacao granulometrica brasileira/americana exige `g/kg` nas tres fracoes e nao converte de `g/dm3`.
4. A Recommendation preserva o comportamento antigo para calculos de P Mehlich-1, S e calagem: usa `teorArgila` diretamente em limites numericos historicos sem validar unidade.
5. Existe risco tecnico de inconsistencia quando a tela/analise fisica registra `g/dm3` e a classificacao textural exige `g/kg`. O backend atualmente evita classificar textura nesse caso, mas outros calculos continuam usando o valor bruto de argila.

## Limitacoes restantes

- Nao foi possivel confirmar, somente pelo backend, qual unidade o frontend envia historicamente para cada cadastro.
- Nao foi alterada a classificacao textural, conversao de unidade, regra agronomica ou endpoint neste prompt.
- Uma decisao futura precisa definir se `g/dm3` deve ser convertido, rejeitado para classificacao, migrado, ou mantido apenas como dado legado.
- Antes de qualquer mudanca funcional, e necessario confirmar a unidade real esperada pelas tabelas auxiliares de P, S e calagem, pois hoje os limites sao aplicados sobre o numero bruto de `teorArgila`.
