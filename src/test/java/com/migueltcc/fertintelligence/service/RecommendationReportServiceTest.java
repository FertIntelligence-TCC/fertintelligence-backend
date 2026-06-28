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
                        .build();

        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("g/kg"));
        assertTrue(report.contains("mmolc/dm³"));
        assertTrue(report.contains("mg/dm³"));
        assertTrue(report.contains("(mmolc)**0.5"));
        assertTrue(report.contains("kg/ha"));
        assertFalse(report.contains("g/dm3"));
        assertFalse(report.contains("cmolc/dm³"));
        assertFalse(report.contains("CaCO3"));
    }
}
