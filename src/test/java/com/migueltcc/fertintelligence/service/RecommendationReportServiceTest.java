package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.CalciumMagnesiumBalanceCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationReportServiceTest {

    private final RecommendationReportService reportService = new RecommendationReportService();

    @Test
    void addsGypsumAndCorrectiveSimpleSuperphosphateWarningsOnlyWhenEffective() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .recommendationType("BOTH")
                        .gypsumRequirement(RecommendationCalculationService.GypsumRequirementResult.builder()
                                .needed(true).calculatedRequirement(1742.5d).unit("kg/ha")
                                .sulfurEquivalent(261.375d).warnings(List.of()).build())
                        .correctiveFertilizationRows(List.of(
                                RecommendationCalculationService.CorrectiveFertilizationRow.builder()
                                        .correctedAttribute("P2O5 corretivo").suggestedSource("Superfosfato simples")
                                        .dose(200d).doseUnit("kg/ha").build()))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("Caso se faça gessagem da área"));
        assertTrue(report.contains("Se utilizar superfosfato simples na adubação corretiva"));
        assertTrue(report.indexOf("Caso se faça gessagem da área") > report.indexOf("Dose de gesso"));
    }

    @Test
    void omitsGypsumWarningForZeroDoseAndCorrectiveWarningForZeroDose() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .recommendationType("BOTH")
                        .gypsumRequirement(RecommendationCalculationService.GypsumRequirementResult.builder()
                                .needed(true).calculatedRequirement(0d).warnings(List.of()).build())
                        .correctiveFertilizationRows(List.of(
                                RecommendationCalculationService.CorrectiveFertilizationRow.builder()
                                        .suggestedSource("Superfosfato simples").dose(0d).build()))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertFalse(report.contains("Caso se faça gessagem da área"));
        assertFalse(report.contains("Se utilizar superfosfato simples na adubação corretiva"));
    }

    @Test
    void buildTechnicalReport_UsesUpdatedRecommendationUnits() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .requesterName("Produtor")
                        .requesterUsername("produtor")
                        .propertyName("Fazenda")
                        .plotIdentification("Talhao 1")
                        .cropName("ALGODAO")
                        .annualCropFolderYear(2026)
                        .recommendationType("BOTH")
                        .issuedAt(LocalDateTime.of(2026, 6, 24, 10, 0))
                        .requiredN(40.0)
                        .requiredP2O5(80.0)
                        .requiredK2O(60.0)
                        .soilPhysicalDiagnosis(List.of(
                                RecommendationCalculationService.SoilPhysicalDiagnosisItem.builder()
                                        .attribute("Argila")
                                        .analyzedValue(420.0)
                                        .unit("g/kg")
                                        .technicalObservation("Unidade de granulometria atualizada.")
                                        .build()))
                        .soilChemicalDiagnosis(List.of(
                                RecommendationCalculationService.SoilChemicalDiagnosisItem.builder()
                                        .attribute("Potássio (K) trocável")
                                        .analyzedValue(2.4)
                                        .unit("mmolc/dm³")
                                        .interpretation("Médio")
                                        .usedCriterion("2 a 3")
                                        .technicalObservation("Unidade do complexo de troca atualizada.")
                                        .build(),
                                RecommendationCalculationService.SoilChemicalDiagnosisItem.builder()
                                        .attribute("Fósforo (P) Mehlich-1")
                                        .analyzedValue(12.0)
                                        .unit("mg/dm³")
                                        .interpretation("Baixo")
                                        .usedCriterion("10 a 20")
                                        .technicalObservation("Fósforo disponível.")
                                        .build()))
                        .soilSalinityDiagnosis(List.of(
                                RecommendationCalculationService.SoilSalinityDiagnosisItem.builder()
                                        .attribute("RAS")
                                        .analyzedValue(1.5)
                                        .unit("(mmolc)**0.5")
                                        .technicalObservation("Unidade de RAS atualizada.")
                                        .build()))
                        .nutrientBalanceRows(List.of(
                                RecommendationCalculationService.NutrientBalanceRow.builder()
                                        .nutrient("N")
                                        .requiredTotalKgHa(40.0)
                                        .providedByPlantingKgHa(20.0)
                                        .recommendedCoverageKgHa(20.0)
                                        .providedByCoverageKgHa(20.0)
                                        .providedTotalKgHa(40.0)
                                        .finalBalanceKgHa(0.0)
                                        .status("Atendido")
                                        .build()))
                        .fertilizationRecommendationRows(List.of(
                                RecommendationCalculationService.FertilizationRecommendationRow.builder()
                                        .phase("Plantio")
                                        .nutrients("N")
                                        .suggestedFertilizer("Ureia")
                                        .fertilizerQuantityKgHa(100.0)
                                        .applicationMode("Aplicar no sulco.")
                                        .build()))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("g/kg"));
        assertTrue(report.contains("mmolc/dm³"));
        assertTrue(report.contains("mg/dm³"));
        assertTrue(report.contains("(mmolc)**0.5"));
        assertTrue(report.contains("kg/ha"));
        assertFalse(report.startsWith("#"));
        assertFalse(report.contains("## "));
        assertFalse(report.contains("### "));
        assertFalse(report.contains("g/dm3"));
        assertFalse(report.contains("cmolc/dm³"));
        assertFalse(report.contains("CaCO3"));
        assertFalse(report.contains("15. Memória de cálculo"));
        assertFalse(report.contains("12. Balanço nutricional"));
    }

    @Test
    void buildTechnicalReport_NormalizesCoverageCorrectiveAndWarnings() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .requesterName("Produtor")
                        .propertyName("Fazenda")
                        .plotIdentification("Talhao 1")
                        .cropName("MILHO")
                        .issuedAt(LocalDateTime.of(2026, 7, 4, 10, 0))
                        .gypsumRequirement(RecommendationCalculationService.GypsumRequirementResult.builder()
                                .evaluated(false)
                                .justification("Camada subsuperficial 20-40 cm ausente.")
                                .warnings(List.of("Aviso duplicado que não deve aparecer."))
                                .build())
                        .correctiveFertilizationRows(List.of(
                                RecommendationCalculationService.CorrectiveFertilizationRow.builder()
                                        .correctedAttribute("P2O5 corretivo")
                                        .suggestedSource("Superfosfato Simples")
                                        .dose(0.0)
                                        .doseUnit("kg/ha")
                                        .technicalWarning("Dose zero.")
                                        .build(),
                                RecommendationCalculationService.CorrectiveFertilizationRow.builder()
                                        .correctedAttribute("Micronutrientes corretivos")
                                        .need("Bloqueado por pH em água > 7,0")
                                        .suggestedSource("Não recomendada no solo")
                                        .technicalWarning("Solos com reação alcalina.")
                                        .build()))
                        .fertilizationRecommendationRows(List.of(
                                RecommendationCalculationService.FertilizationRecommendationRow.builder()
                                        .phase("Cobertura 1")
                                        .nutrients("N 60 kg/ha / P2O5 20 kg/ha / K2O 40 kg/ha")
                                        .suggestedFertilizer("20-00-20")
                                        .fertilizerQuantityKgHa(300.0)
                                        .providedN(60.0)
                                        .providedP2O5(0.0)
                                        .providedK2O(60.0)
                                        .build()))
                        .warnings(List.of("Aviso único."))
                        .diagnosticMessages(List.of("Aviso secundário."))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("Camada subsuperficial 20-40 cm ausente."));
        assertFalse(report.contains("Aviso duplicado que não deve aparecer."));
        assertFalse(report.contains("P2O5 20 kg/ha"));
        assertFalse(report.contains("Fornecido: N 60.00, P2O5"));
        assertFalse(report.contains("Dose zero."));
        assertFalse(report.contains("Superfosfato Simples"));
        assertTrue(report.contains("Bloqueado por pH em água > 7,0"));
        assertTrue(report.contains("Aviso único."));
        assertFalse(report.contains("Aviso secundário."));
    }

    @Test
    void buildTechnicalReport_WhenOnlyAcidityCorrectionSkipsFertilizationSections() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .requesterName("Produtor")
                        .propertyName("Fazenda")
                        .plotIdentification("Talhao 1")
                        .cropName("MILHO")
                        .recommendationType("ACIDITY_OR_SALINITY_CORRECTION")
                        .issuedAt(LocalDateTime.of(2026, 7, 9, 10, 0))
                        .limingRequirement(RecommendationCalculationService.LimingRequirementResult.builder()
                                .selectedCriteria("SATURACAO_POR_BASES_TROCAVEIS")
                                .criterionJustification("Critério selecionado no payload.")
                                .formula("NC = CTC * (V2 - V1) / 100")
                                .calculatedRequirement(2.0)
                                .calciumMagnesiumBalance(new CalciumMagnesiumBalanceCalculator().calculate(4d, 20d, 10d))
                                .unit("t/ha")
                                .warnings(List.of())
                                .build())
                        .gypsumRequirement(RecommendationCalculationService.GypsumRequirementResult.builder()
                                .evaluated(false)
                                .justification("Não é possível recomendar gessagem sem análises das camadas subsuperficiais.")
                                .warnings(List.of("Análise de fertilidade sem extrato/camada subsuperficial 21-40 ou 41-60 cm."))
                                .build())
                        .correctiveFertilizationRows(List.of(
                                RecommendationCalculationService.CorrectiveFertilizationRow.builder()
                                        .correctedAttribute("P2O5 corretivo")
                                        .suggestedSource("Superfosfato Simples")
                                        .dose(120.0)
                                        .doseUnit("kg/ha")
                                        .build()))
                        .fertilizationRecommendationRows(List.of(
                                RecommendationCalculationService.FertilizationRecommendationRow.builder()
                                        .phase("Plantio")
                                        .suggestedFertilizer("04-14-08")
                                        .fertilizerQuantityKgHa(250.0)
                                        .build()))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("7. Calagem"));
        assertTrue(report.contains("A relação Ca/Mg do solo é igual a 2.00"));
        assertTrue(report.contains("relação Ca/Mg esperada entre 3:1 e 4:1"));
        assertTrue(report.contains("Composição teórica para 3:1"));
        assertTrue(report.contains("classificação: Calcário dolomítico"));
        assertTrue(report.contains("Composição teórica para 4:1"));
        assertTrue(report.contains("classificação: Calcário calcítico"));
        assertTrue(report.contains("8. Gessagem"));
        assertTrue(report.contains("Não é possível recomendar gessagem"));
        assertFalse(report.contains("9. Adubação corretiva"));
        assertFalse(report.contains("10. Adubação de plantio"));
        assertFalse(report.contains("11. Adubação de cobertura"));
        assertFalse(report.contains("13. Fertilizantes recomendados"));
        assertFalse(report.contains("Superfosfato Simples"));
        assertFalse(report.contains("04-14-08"));
    }

    @Test
    void buildTechnicalReport_DoesNotRenderEmptyCoverageNutrientLabel() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .requesterName("Produtor")
                        .propertyName("Fazenda")
                        .plotIdentification("Talhao 1")
                        .cropName("MILHO")
                        .recommendationType("BOTH")
                        .issuedAt(LocalDateTime.of(2026, 7, 9, 10, 0))
                        .fertilizationRecommendationRows(List.of(
                                RecommendationCalculationService.FertilizationRecommendationRow.builder()
                                        .phase("Opção 1 - Cobertura com formulado")
                                        .nutrients("N: 20.00 kg/ha, : 0.00 kg/ha, K2O: 40.00 kg/ha, S: 0.00 kg/ha")
                                        .suggestedFertilizer("20-00-20")
                                        .fertilizerQuantityKgHa(200.0)
                                        .providedN(20.0)
                                        .providedP2O5(0.0)
                                        .providedK2O(40.0)
                                        .providedS(0.0)
                                        .build()))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("11. Adubação de cobertura"));
        assertTrue(report.contains("N: 20.00 kg/ha, P2O5: 0.00 kg/ha, K2O: 40.00 kg/ha, S: 0.00 kg/ha"));
        assertFalse(report.contains(", : 0.00 kg/ha"));
        assertFalse(report.contains("| : 0.00 kg/ha"));
    }

    @Test
    void buildTechnicalReport_OrdersFertilizerSubsectionsAndTechnicalWarning() {
        RecommendationCalculationService.RecommendationCalculationResult result =
                RecommendationCalculationService.RecommendationCalculationResult.builder()
                        .requesterName("Produtor")
                        .propertyName("Fazenda")
                        .plotIdentification("Talhao 1")
                        .cropName("AMENDOIM")
                        .recommendationType("BOTH")
                        .issuedAt(LocalDateTime.of(2026, 7, 9, 10, 0))
                        .fertilizerSuggestions(List.of(
                                RecommendationCalculationService.FertilizerSuggestion.builder()
                                        .fertilizerType("FORMULADO")
                                        .fertilizerName("NPK 05-30-05")
                                        .n(5.0)
                                        .p2o5(30.0)
                                        .k2o(5.0)
                                        .reason("Selecionado para plantio.")
                                        .build()))
                        .alternativeFertilizationRows(List.of(
                                RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                                        .sourceType("ORGANOMINERAL")
                                        .nutrientOrObjective("Matéria orgânica")
                                        .sourceName("Composto")
                                        .dose("100.00")
                                        .unit("kg/ha")
                                        .justification("Alternativa válida.")
                                        .build()))
                        .opportunityCostDecisionRows(List.of(
                                RecommendationCalculationService.OpportunityCostDecisionRow.builder()
                                        .category("FORMULADO")
                                        .fertilizerName("NPK 05-30-05")
                                        .commercialPrice(BigDecimal.TEN)
                                        .opportunityPrice(BigDecimal.ONE)
                                        .commercialWeightKg(BigDecimal.valueOf(50))
                                        .ratio(BigDecimal.TEN)
                                        .decision("avaliado")
                                        .contributionSummary("Comparado por preço.")
                                        .build()))
                        .warnings(List.of("Aviso técnico final."))
                        .build();

        String report = reportService.buildTechnicalReport(result);

        int fertilizers = report.indexOf("13. Fertilizantes recomendados");
        int alternativeSources = report.indexOf("13.1. Fontes quelatadas, orgânicas e organominerais");
        int micronutrientComplements = report.indexOf("13.2. Adubação complementar de micronutrientes com outras fontes");
        int opportunityCost = report.indexOf("13.3. Comparativo de custo de oportunidade");
        int warning = report.indexOf("14. Observações técnicas");

        assertTrue(fertilizers >= 0);
        assertTrue(alternativeSources > fertilizers);
        assertTrue(micronutrientComplements > alternativeSources);
        assertTrue(opportunityCost > micronutrientComplements);
        assertTrue(warning > opportunityCost);
        assertFalse(report.contains("14. Fontes orgânicas"));
        assertFalse(report.contains("16. Aviso técnico"));
    }
}
