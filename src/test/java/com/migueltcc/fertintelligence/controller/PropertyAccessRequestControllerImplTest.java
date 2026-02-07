package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
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

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.migueltcc.fertintelligence.controller.implementation.PropertyAccessRequestControllerImpl.class)
class PropertyAccessRequestControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyAccessRequestService propertyAccessRequestService;

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Deve decidir (aprovar/rejeitar) uma solicitação com sucesso")
    void shouldDecideRequest() throws Exception {
        PropertyAccessRequestResponseDto responseDto = PropertyAccessRequestResponseDto.builder()
                .id(4L)
                .status(AccessRequestStatus.APPROVED)
                .build();

        Mockito.when(propertyAccessRequestService.decideRequest(anyLong(), anyBoolean(), anyString()))
                .thenReturn(responseDto);

        PropertyAccessRequestDecisionRequestDto decisionDto = PropertyAccessRequestDecisionRequestDto.builder()
                .approve(true)
                .build();

        mockMvc.perform(post("/property-access/4/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decisionDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(AccessRequestStatus.APPROVED.name())));

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
                // Nota: Verifique se PropertyResponseDto usa "nome" ou "name". Geralmente é "nome" no seu projeto.
                .andExpect(jsonPath("$[0].nome", is("Fazenda Aprovada")));
    }

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Deve retornar todas as solicitações recebidas pelo proprietário")
    void shouldGetReceivedRequests() throws Exception {
        PropertyAccessRequestResponseDto responseDto = PropertyAccessRequestResponseDto.builder()
                .id(5L)
                .propertyName("Fazenda Teste")
                .requesterName("Solicitante")
                .status(AccessRequestStatus.PENDING)
                .build();

        Mockito.when(propertyAccessRequestService.getReceivedRequests("owner"))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/property-access/received")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                // CORREÇÃO AQUI: Mudado de propertyName para nome_propriedade
                .andExpect(jsonPath("$[0].nome_propriedade", is("Fazenda Teste")));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Deve revogar o acesso ou desvincular-se de uma propriedade")
    void shouldRevokeAccess() throws Exception {
        Mockito.doNothing().when(propertyAccessRequestService).revokeAccess(anyLong(), anyString());

        mockMvc.perform(delete("/property-access/revoke/15")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(propertyAccessRequestService).revokeAccess(15L, "user");
    }
}