package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class CropControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel secretarioUser;
    private UserModel managerUser;
    private UserModel residentUser;
    private UserModel consultantUser;
    private UserModel otherProprietarioUser;
    private PropertyModel ownerProperty;
    private PlotModel ownerPlot;
    private AnnualCropFolderModel ownerFolder;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        secretarioUser = UserModel.builder()
                .id(2L)
                .username("testuser")
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(4L)
                .username("manageruser")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        residentUser = UserModel.builder()
                .id(5L)
                .username("residentuser")
                .name("Resident User")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        consultantUser = UserModel.builder()
                .id(6L)
                .username("consultantuser")
                .name("Consultant User")
                .cargo(Cargo.AGRONOMO_CONSULTOR)
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
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
                .build();

        ownerPlot = PlotModel.builder()
                .id(100L)
                .property(ownerProperty)
                .identification("Talhao Principal")
                .area(25.0)
                .soilClass(ClasseSolo.ARGISSOLO)
                .soilTexture(TexturaSolo.FRANCO_ARGILOSO_ARENOSA)
                .cropIncorporationYear(2020)
                .irrigatedArea(AreaIrrigada.SIM)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .build();

        ownerFolder = AnnualCropFolderModel.builder()
                .id(1000L)
                .plot(ownerPlot)
                .cropsYear(2024)
                .build();
    }

    private CropCreateRequestDto createCreateRequestDto() {
        return CropCreateRequestDto.builder()
                .cultivationType(CultivationType.SAFRA)
                .name("Soja")
                .variety("TMG 7062 IPRO")
                .cycle(180)
                .distanceBetweenLines(0.45)
                .plantsPerMeter(12.0)
                .expectedProductivity(3600.0)
                .obtainedProductivity(3400.0)
                .usedAreaInThePlot(45.0)
                .plantingDate(new Date(15, 10, 2024))
                .emergenceDate(new Date(23, 10, 2024))
                .buttoningDate(new Date(5, 12, 2024))
                .floweringDate(new Date(20, 12, 2024))
                .harvestDate(new Date(12, 3, 2025))
                .build();
    }

    private CropPostRequestDto createPostRequestDto() {
        return CropPostRequestDto.builder()
                .cultivationType(CultivationType.SAFRINHA)
                .name("Trigo")
                .variety("TBIO Toruk")
                .cycle(135)
                .distanceBetweenLines(0.17)
                .plantsPerMeter(330.0)
                .expectedProductivity(4200.0)
                .obtainedProductivity(4000.0)
                .usedAreaInThePlot(50.0)
                .plantingDate(new Date(20, 3, 2025))
                .emergenceDate(new Date(28, 3, 2025))
                .buttoningDate(new Date(10, 5, 2025))
                .floweringDate(new Date(30, 5, 2025))
                .harvestDate(new Date(25, 7, 2025))
                .build();
    }

    private CropModel createCropModel(Long id, String name, String variety, AnnualCropFolderModel folder) {
        return CropModel.builder()
                .id(id)
                .folder(folder)
                .cultivationType(CultivationType.SAFRA)
                .name(name)
                .variety(variety)
                .cycle(180)
                .distanceBetweenLines(0.45)
                .plantsPerMeter(12.0)
                .expectedProductivity(3600.0)
                .obtainedProductivity(3400.0)
                .usedAreaInThePlot(45.0)
                .plantingDate(new Date(15, 10, 2024))
                .emergenceDate(new Date(23, 10, 2024))
                .buttoningDate(new Date(5, 12, 2024))
                .floweringDate(new Date(20, 12, 2024))
                .harvestDate(new Date(12, 3, 2025))
                .build();
    }

    private AnnualCropFolderModel createFolderForOtherOwner() {
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .cnpj("98.765.432/0001-99")
                .endereco("Rua das Palmeiras, 50")
                .owner(otherProprietarioUser)
                .localizacao(new Localizacao(8.0, LatitudeDirection.NORTE, 35.0, LongitudeDirection.LESTE, 50.0))
                .build();

        PlotModel otherPlot = PlotModel.builder()
                .id(200L)
                .property(otherProperty)
                .identification("Talhao Outro")
                .area(30.0)
                .soilClass(ClasseSolo.LATOSSOLO)
                .soilTexture(TexturaSolo.ARGILA)
                .cropIncorporationYear(2021)
                .irrigatedArea(AreaIrrigada.NAO)
                .declivity(7.0)
                .monthlyPluviosity(220.0)
                .annualPluviosity(1300.0)
                .build();

        return AnnualCropFolderModel.builder()
                .id(2000L)
                .plot(otherPlot)
                .cropsYear(2024)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropSuccessfully() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(1L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Soja"))
                .andExpect(jsonPath("$.variedade").value("TMG 7062 IPRO"))
                .andExpect(jsonPath("$.id_pasta_culturas_anuais").value(1000L))
                .andExpect(jsonPath("$.ano_culturas").value(2024));
    }

    @Test
    @WithMockUser(username = "manageruser")
    void createCropAsManagerSuccessfully() throws Exception {
        ownerProperty.setManager(managerUser);

        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(2L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername("manageruser")).thenReturn(Optional.of(managerUser));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome").value("Soja"));
    }

    @Test
    @WithMockUser(username = "residentuser")
    void createCropAsApprovedResidentSuccessfully() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(3L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername("residentuser")).thenReturn(Optional.of(residentUser));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(ownerPlot, residentUser, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(PlotAccessRequestModel.builder()
                        .id(50L)
                        .property(ownerProperty)
                        .plot(ownerPlot)
                        .requester(residentUser)
                        .status(AccessRequestStatus.APPROVED)
                        .createdAt(LocalDateTime.now())
                        .build()));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nome").value("Soja"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCropFails_WhenFolderDoesNotExist() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(secretarioUser));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secretaryuser")
    void createCropFails_ForSecretaryEvenWithApprovedAccess() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(1L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        UserModel secretary = secretarioUser.builder()
                .username("secretaryuser")
                .build();

        when(userRepository.findByUsername("secretaryuser")).thenReturn(Optional.of(secretary));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(ownerPlot, secretary, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(PlotAccessRequestModel.builder()
                        .id(55L)
                        .property(ownerProperty)
                        .plot(ownerPlot)
                        .requester(secretary)
                        .status(AccessRequestStatus.APPROVED)
                        .createdAt(LocalDateTime.now())
                        .build()));
        when(cropRepository.findByNameAndVarietyAndFolder(any(), any(), any())).thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Soja"))
                .andExpect(jsonPath("$.variedade").value("TMG 7062 IPRO"));
    }


    @Test
    @WithMockUser(username = "testuser")
    void createCropFails_WhenCropAlreadyExists() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel existingCrop = createCropModel(5L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropSuccessfully() throws Exception {
        CropModel crop = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        mockMvc.perform(get("/crop/get")
                        .param("cropId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Soja"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFails_WhenUserIsNotOwner() throws Exception {
        // Criar propriedade e folder para outro dono
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .owner(otherProprietarioUser)
                .build();
        PlotModel otherPlot = PlotModel.builder().id(200L).property(otherProperty).build();
        AnnualCropFolderModel otherFolder = AnnualCropFolderModel.builder().id(2000L).plot(otherPlot).build();

        CropModel crop = createCropModel(1L, "Soja", "TMG 7062 IPRO", otherFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        mockMvc.perform(get("/crop/get")
                        .param("cropId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/crop/get")
                        .param("cropId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCropsByFolderSuccessfully() throws Exception {
        CropModel crop1 = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);
        CropModel crop2 = createCropModel(2L, "Milho", "AG 8088", ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(1000L)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(crop1, crop2));

        mockMvc.perform(get("/crop/get-by-folder")
                        .param("folderId", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropSuccessfully() throws Exception {
        CropModel existingCrop = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);
        CropPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(existingCrop));
        when(cropRepository.findByNameAndVarietyAndFolder(updateRequestDto.getName(), updateRequestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Trigo"))
                .andExpect(jsonPath("$.variedade").value("TBIO Toruk"))
                .andExpect(jsonPath("$.ciclo").value(135));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFails_WhenUserIsNotOwner() throws Exception {
        // Criar propriedade e folder para outro dono
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .owner(otherProprietarioUser)
                .build();
        PlotModel otherPlot = PlotModel.builder().id(200L).property(otherProperty).build();
        AnnualCropFolderModel otherFolder = AnnualCropFolderModel.builder().id(2000L).plot(otherPlot).build();

        CropModel crop = createCropModel(1L, "Soja", "TMG 7062 IPRO", otherFolder);
        CropPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCropFails_WhenCropAlreadyExists() throws Exception {
        CropModel existingCrop = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);
        CropPostRequestDto updateRequestDto = createPostRequestDto();
        CropModel otherCrop = createCropModel(2L, updateRequestDto.getName(), updateRequestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(existingCrop));
        when(cropRepository.findByNameAndVarietyAndFolder(updateRequestDto.getName(), updateRequestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.of(otherCrop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCropSuccessfully() throws Exception {
        CropModel crop = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        doNothing().when(cropRepository).delete(crop);

        mockMvc.perform(delete("/crop/delete")
                        .param("cropId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "secretaryuser")
    void deleteCropFails_WhenUserIsSecretaryEvenWithAccess() throws Exception {
        CropModel crop = createCropModel(1L, "Soja", "TMG 7062 IPRO", ownerFolder);
        UserModel secretary = secretarioUser.builder()
                .username("secretaryuser")
                .build();

        when(userRepository.findByUsername("secretaryuser")).thenReturn(Optional.of(secretary));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(ownerPlot, secretary, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(PlotAccessRequestModel.builder()
                        .id(60L)
                        .property(ownerProperty)
                        .plot(ownerPlot)
                        .requester(secretary)
                        .status(AccessRequestStatus.APPROVED)
                        .createdAt(LocalDateTime.now())
                        .build()));

        mockMvc.perform(delete("/crop/delete")
                        .param("cropId", "1"))
                .andExpect(status().isNoContent());
    }

}