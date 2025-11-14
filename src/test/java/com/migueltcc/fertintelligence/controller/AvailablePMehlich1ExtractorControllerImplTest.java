package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AvailablePMehlich1ExtractorControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private AvailablePMehlich1ExtractorModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(2L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(11L)
                .creator(proprietarioUser)
                .build();

        existingCriterion = AvailablePMehlich1ExtractorModel.builder()
                .id(101L)
                .table(ownerTable)
                // --- Campos para Solo Arenoso (Valores de exemplo 1.0 a 8.0) ---
                .p_content_sandy_too_low(1.0) //
                .p_content_sandy_low_i(2.0) //
                .p_content_sandy_low_f(3.0) //
                .p_content_sandy_medium_i(4.0) //
                .p_content_sandy_medium_f(5.0) //
                .p_content_sandy_hight_i(6.0) //
                .p_content_sandy_hight_f(7.0) //
                .p_content_sandy_too_hight(8.0) //
                // --- Campos para Solo Arenoso/Argiloso (Valores de exemplo 11.0 a 18.0) ---
                .p_content_sandy_clayey_too_low(11.0) //
                .p_content_sandy_clayey_low_i(12.0) //
                .p_content_sandy_clayey_low_f(13.0) //
                .p_content_sandy_clayey_medium_i(14.0) //
                .p_content_sandy_clayey_medium_f(15.0) //
                .p_content_sandy_clayey_hight_i(16.0) //
                .p_content_sandy_clayey_hight_f(17.0) //
                .p_content_sandy_clayey_too_hight(18.0) //
                // --- Campos para Solo Argiloso (Valores de exemplo 21.0 a 28.0) ---
                .p_content_clayey_too_low(21.0) //
                .p_content_clayey_low_i(22.0) //
                .p_content_clayey_low_f(23.0) //
                .p_content_clayey_medium_i(24.0) //
                .p_content_clayey_medium_f(25.0) //
                .p_content_clayey_hight_i(26.0) //
                .p_content_clayey_hight_f(27.0) //
                .p_content_clayey_too_hight(28.0) //
                // --- Campos para Solo Muito Argiloso (Valores de exemplo 31.0 a 38.0) ---
                .p_content_very_clayey_too_low(31.0) //
                .p_content_very_clayey_low_i(32.0) //
                .p_content_very_clayey_low_f(33.0) //
                .p_content_very_clayey_medium_i(34.0) //
                .p_content_very_clayey_medium_f(35.0) //
                .p_content_very_clayey_hight_i(36.0) //
                .p_content_very_clayey_hight_f(37.0) //
                .p_content_very_clayey_too_hight(38.0) //
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAvailablePMehlich1ExtractorSuccessfully() throws Exception {
        AvailablePMehlich1ExtractorCreateRequestDto requestDto =
                AvailablePMehlich1ExtractorCreateRequestDto.builder()
                        .p_content_sandy_too_low(1.0)
                        .p_content_sandy_low_i(2.0)
                        .p_content_sandy_low_f(3.0)
                        .p_content_sandy_medium_i(4.0)
                        .build();

        AvailablePMehlich1ExtractorModel savedCriterion = existingCriterion.toBuilder()
                .id(121L)
                .p_content_sandy_low_i(2.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availablePMehlich1ExtractorRepository.findByTable(ownerTable))
                .thenReturn(Optional.empty());
        when(availablePMehlich1ExtractorRepository.save(any(AvailablePMehlich1ExtractorModel.class)))
                .thenReturn(savedCriterion);

        mockMvc.perform(post("/available-p-mehlich-1-extractor/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/available-p-mehlich-1-extractor/get?criterionId=121"))
                .andExpect(jsonPath("$.id").value(121L))
                .andExpect(jsonPath("$.teor_inicial_baixo_fosforo_solo_arenoso").value(2.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAvailablePMehlich1ExtractorByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availablePMehlich1ExtractorRepository.findByTable(ownerTable))
                .thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/available-p-mehlich-1-extractor/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.teor_inicial_baixo_fosforo_solo_arenoso").value(2.0))
                .andExpect(jsonPath("$.teor_inicial_medio_fosforo_solo_arenoso_argiloso").value(14.0));    }

    @Test
    @WithMockUser(username = "testuser")
    void updateAvailablePMehlich1ExtractorSuccessfully() throws Exception {
        AvailablePMehlich1ExtractorPostRequestDto requestDto =
                AvailablePMehlich1ExtractorPostRequestDto.builder()
                        .p_content_sandy_too_low(99.9)
                        .p_content_clayey_low_i(123.4)
                        .build();

        AvailablePMehlich1ExtractorModel updatedCriterion = existingCriterion.toBuilder()
                .p_content_sandy_too_low(99.9)
                .p_content_clayey_low_i(123.4)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availablePMehlich1ExtractorRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(availablePMehlich1ExtractorRepository.save(any(AvailablePMehlich1ExtractorModel.class)))
                .thenReturn(updatedCriterion);

        mockMvc.perform(put("/available-p-mehlich-1-extractor/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menor_teor_fosforo_solo_arenoso").value(99.9))
                .andExpect(jsonPath("$.teor_inicial_baixo_fosforo_solo_argiloso").value(123.4));    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteAvailablePMehlich1ExtractorSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availablePMehlich1ExtractorRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(availablePMehlich1ExtractorRepository).delete(existingCriterion);

        mockMvc.perform(delete("/available-p-mehlich-1-extractor/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(availablePMehlich1ExtractorRepository).delete(existingCriterion);
    }
}