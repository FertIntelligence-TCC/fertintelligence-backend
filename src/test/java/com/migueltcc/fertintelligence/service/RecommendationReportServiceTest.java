package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationReportService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationReportServiceTest {

    private final RecommendationReportService reportService = new RecommendationReportService();

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
        assertTrue(report.contains("8. Gessagem"));
        assertTrue(report.contains("Não é possível recomendar gessagem"));
        assertFalse(report.contains("9. Adubação corretiva"));
        assertFalse(report.contains("10. Adubação de plantio"));
        assertFalse(report.contains("11. Adubação de cobertura"));
        assertFalse(report.contains("13. Fertilizantes recomendados"));
        assertFalse(report.contains("Superfosfato Simples"));
        assertFalse(report.contains("04-14-08"));
    }
}
