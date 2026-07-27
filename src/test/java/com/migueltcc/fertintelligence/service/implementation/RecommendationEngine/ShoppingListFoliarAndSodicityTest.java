package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingListFoliarAndSodicityTest {

    @Test
    void extractsSodicityGypsumAsSeparateDose() {
        String report = """
                5. Diagnóstico de salinidade/sodicidade

                | Atributo | Valor analisado | Unidade | Interpretação |
                |---|---:|---|---|
                | Dose total estimada de gesso 0–40 cm | 344.00 | kg/ha | Recuperação preliminar por excesso de Na+ |
                """;
        assertThat(ShoppingListReportService.sodicityGypsumDose(report)).isEqualTo(344d);
    }

    @Test
    void keepsBothFoliarAlternativesAndTheirStructuredStatesWithoutSummingThem() {
        String report = """
                Adubação foliar de micronutrientes

                | Micronutriente | Tipo de fonte | Produto | Teor | Dose micronutriente | Dose produto | Unidade comercial | PCkgAMi | R$/kgNut | CUMIC | Decisão | Observação técnica |
                |---|---|---|---:|---:|---:|---|---:|---:|---:|---|---|
                | Cu | Mineral simples | Sulfato de cobre | 25.00% | 0.50 kg/ha | 2.00 kg/ha | saco | R$ 5.00 | R$ 20.00 | R$ 10.00 | SELECTED — Escolhida | orientação |
                | Cu | Quelatada | Cobre quelatado Na2CuEDTA | 13.00% | 0.50 kg/ha | 3.85 kg/ha | saco | R$ 3.12 | R$ 24.00 | R$ 12.00 | NOT_SELECTED — Não escolhida | orientação |
                """;
        var alternatives = ShoppingListReportService.foliarAlternatives(report);
        assertThat(alternatives).hasSize(2);
        assertThat(alternatives).extracting(ShoppingListReportService.FoliarShoppingAlternative::state)
                .containsExactly("SELECTED", "NOT_SELECTED");
        assertThat(alternatives).extracting(ShoppingListReportService.FoliarShoppingAlternative::costPerHa)
                .containsExactly(10d, 12d);
    }
}
