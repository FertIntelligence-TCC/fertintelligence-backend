# Investigacao: origem da renderizacao Markdown das recomendacoes

## Escopo

Esta investigacao cobre somente o backend Spring Boot/Java. Nenhuma funcionalidade, endpoint, DTO, regra agronomica ou texto de recomendacao foi alterado.

## Arquivos inspecionados

- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/RecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationCalculationService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/RecommendationNarrativeService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/FertAiClient.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/RecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/recommendation/RecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/GeneralRecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/GeneralRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/GeneralRecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/generalRecommendation/GeneralRecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/SummaryRecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/SummaryRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/SummaryRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/SummaryRecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/summaryRecommendation/SummaryRecommendationResponseDto.java`
- `src/main/java/com/migueltcc/fertintelligence/controller/implementation/DirectRecommendationControllerImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/DirectRecommendationServiceImpl.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/DirectRecommendationDtoMapper.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/DirectRecommendationReportService.java`
- `src/main/java/com/migueltcc/fertintelligence/service/implementation/RecommendationEngine/TechnicalRecommendationDocumentSupport.java`
- `src/main/java/com/migueltcc/fertintelligence/model/fertintelligence/DirectRecommendationModel.java`
- `src/main/java/com/migueltcc/fertintelligence/dto/directRecommendation/DirectRecommendationResponseDto.java`

## Fluxo principal encontrado

1. `POST /recommendation/generate` entra em `RecommendationControllerImpl.generate`.
2. O controller chama `RecommendationServiceImpl.generate`.
3. `RecommendationServiceImpl.generate` valida usuario, propriedade, talhao e permissao.
4. O calculo estruturado e produzido por `RecommendationCalculationService.calculate`.
5. O texto do laudo principal e gerado por `RecommendationReportService.buildTechnicalReport`.
6. O texto gerado e enviado para `RecommendationNarrativeService.improveNarrative`.
7. `RecommendationNarrativeService` chama `FertAiClient.improveNarrative`, que envia o campo `technical_report` para o endpoint externo `/api/ai/recommendation/narrative`.
8. Se o Fert-AI retornar texto valido, esse texto substitui o laudo gerado pelo backend. Se falhar, o backend preserva o texto original de `RecommendationReportService`.
9. `RecommendationServiceImpl.generate` persiste o texto final em `RecommendationModel.technicalReport`.
10. `GeneralRecommendationService.createInitial` cria a Recomendacao Geral com o mesmo texto final.
11. `DirectRecommendationService.createInitial` cria a Recomendacao Direta com texto de `DirectRecommendationReportService.build(savedRecommendation)`.
12. A resposta HTTP volta por `RecommendationServiceImpl.toDto`, preenchendo `RecommendationResponseDto.technicalReport` com `RecommendationModel.technicalReport` e incluindo `directRecommendation` quando existir.

## Fluxos de consulta encontrados

- `GET /recommendation/get`, `GET /recommendation/print`, `GET /recommendation/my`, `GET /recommendation/property` e `GET /recommendation/plot` retornam `RecommendationResponseDto`.
- `RecommendationResponseDto` possui `laudo_tecnico`, mas nao possui campo declarando formato do conteudo.
- `GET /general-recommendation/get-by-recommendation` retorna `GeneralRecommendationResponseDto`. Se nao houver registro em `general_recommendations`, usa fallback legado com `RecommendationModel.technicalReport`.
- `GET /summary-recommendation/get-by-recommendation` retorna `SummaryRecommendationResponseDto`. Se nao houver registro, gera sob demanda com `SummaryRecommendationReportService.build(recommendation)`.
- `GET /direct-recommendation/get-by-recommendation` retorna `DirectRecommendationResponseDto`. Se nao houver registro, gera sob demanda com `DirectRecommendationReportService.build(recommendation)`.

## Origem dos titulos e caracteres Markdown

O backend gera caracteres Markdown explicitamente.

### Laudo tecnico principal

`RecommendationReportService.buildTechnicalReport` monta um `StringBuilder` com secoes Markdown:

- `## Laudo Técnico de Recomendação Agrícola`
- `## 1. Identificação`
- `## 2. Dados utilizados`
- `## 3. Diagnóstico químico`
- `## 4. Diagnóstico físico`
- `## 5. Diagnóstico de salinidade/sodicidade`
- `## 6. Diagnóstico foliar`
- `## 7. Calagem`
- `## 8. Gessagem`
- `## 9. Adubação corretiva`
- `## 10. Adubação de plantio`
- `## 11. Adubação de cobertura`
- `## 12. Balanço nutricional`
- `## 13. Fertilizantes recomendados`
- `## 14. Limitações e alertas`
- `## 15. Memória de cálculo`
- `## 16. Encerramento`

Tambem ha subtitulos com `###` em `appendInputValues`, por exemplo para `Entradas de calagem` e `Entradas de gessagem`.

Esse laudo tambem usa outros elementos Markdown: listas com `-` e tabelas com pipes (`|`).

### Recomendacao direta

`DirectRecommendationReportService.build` tambem monta Markdown:

- comentario de metadado via `TechnicalRecommendationDocumentSupport.appendStyle`: `<!-- formato: markdown; fonte: Aptos; tamanho: 10 -->`
- cabecalho institucional com `**FertIntelligence**`
- titulo `# LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO`
- secoes `## Observação sobre MAP`, `## Observações finais`, `## Tabela de micronutrientes`, `## Tabela de N, P2O5 e K2O`
- tabelas Markdown com pipes

