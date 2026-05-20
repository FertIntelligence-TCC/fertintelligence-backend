package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;
import com.migueltcc.fertintelligence.service.documentation.EffectivePermissionsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class EffectivePermissionsControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EffectivePermissionsService effectivePermissionsService;

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective - deve retornar permissões efetivas escalares")
    void getEffectivePermissions_ok() throws Exception {
        long propertyId = 10L;

        EffectivePermissionsResponseDto dto = EffectivePermissionsResponseDto.builder()
                .propertyId(propertyId)
                .canManageProperty(false)
                .canEditAllPlotsAnalyses(true)
                .canEditAllPlotsCrops(false)
                .plotsEditableAnalysesCount(3)
                .plotsEditableCropsCount(1)
                .build();

        when(effectivePermissionsService.getEffectivePermissions(propertyId, "john"))
                .thenReturn(dto);

        mockMvc.perform(get("/property-permissions/effective")
                        .param("propertyId", String.valueOf(propertyId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.propertyId").value((int) propertyId))
                .andExpect(jsonPath("$.canManageProperty").value(false))
                .andExpect(jsonPath("$.canEditAllPlotsAnalyses").value(true))
                .andExpect(jsonPath("$.canEditAllPlotsCrops").value(false))
                .andExpect(jsonPath("$.plotsEditableAnalysesCount").value(3))
                .andExpect(jsonPath("$.plotsEditableCropsCount").value(1));

        verify(effectivePermissionsService, times(1))
                .getEffectivePermissions(propertyId, "john");
        verifyNoMoreInteractions(effectivePermissionsService);
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective/plots/analyses - deve listar talhões editáveis para análises")
    void getEditableAnalysesPlots_ok() throws Exception {
        long propertyId = 22L;

        List<PlotSummaryDto> plots = List.of(
                new PlotSummaryDto(1L, "Talhão A"),
                new PlotSummaryDto(2L, "Talhão B")
        );

        // ✅ método correto
        when(effectivePermissionsService.getEditableAnalysesPlots(propertyId, "john"))
                .thenReturn(plots);

        mockMvc.perform(get("/property-permissions/effective/plots/analyses")
                        .param("propertyId", String.valueOf(propertyId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].identification").value("Talhão A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].identification").value("Talhão B"));

        verify(effectivePermissionsService, times(1))
                .getEditableAnalysesPlots(propertyId, "john");
        verifyNoMoreInteractions(effectivePermissionsService);
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective/plots/crops - deve listar talhões editáveis para culturas")
    void getEditableCropsPlots_ok() throws Exception {
        long propertyId = 33L;

        List<PlotSummaryDto> plots = List.of(
                new PlotSummaryDto(7L, "P7"),
                new PlotSummaryDto(9L, "P9"),
                new PlotSummaryDto(11L, "P11")
        );

        // ✅ método correto
        when(effectivePermissionsService.getEditableCropsPlots(propertyId, "john"))
                .thenReturn(plots);

        mockMvc.perform(get("/property-permissions/effective/plots/crops")
                        .param("propertyId", String.valueOf(propertyId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].identification").value("P7"))
                .andExpect(jsonPath("$[1].id").value(9))
                .andExpect(jsonPath("$[1].identification").value("P9"))
                .andExpect(jsonPath("$[2].id").value(11))
                .andExpect(jsonPath("$[2].identification").value("P11"));

        verify(effectivePermissionsService, times(1))
                .getEditableCropsPlots(propertyId, "john");
        verifyNoMoreInteractions(effectivePermissionsService);
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective - sem propertyId deve retornar 500 conforme handler global atual")
    void getEffectivePermissions_missingParam_shouldReturn400() throws Exception {
        mockMvc.perform(get("/property-permissions/effective")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(effectivePermissionsService, never())
                .getEffectivePermissions(anyLong(), anyString());
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective/plots/analyses - sem propertyId deve retornar 500 conforme handler global atual")
    void getEditableAnalysesPlots_missingParam_shouldReturn400() throws Exception {
        mockMvc.perform(get("/property-permissions/effective/plots/analyses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(effectivePermissionsService, never())
                .getEditableAnalysesPlots(anyLong(), anyString());
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("GET /property-permissions/effective/plots/crops - sem propertyId deve retornar 500 conforme handler global atual")
    void getEditableCropsPlots_missingParam_shouldReturn400() throws Exception {
        mockMvc.perform(get("/property-permissions/effective/plots/crops")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(effectivePermissionsService, never())
                .getEditableCropsPlots(anyLong(), anyString());
    }
}