package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationFertilizerResolver;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.ShoppingInputCostService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.ShoppingListReportService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShoppingListReportServiceTest {

    @Test
    void buildDoesNotGenerateLegacyInstitutionalOrIdentificationHeaders() {
        ShoppingListReportService service = new ShoppingListReportService(
                mock(DirectRecommendationRepository.class),
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class),
                mock(DirectRecommendationPlantingFormulatedFertilizerLineRepository.class),
                mock(DirectRecommendationCoverageFormulatedFertilizerLineRepository.class),
                mock(DirectRecommendationFertilizerResolver.class),
                mock(ShoppingInputCostService.class));

        String report = service.build(RecommendationModel.builder().build());

        assertThat(report)
                .contains("Lista de insumos para a área cultivada")
                .doesNotContain("<!-- formato:")
                .doesNotContain("Endereço:")
                .doesNotContain("Telefone/WhatsApp:")
                .doesNotContain("E-mail:")
                .doesNotContain("CEO:")
                .doesNotContain("Identificação\n\n");
    }
}
