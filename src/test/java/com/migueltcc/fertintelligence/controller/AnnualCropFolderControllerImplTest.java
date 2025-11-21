package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AnnualCropFolderControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;
    private PropertyModel ownerProperty;
    private PlotModel ownerPlot;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username("testuser")
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(3L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerProperty = createProperty(10L, "Fazenda Santa Clara", proprietarioUser);
        ownerPlot = createPlotModel(100L, "Talhao 01", ownerProperty);
    }

    private PropertyModel createProperty(Long id, String nome, UserModel owner) {
        return PropertyModel.builder()
                .id(id)
                .nome(nome)
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(owner)
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
                .build();
    }

    private PlotModel createPlotModel(Long id, String identification, PropertyModel property) {
        return PlotModel.builder()
                .id(id)
                .identification(identification)
                .property(property)
                .area(15.0)
                .soilClass(ClasseSolo.ARGISSOLO)
                .soilTexture(TexturaSolo.FRANCO_ARGILOSO_ARENOSA)
                .cropIncorporationYear(2020)
                .irrigatedArea(AreaIrrigada.SIM)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .build();
    }

    private AnnualCropFolderCreateRequestDto createCreateRequestDto() {
        return AnnualCropFolderCreateRequestDto.builder()
                .cropsYear(2023)
                .build();
    }

    private AnnualCropFolderPostRequestDto createPostRequestDto() {
        return AnnualCropFolderPostRequestDto.builder()
                .cropsYear(2024)
                .build();
    }

    private AnnualCropFolderModel createAnnualCropFolderModel(Long id, Integer cropsYear, PlotModel plot) {
        return AnnualCropFolderModel.builder()
                .id(id)
                .cropsYear(cropsYear)
                .plot(plot)
                .build();
    }

    // --- TESTES DE CRIAÇÃO (CREATE) ---
    @Test
    @WithMockUser(username = "testuser")
    void createAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        AnnualCropFolderModel savedAnnualCropFolder = createAnnualCropFolderModel(1L, requestDto.getCropsYear(), ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, requestDto.getCropsYear()))
                .thenReturn(Optional.empty());
        when(annualCropFolderRepository.save(any(AnnualCropFolderModel.class))).thenReturn(savedAnnualCropFolder);

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_culturas").value(2023))
                .andExpect(jsonPath("$.id_talhao").value(ownerPlot.getId()))
                .andExpect(jsonPath("$.identificacao_talhao").value(ownerPlot.getIdentification()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAnnualCropFolderFails_WhenUserIsNotProprietario() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();

        // 1. Configura o usuário logado (sem permissão)
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        // 2. [CORREÇÃO] Mocka o retorno do Talhão para evitar o erro 404
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAnnualCropFolderFails_WhenPlotDoesNotBelongToUser() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(otherPlot.getId())).thenReturn(Optional.of(otherPlot));

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", otherPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAnnualCropFolderFails_WhenPlotNotFound() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createAnnualCropFolderFails_WhenCropsYearAlreadyExists() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        AnnualCropFolderModel existingAnnualCropFolder = createAnnualCropFolderModel(2L, requestDto.getCropsYear(), ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, requestDto.getCropsYear()))
                .thenReturn(Optional.of(existingAnnualCropFolder));

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // --- TESTES DE LEITURA (READ) ---
    @Test
    @WithMockUser(username = "testuser")
    void getAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel annualCropFolder = createAnnualCropFolderModel(1L, 2023, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(annualCropFolder));

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_culturas").value(2023))
                .andExpect(jsonPath("$.id_talhao").value(ownerPlot.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        AnnualCropFolderModel annualCropFolder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(annualCropFolder));

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAnnualCropFolderFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAnnualCropFoldersByPlotSuccessfully() throws Exception {
        AnnualCropFolderModel folder2022 = createAnnualCropFolderModel(1L, 2022, ownerPlot);
        AnnualCropFolderModel folder2023 = createAnnualCropFolderModel(2L, 2023, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(annualCropFolderRepository.findAllByPlot(ownerPlot))
                .thenReturn(List.of(folder2022, folder2023));

        mockMvc.perform(get("/annual-crop-folder/get-by-plot")
                        .param("plotId", ownerPlot.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ano_culturas").value(2022))
                .andExpect(jsonPath("$[1].ano_culturas").value(2023));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAnnualCropFoldersByPlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(otherPlot.getId())).thenReturn(Optional.of(otherPlot));

        mockMvc.perform(get("/annual-crop-folder/get-by-plot")
                        .param("plotId", otherPlot.getId().toString()))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE ATUALIZAÇÃO (UPDATE) ---
    @Test
    @WithMockUser(username = "testuser")
    void updateAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel existingAnnualCropFolder = createAnnualCropFolderModel(1L, 2023, ownerPlot);
        AnnualCropFolderPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(existingAnnualCropFolder));
        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, updateRequestDto.getCropsYear()))
                .thenReturn(Optional.empty());
        when(annualCropFolderRepository.save(existingAnnualCropFolder)).thenReturn(existingAnnualCropFolder);

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano_culturas").value(2024));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateAnnualCropFolderFails_WhenCropsYearAlreadyExists() throws Exception {
        AnnualCropFolderModel existingAnnualCropFolder = createAnnualCropFolderModel(1L, 2023, ownerPlot);
        AnnualCropFolderPostRequestDto updateRequestDto = createPostRequestDto();
        AnnualCropFolderModel conflictingAnnualCropFolder = createAnnualCropFolderModel(2L, 2024, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(existingAnnualCropFolder));
        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, updateRequestDto.getCropsYear()))
                .thenReturn(Optional.of(conflictingAnnualCropFolder));

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        AnnualCropFolderModel annualCropFolder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(annualCropFolder));

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequestDto())))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE EXCLUSÃO (DELETE) ---
    @Test
    @WithMockUser(username = "testuser")
    void deleteAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel annualCropFolder = createAnnualCropFolderModel(1L, 2023, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(annualCropFolder));
        doNothing().when(annualCropFolderRepository).delete(annualCropFolder);

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        AnnualCropFolderModel annualCropFolder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(annualCropFolder));

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteAnnualCropFolderFails_WhenUserIsNotProprietario() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));
        when(annualCropFolderRepository.findById(anyLong())).thenReturn(Optional.of(createAnnualCropFolderModel(1L, 2023, ownerPlot)));

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }
}