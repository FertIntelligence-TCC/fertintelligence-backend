package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
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
public class AvailablePAnionExchangeResinExtractorControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private AvailablePAnionExchangeResinExtractorModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .build();

        existingCriterion = AvailablePAnionExchangeResinExtractorModel.builder()
                .id(100L)
                .table(ownerTable)
                .p_content_cotton_too_low(1.0)
                .p_content_cotton_low_i(2.0)
                .p_content_cotton_low_f(3.0)
                .p_content_cotton_medium_i(4.0)
                .p_content_cotton_medium_f(5.0)
                .p_content_cotton_hight_i(6.0)
                .p_content_cotton_hight_f(7.0)
                .p_content_cotton_too_hight(8.0)
                .p_content_peanut_too_low(1.0)
                .p_content_peanut_low_i(2.0)
                .p_content_peanut_low_f(3.0)
                .p_content_peanut_medium_i(4.0)
                .p_content_peanut_medium_f(5.0)
                .p_content_peanut_hight_i(6.0)
                .p_content_peanut_hight_f(7.0)
                .p_content_peanut_too_hight(8.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAvailablePAnionExchangeResinExtractorSuccessfully() throws Exception {
        AvailablePAnionExchangeResinExtractorCreateRequestDto requestDto =
                AvailablePAnionExchangeResinExtractorCreateRequestDto.builder()
                        .p_content_cotton_too_low(1.0)
                        .p_content_cotton_low_i(2.0)
                        .build();

        AvailablePAnionExchangeResinExtractorModel savedCriterion = existingCriterion.toBuilder()
                .id(120L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availablePAnionExchangeResinExtractorRepository.findByTable(ownerTable))
                .thenReturn(Optional.empty());
        when(availablePAnionExchangeResinExtractorRepository.save(any(AvailablePAnionExchangeResinExtractorModel.class)))
                .thenReturn(savedCriterion);

        mockMvc.perform(post("/available-p-anion-exchange-resin-extractor/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/available-p-anion-exchange-resin-extractor/get?criterionId=120"))
                .andExpect(jsonPath("$.id").value(120L))
                .andExpect(jsonPath("$.menor_teor_fosforo_solo_algodao").value(1.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAvailablePAnionExchangeResinExtractorByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availablePAnionExchangeResinExtractorRepository.findByTable(ownerTable))
                .thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/available-p-anion-exchange-resin-extractor/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.teor_inicial_baixo_fosforo_solo_algodao").value(2.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateAvailablePAnionExchangeResinExtractorSuccessfully() throws Exception {
        AvailablePAnionExchangeResinExtractorPostRequestDto requestDto =
                AvailablePAnionExchangeResinExtractorPostRequestDto.builder()
                        .p_content_cotton_too_low(1.5)
                        .build();

        AvailablePAnionExchangeResinExtractorModel updatedCriterion = existingCriterion.toBuilder()
                .p_content_cotton_too_low(1.5)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availablePAnionExchangeResinExtractorRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(availablePAnionExchangeResinExtractorRepository.save(existingCriterion)).thenReturn(updatedCriterion);

        mockMvc.perform(put("/available-p-anion-exchange-resin-extractor/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.menor_teor_fosforo_solo_algodao").value(1.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteAvailablePAnionExchangeResinExtractorSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availablePAnionExchangeResinExtractorRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(availablePAnionExchangeResinExtractorRepository).delete(existingCriterion);

        mockMvc.perform(delete("/available-p-anion-exchange-resin-extractor/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(availablePAnionExchangeResinExtractorRepository).delete(existingCriterion);
    }
}