package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.TopdressingFertilizationModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.TopDressingFertilizationRepository;
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
import static org.mockito.ArgumentMatchers.eq;
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
public class TopDressingFertilizationControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TopDressingFertilizationRepository topDressingFertilizationRepository;

    @MockitoBean
    private CropRepository cropRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UserModel proprietarioUser;
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

        otherProprietarioUser = UserModel.builder()
                .id(2L)
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
                .buttoningDate(new Date(20, 2, 2024))
                .floweringDate(new Date(10, 3, 2024))
                .harvestDate(new Date(25, 5, 2024))
                .build();
    }

    private TopDressingFertilizationCreateRequestDto createCreateRequestDto() {
        return TopDressingFertilizationCreateRequestDto.builder()
                .date(new Date(15, 5, 2025))
                .order(1)
                .formulated(150.0)
                .ammonium_sulfate(120.0)
                .urea(80.0)
                .potassium_chloride(200.0)
                .triple_superphosphate(0.0)
                .simple_superphosphate(0.0)
                .monoammonium_phosphate(50.0)
                .build();
    }

    private TopDressingFertilizationPostRequestDto createUpdateRequestDto() {
        return TopDressingFertilizationPostRequestDto.builder()
                .date(new Date(20, 5, 2025))
                .order(2)
                .formulated(160.0)
                .ammonium_sulfate(130.0)
                .urea(85.0)
                .potassium_chloride(210.0)
                .triple_superphosphate(5.0)
                .simple_superphosphate(2.5)
                .monoammonium_phosphate(55.0)
                .build();
    }

    private TopdressingFertilizationModel createFertilizationModel(Long id, Date date, Integer order, CropModel crop) {
        return TopdressingFertilizationModel.builder()
                .id(id)
                .crop(crop)
                .date(date)
                .order(order)
                .formulated(150.0)
                .ammonium_sulfate(120.0)
                .urea(80.0)
                .potassium_chloride(200.0)
                .triple_superphosphate(0.0)
                .simple_superphosphate(0.0)
                .monoammonium_phosphate(50.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTopDressingFertilizationSuccessfully() throws Exception {
        TopDressingFertilizationCreateRequestDto requestDto = createCreateRequestDto();
        TopdressingFertilizationModel savedFertilization = createFertilizationModel(1L, requestDto.getDate(), requestDto.getOrder(), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(topDressingFertilizationRepository.findByCropAndOrder(ownerCrop, requestDto.getOrder()))
                .thenReturn(Optional.empty());
        when(topDressingFertilizationRepository.save(any(TopdressingFertilizationModel.class))).thenReturn(savedFertilization);

        mockMvc.perform(post("/top-dressing-fertilization/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/top-dressing-fertilization/get?fertilizationId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.ordem").value(1))
                .andExpect(jsonPath("$.ureia").value(80.0))
                .andExpect(jsonPath("$.data.day").value(15))
                .andExpect(jsonPath("$.data.month").value(5))
                .andExpect(jsonPath("$.data.year").value(2025));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTopDressingFertilizationSuccessfully() throws Exception {
        TopdressingFertilizationModel fertilization = createFertilizationModel(1L, new Date(10, 5, 2025), 1, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(topDressingFertilizationRepository.findById(1L)).thenReturn(Optional.of(fertilization));

        mockMvc.perform(get("/top-dressing-fertilization/get")
                        .param("fertilizationId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.ordem").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTopDressingFertilizationsByCropSuccessfully() throws Exception {
        TopdressingFertilizationModel fertilization1 = createFertilizationModel(1L, new Date(5, 5, 2025), 1, ownerCrop);
        TopdressingFertilizationModel fertilization2 = createFertilizationModel(2L, new Date(15, 5, 2025), 2, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(topDressingFertilizationRepository.findAllByCrop(ownerCrop)).thenReturn(List.of(fertilization1, fertilization2));

        mockMvc.perform(get("/top-dressing-fertilization/get-by-crop")
                        .param("cropId", ownerCrop.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTopDressingFertilizationSuccessfully() throws Exception {
        TopDressingFertilizationPostRequestDto updateRequestDto = createUpdateRequestDto();
        TopdressingFertilizationModel existingFertilization = createFertilizationModel(1L, new Date(10, 5, 2025), 1, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(topDressingFertilizationRepository.findById(1L)).thenReturn(Optional.of(existingFertilization));
        when(topDressingFertilizationRepository.findByCropAndOrder(eq(ownerCrop), eq(updateRequestDto.getOrder())))
                .thenReturn(Optional.empty());
        when(topDressingFertilizationRepository.save(any(TopdressingFertilizationModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/top-dressing-fertilization/update")
                        .param("fertilizationId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordem").value(2))
                .andExpect(jsonPath("$.ureia").value(85.0))
                .andExpect(jsonPath("$.data.day").value(20));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTopDressingFertilizationSuccessfully() throws Exception {
        TopdressingFertilizationModel fertilization = createFertilizationModel(1L, new Date(10, 5, 2025), 1, ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(topDressingFertilizationRepository.findById(1L)).thenReturn(Optional.of(fertilization));
        doNothing().when(topDressingFertilizationRepository).delete(fertilization);

        mockMvc.perform(delete("/top-dressing-fertilization/delete")
                        .param("fertilizationId", "1"))
                .andExpect(status().isNoContent());
    }
}