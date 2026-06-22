package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecommendationControllerImplTest extends AbstractControllerTest {

    @Test
    void recommendationCreateRequestDto_AcceptsCamelCaseTableGroups() throws Exception {
        String json = """
                {
                  "tipo_recomendacao": "ACIDITY_OR_SALINITY_CORRECTION",
                  "id_propriedade": 1,
                  "id_talhao": 2,
                  "id_extrato_analise_fisica": 3,
                  "id_analise_fertilidade_solo": 3,
                  "id_extrato_analise_extrato_saturacao": 3,
                  "id_pasta_cultura_anual": 4,
                  "id_cultura": 8,
                  "id_tabela_adubacao_cultura": 5,
                  "id_tabela_interpretacao_fertilidade_solo": 2,
                  "id_tabela_interpretacao_analise_foliar": 18,
                  "cropFertilizationTableGroup": "DEFAULT",
                  "soilFertilityInterpretationCriteriaTableGroup": "PUBLIC",
                  "cropFoliarAnalysisInterpretationTableGroup": "DEFAULT",
                  "criterio_calagem": null,
                  "origem_adubos": "PUBLIC"
                }
                """;

        RecommendationCreateRequestDto dto = objectMapper.readValue(json, RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertEquals(TechnicalTableGroup.PADRAO, dto.getCropFertilizationTableGroup());
        org.junit.jupiter.api.Assertions.assertEquals(TechnicalTableGroup.PUBLICAS, dto.getSoilFertilityInterpretationCriteriaTableGroup());
        org.junit.jupiter.api.Assertions.assertEquals(TechnicalTableGroup.PADRAO, dto.getCropFoliarAnalysisInterpretationTableGroup());
    }

    @Test
    void recommendationCreateRequestDto_AcceptsEnglishDefaultFertilizerSourceOption() throws Exception {
        String json = """
                {
                  "tipo_recomendacao": "ACIDITY_OR_SALINITY_CORRECTION",
                  "id_propriedade": 1,
                  "id_talhao": 2,
                  "id_extrato_analise_fisica": 3,
                  "id_analise_fertilidade_solo": 3,
                  "id_extrato_analise_extrato_saturacao": 3,
                  "id_pasta_cultura_anual": 4,
                  "id_cultura": 8,
                  "id_tabela_adubacao_cultura": 5,
                  "id_tabela_interpretacao_fertilidade_solo": 2,
                  "id_tabela_interpretacao_analise_foliar": 18,
                  "grupo_tabela_adubacao_cultura": "PADRAO",
                  "grupo_tabela_interpretacao_fertilidade_solo": "PUBLICAS",
                  "grupo_tabela_interpretacao_analise_foliar": "PADRAO",
                  "criterio_calagem": null,
                  "origem_adubos": "DEFAULT"
                }
                """;

        RecommendationCreateRequestDto dto = objectMapper.readValue(json, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto legacyDto = objectMapper.readValue(
                json.replace("\"origem_adubos\": \"DEFAULT\"", "\"origem_adubos\": \"PADRAO\""),
                RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertEquals(FertilizerSourceOption.DEFAULT, dto.getOrigemAdubos());
        org.junit.jupiter.api.Assertions.assertEquals(FertilizerSourceOption.DEFAULT, legacyDto.getOrigemAdubos());
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateRecommendation_ReturnsOk() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationCreateRequestDto request = RecommendationCreateRequestDto.builder()
                .recommendationType(RecommendationType.BOTH)
                .propertyId(10L)
                .plotId(20L)
                .physicalAnalysisExtractId(4L)
                .soilFertilityAnalysisId(2L)
                .saturationExtractAnalysisExtractId(5L)
                .annualCropFolderId(6L)
                .cropId(7L)
                .cropFertilizationTableId(100L)
                .soilFertilityInterpretationCriteriaTableId(200L)
                .cropFoliarAnalysisInterpretationTableId(300L)
                .limingCriteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .origemAdubos(FertilizerSourceOption.BOTH)
                .build();

        RecommendationModel saved = RecommendationModel.builder()
                .id(99L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .origemAdubos(FertilizerSourceOption.BOTH)
                .technicalReport("laudo preliminar")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder().id(2L).plot(plot).analysisYear(2026).build();
        RangeExtractModel rangeExtract = RangeExtractModel.builder().id(3L).analysis(soilAnalysis).build();
        PhysicalAnalysisExtractModel physicalAnalysis = PhysicalAnalysisExtractModel.builder().id(4L).rangeExtract(rangeExtract).build();
        SaturationExtractAnalysisExtractModel saturationAnalysis = SaturationExtractAnalysisExtractModel.builder().id(5L).rangeExtract(rangeExtract).build();
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(6L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder().id(7L).folder(folder).name(NomeComum.ALGODAO).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));
        when(physicalAnalysisExtractRepository.findById(4L)).thenReturn(Optional.of(physicalAnalysis));
        when(soilAnalysisRepository.findById(2L)).thenReturn(Optional.of(soilAnalysis));
        when(saturationExtractAnalysisExtractRepository.findById(5L)).thenReturn(Optional.of(saturationAnalysis));
        when(annualCropFolderRepository.findById(6L)).thenReturn(Optional.of(folder));
        when(cropRepository.findById(7L)).thenReturn(Optional.of(crop));
        when(recommendationRepository.save(any(RecommendationModel.class))).thenReturn(saved);

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMine_ReturnsList() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationModel item = RecommendationModel.builder()
                .id(1L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.FERTILIZATION)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findAllByCreatorOrderByCreatedAtDesc(user)).thenReturn(List.of(item));

        mockMvc.perform(get("/recommendation/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].origem_adubos").value("BOTH"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecommendation_ReturnsOne() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationModel item = RecommendationModel.builder()
                .id(7L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(7L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/recommendation/get").param("id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void preparePrint_ReturnsOne() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationModel item = RecommendationModel.builder()
                .id(8L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(8L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/recommendation/print").param("id", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteRecommendation_ReturnsNoContent() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationModel item = RecommendationModel.builder()
                .id(7L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(7L)).thenReturn(Optional.of(item));
        doNothing().when(recommendationRepository).delete(item);

        mockMvc.perform(delete("/recommendation/delete").param("id", "7"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void improveNarrative_ReturnsOk() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationModel item = RecommendationModel.builder()
                .id(7L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo 100 kg/ha")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(7L)).thenReturn(Optional.of(item));
        when(recommendationRepository.save(any(RecommendationModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/recommendation/improve-narrative").param("id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L));
    }
}
