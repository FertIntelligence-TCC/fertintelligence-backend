package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
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
public class CropFoliarAnalysisInterpretationTableControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private CropFoliarAnalysisInterpretationTableModel ownerTable;

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

        ownerTable = CropFoliarAnalysisInterpretationTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .build();
    }

    private CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto() {
        return CropFoliarAnalysisInterpretationTableCreateRequestDto.builder().build();
    }

    private CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto() {
        return CropFoliarAnalysisInterpretationTablePostRequestDto.builder().build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> {
                    CropFoliarAnalysisInterpretationTableModel table = invocation.getArgument(0);
                    table.setId(25L);
                    return table;
                });

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/crop-foliar-analysis-interpretation-table/get?tableId=25"))
                .andExpect(jsonPath("$.id").value(25L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser))
                .thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void getCropFoliarAnalysisInterpretationTableFails_WhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCropFoliarAnalysisInterpretationTablesSuccessfully() throws Exception {
        CropFoliarAnalysisInterpretationTableModel otherTable = ownerTable.toBuilder()
                .id(11L)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findAllByCreator(proprietarioUser))
                .thenReturn(List.of(ownerTable, otherTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerTable.getId()))
                .andExpect(jsonPath("$[1].id").value(otherTable.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableRepository
                .save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-foliar-analysis-interpretation-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        doNothing().when(cropFoliarAnalysisInterpretationTableLineRepository).deleteAllByTable(ownerTable);
        doNothing().when(cropFoliarAnalysisInterpretationTableRepository).delete(ownerTable);

        mockMvc.perform(delete("/crop-foliar-analysis-interpretation-table/delete")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isNoContent());
    }
}