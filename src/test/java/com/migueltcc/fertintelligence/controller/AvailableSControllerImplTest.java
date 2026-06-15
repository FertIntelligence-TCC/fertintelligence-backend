package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
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
public class AvailableSControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel gerenteUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private AvailableSModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(3L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        gerenteUser = UserModel.builder()
                .id(4L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(12L)
                .creator(proprietarioUser)
                .build();

        existingCriterion = AvailableSModel.builder()
                .id(201L)
                .table(ownerTable)
                .sContentLess400TooLow(1.0)
                .sContentLess400LowI(2.0)
                .sContentLess400LowF(3.0)
                .sContentLess400MediumI(4.0)
                .sContentLess400MediumF(5.0)
                .sContentLess400HighI(6.0)
                .sContentLess400HighF(7.0)
                .sContentLess400TooHigh(8.0)
                .sContentGreater400TooLow(1.0)
                .sContentGreater400LowI(2.0)
                .sContentGreater400LowF(3.0)
                .sContentGreater400MediumI(4.0)
                .sContentGreater400MediumF(5.0)
                .sContentGreater400HighI(6.0)
                .sContentGreater400HighF(7.0)
                .sContentGreater400TooHigh(8.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAvailableSSuccessfully() throws Exception {
        AvailableSCreateRequestDto requestDto = AvailableSCreateRequestDto.builder()
                .sContentLess400TooLow(1.0)
                .sContentLess400LowI(2.0)
                .build();

        AvailableSModel savedCriterion = existingCriterion.toBuilder()
                .id(221L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availableSRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(availableSRepository.save(any(AvailableSModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/available-s/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/available-s/get?criterionId=221"))
                .andExpect(jsonPath("$.id").value(221L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAvailableSByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(availableSRepository.findByTable(ownerTable)).thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/available-s/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.menor_teor_enxofre_argila_menor_400").value(1.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateAvailableSSuccessfully() throws Exception {
        AvailableSPostRequestDto requestDto = AvailableSPostRequestDto.builder()
                .sContentLess400TooLow(1.5)
                .build();

        AvailableSModel updatedCriterion = existingCriterion.toBuilder()
                .sContentLess400TooLow(1.5)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availableSRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(availableSRepository.save(existingCriterion)).thenReturn(updatedCriterion);

        mockMvc.perform(put("/available-s/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menor_teor_enxofre_argila_menor_400").value(1.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteAvailableSSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(availableSRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(availableSRepository).delete(existingCriterion);

        mockMvc.perform(delete("/available-s/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(availableSRepository).delete(existingCriterion);
    }

    @Test
    @WithMockUser(username = "manager")
    void createAvailableSAsManagerSuccessfully() throws Exception {
        AvailableSCreateRequestDto requestDto = AvailableSCreateRequestDto.builder()
                .sContentGreater400TooLow(1.0)
                .build();

        SoilFertilityInterpretationCriteriaTableModel managerTable = ownerTable.toBuilder()
                .creator(gerenteUser)
                .build();

        AvailableSModel savedCriterion = existingCriterion.toBuilder()
                .id(121L)
                .table(managerTable)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(gerenteUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(managerTable));
        when(availableSRepository.findByTable(managerTable)).thenReturn(Optional.empty());
        when(availableSRepository.save(any(AvailableSModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/available-s/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(121L));
    }
}
