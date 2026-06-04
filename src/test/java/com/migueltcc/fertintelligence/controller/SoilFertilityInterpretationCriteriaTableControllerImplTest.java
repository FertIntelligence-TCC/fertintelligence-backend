package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

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
@TestPropertySource(locations = "classpath:application-test.properties")
public class SoilFertilityInterpretationCriteriaTableControllerImplTest extends AbstractControllerTest {

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
                .name("Critérios Base")
                .description("Tabela base para testes")
                .region(Regiao.NORDESTE)
                .observations("Observações iniciais do solo")
                .sources("Fontes iniciais do solo")
                .publicTable(false)
                .build();
    }

    /**
     * IMPORTANTE:
     * O endpoint /register valida name obrigatório (vide log: "name: O nome é obrigatório").
     * Portanto este DTO precisa ter name + description preenchidos para retornar 201.
     */
    private SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto() {
        return SoilFertilityInterpretationCriteriaTableCreateRequestDto.builder()
                .name("Critérios de Fertilidade do Solo - SUL")
                .description("Critérios para interpretação de fertilidade do solo na região SUL.")
                .region(Regiao.SUL)
                .observations("Observações iniciais do solo")
                .sources("Fontes iniciais do solo")
                .public_table(true)
                .build();
    }

    private SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto() {
        return SoilFertilityInterpretationCriteriaTablePostRequestDto.builder()
                .name("Critérios Atualizados - Centro Oeste")
                .description("Atualização dos critérios para a região Centro-Oeste.")
                .region(Regiao.CENTRO_OESTE)
                .observations("Observações atualizadas do solo")
                .sources("Fontes atualizadas do solo")
                .public_table(true)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> {
                    SoilFertilityInterpretationCriteriaTableModel model = invocation.getArgument(0);
                    model.setId(20L);
                    model.setCreator(proprietarioUser);
                    return model;
                });

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                // Ajuste este Location caso o seu controller retorne outro padrão.
                .andExpect(header().string("Location",
                        "http://localhost/soil-fertility-interpretation-criteria-table/register/20"))
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.nome_criador").value("Test User Proprietario"))
                .andExpect(jsonPath("$.regiao").value("SUL"))
                .andExpect(jsonPath("$.observacoes").value("Observações iniciais do solo"))
                .andExpect(jsonPath("$.fontes").value("Fontes iniciais do solo"))
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "manager")
    void createSoilFertilityInterpretationCriteriaTableAsGerenteSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(gerenteUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> {
                    SoilFertilityInterpretationCriteriaTableModel model = invocation.getArgument(0);
                    model.setId(21L);
                    model.setCreator(gerenteUser);
                    return model;
                });

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(21L))
                .andExpect(jsonPath("$.nome_criador").value("Gerente"))
                .andExpect(jsonPath("$.regiao").value("SUL"));
    }

    @Test
    @WithMockUser(username = "residente")
    void createSoilFertilityInterpretationCriteriaTableAsResidenteSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("residente")).thenReturn(Optional.of(residenteUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> {
                    SoilFertilityInterpretationCriteriaTableModel model = invocation.getArgument(0);
                    model.setId(22L);
                    model.setCreator(residenteUser);
                    return model;
                });

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(22L))
                .andExpect(jsonPath("$.nome_criador").value("Agrônomo Residente"))
                .andExpect(jsonPath("$.regiao").value("SUL"));
    }

    @Test
    @WithMockUser(username = "secretario")
    void createSoilFertilityInterpretationCriteriaTableAsSecretarioSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("secretario")).thenReturn(Optional.of(secretarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> {
                    SoilFertilityInterpretationCriteriaTableModel model = invocation.getArgument(0);
                    model.setId(23L);
                    model.setCreator(secretarioUser);
                    return model;
                });

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(23L))
                .andExpect(jsonPath("$.nome_criador").value("Secretário"))
                .andExpect(jsonPath("$.regiao").value("SUL"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

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
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));

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
                .andExpect(jsonPath("$[0].regiao").value("NORDESTE"))
                .andExpect(jsonPath("$[1].id").value(11L))
                .andExpect(jsonPath("$[1].regiao").value("SUL"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        SoilFertilityInterpretationCriteriaTablePostRequestDto requestDto = updateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/soil-fertility-interpretation-criteria-table/update")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regiao").value("CENTRO_OESTE"))
                .andExpect(jsonPath("$.observacoes").value("Observações atualizadas do solo"))
                .andExpect(jsonPath("$.fontes").value("Fontes atualizadas do solo"))
                .andExpect(jsonPath("$.tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilFertilityInterpretationCriteriaTableDefaultsPublicFlagToFalse() throws Exception {
        SoilFertilityInterpretationCriteriaTableCreateRequestDto requestDto = createRequestDto().toBuilder()
                .public_table(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.save(any(SoilFertilityInterpretationCriteriaTableModel.class)))
                .thenAnswer(invocation -> {
                    SoilFertilityInterpretationCriteriaTableModel model = invocation.getArgument(0);
                    model.setId(26L);
                    model.setCreator(proprietarioUser);
                    model.setPublicTable(false);
                    return model;
                });

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tabela_publica").value(false));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllPublicSoilFertilityInterpretationCriteriaTablesReturnsOnlyPublicFromAllCreators() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel publicOwner = ownerTable.toBuilder().id(40L).publicTable(true).build();
        SoilFertilityInterpretationCriteriaTableModel publicOther = ownerTable.toBuilder()
                .id(41L)
                .creator(otherProprietarioUser)
                .publicTable(true)
                .build();

        when(soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrue())
                .thenReturn(List.of(publicOwner, publicOther));

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get-all-public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(40L))
                .andExpect(jsonPath("$[0].tabela_publica").value(true))
                .andExpect(jsonPath("$[1].id").value(41L))
                .andExpect(jsonPath("$[1].id_criador").value(2L))
                .andExpect(jsonPath("$[1].tabela_publica").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSoilFertilityInterpretationCriteriaTableSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilFertilityInterpretationCriteriaTableRepository.findById(ownerTable.getId()))
                .thenReturn(Optional.of(ownerTable));
        doNothing().when(soilFertilityInterpretationCriteriaTableRepository).delete(ownerTable);

        mockMvc.perform(delete("/soil-fertility-interpretation-criteria-table/delete")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilFertilityInterpretationCriteriaTableFails_WhenNameIsMissing() throws Exception {
        // garante o cenário do log: name obrigatório -> 400
        SoilFertilityInterpretationCriteriaTableCreateRequestDto invalid =
                SoilFertilityInterpretationCriteriaTableCreateRequestDto.builder()
                        .name(null)
                        .description(null)
                        .region(Regiao.SUL)
                        .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));

        mockMvc.perform(post("/soil-fertility-interpretation-criteria-table/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
}