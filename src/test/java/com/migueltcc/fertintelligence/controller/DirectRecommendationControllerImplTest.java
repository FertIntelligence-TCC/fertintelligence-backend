package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DirectRecommendationControllerImplTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "testuser")
    void getByRecommendationReturnsPersistedMicronutrientLinesWithApplicableDose() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(30L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder()
                .id(40L)
                .folder(folder)
                .name(NomeComum.ALGODAO)
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5)
                .distanceBetweenPits(0.25)
                .plantsPerPit(2.0)
                .build();
        RecommendationModel recommendation = RecommendationModel.builder()
                .id(7L)
                .creator(user)
                .property(property)
                .plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO)
                .cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(70L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(recommendation));
        when(directRecommendationRepository.findByRecommendation(recommendation)).thenReturn(Optional.of(directRecommendation));
        when(annualCropFolderRepository.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.of(folder));
        when(cropRepository.findTopByFolderAndNameOrderByIdDesc(folder, NomeComum.ALGODAO)).thenReturn(Optional.of(crop));
        when(directRecommendationMicronutrientFertilizerLineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                        .id(701L)
                        .directRecommendation(directRecommendation)
                        .micronutrient(AppliedMicronutrient.Zn)
                        .micronutrientDoseKgHa(2.0)
                        .fertilizerId(801L)
                        .fertilizerDoseKgHa(8.0)
                        .doseUnitMode("PIT")
                        .doseUnitLabel("g/cova")
                        .gramsPerLinearMeter(null)
                        .gramsPerPit(1.0)
                        .technicalObservation("Dose calculada por Zn.")
                        .build()));

        mockMvc.perform(get("/direct-recommendation/get-by-recommendation").param("recommendationId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(70L))
                .andExpect(jsonPath("$.dose_unit_mode").value("PIT"))
                .andExpect(jsonPath("$.dose_unit_label").value("g/cova"))
                .andExpect(jsonPath("$.applicable_dose_column").value("gPerPit"))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].nome_adubo").value("Sulfato de zinco"))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].dose_g_cova").value(1.0))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].dose_aplicavel_valor").value(1.0))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].dose_aplicavel_unidade").value("g/cova"))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].dose_aplicavel_coluna").value("gPerPit"))
                .andExpect(jsonPath("$.adubos_micronutrientes[0].observacao_tecnica").value("Dose calculada por Zn."));
    }
}
