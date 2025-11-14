package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
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
public class DiverseContentRangeControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private DiverseContentRangeModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(4L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(13L)
                .creator(proprietarioUser)
                .build();

        existingCriterion = DiverseContentRangeModel.builder()
                .id(301L)
                .table(ownerTable)
                .organic_carbon_too_low(1.0)
                .organic_carbon_low_i(2.0)
                .organic_carbon_low_f(3.0)
                .organic_carbon_medium_i(4.0)
                .organic_carbon_medium_f(5.0)
                .organic_carbon_hight_i(6.0)
                .organic_carbon_hight_f(7.0)
                .organic_carbon_too_hight(8.0)
                .organic_matter_too_low(1.0)
                .organic_matter_low_i(2.0)
                .organic_matter_low_f(3.0)
                .organic_matter_medium_i(4.0)
                .organic_matter_medium_f(5.0)
                .organic_matter_hight_i(6.0)
                .organic_matter_hight_f(7.0)
                .organic_matter_too_hight(8.0)
                .calcium_too_low(1.0)
                .calcium_low_i(2.0)
                .calcium_low_f(3.0)
                .calcium_medium_i(4.0)
                .calcium_medium_f(5.0)
                .calcium_hight_i(6.0)
                .calcium_hight_f(7.0)
                .calcium_too_hight(8.0)
                .magnesium_too_low(1.0)
                .magnesium_low_i(2.0)
                .magnesium_low_f(3.0)
                .magnesium_medium_i(4.0)
                .magnesium_medium_f(5.0)
                .magnesium_hight_i(6.0)
                .magnesium_hight_f(7.0)
                .magnesium_too_hight(8.0)
                .potassium_too_low(1.0)
                .potassium_low_i(2.0)
                .potassium_low_f(3.0)
                .potassium_medium_i(4.0)
                .potassium_medium_f(5.0)
                .potassium_hight_i(6.0)
                .potassium_hight_f(7.0)
                .potassium_too_hight(8.0)
                .sodium_too_low(1.0)
                .sodium_low_i(2.0)
                .sodium_low_f(3.0)
                .sodium_medium_i(4.0)
                .sodium_medium_f(5.0)
                .sodium_hight_i(6.0)
                .sodium_hight_f(7.0)
                .sodium_too_hight(8.0)
                .sum_of_bases_too_low(1.0)
                .sum_of_bases_low_i(2.0)
                .sum_of_bases_low_f(3.0)
                .sum_of_bases_medium_i(4.0)
                .sum_of_bases_medium_f(5.0)
                .sum_of_bases_hight_i(6.0)
                .sum_of_bases_hight_f(7.0)
                .sum_of_bases_too_hight(8.0)
                .aluminum_too_low(1.0)
                .aluminum_low_i(2.0)
                .aluminum_low_f(3.0)
                .aluminum_medium_i(4.0)
                .aluminum_medium_f(5.0)
                .aluminum_hight_i(6.0)
                .aluminum_hight_f(7.0)
                .aluminum_too_hight(8.0)
                .potential_acidity_too_low(1.0)
                .potential_acidity_low_i(2.0)
                .potential_acidity_low_f(3.0)
                .potential_acidity_medium_i(4.0)
                .potential_acidity_medium_f(5.0)
                .potential_acidity_hight_i(6.0)
                .potential_acidity_hight_f(7.0)
                .potential_acidity_too_hight(8.0)
                .base_saturation_too_low(1.0)
                .base_saturation_low_i(2.0)
                .base_saturation_low_f(3.0)
                .base_saturation_medium_i(4.0)
                .base_saturation_medium_f(5.0)
                .base_saturation_hight_i(6.0)
                .base_saturation_hight_f(7.0)
                .base_saturation_too_hight(8.0)
                .aluminum_saturation_too_low(1.0)
                .aluminum_saturation_low_i(2.0)
                .aluminum_saturation_low_f(3.0)
                .aluminum_saturation_medium_i(4.0)
                .aluminum_saturation_medium_f(5.0)
                .aluminum_saturation_hight_i(6.0)
                .aluminum_saturation_hight_f(7.0)
                .aluminum_saturation_too_hight(8.0)
                .ph_too_low(1.0)
                .ph_low_i(2.0)
                .ph_low_f(3.0)
                .ph_medium_i(4.0)
                .ph_medium_f(5.0)
                .ph_hight_i(6.0)
                .ph_hight_f(7.0)
                .ph_too_hight(8.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createDiverseContentRangeSuccessfully() throws Exception {
        DiverseContentRangeCreateRequestDto requestDto = DiverseContentRangeCreateRequestDto.builder()
                .organic_carbon_too_low(1.0)
                .organic_carbon_low_i(2.0)
                .build();

        DiverseContentRangeModel savedCriterion = existingCriterion.toBuilder()
                .id(321L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(diverseContentRangeRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(diverseContentRangeRepository.save(any(DiverseContentRangeModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/diverse-content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/diverse-content-range/get?criterionId=321"))
                .andExpect(jsonPath("$.id").value(321L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getDiverseContentRangeByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(diverseContentRangeRepository.findByTable(ownerTable)).thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/diverse-content-range/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.menor_teor_carbono_organico").value(1.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateDiverseContentRangeSuccessfully() throws Exception {
        DiverseContentRangePostRequestDto requestDto = DiverseContentRangePostRequestDto.builder()
                .organic_carbon_too_low(1.5)
                .build();

        DiverseContentRangeModel updatedCriterion = existingCriterion.toBuilder()
                .organic_carbon_too_low(1.5)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(diverseContentRangeRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(diverseContentRangeRepository.save(existingCriterion)).thenReturn(updatedCriterion);

        mockMvc.perform(put("/diverse-content-range/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menor_teor_carbono_organico").value(1.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteDiverseContentRangeSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(diverseContentRangeRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(diverseContentRangeRepository).delete(existingCriterion);

        mockMvc.perform(delete("/diverse-content-range/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(diverseContentRangeRepository).delete(existingCriterion);
    }
}