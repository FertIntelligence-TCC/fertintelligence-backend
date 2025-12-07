package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeCientifico;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.SpacingType;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.TipoEsterco;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
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
public class CropFertilizationTableControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;

    private CropFertilizationTableModel ownerTable;

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

        managerUser = UserModel.builder()
                .id(3L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        ownerTable = CropFertilizationTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .region(Regiao.SUL)
                .crop_common_name(NomeComum.MILHO)
                .crop_scientific_nome(NomeCientifico.Zea_mays)
                .cultivares("Cultivar 1")
                .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                .initial_value(0.45)
                .final_value(0.55)
                .used_spacing(0.50)
                .regional_productivity(8000.0)
                .expected_productivity(9000.0)
                .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .manure(TipoEsterco.BOVINO)
                .manure_qtd(3.0)
                .gessing(1.0)
                .micronutrients(150.0)
                .npk(120.0)
                .observations("Observações iniciais")
                .build();
    }

    private CropFertilizationTableCreateRequestDto createRequestDto() {
        return CropFertilizationTableCreateRequestDto.builder()
                .region(Regiao.SUL)
                .crop_common_name(NomeComum.MILHO)
                .crop_scientific_nome(NomeCientifico.Zea_mays)
                .cultivares("Cultivar 1")
                .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                .initial_value(0.45)
                .final_value(0.55)
                .used_spacing(0.50)
                .regional_productivity(8000.0)
                .expected_productivity(9000.0)
                .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .manure(TipoEsterco.BOVINO)
                .manure_qtd(3.0)
                .gessing(1.0)
                .micronutrients(150.0)
                .npk(120.0)
                .observations("Observações iniciais")
                .build();
    }

    private CropFertilizationTablePostRequestDto updateRequestDto() {
        return CropFertilizationTablePostRequestDto.builder()
                .region(Regiao.NORDESTE)
                .crop_common_name(NomeComum.MILHO)
                .crop_scientific_nome(NomeCientifico.Zea_mays)
                .expected_productivity(9500.0)
                .observations("Observações atualizadas")
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableSuccessfully() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto();

        CropFertilizationTableModel savedTable = ownerTable.toBuilder()
                .id(20L)
                .observations("Observações iniciais")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class))).thenReturn(savedTable);

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/crop-fertilization-table/get?tableId=20"))
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.nome_comum_cultura").value("MILHO"))
                .andExpect(jsonPath("$.nome_cientifico_cultura").value("Zea_mays"))
                .andExpect(jsonPath("$.regioes_cultura").value("SUL"))
                .andExpect(jsonPath("$.observacoes").value("Observações iniciais"));
    }

    @Test
    @WithMockUser(username = "manager")
    void createCropFertilizationTableSuccessfullyForManagerRole() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto();

        CropFertilizationTableModel managerTable = ownerTable.toBuilder()
                .creator(managerUser)
                .id(25L)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class))).thenReturn(managerTable);

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/crop-fertilization-table/get?tableId=25"))
                .andExpect(jsonPath("$.id").value(25L))
                .andExpect(jsonPath("$.nome_criador").value("Manager User"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableFails_WhenNamesDoNotMatch() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .crop_scientific_nome(NomeCientifico.Glycine_max)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFertilizationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-fertilization-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regioes_cultura").value("SUL"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void getCropFertilizationTableFails_WhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-fertilization-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFertilizationTablesSuccessfully() throws Exception {
        CropFertilizationTableModel otherTable = ownerTable.toBuilder()
                .id(11L)
                .observations("Outra tabela")
                .region(Regiao.CENTRO_OESTE)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findAllByCreator(proprietarioUser)).thenReturn(List.of(ownerTable, otherTable));

        mockMvc.perform(get("/crop-fertilization-table/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerTable.getId()))
                .andExpect(jsonPath("$[1].observacoes").value("Outra tabela"))
                .andExpect(jsonPath("$[0].regioes_cultura").value("SUL"))
                .andExpect(jsonPath("$[1].regioes_cultura").value("CENTRO_OESTE"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFertilizationTableSuccessfully() throws Exception {
        CropFertilizationTablePostRequestDto requestDto = updateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-fertilization-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtividade_esperada").value(9500.0))
                .andExpect(jsonPath("$.observacoes").value("Observações atualizadas"))
                .andExpect(jsonPath("$.regioes_cultura").value("NORDESTE"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCropFertilizationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        doNothing().when(cropFertilizationTableRepository).delete(ownerTable);

        mockMvc.perform(delete("/crop-fertilization-table/delete")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isNoContent());
    }
}