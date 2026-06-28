package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.MicronutrientFertilizerSelectionService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MicronutrientFertilizerSelectionServiceTest {

    private final MicronutrientFertilizerSelectionService service = new MicronutrientFertilizerSelectionService(null);

    @Test
    void selectsHighestPositiveMicronutrientConcentrationAndCalculatesDose() {
        SimpleMineralFertilizerModel lowBoron = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name("Fonte B 10")
                .B(10d)
                .build();
        SimpleMineralFertilizerModel highBoron = SimpleMineralFertilizerModel.builder()
                .id(2L)
                .name("Fonte B 20")
                .B(20d)
                .build();

        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult result =
                service.selectBoron(2d, List.of(lowBoron, highBoron));

        assertThat(result.micronutrient()).isEqualTo(AppliedMicronutrient.B);
        assertThat(result.selectedFertilizer()).isEqualTo(highBoron);
        assertThat(result.selectedConcentrationPercent()).isEqualTo(20d);
        assertThat(result.fertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(result.technicalMessage()).isNull();
    }

    @Test
    void returnsTechnicalMessageWhenNoApplicableSourceExists() {
        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name("Sem cobre")
                .Cu(0d)
                .build();

        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult result =
                service.selectCopper(1.5d, List.of(fertilizer));

        assertThat(result.selectedFertilizer()).isNull();
        assertThat(result.fertilizerDoseKgHa()).isNull();
        assertThat(result.technicalMessage()).contains("Sem adubo mineral simples sólido cadastrado");
    }

    @Test
    void doesNotCalculateWhenRecommendedDoseIsZero() {
        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name("Fonte Zn")
                .Zn(30d)
                .build();

        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult result =
                service.selectZinc(0d, List.of(fertilizer));

        assertThat(result.selectedFertilizer()).isNull();
        assertThat(result.fertilizerDoseKgHa()).isNull();
        assertThat(result.technicalMessage()).contains("igual a zero");
    }

    @Test
    void returnsOnlyRequestedMicronutrientsInStableOrder() {
        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name("Fonte mista")
                .B(10d)
                .Zn(25d)
                .build();

        List<MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult> results = service.select(
                List.of(fertilizer),
                Map.of(AppliedMicronutrient.Zn, 2.5d, AppliedMicronutrient.B, 1d));

        assertThat(results).extracting(MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult::micronutrient)
                .containsExactly(AppliedMicronutrient.B, AppliedMicronutrient.Zn);
    }
}
