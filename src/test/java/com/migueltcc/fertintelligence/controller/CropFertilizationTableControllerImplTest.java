package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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
public class CropFertilizationTableControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;

    private CropFertilizationTableModel ownerTable;
    private CropFertilizationTableModel complexTable; // Tabela com filhos

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

        // Tabela Simples
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
                .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                .used_spacing_value(0.50)
                .used_spacing_maximum_value(0.60)
                .regional_productivity(8000.0)
                .expected_productivity(9000.0)
                .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .manure(TipoEsterco.BOVINO)
                .manure_qtd(3.0)
                .observations("Observações iniciais")
                .sources("Fontes iniciais")
                .publicTable(false)
                .build();

        // Configuração da Tabela Complexa (3 Ranges, 3 Coverages)
        complexTable = ownerTable.toBuilder().id(50L).build();

        List<ContentRangeModel> ranges = new ArrayList<>();
        // Range 1
        ranges.add(ContentRangeModel.builder()
                .id(100L).table(complexTable).nutrient(Nutriente.FOSFORO)
                .order(1).smallest(null).largest(10.0).application(80.0).build());
        // Range 2
        ranges.add(ContentRangeModel.builder()
                .id(101L).table(complexTable).nutrient(Nutriente.FOSFORO)
                .order(2).smallest(10.0).largest(20.0).application(60.0).build());
        // Range 3
        ranges.add(ContentRangeModel.builder()
                .id(102L).table(complexTable).nutrient(Nutriente.FOSFORO)
                .order(3).smallest(20.0).largest(null).application(40.0).build());

        // Adicionando Coberturas (Assumindo que estão na lista da tabela ou dentro dos ranges)
        // Aqui simulo a estrutura onde a tabela tem acesso às listas para o DTO de resposta
        List<CoverageModel> coverages = new ArrayList<>();
        coverages.add(CoverageModel.builder().id(200L).order(1).application(30.0).build());
        coverages.add(CoverageModel.builder().id(201L).order(2).application(20.0).build());
        coverages.add(CoverageModel.builder().id(202L).order(3).application(10.0).build());

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
                .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                .used_spacing_value(0.50)
                .used_spacing_maximum_value(0.60)
                .regional_productivity(8000.0)
                .expected_productivity(9000.0)
                .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .manure(TipoEsterco.BOVINO)
                .manure_qtd(3.0)
                .observations("Observações iniciais")
                .sources("Fontes iniciais")
                .public_table(true)
                .build();
    }

    private CropFertilizationTablePostRequestDto updateRequestDto() {
        return CropFertilizationTablePostRequestDto.builder()
                .region(Regiao.NORDESTE)
                .crop_common_name(NomeComum.MILHO)
                .crop_scientific_nome(NomeCientifico.Zea_mays)
                .expected_productivity(9500.0)
                .observations("Observações atualizadas")
                .sources("Fontes atualizadas")
                .public_table(true)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableSuccessfully() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto();

        CropFertilizationTableModel savedTable = ownerTable.toBuilder()
                .id(20L)
                .observations("Observações iniciais")
                .sources("Fontes iniciais")
                .publicTable(true)
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
                .andExpect(jsonPath("$.observacoes").value("Observações iniciais"))
                .andExpect(jsonPath("$.fontes").value("Fontes iniciais"))
                .andExpect(jsonPath("$.sugestao_gessagem").doesNotExist())
                .andExpect(jsonPath("$.dose_minima_b").doesNotExist())
                .andExpect(jsonPath("$.dose_maxima_zn").doesNotExist())
                .andExpect(jsonPath("$.sugestao_micronutrientes").doesNotExist())
                .andExpect(jsonPath("$.sugestao_de_adubacao_com_micronutrientes").doesNotExist())
                .andExpect(jsonPath("$.sugestao_npk").doesNotExist())
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableWithChildrenSuccessfully() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class))).thenReturn(complexTable);

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableAcceptsPlantsPerLinearMeterRegionalSpacingLabel() throws Exception {
        String payload = objectMapper.writeValueAsString(createRequestDto().toBuilder()
                .used_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                .used_spacing_value(4.0)
                .used_spacing_maximum_value(6.0)
                .suggested_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                .build());

        CropFertilizationTableModel savedTable = ownerTable.toBuilder()
                .id(26L)
                .used_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                .used_spacing_value(4.0)
                .used_spacing_maximum_value(6.0)
                .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class))).thenReturn(savedTable);

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload.replace("Plantas por metro linear (m)", "Plantas por Metro")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.espacamentos_sugeridos").value("Entre Linhas (m)"))
                .andExpect(jsonPath("$.espacamento_usado").value("Plantas por metro linear (m)"))
                .andExpect(jsonPath("$.valor_espacamento_usado").value(4.0))
                .andExpect(jsonPath("$.valor_maximo_espacamento_usado").value(6.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableRejectsBetweenLinesAsRegionalSpacing() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("$.id").value(25L))
                .andExpect(jsonPath("$.nome_criador").value("Manager User"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableFails_WhenNamesDoNotMatch() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .crop_scientific_nome(NomeCientifico.Glycine_max) // Soja != Milho
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
        when(cropFertilizationTableRepository.findById(complexTable.getId())).thenReturn(Optional.of(complexTable));

        mockMvc.perform(get("/crop-fertilization-table/get")
                        .param("tableId", complexTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(complexTable.getId()))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regioes_cultura").value("SUL"))
                .andExpect(jsonPath("$.dose_minima_ni").doesNotExist())
                .andExpect(jsonPath("$.dose_maxima_mo").doesNotExist())
                .andExpect(jsonPath("$.valor_inicial").value(0.45))
                .andExpect(jsonPath("$.espacamentos_sugeridos").value("Entre Linhas (m)"))
                .andExpect(jsonPath("$.valor_maximo_espacamento_usado").value(0.60));
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
                .sources("Outra fonte")
                .region(Regiao.CENTRO_OESTE)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findAllByCreator(proprietarioUser))
                .thenReturn(List.of(ownerTable, otherTable));

        mockMvc.perform(get("/crop-fertilization-table/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ownerTable.getId()))
                .andExpect(jsonPath("$[1].regioes_cultura").value("CENTRO_OESTE"))
                .andExpect(jsonPath("$[0].tabela_publica").value(false));
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
                .andExpect(jsonPath("$.fontes").value("Fontes atualizadas"))
                .andExpect(jsonPath("$.dose_minima_b").doesNotExist())
                .andExpect(jsonPath("$.dose_maxima_zn").doesNotExist())
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFertilizationTableChangesRegionalSpacingToPlantsPerLinearMeter() throws Exception {
        CropFertilizationTablePostRequestDto requestDto = CropFertilizationTablePostRequestDto.builder()
                .used_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                .used_spacing_value(5.0)
                .used_spacing_maximum_value(7.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop-fertilization-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.espacamentos_sugeridos").value("Entre Linhas (m)"))
                .andExpect(jsonPath("$.espacamento_usado").value("Plantas por metro linear (m)"))
                .andExpect(jsonPath("$.valor_espacamento_usado").value(5.0))
                .andExpect(jsonPath("$.valor_maximo_espacamento_usado").value(7.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFertilizationTableDefaultsPublicFlagToFalse() throws Exception {
        CropFertilizationTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .public_table(null)
                .build();

        CropFertilizationTableModel savedTable = ownerTable.toBuilder()
                .id(21L)
                .publicTable(false)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.save(any(CropFertilizationTableModel.class))).thenReturn(savedTable);

        mockMvc.perform(post("/crop-fertilization-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tabela_publica").value(false));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllPublicCropFertilizationTablesReturnsOnlyPublicFromAllCreators() throws Exception {
        CropFertilizationTableModel publicOwner = ownerTable.toBuilder().id(31L).publicTable(true).build();
        CropFertilizationTableModel publicOther = ownerTable.toBuilder()
                .id(32L)
                .creator(otherProprietarioUser)
                .publicTable(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO))
                .thenReturn(List.of(publicOwner, publicOther));

        mockMvc.perform(get("/crop-fertilization-table/get-all-public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(31L))
                .andExpect(jsonPath("$[0].tabela_publica").value(true))
                .andExpect(jsonPath("$[1].id").value(32L))
                .andExpect(jsonPath("$[1].id_criador").value(2L))
                .andExpect(jsonPath("$[1].tabela_publica").value(true));
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
