package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
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
    @WithMockUser(username = "testuser")
    void generateRecommendation_ReturnsOk() throws Exception {
        UserModel user = UserModel.builder().id(1L).username("testuser").name("Test User").cargo(Cargo.AGRONOMO_CONSULTOR).build();
        PropertyModel property = PropertyModel.builder().id(10L).nome("Fazenda Teste").owner(user).build();
        PlotModel plot = PlotModel.builder().id(20L).identification("Talhao A").property(property).build();

        RecommendationCreateRequestDto request = RecommendationCreateRequestDto.builder()
                .recommendationType(RecommendationType.BOTH)
                .propertyId(10L)
                .plotId(20L)
                .cropYear(2026)
                .cropName(NomeComum.ALGODAO)
                .cropFertilizationTableId(100L)
                .soilFertilityInterpretationCriteriaTableId(200L)
                .cropFoliarAnalysisInterpretationTableId(300L)
                .limingCriteria(CriterioCalagem.PORCENTAGEM_DE_SATURACAO_DAS_BASES)
                .build();

        RecommendationModel saved = RecommendationModel.builder()
                .id(99L).creator(user).property(property).plot(plot)
                .recommendationType(RecommendationType.BOTH)
                .cropName(NomeComum.ALGODAO).cropYear(2026)
                .technicalReport("laudo preliminar")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));
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
                .cropName(NomeComum.ALGODAO).cropYear(2025)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findAllByCreatorOrderByCreatedAtDesc(user)).thenReturn(List.of(item));

        mockMvc.perform(get("/recommendation/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
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
                .cropName(NomeComum.ALGODAO).cropYear(2024)
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
                .cropName(NomeComum.ALGODAO).cropYear(2024)
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
                .cropName(NomeComum.ALGODAO).cropYear(2024)
                .technicalReport("laudo")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(7L)).thenReturn(Optional.of(item));
        doNothing().when(recommendationRepository).delete(item);

        mockMvc.perform(delete("/recommendation/delete").param("id", "7"))
                .andExpect(status().isNoContent());
    }
}