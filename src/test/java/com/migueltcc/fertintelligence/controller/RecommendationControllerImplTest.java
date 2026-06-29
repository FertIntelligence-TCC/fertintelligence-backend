package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOptionConverter;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
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
    void recommendationCreateRequestDto_AcceptsAllFertilizerSourceOption() throws Exception {
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
                  "origem_adubos": "ALL"
                }
                """;

        RecommendationCreateRequestDto dto = objectMapper.readValue(json, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto legacyDto = objectMapper.readValue(
                json.replace("\"origem_adubos\": \"ALL\"", "\"origem_adubos\": \"TODOS\""),
                RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertEquals(FertilizerSourceOption.ALL, dto.getOrigemAdubos());
        org.junit.jupiter.api.Assertions.assertEquals(FertilizerSourceOption.ALL, legacyDto.getOrigemAdubos());
        org.junit.jupiter.api.Assertions.assertEquals(
                "TODOS",
                new FertilizerSourceOptionConverter().convertToDatabaseColumn(dto.getOrigemAdubos()));
    }

    @Test
    void recommendationCreateRequestDto_AcceptsOptionalOrganicFertilizerFields() throws Exception {
        String legacyJson = """
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
                  "origem_adubos": "ALL"
                }
                """;
        String organicJson = legacyJson.replace(
                "\"origem_adubos\": \"ALL\"",
                """
                "origem_adubos": "ALL",
                  "usar_adubo_organico": true,
                  "nutriente_referencia_adubo_organico": "NITROGENIO"
                """);
        String camelCaseJson = organicJson
                .replace("\"usar_adubo_organico\"", "\"useOrganicFertilizer\"")
                .replace("\"nutriente_referencia_adubo_organico\"", "\"organicFertilizerReferenceNutrient\"");

        RecommendationCreateRequestDto legacyDto = objectMapper.readValue(legacyJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto organicDto = objectMapper.readValue(organicJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto camelCaseDto = objectMapper.readValue(camelCaseJson, RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getUseOrganicFertilizer());
        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getOrganicFertilizerReferenceNutrient());
        org.junit.jupiter.api.Assertions.assertTrue(organicDto.getUseOrganicFertilizer());
        org.junit.jupiter.api.Assertions.assertEquals(Nutriente.NITROGENIO, organicDto.getOrganicFertilizerReferenceNutrient());
        org.junit.jupiter.api.Assertions.assertTrue(camelCaseDto.getUseOrganicFertilizer());
        org.junit.jupiter.api.Assertions.assertEquals(Nutriente.NITROGENIO, camelCaseDto.getOrganicFertilizerReferenceNutrient());
    }

    @Test
    void recommendationCreateRequestDto_AcceptsOptionalGreenFertilizerFields() throws Exception {
        String legacyJson = """
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
                  "origem_adubos": "ALL"
                }
                """;
        String greenFertilizerJson = legacyJson.replace(
                "\"origem_adubos\": \"ALL\"",
                """
                "origem_adubos": "ALL",
                  "usar_adubo_verde": true,
                  "especie_adubo_verde": "Crotalaria",
                  "massa_verde_adubo_verde": 10000.0,
                  "umidade_adubo_verde_percentual": 70.0,
                  "massa_seca_adubo_verde": 3000.0
                """);
        String camelCaseJson = greenFertilizerJson
                .replace("\"usar_adubo_verde\"", "\"useGreenFertilizer\"")
                .replace("\"especie_adubo_verde\"", "\"greenFertilizerSpecies\"")
                .replace("\"massa_verde_adubo_verde\"", "\"greenFertilizerGreenMass\"")
                .replace("\"umidade_adubo_verde_percentual\"", "\"greenFertilizerMoisturePercentage\"")
                .replace("\"massa_seca_adubo_verde\"", "\"greenFertilizerDryMass\"");

        RecommendationCreateRequestDto legacyDto = objectMapper.readValue(legacyJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto greenFertilizerDto = objectMapper.readValue(greenFertilizerJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto camelCaseDto = objectMapper.readValue(camelCaseJson, RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getUseGreenFertilizer());
        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getGreenFertilizerSpecies());
        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getGreenFertilizerGreenMass());
        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getGreenFertilizerMoisturePercentage());
        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getGreenFertilizerDryMass());
        org.junit.jupiter.api.Assertions.assertTrue(greenFertilizerDto.getUseGreenFertilizer());
        org.junit.jupiter.api.Assertions.assertEquals("Crotalaria", greenFertilizerDto.getGreenFertilizerSpecies());
        org.junit.jupiter.api.Assertions.assertEquals(10000.0, greenFertilizerDto.getGreenFertilizerGreenMass());
        org.junit.jupiter.api.Assertions.assertEquals(70.0, greenFertilizerDto.getGreenFertilizerMoisturePercentage());
        org.junit.jupiter.api.Assertions.assertEquals(3000.0, greenFertilizerDto.getGreenFertilizerDryMass());
        org.junit.jupiter.api.Assertions.assertTrue(camelCaseDto.getUseGreenFertilizer());
        org.junit.jupiter.api.Assertions.assertEquals("Crotalaria", camelCaseDto.getGreenFertilizerSpecies());
        org.junit.jupiter.api.Assertions.assertEquals(10000.0, camelCaseDto.getGreenFertilizerGreenMass());
        org.junit.jupiter.api.Assertions.assertEquals(70.0, camelCaseDto.getGreenFertilizerMoisturePercentage());
        org.junit.jupiter.api.Assertions.assertEquals(3000.0, camelCaseDto.getGreenFertilizerDryMass());
    }

    @Test
    void recommendationCreateRequestDto_AcceptsOptionalBioFertilizerField() throws Exception {
        String legacyJson = """
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
                  "origem_adubos": "ALL"
                }
                """;
        String bioFertilizerJson = legacyJson.replace(
                "\"origem_adubos\": \"ALL\"",
                """
                "origem_adubos": "ALL",
                  "usar_biofertilizante": true
                """);
        String camelCaseJson = bioFertilizerJson
                .replace("\"usar_biofertilizante\"", "\"useBioFertilizer\"");

        RecommendationCreateRequestDto legacyDto = objectMapper.readValue(legacyJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto bioFertilizerDto = objectMapper.readValue(bioFertilizerJson, RecommendationCreateRequestDto.class);
        RecommendationCreateRequestDto camelCaseDto = objectMapper.readValue(camelCaseJson, RecommendationCreateRequestDto.class);

        org.junit.jupiter.api.Assertions.assertNull(legacyDto.getUseBioFertilizer());
        org.junit.jupiter.api.Assertions.assertTrue(bioFertilizerDto.getUseBioFertilizer());
        org.junit.jupiter.api.Assertions.assertTrue(camelCaseDto.getUseBioFertilizer());
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateRecommendation_RejectsOrganicReferenceNutrientWhenOrganicFertilizerIsNotEnabled() throws Exception {
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
                  "origem_adubos": "ALL",
                  "usar_adubo_organico": false,
                  "nutriente_referencia_adubo_organico": "NITROGENIO"
                }
                """;

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("organicFertilizerReferenceNutrientConsistent: O nutriente de referência do adubo orgânico só pode ser informado quando o uso de adubo orgânico estiver habilitado."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateRecommendation_RejectsGreenFertilizerDataWhenGreenFertilizerIsNotEnabled() throws Exception {
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
                  "origem_adubos": "ALL",
                  "usar_adubo_verde": false,
                  "especie_adubo_verde": "Crotalaria"
                }
                """;

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("greenFertilizerDataConsistentWithUsage: Os dados de adubo verde só podem ser informados quando o uso de adubo verde estiver habilitado."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateRecommendation_RejectsInconsistentGreenFertilizerMassesAndMoisture() throws Exception {
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
                  "origem_adubos": "ALL",
                  "usar_adubo_verde": true,
                  "massa_verde_adubo_verde": 10000.0,
                  "umidade_adubo_verde_percentual": 70.0,
                  "massa_seca_adubo_verde": 5000.0
                }
                """;

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("greenFertilizerMassAndMoistureConsistent: A massa seca do adubo verde deve ser consistente com massa verde e umidade informadas."));
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
                .cropFertilizationTableGroup(TechnicalTableGroup.PADRAO)
                .soilFertilityInterpretationCriteriaTableId(200L)
                .soilFertilityInterpretationCriteriaTableGroup(TechnicalTableGroup.PADRAO)
                .cropFoliarAnalysisInterpretationTableId(300L)
                .cropFoliarAnalysisInterpretationTableGroup(TechnicalTableGroup.PADRAO)
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
        CropFertilizationTableModel cropFertilizationTable = CropFertilizationTableModel.builder().id(100L).creator(user).crop_common_name(NomeComum.ALGODAO).build();
        SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable = SoilFertilityInterpretationCriteriaTableModel.builder().id(200L).creator(user).build();
        CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable = CropFoliarAnalysisInterpretationTableModel.builder().id(300L).creator(user).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));
        when(physicalAnalysisExtractRepository.findById(4L)).thenReturn(Optional.of(physicalAnalysis));
        when(soilAnalysisRepository.findById(2L)).thenReturn(Optional.of(soilAnalysis));
        when(saturationExtractAnalysisExtractRepository.findById(5L)).thenReturn(Optional.of(saturationAnalysis));
        when(annualCropFolderRepository.findById(6L)).thenReturn(Optional.of(folder));
        when(cropRepository.findById(7L)).thenReturn(Optional.of(crop));
        when(cropFertilizationTableRepository.findByIdAndCreator_CargoAndPublicTableTrue(100L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(cropFertilizationTable));
        when(soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(200L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(soilInterpretationTable));
        when(cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(300L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(foliarInterpretationTable));
        when(recommendationRepository.save(any(RecommendationModel.class))).thenReturn(saved);

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L));

        ArgumentCaptor<RecommendationModel> recommendationCaptor = ArgumentCaptor.forClass(RecommendationModel.class);
        verify(recommendationRepository).save(recommendationCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(FertilizerSourceOption.BOTH, recommendationCaptor.getValue().getOrigemAdubos());
        org.junit.jupiter.api.Assertions.assertEquals(
                "AMBAS",
                new FertilizerSourceOptionConverter().convertToDatabaseColumn(recommendationCaptor.getValue().getOrigemAdubos()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateRecommendation_BlocksIncompatibleCropAndFertilizationTable() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationCreateRequestDto request = RecommendationCreateRequestDto.builder()
                .recommendationType(RecommendationType.BOTH)
                .propertyId(10L)
                .plotId(20L)
                .soilFertilityAnalysisId(2L)
                .annualCropFolderId(6L)
                .cropId(7L)
                .cropFertilizationTableId(100L)
                .cropFertilizationTableGroup(TechnicalTableGroup.PADRAO)
                .soilFertilityInterpretationCriteriaTableId(200L)
                .soilFertilityInterpretationCriteriaTableGroup(TechnicalTableGroup.PADRAO)
                .cropFoliarAnalysisInterpretationTableId(300L)
                .cropFoliarAnalysisInterpretationTableGroup(TechnicalTableGroup.PADRAO)
                .origemAdubos(FertilizerSourceOption.BOTH)
                .build();

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder().id(2L).plot(plot).analysisYear(2026).build();
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(6L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder().id(7L).folder(folder).name(NomeComum.SOJA).build();
        CropFertilizationTableModel cropFertilizationTable = CropFertilizationTableModel.builder().id(100L).creator(user).crop_common_name(NomeComum.MILHO).build();
        SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable = SoilFertilityInterpretationCriteriaTableModel.builder().id(200L).creator(user).build();
        CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable = CropFoliarAnalysisInterpretationTableModel.builder().id(300L).creator(user).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));
        when(soilAnalysisRepository.findById(2L)).thenReturn(Optional.of(soilAnalysis));
        when(annualCropFolderRepository.findById(6L)).thenReturn(Optional.of(folder));
        when(cropRepository.findById(7L)).thenReturn(Optional.of(crop));
        when(cropFertilizationTableRepository.findByIdAndCreator_CargoAndPublicTableTrue(100L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(cropFertilizationTable));
        when(soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(200L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(soilInterpretationTable));
        when(cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(300L, Cargo.USUARIO_SUPREMO)).thenReturn(Optional.of(foliarInterpretationTable));

        mockMvc.perform(post("/recommendation/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cultura Anual e Tabela de Adubação de Culturas incompatíveis!"));
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
    void getRecommendation_ReturnsDirectRecommendationDoseUnitMetadataWhenGenerated() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(30L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder()
                .id(40L)
                .folder(folder)
                .name(NomeComum.ALGODAO)
                .spacingMode(CropSpacingMode.PLANTS_PER_LINEAR_METER)
                .plantsPerMeter(8.0)
                .build();

        RecommendationModel item = RecommendationModel.builder()
                .id(7L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(70L)
                .recommendation(item)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        item.setDirectRecommendation(directRecommendation);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(7L)).thenReturn(Optional.of(item));
        when(annualCropFolderRepository.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.of(folder));
        when(cropRepository.findTopByFolderAndNameOrderByIdDesc(folder, NomeComum.ALGODAO)).thenReturn(Optional.of(crop));
        when(directRecommendationMicronutrientFertilizerLineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                        .id(701L)
                        .directRecommendation(directRecommendation)
                        .micronutrient(AppliedMicronutrient.B)
                        .micronutrientDoseKgHa(1.2)
                        .fertilizerId(801L)
                        .fertilizerName("Borax")
                        .fertilizerDoseKgHa(10.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(0.5)
                        .gramsPerPit(null)
                        .technicalObservation("Dose calculada por B.")
                        .build()));
        when(directRecommendationPlantingFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                        .id(702L)
                        .directRecommendation(directRecommendation)
                        .phase("Plantio")
                        .fertilizerId(802L)
                        .fertilizerName("04-14-08")
                        .relationUsed("1-3.5-2")
                        .doseKgHa(250.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(12.5)
                        .technicalObservation("Formulado de plantio selecionado.")
                        .build()));
        when(directRecommendationCoverageFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                        .id(703L)
                        .directRecommendation(directRecommendation)
                        .coverageOrder(1)
                        .phase("Cobertura")
                        .fertilizerId(803L)
                        .fertilizerName("20-00-20")
                        .relationUsed("1-0-1")
                        .doseKgHa(180.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(9.0)
                        .technicalObservation("Formulado de cobertura selecionado.")
                        .build()));

        mockMvc.perform(get("/recommendation/get").param("id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_recomendacao_direta").value(70L))
                .andExpect(jsonPath("$.recomendacao_direta.id").value(70L))
                .andExpect(jsonPath("$.recomendacao_direta.dose_unit_mode").value("LINEAR_METER"))
                .andExpect(jsonPath("$.recomendacao_direta.dose_unit_label").value("g/m linear"))
                .andExpect(jsonPath("$.recomendacao_direta.applicable_dose_column").value("gPerLinearMeter"))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].nome_adubo").value("Borax"))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].dose_g_m_linear").value(0.5))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].dose_aplicavel_valor").value(0.5))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].dose_aplicavel_unidade").value("g/m linear"))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].dose_aplicavel_coluna").value("gPerLinearMeter"))
                .andExpect(jsonPath("$.recomendacao_direta.adubos_micronutrientes[0].observacao_tecnica").value("Dose calculada por B."))
                .andExpect(jsonPath("$.recomendacao_direta.formulados_plantio[0].nome_formulado").value("04-14-08"))
                .andExpect(jsonPath("$.recomendacao_direta.formulados_plantio[0].dose_aplicavel_valor").value(12.5))
                .andExpect(jsonPath("$.recomendacao_direta.formulados_cobertura[0].nome_formulado").value("20-00-20"))
                .andExpect(jsonPath("$.recomendacao_direta.formulados_cobertura[0].dose_aplicavel_valor").value(9.0));
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
