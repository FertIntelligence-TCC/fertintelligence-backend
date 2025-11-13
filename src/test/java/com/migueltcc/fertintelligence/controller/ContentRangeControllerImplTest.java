package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ContentRangeControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;

    private CropFertilizationTableModel ownerTable;
    private ContentRangeModel fosforoRange;
    private ContentRangeModel fosforoFinalRange;
    private ContentRangeModel nitrogenRange;

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

        ownerTable = CropFertilizationTableModel.builder()
                .id(10L)
                .creator(proprietarioUser)
                .build();

        fosforoRange = ContentRangeModel.builder()
                .id(100L)
                .table(ownerTable)
                .nutrient(Nutriente.FOSFORO)
                .order(1)
                .smallest(null)
                .largest(10.0)
                .application(80.0)
                .build();

        fosforoFinalRange = ContentRangeModel.builder()
                .id(101L)
                .table(ownerTable)
                .nutrient(Nutriente.FOSFORO)
                .order(2)
                .smallest(10.0)
                .largest(null)
                .application(60.0)
                .build();

        nitrogenRange = ContentRangeModel.builder()
                .id(200L)
                .table(ownerTable)
                .nutrient(Nutriente.NITROGENIO)
                .order(1)
                .smallest(null)
                .largest(null)
                .application(50.0)
                .build();
    }

    private ContentRangeCreateRequestDto createFosforoFirstRequest() {
        return ContentRangeCreateRequestDto.builder()
                .nutrient(Nutriente.FOSFORO)
                .order(1)
                .smallest(null)
                .largest(10.0)
                .application(80.0)
                .build();
    }

    private ContentRangeCreateRequestDto createFosforoFinalRequest() {
        return ContentRangeCreateRequestDto.builder()
                .nutrient(Nutriente.FOSFORO)
                .order(2)
                .smallest(10.0)
                .largest(null)
                .application(60.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createContentRangeForFosforoSuccessfully() throws Exception {
        ContentRangeCreateRequestDto requestDto = createFosforoFirstRequest();

        ContentRangeModel savedRange = fosforoRange.toBuilder().id(120L).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of());
        when(contentRangeRepository.save(any(ContentRangeModel.class))).thenReturn(savedRange);

        mockMvc.perform(post("/content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/content-range/get?contentRangeId=120"))
                .andExpect(jsonPath("$.id").value(120L))
                .andExpect(jsonPath("$.nutriente").value("FOSFORO"))
                .andExpect(jsonPath("$.ordem_teor").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFinalContentRangeForFosforoSuccessfully() throws Exception {
        ContentRangeCreateRequestDto requestDto = createFosforoFinalRequest();

        ContentRangeModel savedRange = fosforoFinalRange.toBuilder().id(130L).build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(fosforoRange)).thenReturn(List.of());
        when(contentRangeRepository.save(any(ContentRangeModel.class))).thenReturn(savedRange);

        mockMvc.perform(post("/content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(130L))
                .andExpect(jsonPath("$.ordem_teor").value(2))
                .andExpect(jsonPath("$.maior_teor").doesNotExist());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createContentRangeCopiesCoverageStructureFromExistingRanges() throws Exception {
        ContentRangeCreateRequestDto requestDto = createFosforoFinalRequest();

        ContentRangeModel savedRange = fosforoFinalRange.toBuilder().id(130L).build();

        CoverageModel existingCoverage = CoverageModel.builder()
                .id(400L)
                .range(fosforoRange)
                .order(1)
                .application(70.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(fosforoRange)).thenReturn(List.of(existingCoverage));
        when(contentRangeRepository.save(any(ContentRangeModel.class))).thenReturn(savedRange);

        mockMvc.perform(post("/content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<Iterable<CoverageModel>> placeholdersCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(coverageRepository).saveAll(placeholdersCaptor.capture());

        List<CoverageModel> placeholders = StreamSupport.stream(placeholdersCaptor.getValue().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(1, placeholders.size());
        CoverageModel placeholder = placeholders.get(0);
        assertEquals(savedRange.getId(), placeholder.getRange().getId());
        assertEquals(existingCoverage.getOrder(), placeholder.getOrder());
        assertNull(placeholder.getApplication());
    }


    @Test
    @WithMockUser(username = "testuser")
    void createContentRangeFails_WhenFosforoAlreadyFinished() throws Exception {
        ContentRangeCreateRequestDto requestDto = createFosforoFinalRequest().toBuilder()
                .order(3)
                .smallest(20.0)
                .largest(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange, fosforoFinalRange));

        mockMvc.perform(post("/content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createContentRangeFails_WhenNitrogenAlreadyExists() throws Exception {
        ContentRangeCreateRequestDto requestDto = ContentRangeCreateRequestDto.builder()
                .nutrient(Nutriente.NITROGENIO)
                .order(1)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.NITROGENIO))
                .thenReturn(List.of(nitrogenRange));

        mockMvc.perform(post("/content-range/register")
                        .param("tableId", ownerTable.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getContentRangeSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(fosforoRange.getId())).thenReturn(Optional.of(fosforoRange));

        mockMvc.perform(get("/content-range/get")
                        .param("contentRangeId", fosforoRange.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fosforoRange.getId()))
                .andExpect(jsonPath("$.nutriente").value("FOSFORO"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void getContentRangeFails_WhenUserIsNotCreator() throws Exception {
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherProprietarioUser));
        when(contentRangeRepository.findById(fosforoRange.getId())).thenReturn(Optional.of(fosforoRange));

        mockMvc.perform(get("/content-range/get")
                        .param("contentRangeId", fosforoRange.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void listContentRangesSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropFertilizationTableRepository.findById(ownerTable.getId())).thenReturn(Optional.of(ownerTable));
        when(contentRangeRepository.findAllByTableOrderByNutrientAscOrderAsc(ownerTable))
                .thenReturn(List.of(nitrogenRange, fosforoRange, fosforoFinalRange));

        mockMvc.perform(get("/content-range/get-by-table")
                        .param("tableId", ownerTable.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nutriente").value("NITROGENIO"))
                .andExpect(jsonPath("$[2].ordem_teor").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateContentRangeSuccessfully() throws Exception {
        ContentRangePostRequestDto requestDto = ContentRangePostRequestDto.builder()
                .order(2)
                .smallest(10.0)
                .largest(null)
                .application(70.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(fosforoFinalRange.getId())).thenReturn(Optional.of(fosforoFinalRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange, fosforoFinalRange));
        when(contentRangeRepository.save(any(ContentRangeModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/content-range/update")
                        .param("contentRangeId", fosforoFinalRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacao_recomendada_plantio").value(70.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateContentRangeFails_WhenOrderInvalid() throws Exception {
        ContentRangePostRequestDto requestDto = ContentRangePostRequestDto.builder()
                .order(3)
                .smallest(20.0)
                .largest(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(fosforoRange.getId())).thenReturn(Optional.of(fosforoRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange, fosforoFinalRange));

        mockMvc.perform(put("/content-range/update")
                        .param("contentRangeId", fosforoRange.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteContentRangeSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(fosforoFinalRange.getId())).thenReturn(Optional.of(fosforoFinalRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange, fosforoFinalRange));
        when(coverageRepository.findAllByRangeOrderByOrderAsc(fosforoFinalRange)).thenReturn(List.of());
        doNothing().when(contentRangeRepository).delete(fosforoFinalRange);

        mockMvc.perform(delete("/content-range/delete")
                        .param("contentRangeId", fosforoFinalRange.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteContentRangeFails_WhenOnlyInterval() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(fosforoRange.getId())).thenReturn(Optional.of(fosforoRange));
        when(contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(ownerTable, Nutriente.FOSFORO))
                .thenReturn(List.of(fosforoRange));

        mockMvc.perform(delete("/content-range/delete")
                        .param("contentRangeId", fosforoRange.getId().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteContentRangeFails_WhenNutrientIsNitrogen() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(contentRangeRepository.findById(nitrogenRange.getId())).thenReturn(Optional.of(nitrogenRange));

        mockMvc.perform(delete("/content-range/delete")
                        .param("contentRangeId", nitrogenRange.getId().toString()))
                .andExpect(status().isBadRequest());
    }
}
