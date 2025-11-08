package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.SolidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.SolidSourceRepository;
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
public class SolidSourceControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SolidSourceRepository solidSourceRepository;

    @MockitoBean
    private CropRepository cropRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;

    private PropertyModel ownerProperty;
    private PropertyModel otherProperty;

    private PlotModel ownerPlot;
    private PlotModel otherPlot;

    private AnnualCropFolderModel ownerFolder;
    private AnnualCropFolderModel otherFolder;

    private CropModel ownerCrop;
    private CropModel otherCrop;

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

        ownerProperty = PropertyModel.builder()
                .id(10L)
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(proprietarioUser)
                .localizacao(null)
                .build();

        otherProperty = PropertyModel.builder()
                .id(11L)
                .nome("Fazenda Boa Esperança")
                .cnpj("98.765.432/0001-11")
                .endereco("Estrada BR 020, KM 12")
                .owner(otherProprietarioUser)
                .localizacao(null)
                .build();

        ownerPlot = PlotModel.builder()
                .id(100L)
                .property(ownerProperty)
                .identification("Talhao 01")
                .area(15.0)
                .soilClass(null)
                .soilTexture(null)
                .cropIncorporationYear(2020)
                .irrigatedArea(null)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .build();

        otherPlot = PlotModel.builder()
                .id(101L)
                .property(otherProperty)
                .identification("Talhao 02")
                .area(18.0)
                .soilClass(null)
                .soilTexture(null)
                .cropIncorporationYear(2019)
                .irrigatedArea(null)
                .declivity(6.0)
                .monthlyPluviosity(210.0)
                .annualPluviosity(1150.0)
                .build();

        ownerFolder = AnnualCropFolderModel.builder()
                .id(200L)
                .plot(ownerPlot)
                .cropsYear(2024)
                .build();

        otherFolder = AnnualCropFolderModel.builder()
                .id(201L)
                .plot(otherPlot)
                .cropsYear(2023)
                .build();

        ownerCrop = CropModel.builder()
                .id(300L)
                .folder(ownerFolder)
                .cultivationType(null)
                .name("Algodão")
                .variety("BRS 432")
                .cycle(150)
                .distanceBetweenLines(0.45)
                .plantsPerMeter(10.0)
                .expectedProductivity(4000.0)
                .obtainedProductivity(3800.0)
                .usedAreaInThePlot(20.0)
                .plantingDate(new Date(10, 2, 2024))
                .emergenceDate(new Date(20, 2, 2024))
                .buttoningDate(new Date(15, 3, 2024))
                .floweringDate(new Date(5, 4, 2024))
                .harvestDate(new Date(20, 6, 2024))
                .build();

        otherCrop = CropModel.builder()
                .id(301L)
                .folder(otherFolder)
                .cultivationType(null)
                .name("Milho")
                .variety("AG 8088")
                .cycle(140)
                .distanceBetweenLines(0.5)
                .plantsPerMeter(7.0)
                .expectedProductivity(9000.0)
                .obtainedProductivity(8500.0)
                .usedAreaInThePlot(25.0)
                .plantingDate(new Date(5, 1, 2024))
                .emergenceDate(new Date(15, 1, 2024))
                .buttoningDate(new Date(10, 2, 2024))
                .floweringDate(new Date(1, 3, 2024))
                .harvestDate(new Date(30, 6, 2024))
                .build();
    }

    private SolidSourceCreateRequestDto createRequestDto() {
        return SolidSourceCreateRequestDto.builder()
                .date(new Date(25, 7, 2024))
                .micronutrient(AppliedMicronutrient.Mn)
                .source("Sulfato de Manganês")
                .concentration(30.0)
                .quantity(5.0)
                .build();
    }

    private SolidSourcePostRequestDto updateRequestDto() {
        return SolidSourcePostRequestDto.builder()
                .date(new Date(26, 7, 2024))
                .micronutrient(AppliedMicronutrient.Zn)
                .source("Sulfato de Zinco")
                .concentration(28.0)
                .quantity(4.5)
                .build();
    }

    private SolidSourceModel createSolidSourceModel(Long id, CropModel crop) {
        return SolidSourceModel.builder()
                .id(id)
                .crop(crop)
                .date(new Date(25, 7, 2024))
                .micronutrient(AppliedMicronutrient.Mn)
                .source("Sulfato de Manganês")
                .concentration(30.0)
                .quantity(5.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSolidSourceSuccessfully() throws Exception {
        SolidSourceCreateRequestDto requestDto = createRequestDto();
        SolidSourceModel savedSource = createSolidSourceModel(1L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(solidSourceRepository.save(any(SolidSourceModel.class))).thenReturn(savedSource);

        mockMvc.perform(post("/foliar-fertilization/solid-source/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/foliar-fertilization/solid-source/get?solidSourceId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("Mn"))
                .andExpect(jsonPath("$.fonte").value("Sulfato de Manganês"))
                .andExpect(jsonPath("$.concentracao").value(30.0))
                .andExpect(jsonPath("$.quantidade_aplicada").value(5.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSolidSourceSuccessfully() throws Exception {
        SolidSourceModel source = createSolidSourceModel(2L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(solidSourceRepository.findById(2L)).thenReturn(Optional.of(source));

        mockMvc.perform(get("/foliar-fertilization/solid-source/get")
                        .param("solidSourceId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("Mn"))
                .andExpect(jsonPath("$.fonte").value("Sulfato de Manganês"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSolidSourcesByCropSuccessfully() throws Exception {
        SolidSourceModel source1 = createSolidSourceModel(3L, ownerCrop);
        SolidSourceModel source2 = createSolidSourceModel(4L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(solidSourceRepository.findAllByCrop(ownerCrop)).thenReturn(List.of(source1, source2));

        mockMvc.perform(get("/foliar-fertilization/solid-source/get-by-crop")
                        .param("cropId", ownerCrop.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[1].id").value(4L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSolidSourceSuccessfully() throws Exception {
        SolidSourceModel existingSource = createSolidSourceModel(5L, ownerCrop);
        SolidSourceModel updatedSource = SolidSourceModel.builder()
                .id(5L)
                .crop(ownerCrop)
                .date(new Date(26, 7, 2024))
                .micronutrient(AppliedMicronutrient.Zn)
                .source("Sulfato de Zinco")
                .concentration(28.0)
                .quantity(4.5)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(solidSourceRepository.findById(5L)).thenReturn(Optional.of(existingSource));
        when(solidSourceRepository.save(existingSource)).thenReturn(updatedSource);

        mockMvc.perform(put("/foliar-fertilization/solid-source/update")
                        .param("solidSourceId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("Zn"))
                .andExpect(jsonPath("$.fonte").value("Sulfato de Zinco"))
                .andExpect(jsonPath("$.concentracao").value(28.0))
                .andExpect(jsonPath("$.quantidade_aplicada").value(4.5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSolidSourceSuccessfully() throws Exception {
        SolidSourceModel existingSource = createSolidSourceModel(6L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(solidSourceRepository.findById(6L)).thenReturn(Optional.of(existingSource));
        doNothing().when(solidSourceRepository).delete(existingSource);

        mockMvc.perform(delete("/foliar-fertilization/solid-source/delete")
                        .param("solidSourceId", "6"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSolidSourceFailsWhenUserIsNotProprietario() throws Exception {
        SolidSourceCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        mockMvc.perform(post("/foliar-fertilization/solid-source/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSolidSourceFailsWhenAccessingAnotherOwnersResource() throws Exception {
        SolidSourceModel source = createSolidSourceModel(7L, otherCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(solidSourceRepository.findById(7L)).thenReturn(Optional.of(source));

        mockMvc.perform(get("/foliar-fertilization/solid-source/get")
                        .param("solidSourceId", "7"))
                .andExpect(status().isForbidden());
    }
}