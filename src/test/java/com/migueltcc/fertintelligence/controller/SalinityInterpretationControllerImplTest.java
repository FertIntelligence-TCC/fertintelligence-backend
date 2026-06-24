package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SalinityInterpretationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
public class SalinityInterpretationControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel managerUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private SalinityInterpretationModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(6L)
                .username("testuser")
                .name("Test User")
                .build();

        managerUser = UserModel.builder()
                .id(7L)
                .username("manager")
                .name("Manager User")
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(15L)
                .creator(proprietarioUser)
                .build();

        existingCriterion = SalinityInterpretationModel.builder()
                .id(501L)
                .table(ownerTable)
                .normal_soil_highest_ce(2.0)
                .normal_soil_highest_pst(10.0)
                .normal_soil_highest_ph(7.0)
                .normal_soil_highest_ras(5.0)
                .saline_soil_lowest_ce(4.0)
                .saline_soil_highest_pst(15.0)
                .saline_soil_highest_ph(7.5)
                .saline_soil_highest_ras(10.0)
                .sodic_saline_soil_highest_ce(4.5)
                .sodic_saline_soil_lowest_pst(15.0)
                .sodic_saline_soil_lowest_ph(7.5)
                .sodic_saline_soil_lowest_ras(10.0)
                .sodic_soil_highest_ce(2.5)
                .sodic_soil_lowest_pst(15.0)
                .sodic_soil_lowest_ph(8.0)
                .sodic_soil_lowest_ras(10.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSalinityInterpretationSuccessfully() throws Exception {
        SalinityInterpretationCreateRequestDto requestDto = SalinityInterpretationCreateRequestDto.builder()
                .normal_soil_highest_ce(2.0)
                .normal_soil_highest_pst(10.0)
                .build();

        SalinityInterpretationModel savedCriterion = existingCriterion.toBuilder()
                .id(521L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(salinityInterpretationRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(salinityInterpretationRepository.save(any(SalinityInterpretationModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/salinity-interpretation/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/salinity-interpretation/get?criterionId=521"))
                .andExpect(jsonPath("$.id").value(521L))
                .andExpect(jsonPath("$.unidade_ras").value("(mmolc)**0.5"));
    }

    @Test
    @WithMockUser(username = "manager")
    void createSalinityInterpretationAsManagerCreator() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel managerTable = ownerTable.toBuilder()
                .creator(managerUser)
                .id(25L)
                .build();

        SalinityInterpretationCreateRequestDto requestDto = SalinityInterpretationCreateRequestDto.builder()
                .normal_soil_highest_ce(1.5)
                .build();

        SalinityInterpretationModel savedCriterion = existingCriterion.toBuilder()
                .id(530L)
                .table(managerTable)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(managerTable.getId()))
                .thenReturn(Optional.of(managerTable));
        when(salinityInterpretationRepository.findByTable(managerTable)).thenReturn(Optional.empty());
        when(salinityInterpretationRepository.save(any(SalinityInterpretationModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/salinity-interpretation/register")
                        .param("tableId", managerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(530L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSalinityInterpretationByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(salinityInterpretationRepository.findByTable(ownerTable)).thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/salinity-interpretation/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.unidade_ras").value("(mmolc)**0.5"))
                .andExpect(jsonPath("$.normal_soil_highest_ce").value(2.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSalinityInterpretationSuccessfully() throws Exception {
        SalinityInterpretationPostRequestDto requestDto = SalinityInterpretationPostRequestDto.builder()
                .normal_soil_highest_ce(3.0)
                .build();

        SalinityInterpretationModel updatedCriterion = existingCriterion.toBuilder()
                .normal_soil_highest_ce(3.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(salinityInterpretationRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(salinityInterpretationRepository.save(existingCriterion)).thenReturn(updatedCriterion);

        mockMvc.perform(put("/salinity-interpretation/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normal_soil_highest_ce").value(3.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSalinityInterpretationSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(salinityInterpretationRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(salinityInterpretationRepository).delete(existingCriterion);

        mockMvc.perform(delete("/salinity-interpretation/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(salinityInterpretationRepository).delete(existingCriterion);
    }
}