O helper `TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage` tambem cria secoes com `## ` concatenado ao titulo recebido.

### Recomendacao resumida

`SummaryRecommendationReportService.build` monta Markdown:

- comentario de metadado via `TechnicalRecommendationDocumentSupport.appendStyle`
- cabecalho institucional com `**FertIntelligence**`
- titulo `# LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO`
- secoes como `## Diagnóstico da Fertilidade do Solo da Área Avaliada`, `## Diagnóstico geral`, `## Recomendação de calagem e gessagem`, `## Recomendação de micronutrientes` e `## Observações`

### Recomendacao geral

`GeneralRecommendationServiceImpl` nao possui builder proprio de narrativa. Ele persiste e retorna o texto recebido:

- na criacao automatica, recebe o `improvedReport` criado no fluxo principal;
- na criacao manual, recebe `dto.getTechnicalReport()`;
- no fallback legado, retorna `RecommendationModel.technicalReport`.

Portanto, a Recomendacao Geral herda o Markdown do laudo principal ou de conteudo manual informado pela API.

## DTOs e formato retornado

O backend retorna texto em campos JSON. Nao foi encontrado retorno HTML nesses fluxos.

- `RecommendationResponseDto` retorna `laudo_tecnico` como `String`, sem `formato_conteudo`.
- `GeneralRecommendationResponseDto` retorna `laudo_tecnico`, `conteudo` e declara `formato_conteudo = "markdown"`.
- `SummaryRecommendationResponseDto` retorna `laudo_tecnico`, `conteudo` e declara `formato_conteudo = "markdown"`.
- `DirectRecommendationResponseDto` retorna `laudo_tecnico`, `conteudo` e declara `formato_conteudo = "markdown"`.
- `DirectRecommendationDtoMapper` copia `DirectRecommendationModel.technicalReport` para `technicalReport` e `content`.

Conclusao: para os documentos geral, resumido e direto, o contrato do backend indica explicitamente Markdown. Para o endpoint principal de recomendacao, o campo `laudo_tecnico` tambem contem Markdown, embora o DTO principal nao declare um campo de formato.

## Persistencia envolvida

- `RecommendationModel.technicalReport` persiste o laudo principal em `RECOMMENDATIONS.TECHNICAL_REPORT`.
- `GeneralRecommendationModel.technicalReport` persiste a Recomendacao Geral.
- `SummaryRecommendationModel.technicalReport` persiste a Recomendacao Resumida.
- `DirectRecommendationModel.technicalReport` persiste a Recomendacao Direta.

Nao foi identificado campo persistente separado para armazenar HTML renderizado.

## Papel do Fert-AI

`RecommendationNarrativeService.improveNarrative` envia o laudo principal ja gerado para o Fert-AI. O retorno externo pode substituir o conteudo antes da persistencia em `RecommendationModel.technicalReport`.

Esta investigacao nao altera nem inspeciona o repositorio Fert-AI, por regra do prompt. Assim, o ponto confirmado no backend e:

- se Fert-AI falhar, o Markdown vem diretamente de `RecommendationReportService`;
- se Fert-AI responder com sucesso, o backend persiste o texto retornado pelo Fert-AI, que pode manter, alterar ou inserir Markdown. Isso nao e verificavel neste repositorio.

## Confirmacao sobre texto puro, Markdown ou HTML

- O backend nao retorna HTML nesses fluxos.
- O backend retorna strings JSON contendo Markdown.
- A presenca de `#`, `##` e `###` no texto e esperada pelo codigo atual.
- A exibicao visual dos caracteres `#` indica que alguma camada consumidora pode estar tratando o campo como texto puro em vez de renderizar Markdown, ou pode estar usando o endpoint principal sem metadado de formato.

## Recomendacao tecnica para o proximo prompt

Antes de corrigir exibicao, definir qual contrato deve prevalecer por tipo de documento:

1. Se o objetivo for renderizar titulos, a camada consumidora deve tratar `laudo_tecnico`/`conteudo` como Markdown, especialmente quando `formato_conteudo = "markdown"`.
2. Se o endpoint principal `/recommendation/*` tambem for consumido como documento renderizavel, avaliar adicionar metadado de formato em prompt futuro, preservando compatibilidade, porque hoje `RecommendationResponseDto` nao declara `formato_conteudo`.
3. Se a decisao de produto for retornar texto puro, a remocao/conversao dos marcadores deve ocorrer em um ponto unico de formatacao de saida, nao dentro da logica agronomica nem nos textos tecnicos de calculo.
4. Evitar alterar os builders agronomicos sem contrato definido, pois `DirectRecommendationReportService`, `SummaryRecommendationReportService`, `TechnicalRecommendationDocumentSupport` e `ShoppingListReportService` dependem de secoes Markdown para extrair e recompor partes do laudo.

## Riscos para a proxima implementacao

- Remover `##` do laudo principal pode quebrar `TechnicalRecommendationDocumentSupport.section` e `subsection`, que procuram marcadores `## ` e `### ` para montar recomendacoes resumidas, diretas e lista de compras.
- Corrigir apenas o frontend pode resolver a visualizacao, mas manter inconsistencia caso algum consumidor use `RecommendationResponseDto.laudo_tecnico` sem saber o formato.
- Converter Markdown para HTML no backend mudaria o contrato atual dos DTOs que declaram `formato_conteudo = "markdown"`.
- O Fert-AI e um ponto externo capaz de alterar a narrativa final; qualquer normalizacao futura precisa considerar sucesso e fallback desse servico.
