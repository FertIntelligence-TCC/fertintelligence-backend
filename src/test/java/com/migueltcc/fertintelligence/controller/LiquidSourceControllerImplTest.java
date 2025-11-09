package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.LiquidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.LiquidSourceRepository;
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
public class LiquidSourceControllerImplTest extends AbstractControllerTest {

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

    private LiquidSourceCreateRequestDto createRequestDto() {
        return LiquidSourceCreateRequestDto.builder()
                .date(new Date(20, 6, 2024))
                .micronutrient(AppliedMicronutrient.B)
                .source("Ácido Bórico")
                .concentration(17.0)
                .density(1.2)
                .applied_volume(2.5)
                .tail_volume(200.0)
                .build();
    }

    private LiquidSourcePostRequestDto updateRequestDto() {
        return LiquidSourcePostRequestDto.builder()
                .date(new Date(21, 6, 2024))
                .micronutrient(AppliedMicronutrient.Zn)
                .source("Novo Ácido Bórico")
                .concentration(18.0)
                .density(1.3)
                .applied_volume(3.0)
                .tail_volume(210.0)
                .build();
    }

    private LiquidSourceModel createLiquidSourceModel(Long id, CropModel crop) {
        return LiquidSourceModel.builder()
                .id(id)
                .crop(crop)
                .date(new Date(20, 6, 2024))
                .micronutrient(AppliedMicronutrient.B)
                .source("Ácido Bórico")
                .concentration(17.0)
                .density(1.2)
                .applied_volume(2.5)
                .tail_volume(200.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createLiquidSourceSuccessfully() throws Exception {
        LiquidSourceCreateRequestDto requestDto = createRequestDto();
        LiquidSourceModel savedSource = createLiquidSourceModel(1L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(liquidSourceRepository.save(any(LiquidSourceModel.class))).thenReturn(savedSource);

        mockMvc.perform(post("/foliar-fertilization/liquid-source/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/foliar-fertilization/liquid-source/get?liquidSourceId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("B"))
                .andExpect(jsonPath("$.fonte").value("Ácido Bórico"))
                .andExpect(jsonPath("$.concentracao").value(17.0))
                .andExpect(jsonPath("$.densidade").value(1.2))
                .andExpect(jsonPath("$.volume_aplicado").value(2.5))
                .andExpect(jsonPath("$.volume_calda").value(200.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getLiquidSourceSuccessfully() throws Exception {
        LiquidSourceModel source = createLiquidSourceModel(2L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(liquidSourceRepository.findById(2L)).thenReturn(Optional.of(source));

        mockMvc.perform(get("/foliar-fertilization/liquid-source/get")
                        .param("liquidSourceId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("B"))
                .andExpect(jsonPath("$.fonte").value("Ácido Bórico"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getLiquidSourcesByCropSuccessfully() throws Exception {
        LiquidSourceModel source1 = createLiquidSourceModel(3L, ownerCrop);
        LiquidSourceModel source2 = createLiquidSourceModel(4L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(liquidSourceRepository.findAllByCrop(ownerCrop)).thenReturn(List.of(source1, source2));

        mockMvc.perform(get("/foliar-fertilization/liquid-source/get-by-crop")
                        .param("cropId", ownerCrop.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[1].id").value(4L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateLiquidSourceSuccessfully() throws Exception {
        LiquidSourceModel existingSource = createLiquidSourceModel(5L, ownerCrop);
        LiquidSourceModel updatedSource = LiquidSourceModel.builder()
                .id(5L)
                .crop(ownerCrop)
                .date(new Date(21, 6, 2024))
                .micronutrient(AppliedMicronutrient.Zn)
                .source("Novo Ácido Bórico")
                .concentration(18.0)
                .density(1.3)
                .applied_volume(3.0)
                .tail_volume(210.0)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(liquidSourceRepository.findById(5L)).thenReturn(Optional.of(existingSource));
        when(liquidSourceRepository.save(existingSource)).thenReturn(updatedSource);

        mockMvc.perform(put("/foliar-fertilization/liquid-source/update")
                        .param("liquidSourceId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.micronutriente_aplicado").value("Zn"))
                .andExpect(jsonPath("$.fonte").value("Novo Ácido Bórico"))
                .andExpect(jsonPath("$.concentracao").value(18.0))
                .andExpect(jsonPath("$.densidade").value(1.3))
                .andExpect(jsonPath("$.volume_aplicado").value(3.0))
                .andExpect(jsonPath("$.volume_calda").value(210.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteLiquidSourceSuccessfully() throws Exception {
        LiquidSourceModel existingSource = createLiquidSourceModel(6L, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(liquidSourceRepository.findById(6L)).thenReturn(Optional.of(existingSource));
        doNothing().when(liquidSourceRepository).delete(existingSource);

        mockMvc.perform(delete("/foliar-fertilization/liquid-source/delete")
                        .param("liquidSourceId", "6"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createLiquidSourceFailsWhenUserIsNotProprietario() throws Exception {
        LiquidSourceCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        mockMvc.perform(post("/foliar-fertilization/liquid-source/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getLiquidSourceFailsWhenAccessingAnotherOwnersResource() throws Exception {
        LiquidSourceModel source = createLiquidSourceModel(7L, otherCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(liquidSourceRepository.findById(7L)).thenReturn(Optional.of(source));

        mockMvc.perform(get("/foliar-fertilization/liquid-source/get")
                        .param("liquidSourceId", "7"))
                .andExpect(status().isForbidden());
    }
}