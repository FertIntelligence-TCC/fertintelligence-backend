package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoveragePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class CoverageControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel managerUser;

    private CropFertilizationTableModel ownerTable;
    private CropFertilizationTableModel managerTable;
    private ContentRangeModel ownerRange;
    private ContentRangeModel managerRange;
    private CoverageModel coverageOne;
    private CoverageModel coverageTwo;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username("employee")
                .name("Employee User")
                .cargo(Cargo.SECRETARIO)
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
                .build();

        managerTable = CropFertilizationTableModel.builder()
                .id(11L)
                .creator(managerUser)
                .build();

        ownerRange = ContentRangeModel.builder()
                .id(100L)
                .table(ownerTable)
                .nutrient(Nutriente.FOSFORO)
                .order(1)
                .smallest(null)
                .largest(10.0)
                .application(80.0)
                .build();

        managerRange = ownerRange.toBuilder()
                .id(101L)
                .table(managerTable)
                .build();

        coverageOne = CoverageModel.builder()
                .id(200L)
                .range(ownerRange)
                .order(1)
                .application(30.0)
                .build();

        coverageTwo = CoverageModel.builder()
                .id(201L)
                .range(ownerRange)
                .order(2)
                .application(20.0)
                .build();
    }

    private CoverageCreateRequestDto createCoverageRequest(int order, double application) {
        return CoverageCreateRequestDto.builder()
                .order(order)
                .application(application)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCoverageSuccessfully() throws Exception {
        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 30.0);

        CoverageModel savedCoverage = coverageOne.toBuilder().id(300L).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of());
        when(coverageRepository.save(any(CoverageModel.class))).thenReturn(savedCoverage);

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", ownerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/coverage/get?coverageId=300"))
                .andExpect(jsonPath("$.ordem_cobertura").value(1))
                .andExpect(jsonPath("$.aplicacao_recomendada_cobertura").value(30.0));
    }

    @Test
    @WithMockUser(username = "manager")
    void createCoverageSuccessfullyForManagerOwnedRange() throws Exception {
        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 25.0);

        CoverageModel savedCoverage = coverageOne.toBuilder()
                .id(305L)
                .range(managerRange)
                .application(25.0)
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(contentRangeRepository.findById(managerRange.getId())).thenReturn(Optional.of(managerRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(managerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(managerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(managerRange)).thenReturn(List.of());
        when(coverageRepository.save(any(CoverageModel.class))).thenReturn(savedCoverage);

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", managerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/coverage/get?coverageId=305"));
        // CORREÇÃO 1: Removida verificação de 'nome_criador_tabela' pois não existe no response DTO
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCoverageCreatesPlaceholderForSiblingRange() throws Exception {
        ContentRangeModel siblingRange = ownerRange.toBuilder()
                .id(101L)
                .order(2)
                .build();

        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 30.0);

        CoverageModel savedCoverage = coverageOne.toBuilder().id(300L).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange, siblingRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of());
        when(coverageRepository.findAllByRangeOrderByOrderAsc(siblingRange)).thenReturn(List.of());
        when(coverageRepository.save(any(CoverageModel.class))).thenReturn(savedCoverage);

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", ownerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<Iterable<CoverageModel>> placeholdersCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(coverageRepository).saveAll(placeholdersCaptor.capture());

        List<CoverageModel> placeholders = StreamSupport.stream(placeholdersCaptor.getValue().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(1, placeholders.size());
        CoverageModel placeholder = placeholders.get(0);
        assertEquals(siblingRange.getId(), placeholder.getRange().getId());
        assertEquals(1, placeholder.getOrder());
        assertNull(placeholder.getApplication());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCoverageUpdatesExistingPlaceholderForSameOrder() throws Exception {
        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 35.0);

        CoverageModel placeholder = coverageOne.toBuilder()
                .application(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(placeholder));
        when(coverageRepository.save(any(CoverageModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", ownerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(coverageOne.getId()))
                .andExpect(jsonPath("$.ordem_cobertura").value(1))
                .andExpect(jsonPath("$.aplicacao_recomendada_cobertura").value(35.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCoverageFails_WhenCoverageForSameOrderAlreadyHasApplication() throws Exception {
        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 35.0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne));

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", ownerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCoverageFails_WhenOrderNotSequential() throws Exception {
        CoverageCreateRequestDto requestDto = createCoverageRequest(2, 25.0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of());

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", ownerRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "employee")
    void createCoverageFailsForPublicTableWhenUserIsNotOwner() throws Exception {
        CropFertilizationTableModel publicTable = ownerTable.toBuilder()
                .publicTable(true)
                .build();

        ContentRangeModel publicRange = ownerRange.toBuilder()
                .table(publicTable)
                .build();

        CoverageCreateRequestDto requestDto = createCoverageRequest(1, 30.0);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(funcionarioUser));
        when(contentRangeRepository.findById(publicRange.getId())).thenReturn(Optional.of(publicRange));

        mockMvc.perform(post("/coverage/register")
                        .param("contentRangeId", publicRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCoverageSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageOne.getId())).thenReturn(Optional.of(coverageOne));

        mockMvc.perform(get("/coverage/get")
                        .param("coverageId", coverageOne.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coverageOne.getId()))
                .andExpect(jsonPath("$.ordem_cobertura").value(1));
    }

    @Test
    @WithMockUser(username = "employee")
    void getCoverageFails_WhenUserNotProprietario() throws Exception {
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(funcionarioUser));
        // CORREÇÃO 2: Mockar o retorno do repositório para evitar 404 e permitir que a lógica chegue na verificação de permissão (403)
        when(coverageRepository.findById(coverageOne.getId())).thenReturn(Optional.of(coverageOne));

        mockMvc.perform(get("/coverage/get")
                        .param("coverageId", coverageOne.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listCoveragesSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne, coverageTwo));

        mockMvc.perform(get("/coverage/get-by-range")
                        .param("contentRangeId", ownerRange.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].ordem_cobertura").value(2));
    }


    @Test
    @WithMockUser(username = "employee")
    void listCoveragesSuccessfullyForPublicTableWhenNotOwner() throws Exception {
        CropFertilizationTableModel publicTable = ownerTable.toBuilder()
                .publicTable(true)
                .build();

        ContentRangeModel publicRange = ownerRange.toBuilder()
                .table(publicTable)
                .build();

        CoverageModel publicCoverage = coverageOne.toBuilder()
                .range(publicRange)
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(funcionarioUser));
        when(contentRangeRepository.findById(publicRange.getId())).thenReturn(Optional.of(publicRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(publicRange)).thenReturn(List.of(publicCoverage));

        mockMvc.perform(get("/coverage/get-by-range")
                        .param("contentRangeId", publicRange.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordem_cobertura").value(1));
    }

    @Test
    @WithMockUser(username = "employee")
    void listCoveragesFailsForPrivateTableWhenNotOwner() throws Exception {
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(funcionarioUser));
        when(contentRangeRepository.findById(ownerRange.getId())).thenReturn(Optional.of(ownerRange));

        mockMvc.perform(get("/coverage/get-by-range")
                        .param("contentRangeId", ownerRange.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCoverageSuccessfully() throws Exception {
        CoveragePostRequestDto requestDto = CoveragePostRequestDto.builder()
                .order(2)
                .application(22.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageTwo.getId())).thenReturn(Optional.of(coverageTwo));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne, coverageTwo));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));
        when(coverageRepository.save(any(CoverageModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/coverage/update")
                        .param("coverageId", coverageTwo.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacao_recomendada_cobertura").value(22.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCoverageFails_WhenOrderInvalid() throws Exception {
        CoveragePostRequestDto requestDto = CoveragePostRequestDto.builder()
                .order(3)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageOne.getId())).thenReturn(Optional.of(coverageOne));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne, coverageTwo));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));

        mockMvc.perform(put("/coverage/update")
                        .param("coverageId", coverageOne.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCoverageSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageTwo.getId())).thenReturn(Optional.of(coverageTwo));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne, coverageTwo));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange));
        doNothing().when(coverageRepository).delete(coverageTwo);

        mockMvc.perform(delete("/coverage/delete")
                        .param("coverageId", coverageTwo.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCoverageRemovesSiblingCoverages() throws Exception {
        ContentRangeModel siblingRange = ownerRange.toBuilder()
                .id(101L)
                .order(2)
                .build();

        CoverageModel siblingCoverage = coverageOne.toBuilder()
                .id(210L)
                .range(siblingRange)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageOne.getId())).thenReturn(Optional.of(coverageOne));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(ownerRange, siblingRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(siblingRange)).thenReturn(List.of(siblingCoverage));
        doNothing().when(coverageRepository).delete(coverageOne);
        doNothing().when(coverageRepository).deleteAll(any());

        mockMvc.perform(delete("/coverage/delete")
                        .param("coverageId", coverageOne.getId().toString()))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Iterable<CoverageModel>> deletedCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(coverageRepository).deleteAll(deletedCaptor.capture());

        List<CoverageModel> deletedCoverages = StreamSupport.stream(deletedCaptor.getValue().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(1, deletedCoverages.size());
        assertEquals(siblingCoverage.getId(), deletedCoverages.get(0).getId());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCoverageFails_WhenNotLast() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(coverageRepository.findById(coverageOne.getId())).thenReturn(Optional.of(coverageOne));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(ownerRange)).thenReturn(List.of(coverageOne, coverageTwo));

        mockMvc.perform(delete("/coverage/delete")
                .param("coverageId", coverageOne.getId().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "employee")
    void deleteCoverageFailsForPublicTableWhenUserIsNotOwner() throws Exception {
        CropFertilizationTableModel publicTable = ownerTable.toBuilder()
                .publicTable(true)
                .build();

        ContentRangeModel publicRange = ownerRange.toBuilder()
                .table(publicTable)
                .build();

        CoverageModel publicCoverage = coverageOne.toBuilder()
                .range(publicRange)
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(funcionarioUser));
        when(coverageRepository.findById(publicCoverage.getId())).thenReturn(Optional.of(publicCoverage));

        mockMvc.perform(delete("/coverage/delete")
                        .param("coverageId", publicCoverage.getId().toString()))
                .andExpect(status().isForbidden());
    }
}
