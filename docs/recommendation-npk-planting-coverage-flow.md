# Investigacao do fluxo NPK: plantio e cobertura

Documento tecnico do Prompt 05. O objetivo e registrar o fluxo atual de `RecommendationCalculationService` sem alterar comportamento de producao.

## Escopo investigado

- `RecommendationCalculationService`
- `RecommendationReportService`
- `ContentRangeModel`
- `CoverageModel`
- `CoverageServiceImpl`
- DTOs de intervalo/cobertura
- modelos de adubos minerais formulados e simples

## Fluxo atual

1. A recomendacao resolve a tabela de adubacao de culturas selecionada na requisicao.
2. Para N, P e K, o servico busca os intervalos em `ContentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc`.
3. Nitrogenio usa sempre o primeiro intervalo retornado.
4. Fosforo e potassio tentam classificar o valor do ultimo extrato de fertilidade; sem valor suficiente, o primeiro intervalo da tabela e usado com aviso tecnico.
5. A necessidade de plantio vem de `ContentRangeModel.application`, persistido em `APLICACAO_RECOMENDADA_PLANTIO`.
6. O plantio seleciona um fertilizante mineral formulado quando disponivel; se nao houver formulado adequado, tenta adubo mineral simples.
7. A dose comercial do plantio e estimada pelo maior fator entre N, P2O5 e K2O:

   ```text
   dose_kg_ha = max(
       N_necessario / percentual_N * 100,
       P2O5_necessario / percentual_P2O5 * 100,
       K2O_necessario / percentual_K2O * 100
   )
   ```

8. Os nutrientes fornecidos no plantio sao calculados a partir da dose comercial e dos percentuais do fertilizante:

   ```text
   fornecido_N = dose_kg_ha * percentual_N / 100
   fornecido_P2O5 = dose_kg_ha * percentual_P2O5 / 100
   fornecido_K2O = dose_kg_ha * percentual_K2O / 100
   ```

9. O saldo exibido no plantio e:

   ```text
   saldo = nutriente_fornecido - nutriente_necessario_no_plantio
   ```

10. Depois da linha de plantio, o servico percorre os intervalos selecionados de N, P e K e chama `buildCoverageRows` para cada intervalo.
11. `buildCoverageRows` busca `CoverageModel` por intervalo em `CoverageRepository.findAllByRangeOrderByOrderAsc`.
12. Cada `CoverageModel.application` positivo gera uma linha de cobertura independente.
13. Para cobertura, o servico seleciona apenas adubo mineral simples, escolhendo o maior percentual do nutriente do intervalo:

   - N: maior `SimpleMineralFertilizerModel.N`
   - P: maior `SimpleMineralFertilizerModel.P2O5`
   - K: maior `SimpleMineralFertilizerModel.K2O`

14. A dose comercial da cobertura e:

   ```text
   dose_cobertura_kg_ha = aplicacao_cobertura / percentual_do_nutriente * 100
   ```

15. A linha de cobertura nao registra `providedN`, `providedP2O5`, `providedK2O`, `balanceN`, `balanceP2O5` nem `balanceK2O`; o relatorio mostra apenas fase, nutriente, fertilizante, quantidade e modo de aplicacao.

## Papel atual de CoverageModel

Pelo modelo e pelo servico de cadastro, `CoverageModel` representa doses de cobertura vinculadas a um intervalo de teor (`ContentRangeModel`) e organizadas por ordem sequencial. A implementacao de `CoverageServiceImpl` tambem cria placeholders em intervalos irmaos do mesmo nutriente para manter a mesma quantidade de coberturas cadastradas.

O codigo atual nao possui campo, flag ou regra que indique se a cobertura e um parcelamento tecnico obrigatorio da dose total ou uma recomendacao independente adicional. Na pratica, o calculo trata `CoverageModel.application` como uma aplicacao adicional independente do saldo de plantio.

## Onde a duplicacao pode ocorrer

A duplicacao pode ocorrer quando a dose de plantio ja cobre todo ou parte do nutriente e existe cobertura cadastrada para o mesmo nutriente selecionado.

Exemplo de K:

1. O intervalo de potassio selecionado define `ContentRangeModel.application = 70 kg/ha de K2O` para plantio.
2. O formulado de plantio e escolhido considerando N/P/K juntos.
3. A dose comercial estimada pelo maior fator pode fornecer `K2O >= 70 kg/ha`.
4. Mesmo assim, se o mesmo intervalo de potassio tiver `CoverageModel.application = 40 kg/ha`, `buildCoverageRows` gera uma linha `Cobertura <ordem> - POTASSIO`.
5. A dose de cobertura de K e convertida para produto comercial sem abater `providedK2O` nem consultar `balanceK2O` do plantio.

Portanto, a causa raiz mapeada e a ausencia de integracao entre o saldo calculado no plantio e as linhas de cobertura. O saldo final global por nutriente nao e consolidado no fluxo atual.

## Saldo final atual

O saldo final existente e apenas o saldo da linha de plantio, calculado em `buildSelection`. As linhas de cobertura nao atualizam saldo global, nao compensam excesso/deficit do plantio e nao acumulam nutrientes fornecidos por cobertura.

## Limitacoes identificadas

- Nao ha regra modelada para distinguir cobertura como parcelamento de uma dose total versus recomendacao adicional.
- Nao ha totalizador final por nutriente somando plantio e cobertura.
- Nao ha abatimento automatico de nutrientes fornecidos pelo formulado de plantio antes de calcular cobertura.
- Nao ha memoria de calculo estruturada no DTO de resposta alem das linhas de recomendacao e sugestoes de fertilizantes.
- O relatorio tecnico apresenta o saldo do plantio quando presente, mas nao apresenta saldo consolidado apos coberturas.

## Conclusao para a proxima etapa

Antes de corrigir o comportamento, sera necessario decidir uma regra agronomica explicita: se `CoverageModel.application` representa uma parcela obrigatoria adicional por fase, ela deve ser preservada e o saldo final deve somar tudo; se representa complemento condicionado ao que sobrou apos plantio, o calculo deve abater os nutrientes ja fornecidos pelo formulado antes de gerar as coberturas.
