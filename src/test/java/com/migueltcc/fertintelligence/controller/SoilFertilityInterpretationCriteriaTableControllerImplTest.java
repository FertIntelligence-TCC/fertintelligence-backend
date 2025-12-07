package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SoilFertilityInterpretationCriteriaTableControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel gerenteUser;
    private UserModel residenteUser;
    private UserModel secretarioUser;

    private SoilFertilityInterpretationCriteriaTableModel ownerTable;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(2L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        gerenteUser = UserModel.builder()
                .id(3L)
                .username("manager")
                .name("Gerente")
                .cargo(Cargo.GERENTE)
                .build();

        residenteUser = UserModel.builder()
                .id(4L)
                .username("residente")
                .name("Agrônomo Residente")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        secretarioUser = UserModel.builder()
                .id(5L)
                .username("secretario")
                .name("Secretário")
                .cargo(Cargo.SECRETARIO)
                .build();

        ownerTable = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .region(Regiao.NORDESTE)
                .build();
    }

    private SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto() {
        return SoilFertilityInterpretationCriteriaTableCreateRequestDto.builder()
                .region(Regiao.SUL)
                .build();
    }

    private SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto() {
        return SoilFertilityInterpretationCriteriaTablePostRequestDto.builder()
                .region(Regiao.CENTRO_OESTE)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        SoilFertilityInterpretationCriteriaTableModel savedTable = ownerTable.toBuilder()
                .id(20L)
                .region(Regiao.SUL)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenReturn(savedTable);

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/soil-fertility-interpretation-criteria-table/get?tableId=20"))
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regiao").value("SUL"));
    }

    @Test
    @WithMockUser(username = "manager")
    void createSoilFertilityInterpretationCriteriaTableAsGerenteSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        SoilFertilityInterpretationCriteriaTableModel savedTable = ownerTable.toBuilder()
                .id(21L)
                .creator(gerenteUser)
                .region(Regiao.SUL)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(gerenteUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenReturn(savedTable);

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(21L))
                .andExpect(jsonPath("$.nome_criador").value("Gerente"));
    }

    @Test
    @WithMockUser(username = "residente")
    void createSoilFertilityInterpretationCriteriaTableAsResidenteSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        SoilFertilityInterpretationCriteriaTableModel savedTable = ownerTable.toBuilder()
                .id(22L)
                .creator(residenteUser)
                .region(Regiao.SUL)
                .build();

        when(userRepository.findByUsername("residente")).thenReturn(Optional.of(residenteUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenReturn(savedTable);

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(22L))
                .andExpect(jsonPath("$.nome_criador").value("Agrônomo Residente"));
    }

    @Test
    @WithMockUser(username = "secretario")
    void createSoilFertilityInterpretationCriteriaTableAsSecretarioSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        SoilFertilityInterpretationCriteriaTableModel savedTable = ownerTable.toBuilder()
                .id(23L)
                .creator(secretarioUser)
                .region(Regiao.SUL)
                .build();

        when(userRepository.findByUsername("secretario")).thenReturn(Optional.of(secretarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenReturn(savedTable);

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(23L))
                .andExpect(jsonPath("$.nome_criador").value("Secretário"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regiao").value("NORDESTE"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void getSoilFertilityInterpretationCriteriaTableFails_WhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listSoilFertilityInterpretationCriteriaTablesSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel otherTable = ownerTable.toBuilder()
                .id(11L)
                .region(Regiao.SUL)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(proprietarioUser))
                .thenReturn(List.of(ownerTable, otherTable));

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerTable.getId()))
                .andExpect(jsonPath("$[1].regiao").value("SUL"))
                .andExpect(jsonPath("$[0].regiao").value("NORDESTE"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTablePostRequestDto requestDto = updateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/soil-fertility-interpretation-criteria-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regiao").value("CENTRO_OESTE"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        doNothing().when(soilFertilityInterpretationCriteriaTableRepository).delete(ownerTable);

        mockMvc.perform(delete("/soil-fertility-interpretation-criteria-table/delete")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isNoContent());
    }
}