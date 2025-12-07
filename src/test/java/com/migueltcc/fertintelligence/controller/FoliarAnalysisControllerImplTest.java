package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.BeneficialElementsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.BeneficialElementsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MacronutrientsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MicronutrientsContentDto;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
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
public class FoliarAnalysisControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel managerUser;
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
                .username("secretary")
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(4L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
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
                .manager(managerUser)
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

    private FoliarAnalysisCreateRequestDto createCreateRequestDto() {
        return FoliarAnalysisCreateRequestDto.builder()
                .collectDate(new Date(15, 1, 2025))
                .laboratory("Laboratório Foliar Nordeste")
                .micronutrients(createMicronutrientsContentDto())
                .macronutrients(createMacronutrientsContentDto())
                .elements(createBeneficialElementsContentDto())
                .build();
    }

    private FoliarAnalysisPostRequestDto createPostRequestDto() {
        return FoliarAnalysisPostRequestDto.builder()
                .collectDate(new Date(20, 2, 2025))
                .laboratory("Laboratório Agro Atualizado")
                .micronutrients(new MicronutrientsContentDto(45.0, 12.0, 160.0, 0.2, 80.0, 0.6, 35.0))
                .macronutrients(new MacronutrientsContentDto(4.0, 0.25, 2.0, 1.2, 0.4, 0.18))
                .elements(new BeneficialElementsContentDto(6.0, 25.0, 0.03, 0.02, 0.06))
                .build();
    }

    private FoliarAnalysisModel createFoliarAnalysisModel(Long id, Date date, CropModel crop) {
        return FoliarAnalysisModel.builder()
                .id(id)
                .collectDate(date)
                .laboratory("Laboratório Foliar Nordeste")
                .micronutrients(createMicronutrientsContent())
                .macronutrients(createMacronutrientsContent())
                .elements(createBeneficialElementsContent())
                .crop(crop)
                .build();
    }

    private MicronutrientsContentDto createMicronutrientsContentDto() {
        return new MicronutrientsContentDto(40.0, 10.0, 150.0, 0.1, 70.0, 0.5, 30.0);
    }

    private MacronutrientsContentDto createMacronutrientsContentDto() {
        return new MacronutrientsContentDto(3.5, 0.2, 1.8, 1.0, 0.3, 0.15);
    }

    private BeneficialElementsContentDto createBeneficialElementsContentDto() {
        return new BeneficialElementsContentDto(5.0, 20.0, 0.02, 0.01, 0.05);
    }

    private MicronutrientsContent createMicronutrientsContent() {
        return new MicronutrientsContent(40.0, 10.0, 150.0, 0.1, 70.0, 0.5, 30.0);
    }

    private MacronutrientsContent createMacronutrientsContent() {
        return new MacronutrientsContent(3.5, 0.2, 1.8, 1.0, 0.3, 0.15);
    }

    private BeneficialElementsContent createBeneficialElementsContent() {
        return new BeneficialElementsContent(5.0, 20.0, 0.02, 0.01, 0.05);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFoliarAnalysisSuccessfully() throws Exception {
        FoliarAnalysisCreateRequestDto requestDto = createCreateRequestDto();
        FoliarAnalysisModel savedAnalysis = createFoliarAnalysisModel(1L, requestDto.getCollectDate(), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, requestDto.getCollectDate()))
                .thenReturn(Optional.empty());
        when(foliarAnalysisRepository.save(any(FoliarAnalysisModel.class))).thenReturn(savedAnalysis);

        mockMvc.perform(post("/foliar-analysis/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/foliar-analysis/get?analysisId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.laboratorio").value("Laboratório Foliar Nordeste"))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutrientes.b_content").value(40.0))
                .andExpect(jsonPath("$.macronutrientes.n_content").value(3.5))
                .andExpect(jsonPath("$.elementos_beneficos.na_content").value(5.0))
                .andExpect(jsonPath("$.data_coleta.day").value(15))
                .andExpect(jsonPath("$.data_coleta.month").value(1))
                .andExpect(jsonPath("$.data_coleta.year").value(2025));
    }

    @Test
    @WithMockUser(username = "secretary")
    void createFoliarAnalysisFailsForSecretaryEdit() throws Exception {
        FoliarAnalysisCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("secretary")).thenReturn(Optional.of(funcionarioUser));

        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));

        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, requestDto.getCollectDate()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/foliar-analysis/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "manager")
    void createFoliarAnalysisAsManager() throws Exception {
        FoliarAnalysisCreateRequestDto requestDto = createCreateRequestDto();
        FoliarAnalysisModel savedAnalysis = createFoliarAnalysisModel(2L, requestDto.getCollectDate(), ownerCrop);

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, requestDto.getCollectDate()))
                .thenReturn(Optional.empty());
        when(foliarAnalysisRepository.save(any(FoliarAnalysisModel.class))).thenReturn(savedAnalysis);

        mockMvc.perform(post("/foliar-analysis/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFoliarAnalysisFails_WhenCropBelongsToAnotherOwner() throws Exception {
        FoliarAnalysisCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(otherCrop.getId())).thenReturn(Optional.of(otherCrop));

        mockMvc.perform(post("/foliar-analysis/register")
                        .param("cropId", otherCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFoliarAnalysisFails_WhenDuplicateCollectDate() throws Exception {
        FoliarAnalysisCreateRequestDto requestDto = createCreateRequestDto();
        FoliarAnalysisModel existing = createFoliarAnalysisModel(2L, requestDto.getCollectDate(), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, requestDto.getCollectDate()))
                .thenReturn(Optional.of(existing));

        mockMvc.perform(post("/foliar-analysis/register")
                        .param("cropId", ownerCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoliarAnalysisSuccessfully() throws Exception {
        FoliarAnalysisModel analysis = createFoliarAnalysisModel(1L, new Date(10, 3, 2025), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(get("/foliar-analysis/get")
                        .param("analysisId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_cultura").value(ownerCrop.getId()))
                .andExpect(jsonPath("$.micronutrientes.b_content").value(40.0))
                .andExpect(jsonPath("$.data_coleta.day").value(10));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoliarAnalysisFails_WhenNotOwner() throws Exception {
        FoliarAnalysisModel analysis = createFoliarAnalysisModel(1L, new Date(10, 3, 2025), otherCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(get("/foliar-analysis/get")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoliarAnalysisFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/foliar-analysis/get")
                        .param("analysisId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoliarAnalysesByCropSuccessfully() throws Exception {
        FoliarAnalysisModel analysis1 = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), ownerCrop);
        FoliarAnalysisModel analysis2 = createFoliarAnalysisModel(2L, new Date(15, 3, 2025), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(ownerCrop.getId())).thenReturn(Optional.of(ownerCrop));
        when(foliarAnalysisRepository.findAllByCrop(ownerCrop)).thenReturn(List.of(analysis1, analysis2));

        mockMvc.perform(get("/foliar-analysis/get-by-crop")
                        .param("cropId", ownerCrop.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateFoliarAnalysisSuccessfully() throws Exception {
        FoliarAnalysisModel existing = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), ownerCrop);
        FoliarAnalysisPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, updateRequestDto.getCollectDate()))
                .thenReturn(Optional.empty());
        when(foliarAnalysisRepository.save(any(FoliarAnalysisModel.class))).thenAnswer(invocation -> {
            FoliarAnalysisModel toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });

        mockMvc.perform(put("/foliar-analysis/update")
                        .param("analysisId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.laboratorio").value("Laboratório Agro Atualizado"))
                .andExpect(jsonPath("$.micronutrientes.b_content").value(45.0))
                .andExpect(jsonPath("$.macronutrientes.n_content").value(4.0))
                .andExpect(jsonPath("$.elementos_beneficos.na_content").value(6.0))
                .andExpect(jsonPath("$.data_coleta.day").value(20))
                .andExpect(jsonPath("$.data_coleta.month").value(2))
                .andExpect(jsonPath("$.data_coleta.year").value(2025));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateFoliarAnalysisFails_WhenDuplicateCollectDate() throws Exception {
        FoliarAnalysisModel existing = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), ownerCrop);
        FoliarAnalysisModel conflicting = createFoliarAnalysisModel(2L, new Date(20, 2, 2025), ownerCrop);
        FoliarAnalysisPostRequestDto updateRequestDto = FoliarAnalysisPostRequestDto.builder()
                .collectDate(new Date(20, 2, 2025))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(foliarAnalysisRepository.findByCropAndCollectDate(ownerCrop, updateRequestDto.getCollectDate()))
                .thenReturn(Optional.of(conflicting));

        mockMvc.perform(put("/foliar-analysis/update")
                        .param("analysisId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteFoliarAnalysisSuccessfully() throws Exception {
        FoliarAnalysisModel analysis = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));
        doNothing().when(foliarAnalysisRepository).delete(analysis);

        mockMvc.perform(delete("/foliar-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteFoliarAnalysisFails_WhenNotOwner() throws Exception {
        FoliarAnalysisModel analysis = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), otherCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(delete("/foliar-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteFoliarAnalysisFails_WhenUserIsNotProprietario() throws Exception {
        FoliarAnalysisModel analysis = createFoliarAnalysisModel(1L, new Date(5, 2, 2025), ownerCrop);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));
        when(foliarAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(delete("/foliar-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }
}