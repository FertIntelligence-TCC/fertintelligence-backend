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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
public class DiverseContentRangeControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel managerUser;
    private SoilFertilityInterpretationCriteriaTableModel ownerTable;
    private SoilFertilityInterpretationCriteriaTableModel managerTable;
    private DiverseContentRangeModel existingCriterion;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(4L)
                .username("testuser")
                .name("Test User")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(5L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(13L)
                .creator(proprietarioUser)
                .build();

        managerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(14L)
                .creator(managerUser)
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
                .effective_cec_too_low(1.0)
                .effective_cec_low_i(2.0)
                .effective_cec_low_f(3.0)
                .effective_cec_medium_i(4.0)
                .effective_cec_medium_f(5.0)
                .effective_cec_hight_i(6.0)
                .effective_cec_hight_f(7.0)
                .effective_cec_too_hight(8.0)
                .ph7_cec_too_low(1.0)
                .ph7_cec_low_i(2.0)
                .ph7_cec_low_f(3.0)
                .ph7_cec_medium_i(4.0)
                .ph7_cec_medium_f(5.0)
                .ph7_cec_hight_i(6.0)
                .ph7_cec_hight_f(7.0)
                .ph7_cec_too_hight(8.0)
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
                .ph_cacl2_too_low(1.0)
                .ph_cacl2_low_i(2.0)
                .ph_cacl2_low_f(3.0)
                .ph_cacl2_medium_i(4.0)
                .ph_cacl2_medium_f(5.0)
                .ph_cacl2_hight_i(6.0)
                .ph_cacl2_hight_f(7.0)
                .ph_cacl2_too_hight(8.0)
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
                .andExpect(jsonPath("$.id").value(321L))
                .andExpect(jsonPath("$.unidade_carbono_organico").value("g/dm³"))
                .andExpect(jsonPath("$.unidade_materia_organica").value("g/dm³"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createDiverseContentRangeAcceptsRequestedNutrientAliases() throws Exception {
        String requestJson = """
                {
                  "menor_teor_al_trocavel": 0.1,
                  "teor_inicial_baixo_aluminio_mais_hidrogenio": 1.1,
                  "teor_final_baixo_ctc_t": 2.2,
                  "teor_inicial_medio_ctc_ph7": 3.3,
                  "menor_teor_ph_agua": 4.4,
                  "valor_inicial_baixo_ph_cacl2_0_01_mol_l": 5.5
                }
                """;

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(diverseContentRangeRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(diverseContentRangeRepository.save(any(DiverseContentRangeModel.class))).thenAnswer(invocation -> {
            DiverseContentRangeModel criterion = invocation.getArgument(0);
            criterion.setId(323L);
            assertEquals(0.1, criterion.getAluminum_too_low());
            assertEquals(1.1, criterion.getPotential_acidity_low_i());
            assertEquals(2.2, criterion.getEffective_cec_low_f());
            assertEquals(3.3, criterion.getPh7_cec_medium_i());
            assertEquals(4.4, criterion.getPh_too_low());
            assertEquals(5.5, criterion.getPh_cacl2_low_i());
            return criterion;
        });

        mockMvc.perform(post("/diverse-content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.menor_teor_aluminio").value(0.1))
                .andExpect(jsonPath("$.teor_inicial_baixo_acidez_potencial").value(1.1))
                .andExpect(jsonPath("$.teor_final_baixo_ctc_efetiva").value(2.2))
                .andExpect(jsonPath("$.teor_inicial_medio_ctc_ph_7").value(3.3))
                .andExpect(jsonPath("$.menor_valor_ph").value(4.4))
                .andExpect(jsonPath("$.menor_teor_ph_agua").value(4.4))
                .andExpect(jsonPath("$.valor_inicial_baixo_ph_cacl2").value(5.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createDiverseContentRangeAcceptsMicronutrientsWithoutExtremeFields() throws Exception {
        DiverseContentRangeCreateRequestDto requestDto = DiverseContentRangeCreateRequestDto.builder()
                .boron_low_i(0.1)
                .boron_low_f(0.2)
                .boron_medium_i(0.3)
                .boron_medium_f(0.4)
                .boron_hight_i(0.5)
                .boron_hight_f(0.6)
                .copper_low_i(1.1)
                .copper_low_f(1.2)
                .copper_medium_i(1.3)
                .copper_medium_f(1.4)
                .copper_hight_i(1.5)
                .copper_hight_f(1.6)
                .iron_low_i(2.1)
                .iron_low_f(2.2)
                .iron_medium_i(2.3)
                .iron_medium_f(2.4)
                .iron_hight_i(2.5)
                .iron_hight_f(2.6)
                .manganese_low_i(3.1)
                .manganese_low_f(3.2)
                .manganese_medium_i(3.3)
                .manganese_medium_f(3.4)
                .manganese_hight_i(3.5)
                .manganese_hight_f(3.6)
                .zinc_low_i(4.1)
                .zinc_low_f(4.2)
                .zinc_medium_i(4.3)
                .zinc_medium_f(4.4)
                .zinc_hight_i(4.5)
                .zinc_hight_f(4.6)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(diverseContentRangeRepository.findByTable(ownerTable)).thenReturn(Optional.empty());
        when(diverseContentRangeRepository.save(any(DiverseContentRangeModel.class))).thenAnswer(invocation -> {
            DiverseContentRangeModel criterion = invocation.getArgument(0);
            criterion.setId(324L);
            assertNull(criterion.getBoron_too_low());
            assertNull(criterion.getBoron_too_hight());
            assertNull(criterion.getCopper_too_low());
            assertNull(criterion.getCopper_too_hight());
            assertNull(criterion.getIron_too_low());
            assertNull(criterion.getIron_too_hight());
            assertNull(criterion.getManganese_too_low());
            assertNull(criterion.getManganese_too_hight());
            assertNull(criterion.getZinc_too_low());
            assertNull(criterion.getZinc_too_hight());
            return criterion;
        });

        mockMvc.perform(post("/diverse-content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(324L))
                .andExpect(jsonPath("$.teor_inicial_baixo_boro").value(0.1))
                .andExpect(jsonPath("$.teor_final_alto_zinco").value(4.6));
    }

    @Test
    @WithMockUser(username = "manager")
    void createDiverseContentRangeForManagerOwnedTable() throws Exception {
        DiverseContentRangeCreateRequestDto requestDto = DiverseContentRangeCreateRequestDto.builder()
                .organic_carbon_too_low(1.2)
                .organic_carbon_low_i(2.2)
                .build();

        DiverseContentRangeModel savedCriterion = existingCriterion.toBuilder()
                .id(322L)
                .table(managerTable)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(managerTable.getId()))
                .thenReturn(Optional.of(managerTable));
        when(diverseContentRangeRepository.findByTable(managerTable)).thenReturn(Optional.empty());
        when(diverseContentRangeRepository.save(any(DiverseContentRangeModel.class))).thenReturn(savedCriterion);

        mockMvc.perform(post("/diverse-content-range/register")
                        .param("tableId", managerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(322L))
                // campo e valor de acordo com o JSON real
                .andExpect(jsonPath("$.menor_teor_carbono_organico").value(1.0));
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
                .andExpect(jsonPath("$.unidade_carbono_organico").value("g/dm³"))
                .andExpect(jsonPath("$.unidade_materia_organica").value("g/dm³"))
                .andExpect(jsonPath("$.menor_teor_carbono_organico").value(1.0))
                .andExpect(jsonPath("$.menor_teor_potassio").value(1.0))
                .andExpect(jsonPath("$.menor_teor_sodio").value(1.0))
                .andExpect(jsonPath("$.menor_teor_soma_bases").value(1.0))
                .andExpect(jsonPath("$.menor_teor_aluminio").value(1.0))
                .andExpect(jsonPath("$.menor_teor_aluminio_mais_hidrogenio").value(1.0))
                .andExpect(jsonPath("$.menor_teor_ctc_ph7").value(1.0))
                .andExpect(jsonPath("$.menor_teor_pst").value(1.0))
                .andExpect(jsonPath("$.menor_valor_ph_agua").value(1.0))
                .andExpect(jsonPath("$.menor_teor_ph_agua").value(1.0));
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
                .andExpect(jsonPath("$.unidade_carbono_organico").value("g/dm³"))
                .andExpect(jsonPath("$.unidade_materia_organica").value("g/dm³"))
                .andExpect(jsonPath("$.menor_teor_carbono_organico").value(1.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateDiverseContentRangeAcceptsRequestedNutrientAliases() throws Exception {
        String requestJson = """
                {
                  "menor_teor_aluminio_mais_hidrogenio": 1.2,
                  "teor_inicial_baixo_ctc_ph7": 2.3,
                  "novo_menor_teor_ph_agua": 4.5
                }
                """;

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(diverseContentRangeRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(diverseContentRangeRepository.save(existingCriterion)).thenAnswer(invocation -> {
            DiverseContentRangeModel criterion = invocation.getArgument(0);
            assertEquals(1.2, criterion.getPotential_acidity_too_low());
            assertEquals(2.3, criterion.getPh7_cec_low_i());
            assertEquals(4.5, criterion.getPh_too_low());
            return criterion;
        });

        mockMvc.perform(put("/diverse-content-range/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menor_teor_aluminio_mais_hidrogenio").value(1.2))
                .andExpect(jsonPath("$.teor_inicial_baixo_ctc_ph7").value(2.3))
                .andExpect(jsonPath("$.menor_valor_ph_agua").value(4.5))
                .andExpect(jsonPath("$.menor_teor_ph_agua").value(4.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateDiverseContentRangeAcceptsMicronutrientsWithoutExtremeFields() throws Exception {
        DiverseContentRangePostRequestDto requestDto = DiverseContentRangePostRequestDto.builder()
                .boron_low_i(0.7)
                .boron_low_f(0.8)
                .boron_medium_i(0.9)
                .boron_medium_f(1.0)
                .boron_hight_i(1.1)
                .boron_hight_f(1.2)
                .copper_low_i(1.7)
                .copper_low_f(1.8)
                .copper_medium_i(1.9)
                .copper_medium_f(2.0)
                .copper_hight_i(2.1)
                .copper_hight_f(2.2)
                .iron_low_i(2.7)
                .iron_low_f(2.8)
                .iron_medium_i(2.9)
                .iron_medium_f(3.0)
                .iron_hight_i(3.1)
                .iron_hight_f(3.2)
                .manganese_low_i(3.7)
                .manganese_low_f(3.8)
                .manganese_medium_i(3.9)
                .manganese_medium_f(4.0)
                .manganese_hight_i(4.1)
                .manganese_hight_f(4.2)
                .zinc_low_i(4.7)
                .zinc_low_f(4.8)
                .zinc_medium_i(4.9)
                .zinc_medium_f(5.0)
                .zinc_hight_i(5.1)
                .zinc_hight_f(5.2)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(diverseContentRangeRepository.findById(existingCriterion.getId()))
                .thenReturn(Optional.of(existingCriterion));
        when(diverseContentRangeRepository.save(existingCriterion)).thenAnswer(invocation -> {
            DiverseContentRangeModel criterion = invocation.getArgument(0);
            assertNull(criterion.getBoron_too_low());
            assertNull(criterion.getBoron_too_hight());
            assertNull(criterion.getCopper_too_low());
            assertNull(criterion.getCopper_too_hight());
            assertNull(criterion.getIron_too_low());
            assertNull(criterion.getIron_too_hight());
            assertNull(criterion.getManganese_too_low());
            assertNull(criterion.getManganese_too_hight());
            assertNull(criterion.getZinc_too_low());
            assertNull(criterion.getZinc_too_hight());
            return criterion;
        });

        mockMvc.perform(put("/diverse-content-range/update")
                        .param("criterionId", existingCriterion.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teor_inicial_baixo_boro").value(0.7))
                .andExpect(jsonPath("$.teor_final_alto_zinco").value(5.2));
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
