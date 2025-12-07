package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KExchangeableContentModel;
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
public class KExchangeableContentControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel managerUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private SoilFertilityInterpretationCriteriaTableModel managerTable;
    private KExchangeableContentModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(5L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(6L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(14L)
                .creator(proprietarioUser)
                .build();

        managerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(15L)
                .creator(managerUser)
                .build();

        existingCriterion = KExchangeableContentModel.builder()
                .id(401L)
                .table(ownerTable)
                .k_content_cec_less_20_too_low(1.0)
                .k_content_cec_less_20_low_i(2.0)
                .k_content_cec_less_20_low_f(3.0)
                .k_content_cec_less_20_medium_i(4.0)
                .k_content_cec_less_20_medium_f(5.0)
                .k_content_cec_less_20_hight_i(6.0)
                .k_content_cec_less_20_hight_f(7.0)
                .k_content_cec_less_20_too_hight(8.0)
                .k_content_cec_20_40_too_low(1.0)
                .k_content_cec_20_40_low_i(2.0)
                .k_content_cec_20_40_low_f(3.0)
                .k_content_cec_20_40_medium_i(4.0)
                .k_content_cec_20_40_medium_f(5.0)
                .k_content_cec_20_40_hight_i(6.0)
                .k_content_cec_20_40_hight_f(7.0)
                .k_content_cec_20_40_too_hight(8.0)
                .k_content_cec_41_80_too_low(1.0)
                .k_content_cec_41_80_low_i(2.0)
                .k_content_cec_41_80_low_f(3.0)
                .k_content_cec_41_80_medium_i(4.0)
                .k_content_cec_41_80_medium_f(5.0)
                .k_content_cec_41_80_hight_i(6.0)
                .k_content_cec_41_80_hight_f(7.0)
                .k_content_cec_41_80_too_hight(8.0)
                .k_content_cec_81_120_too_low(1.0)
                .k_content_cec_81_120_low_i(2.0)
                .k_content_cec_81_120_low_f(3.0)
                .k_content_cec_81_120_medium_i(4.0)
                .k_content_cec_81_120_medium_f(5.0)
                .k_content_cec_81_120_hight_i(6.0)
                .k_content_cec_81_120_hight_f(7.0)
                .k_content_cec_81_120_too_hight(8.0)
                .k_content_cec_greater_120_too_low(1.0)
                .k_content_cec_greater_120_low_i(2.0)
                .k_content_cec_greater_120_low_f(3.0)
                .k_content_cec_greater_120_medium_i(4.0)
                .k_content_cec_greater_120_medium_f(5.0)
                .k_content_cec_greater_120_hight_i(6.0)
                .k_content_cec_greater_120_hight_f(7.0)
                .k_content_cec_greater_120_too_hight(8.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createKExchangeableContentSuccessfully() throws Exception {
        KExchangeableContentCreateRequestDto requestDto = KExchangeableContentCreateRequestDto.builder()
                .k_content_cec_less_20_too_low(1.0)
                .k_content_cec_less_20_low_i(2.0)
                .build();

        KExchangeableContentModel savedCriterion = existingCriterion.toBuilder()
                .id(421L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(kExchangeableContentRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(kExchangeableContentRepository.save(any(KExchangeableContentModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/k-exchangeable-content/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/k-exchangeable-content/get?criterionId=421"))
                .andExpect(jsonPath("$.id").value(421L));
    }

    @Test
    @WithMockUser(username = "manager")
    void createKExchangeableContentForManagerOwnedTable() throws Exception {
        KExchangeableContentCreateRequestDto requestDto = KExchangeableContentCreateRequestDto.builder()
                .k_content_cec_less_20_too_low(1.5)
                .k_content_cec_less_20_low_i(2.5)
                .build();

        KExchangeableContentModel savedCriterion = existingCriterion.toBuilder()
                .id(422L)
                .table(managerTable)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(managerTable.getId()))
                .thenReturn(Optional.of(managerTable));
        when(kExchangeableContentRepository.findByTable(managerTable)).thenReturn(Optional.empty());
        when(kExchangeableContentRepository.save(any(KExchangeableContentModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/k-exchangeable-content/register")
                        .param("tableId", managerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(422L))
                // campo e valor conforme o JSON de resposta
                .andExpect(jsonPath("$.menor_teor_k_ctc_menor_20").value(1.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getKExchangeableContentByTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(kExchangeableContentRepository.findByTable(ownerTable)).thenReturn(Optional.of(existingCriterion));

        mockMvc.perform(get("/k-exchangeable-content/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCriterion.getId()))
                .andExpect(jsonPath("$.menor_teor_k_ctc_menor_20").value(1.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateKExchangeableContentSuccessfully() throws Exception {
        KExchangeableContentPostRequestDto requestDto = KExchangeableContentPostRequestDto.builder()
                .k_content_cec_less_20_too_low(1.5)
                .build();

        KExchangeableContentModel updatedCriterion = existingCriterion.toBuilder()
                .k_content_cec_less_20_too_low(1.5)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(kExchangeableContentRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(kExchangeableContentRepository.save(existingCriterion)).thenReturn(updatedCriterion);

        mockMvc.perform(put("/k-exchangeable-content/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menor_teor_k_ctc_menor_20").value(1.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteKExchangeableContentSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(kExchangeableContentRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        doNothing().when(kExchangeableContentRepository).delete(existingCriterion);

        mockMvc.perform(delete("/k-exchangeable-content/delete")
                        .param("criterionId", existingCriterion.getId().toString()))
                .andExpect(status().isNoContent());

        verify(kExchangeableContentRepository).delete(existingCriterion);
    }
}