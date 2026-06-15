package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class CropFoliarAnalysisInterpretationTableLineControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;
    private CropFoliarAnalysisInterpretationTableModel ownerTable;
    private CropFoliarAnalysisInterpretationTableLineModel ownerLine;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(5L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(2L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerTable = CropFoliarAnalysisInterpretationTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .publicTable(false)
                .build();

        ownerLine = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .id(100L)
                .table(ownerTable)
                .crop(NomeComum.SOJA)
                .n_content(createTeor(2.0, 4.0))
                .build();
    }

    private MenorMaiorTeores createTeor(double menor, double maior) {
        return MenorMaiorTeores.builder()
                .menor(menor)
                .maior(maior)
                .unity(UnidadeTeor.g_per_kg)
                .build();
    }

    private CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto() {
        return CropFoliarAnalysisInterpretationTableLineCreateRequestDto.builder()
                .crop(NomeComum.SOJA)
                .n_content(createTeor(2.0, 4.0))
                .build();
    }

    private CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto() {
        return CropFoliarAnalysisInterpretationTableLinePostRequestDto.builder()
                .crop(NomeComum.MILHO)
                .k_content(createTeor(1.5, 3.0))
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFoliarAnalysisInterpretationTableLineSuccessfully() throws Exception {
        CropFoliarAnalysisInterpretationTableLineModel savedLine = ownerLine.toBuilder()
                .id(200L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findByTableAndCrop(ownerTable, NomeComum.SOJA))
                .thenReturn(Optional.empty());
        when(cropFoliarAnalysisInterpretationTableLineRepository.save(any(CropFoliarAnalysisInterpretationTableLineModel.class)))
                .thenReturn(savedLine);

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table-line/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/crop-foliar-analysis-interpretation-table-line/register/200"))
                .andExpect(jsonPath("$.id").value(200L))
                .andExpect(jsonPath("$.nome_cultura").value("SOJA"));
    }

    @Test
    @WithMockUser(username = "manager")
    void createCropFoliarAnalysisInterpretationTableLineAsManagerSuccessfully() throws Exception {
        CropFoliarAnalysisInterpretationTableModel managerTable = ownerTable.toBuilder()
                .id(300L)
                .creator(managerUser)
                .build();

        CropFoliarAnalysisInterpretationTableLineModel managerLine = ownerLine.toBuilder()
                .id(301L)
                .table(managerTable)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(managerTable.getId()))
                .thenReturn(Optional.of(managerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findByTableAndCrop(managerTable, NomeComum.SOJA))
                .thenReturn(Optional.empty());
        when(cropFoliarAnalysisInterpretationTableLineRepository.save(any(CropFoliarAnalysisInterpretationTableLineModel.class)))
                .thenReturn(managerLine);

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table-line/register")
                        .param("tableId", managerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(301L))
                .andExpect(jsonPath("$.nome_cultura").value("SOJA")); // <-- trocado aqui
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFoliarAnalysisInterpretationTableLineFails_WhenCropAlreadyExists() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findByTableAndCrop(ownerTable, NomeComum.SOJA))
                .thenReturn(Optional.of(ownerLine));

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table-line/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFoliarAnalysisInterpretationTableLineSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findById(ownerLine.getId()))
                .thenReturn(Optional.of(ownerLine));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get")
                        .param("lineId", ownerLine.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerLine.getId()))
                .andExpect(jsonPath("$.nome_cultura").value("SOJA"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void getCropFoliarAnalysisInterpretationTableLineFails_WhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findById(ownerLine.getId()))
                .thenReturn(Optional.of(ownerLine));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get")
                        .param("lineId", ownerLine.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFoliarAnalysisInterpretationTableLinesByTableSuccessfully() throws Exception {
        CropFoliarAnalysisInterpretationTableLineModel otherLine = ownerLine.toBuilder()
                .id(101L)
                .crop(NomeComum.MILHO)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findAllByTableOrderByIdAsc(ownerTable))
                .thenReturn(List.of(ownerLine, otherLine));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerLine.getId()))
                .andExpect(jsonPath("$[1].nome_cultura").value("MILHO"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void listCropFoliarAnalysisInterpretationTableLinesByPublicTableSuccessfully() throws Exception {
        CropFoliarAnalysisInterpretationTableModel publicTable = ownerTable.toBuilder()
                .id(11L)
                .publicTable(true)
                .build();
        CropFoliarAnalysisInterpretationTableLineModel publicLine = ownerLine.toBuilder()
                .id(201L)
                .table(publicTable)
                .build();

        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(publicTable.getId()))
                .thenReturn(Optional.of(publicTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findAllByTableOrderByIdAsc(publicTable))
                .thenReturn(List.of(publicLine));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", publicTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(publicLine.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFoliarAnalysisInterpretationTableLinesByTableReturnsEmptyList() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findAllByTableOrderByIdAsc(ownerTable))
                .thenReturn(List.of());

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFoliarAnalysisInterpretationTableLinesByTableReturnsNotFoundWhenTableDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "otheruser")
    void listCropFoliarAnalysisInterpretationTableLinesByPrivateTableFailsWhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFoliarAnalysisInterpretationTableLinesByTableWithNullableFieldsDoesNotFail() throws Exception {
        CropFoliarAnalysisInterpretationTableLineModel nullFieldsLine = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .id(301L)
                .table(ownerTable)
                .crop(NomeComum.SOJA)
                .n_content(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findAllByTableOrderByIdAsc(ownerTable))
                .thenReturn(List.of(nullFieldsLine));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table-line/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teores_n").doesNotExist());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFoliarAnalysisInterpretationTableLineSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findById(ownerLine.getId()))
                .thenReturn(Optional.of(ownerLine));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findByTableAndCrop(ownerTable, NomeComum.MILHO))
                .thenReturn(Optional.empty());
        when(cropFoliarAnalysisInterpretationTableLineRepository.save(any(CropFoliarAnalysisInterpretationTableLineModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-foliar-analysis-interpretation-table-line/update")
                        .param("lineId", ownerLine.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_cultura").value("MILHO"))
                .andExpect(jsonPath("$.teores_k.maior").value(3.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCropFoliarAnalysisInterpretationTableLineSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findById(ownerLine.getId()))
                .thenReturn(Optional.of(ownerLine));
        when(cropFoliarAnalysisInterpretationTableLineRepository.countByTable(ownerTable)).thenReturn(2L);
        doNothing().when(cropFoliarAnalysisInterpretationTableLineRepository).delete(ownerLine);

        mockMvc.perform(delete("/crop-foliar-analysis-interpretation-table-line/delete")
                        .param("lineId", ownerLine.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCropFoliarAnalysisInterpretationTableLineDeletesEvenWhenItIsTheLastLine() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findById(ownerLine.getId()))
                .thenReturn(Optional.of(ownerLine));
        when(cropFoliarAnalysisInterpretationTableLineRepository.countByTable(ownerTable)).thenReturn(1L);

        mockMvc.perform(delete("/crop-foliar-analysis-interpretation-table-line/delete")
                        .param("lineId", ownerLine.getId().toString()))
                .andExpect(status().isNoContent());
    }
}
