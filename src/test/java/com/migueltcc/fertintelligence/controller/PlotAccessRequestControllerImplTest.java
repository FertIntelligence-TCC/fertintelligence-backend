package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.controller.implementation.PlotAccessRequestControllerImpl;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PlotAccessRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// ✅ NOVA IMPORTAÇÃO DO MOCKITOBEAN (Substitui o antigo @MockBean)
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlotAccessRequestControllerImpl.class)
class PlotAccessRequestControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ USANDO A NOVA ANOTAÇÃO
    @MockitoBean
    private PlotAccessRequestService plotAccessRequestService;

    @Test
    @WithMockUser(username = "consultor")
    @DisplayName("Deve criar solicitação de acesso aos talhões")
    void shouldCreatePlotAccessRequest() throws Exception {
        PlotAccessRequestResponseDto responseDto = PlotAccessRequestResponseDto.builder()
                .id(1L)
                .propertyId(2L)
                .propertyName("Fazenda Teste")
                .requesterId(5L)
                .requesterName("Consultor")
                .requesterCargo(Cargo.AGRONOMO_CONSULTOR)
                .plotId(9L)
                .plotIdentification("Talhão 09")
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 5, 1, 10, 0))
                .build();

        // ✅ CORREÇÃO: Removido o argumento extra (PermissionType) da chamada
        Mockito.when(plotAccessRequestService.requestAccess(anyLong(), anyLong(), eq("consultor")))
                .thenReturn(responseDto);

        PlotAccessRequestCreateRequestDto requestDto = PlotAccessRequestCreateRequestDto.builder()
                .propertyId(2L)
                .plotId(9L)
                .build();

        mockMvc.perform(post("/plot-access/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.id_propriedade", is(2)))
                .andExpect(jsonPath("$.id_talhao", is(9)))
                .andExpect(jsonPath("$.status", is(AccessRequestStatus.PENDING.name())));

        // ✅ CORREÇÃO: Removido o argumento extra no verifier
        Mockito.verify(plotAccessRequestService).requestAccess(eq(2L), eq(9L), eq("consultor"));
    }

    @Test
    @WithMockUser(username = "residente")
    @DisplayName("Deve permitir que residente solicite acesso aos talhões")
    void shouldAllowResidentRequest() throws Exception {
        PlotAccessRequestResponseDto responseDto = PlotAccessRequestResponseDto.builder()
                .id(2L)
                .propertyId(2L)
                .propertyName("Fazenda Teste")
                .requesterId(7L)
                .requesterName("Residente")
                .requesterCargo(Cargo.AGRONOMO_RESIDENTE)
                .plotId(9L)
                .plotIdentification("Talhão 09")
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 5, 2, 10, 0))
                .build();

        // ✅ CORREÇÃO: Removido o argumento extra (PermissionType) da chamada
        Mockito.when(plotAccessRequestService.requestAccess(anyLong(), anyLong(), eq("residente")))
                .thenReturn(responseDto);

        PlotAccessRequestCreateRequestDto requestDto = PlotAccessRequestCreateRequestDto.builder()
                .propertyId(2L)
                .plotId(9L)
                .build();

        mockMvc.perform(post("/plot-access/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.cargo_solicitante", is(Cargo.AGRONOMO_RESIDENTE.name())));

        // ✅ CORREÇÃO: Removido o argumento extra no verifier
        Mockito.verify(plotAccessRequestService).requestAccess(eq(2L), eq(9L), eq("residente"));
    }

    @Test
    @WithMockUser(username = "gerente")
    @DisplayName("Deve listar solicitações para o gerente")
    void shouldListRequestsForManager() throws Exception {
        PlotAccessRequestResponseDto responseDto = PlotAccessRequestResponseDto.builder()
                .id(10L)
                .propertyId(4L)
                .propertyName("Propriedade ABC")
                .requesterId(6L)
                .requesterName("Outro Residente")
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 6, 1, 12, 30))
                .build();

        Mockito.when(plotAccessRequestService.getRequestsForManager(4L, AccessRequestStatus.PENDING, "gerente"))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/plot-access/requests")
                        .param("propertyId", "4")
                        .param("status", "PENDING")) // ✅ CORREÇÃO: Parâmetro adicionado para bater com o Mock
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].nome_propriedade", is("Propriedade ABC")));

        Mockito.verify(plotAccessRequestService).getRequestsForManager(4L, AccessRequestStatus.PENDING, "gerente");
    }

    @Test
    @WithMockUser(username = "gerente")
    @DisplayName("Deve permitir que o gerente decida sobre a solicitação")
    void shouldDecideOnPlotRequest() throws Exception {
        PlotAccessRequestResponseDto responseDto = PlotAccessRequestResponseDto.builder()
                .id(7L)
                .propertyId(3L)
                .status(AccessRequestStatus.APPROVED)
                .createdAt(LocalDateTime.of(2024, 7, 1, 14, 45))
                .build();

        Mockito.when(plotAccessRequestService.decideRequest(eq(7L), eq(true), eq("gerente")))
                .thenReturn(responseDto);

        PlotAccessRequestDecisionRequestDto decisionDto = PlotAccessRequestDecisionRequestDto.builder()
                .approve(true)
                .build();

        mockMvc.perform(post("/plot-access/7/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decisionDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(AccessRequestStatus.APPROVED.name())));

        Mockito.verify(plotAccessRequestService).decideRequest(7L, true, "gerente");
    }
}