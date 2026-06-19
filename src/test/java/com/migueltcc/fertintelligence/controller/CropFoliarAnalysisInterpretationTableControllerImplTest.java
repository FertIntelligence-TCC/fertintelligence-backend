package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

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

@SpringBootTest
@AutoConfigureMockMvc
public class CropFoliarAnalysisInterpretationTableControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;

    private CropFoliarAnalysisInterpretationTableModel ownerTable;

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
                .region(Regiao.SUL)
                .observations("Observações foliares iniciais")
                .sources("Fontes foliares iniciais")
                .publicTable(false)
                .build();
    }

    private CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto() {
        return CropFoliarAnalysisInterpretationTableCreateRequestDto.builder()
                .publicTable(true)
                .region(Regiao.SUL)
                .observations("Observações foliares iniciais")
                .sources("Fontes foliares iniciais")
                .build();
    }

    private CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto() {
        return CropFoliarAnalysisInterpretationTablePostRequestDto.builder()
                .publicTable(true)
                .region(Regiao.NORDESTE)
                .observations("Observações foliares atualizadas")
                .sources("Fontes foliares atualizadas")
                .build();
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
                // CORREÇÃO: o controller retorna Location apontando para /register/{id}
                .andExpect(header().string("Location",
                        "http://localhost/crop-foliar-analysis-interpretation-table/register/25"))
                .andExpect(jsonPath("$.id").value(25L))
                .andExpect(jsonPath("$.regiao_analise_foliar_culturas").value("SUL"))
                .andExpect(jsonPath("$.observacoes").value("Observações foliares iniciais"))
                .andExpect(jsonPath("$.fontes").value("Fontes foliares iniciais"))
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "manager")
    void createCropFoliarAnalysisInterpretationTableAsManagerSuccessfully() throws Exception {
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(cropFoliarAnalysisInterpretationTableRepository.save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> {
                    CropFoliarAnalysisInterpretationTableModel table = invocation.getArgument(0);
                    table.setId(30L);
                    return table;
                });

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto())))
                .andExpect(status().isCreated())
                // CORREÇÃO: o controller retorna Location apontando para /register/{id}
                .andExpect(header().string("Location",
                        "http://localhost/crop-foliar-analysis-interpretation-table/register/30"))
                .andExpect(jsonPath("$.id").value(30L))
                .andExpect(jsonPath("$.nome_criador").value("Manager User"))
                .andExpect(jsonPath("$.regiao_analise_foliar_culturas").value("SUL"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regiao_analise_foliar_culturas").value("SUL"));
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
                .region(Regiao.CENTRO_OESTE)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findAllByCreator(proprietarioUser))
                .thenReturn(List.of(ownerTable, otherTable));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerTable.getId()))
                .andExpect(jsonPath("$[1].id").value(otherTable.getId()))
                .andExpect(jsonPath("$[0].regiao_analise_foliar_culturas").value("SUL"))
                .andExpect(jsonPath("$[1].regiao_analise_foliar_culturas").value("CENTRO_OESTE"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFoliarAnalysisInterpretationTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(cropFoliarAnalysisInterpretationTableRepository.save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-foliar-analysis-interpretation-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerTable.getId()))
                .andExpect(jsonPath("$.regiao_analise_foliar_culturas").value("NORDESTE"))
                .andExpect(jsonPath("$.observacoes").value("Observações foliares atualizadas"))
                .andExpect(jsonPath("$.fontes").value("Fontes foliares atualizadas"))
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFoliarAnalysisInterpretationTableDefaultsPublicFlagToFalse() throws Exception {
        CropFoliarAnalysisInterpretationTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .publicTable(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> {
                    CropFoliarAnalysisInterpretationTableModel table = invocation.getArgument(0);
                    table.setId(26L);
                    table.setPublicTable(false);
                    return table;
                });

        mockMvc.perform(post("/crop-foliar-analysis-interpretation-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tabela_publica").value(false));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFoliarAnalysisInterpretationTableAcceptsRegionAliasAndPublicToggle() throws Exception {
        String requestBody = """
                {
                  "novo_nome_tabela":"TIAF Milho NE v2",
                  "novo_regiao_analise_foliar_culturas":"NORDESTE",
                  "tabela_publica": false
                }
                """;

        CropFoliarAnalysisInterpretationTableModel publicTable = ownerTable.toBuilder()
                .publicTable(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(publicTable));
        when(cropFoliarAnalysisInterpretationTableRepository.save(any(CropFoliarAnalysisInterpretationTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-foliar-analysis-interpretation-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regiao_analise_foliar_culturas").value("NORDESTE"))
                .andExpect(jsonPath("$.tabela_publica").value(false));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllPublicCropFoliarAnalysisInterpretationTablesReturnsOnlyPublicFromAllCreators() throws Exception {
        CropFoliarAnalysisInterpretationTableModel publicOwner = ownerTable.toBuilder().id(55L).publicTable(true).build();
        CropFoliarAnalysisInterpretationTableModel publicOther = ownerTable.toBuilder()
                .id(56L)
                .creator(otherProprietarioUser)
                .publicTable(true)
                .build();

        when(cropFoliarAnalysisInterpretationTableRepository.findAllByPublicTableTrue())
                .thenReturn(List.of(publicOwner, publicOther));

        mockMvc.perform(get("/crop-foliar-analysis-interpretation-table/get-all-public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(55L))
                .andExpect(jsonPath("$[0].tabela_publica").value(true))
                .andExpect(jsonPath("$[1].id").value(56L))
                .andExpect(jsonPath("$[1].id_criador").value(2L))
                .andExpect(jsonPath("$[1].tabela_publica").value(true));
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