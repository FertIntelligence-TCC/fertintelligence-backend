package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.controller.implementation.PropertyAccessRequestControllerImpl;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PropertyAccessRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyAccessRequestControllerImpl.class)
class PropertyAccessRequestControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyAccessRequestService propertyAccessRequestService;

    @Test
    @WithMockUser(username = "agronomo")
    @DisplayName("Deve criar uma solicitação de acesso")
    void shouldCreateAccessRequest() throws Exception {
        PropertyAccessRequestResponseDto responseDto = PropertyAccessRequestResponseDto.builder()
                .id(10L)
                .propertyId(5L)
                .propertyName("Fazenda Modelo")
                .requesterId(2L)
                .requesterName("Agrônomo")
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        // CORREÇÃO AQUI: O serviço espera Long (id da propriedade), não o DTO
        Mockito.when(propertyAccessRequestService.requestAccess(anyLong(), Mockito.eq("agronomo")))
                .thenReturn(responseDto);

        // Aqui usamos o DTO para gerar o JSON correto com "id_propriedade"
        PropertyAccessRequestCreateRequestDto requestDto = PropertyAccessRequestCreateRequestDto.builder()
                .propertyId(5L)
                .build();

        mockMvc.perform(post("/property-access/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.id_propriedade", is(5)))
                .andExpect(jsonPath("$.status", is(AccessRequestStatus.PENDING.name())));

        // Verifica se o serviço foi chamado com o ID extraído do DTO (5L)
        Mockito.verify(propertyAccessRequestService).requestAccess(5L, "agronomo");
    }

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Deve listar solicitações de uma propriedade do proprietário")
    void shouldListRequestsForProperty() throws Exception {
        PropertyAccessRequestResponseDto responseDto = PropertyAccessRequestResponseDto.builder()
                .id(3L)
                .propertyId(1L)
                .propertyName("Sítio do Lago")
                .requesterId(7L)
                .requesterName("Consultor")
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2024, 2, 10, 8, 30))
                .build();

        Mockito.when(propertyAccessRequestService.getRequestsForProperty(1L, "owner"))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/property-access/requests")
                        .param("propertyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].nome_propriedade", is("Sítio do Lago")));

        Mockito.verify(propertyAccessRequestService).getRequestsForProperty(1L, "owner");
    }

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Deve permitir que o proprietário decida sobre a solicitação")
    void shouldDecideOnRequest() throws Exception {
        PropertyAccessRequestResponseDto responseDto = PropertyAccessRequestResponseDto.builder()
                .id(4L)
                .propertyId(2L)
                .status(AccessRequestStatus.APPROVED)
                .createdAt(LocalDateTime.of(2024, 3, 15, 14, 45))
                .build();

        // CORREÇÃO AQUI: O serviço espera Boolean, não o DTO
        Mockito.when(propertyAccessRequestService.decideRequest(anyLong(), anyBoolean(), Mockito.eq("owner")))
                .thenReturn(responseDto);

        // Aqui usamos o DTO para gerar o JSON correto com "solicitacao_aprovada"
        PropertyAccessRequestDecisionRequestDto decisionDto = PropertyAccessRequestDecisionRequestDto.builder()
                .approve(true)
                .build();

        mockMvc.perform(post("/property-access/4/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decisionDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(AccessRequestStatus.APPROVED.name())));

        // Verifica se o serviço foi chamado com o boolean extraído do DTO (true)
        Mockito.verify(propertyAccessRequestService).decideRequest(4L, true, "owner");
    }

    @Test
    @WithMockUser(username = "requester")
    @DisplayName("Deve retornar as propriedades aprovadas para o usuário logado")
    void shouldGetApprovedProperties() throws Exception {
        PropertyResponseDto propertyDto = PropertyResponseDto.builder()
                .id(10L)
                .nome("Fazenda Aprovada")
                .cnpj("12.345.678/0001-90")
                .build();

        Mockito.when(propertyAccessRequestService.getApprovedPropertiesForUser("requester"))
                .thenReturn(Collections.singletonList(propertyDto));

        mockMvc.perform(get("/property-access/my-approved-properties")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].nome", is("Fazenda Aprovada")));
    }

}