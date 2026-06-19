package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
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
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CropControllerImplTest extends AbstractControllerTest {

    private static final String OWNER_USERNAME = "testuser";
    private static final String SECRETARY_USERNAME = "secretaryuser";
    private static final String MANAGER_USERNAME = "manageruser";
    private static final String RESIDENT_USERNAME = "residentuser";

    private static final Long OWNER_PROPERTY_ID = 10L;
    private static final Long OWNER_PLOT_ID = 100L;
    private static final Long OWNER_FOLDER_ID = 1000L;

    private static final String SECRETARY_FORBIDDEN_MESSAGE =
            "Você não tem permissão para editar culturas neste talhão.";

    private UserModel proprietarioUser;
    private UserModel secretarioUser;
    private UserModel managerUser;
    private UserModel residentUser;
    private UserModel otherProprietarioUser;

    private PropertyModel ownerProperty;
    private PlotModel ownerPlot;
    private AnnualCropFolderModel ownerFolder;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username(OWNER_USERNAME)
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        secretarioUser = UserModel.builder()
                .id(2L)
                .username(SECRETARY_USERNAME)
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(4L)
                .username(MANAGER_USERNAME)
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        residentUser = UserModel.builder()
                .id(5L)
                .username(RESIDENT_USERNAME)
                .name("Resident User")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(3L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        ownerProperty = PropertyModel.builder()
                .id(OWNER_PROPERTY_ID)
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(proprietarioUser)
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
                .build();

        ownerPlot = PlotModel.builder()
                .id(OWNER_PLOT_ID)
                .property(ownerProperty)
                .identification("Talhao Principal")
                .area(100.0)
                .soilClass(ClasseSolo.ARGISSOLO)
                .soilTexture(TexturaSolo.FRANCO_ARGILOSO_ARENOSA)
                .cropIncorporationYear(2020)
                .irrigatedArea(AreaIrrigada.SIM)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .build();

        ownerFolder = AnnualCropFolderModel.builder()
                .id(OWNER_FOLDER_ID)
                .plot(ownerPlot)
                .cropsYear(2024)
                .build();

        // alguns fluxos consultam repositórios de grafo
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(propertyRepository.findById(ownerProperty.getId())).thenReturn(Optional.of(ownerProperty));

        // MOCK NECESSÁRIO PARA O PermissionManager.findPlotInProperty()
        when(plotRepository.findByIdAndPropertyId(ownerPlot.getId(), ownerProperty.getId()))
                .thenReturn(Optional.of(ownerPlot));
        when(annualCropFolderRepository.findAllByPlot(ownerPlot)).thenReturn(List.of(ownerFolder));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of());
    }

    /* =========================
       Helpers de permissão (NOVO CONTRATO)
       ========================= */

    private void allowRead(UserModel user, PlotModel plot) {
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                plot.getProperty(),
                user,
                AccessRequestStatus.APPROVED
        )).thenReturn(Optional.of(PropertyAccessRequestModel.builder()
                .id(1000L)
                .property(plot.getProperty())
                .requester(user)
                .status(AccessRequestStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build()));
    }

    private void allowEditCrops(UserModel user, PlotModel plot) {
        // 1) membership aprovado (owner aprovou entrada)
        allowRead(user, plot);

        // 2) permissão de edição (manager aprovou) - escopo PROPERTY
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                eq(plot.getProperty()),
                eq(user),
                eq(PermissionScope.PROPERTY),
                any(),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(true);
    }

    /* =========================
       DTO / Models
       ========================= */

    private CropCreateRequestDto createCreateRequestDto() {
        return CropCreateRequestDto.builder()
                .cultivationType(CultivationType.SAFRA)
                .name(NomeComum.SOJA)
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
                .name(NomeComum.MILHO)
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

    private CropModel createCropModel(Long id, NomeComum name, String variety, AnnualCropFolderModel folder) {
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

    private CropModel createCropModel(
            Long id,
            NomeComum name,
            String variety,
            AnnualCropFolderModel folder,
            Double usedAreaInThePlot,
            Date plantingDate,
            Date harvestDate
    ) {
        CropModel crop = createCropModel(id, name, variety, folder);
        crop.setUsedAreaInThePlot(usedAreaInThePlot);
        crop.setPlantingDate(plantingDate);
        crop.setEmergenceDate(plantingDate);
        crop.setButtoningDate(plantingDate);
        crop.setFloweringDate(harvestDate);
        crop.setHarvestDate(harvestDate);
        return crop;
    }

    private void mockOwnerCreateFlow(CropCreateRequestDto requestDto, CropModel savedCrop) {
        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);
    }

    private void mockOwnerUpdateFlow(CropModel existingCrop, CropPostRequestDto updateRequestDto) {
        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(existingCrop.getId())).thenReturn(Optional.of(existingCrop));
        when(cropRepository.findByNameAndVarietyAndFolder(updateRequestDto.getName(), updateRequestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
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

        when(plotRepository.findById(200L)).thenReturn(Optional.of(otherPlot));
        when(propertyRepository.findById(20L)).thenReturn(Optional.of(otherProperty));
        // Mock necessário para PermissionManager deste outro cenário se fosse testar write
        when(plotRepository.findByIdAndPropertyId(200L, 20L)).thenReturn(Optional.of(otherPlot));

        return AnnualCropFolderModel.builder()
                .id(2000L)
                .plot(otherPlot)
                .cropsYear(2024)
                .build();
    }

    /* =========================
       Tests
       ========================= */

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropSuccessfully() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(1L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("SOJA"))
                .andExpect(jsonPath("$.variedade").value("TMG 7062 IPRO"))
                .andExpect(jsonPath("$.id_pasta_culturas_anuais").value(OWNER_FOLDER_ID))
                .andExpect(jsonPath("$.ano_culturas").value(2024));
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME)
    void createCropAsManagerSuccessfully() throws Exception {
        ownerProperty.setManager(managerUser);

        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(2L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername(MANAGER_USERNAME)).thenReturn(Optional.of(managerUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome").value("SOJA"));
    }

    @Test
    @WithMockUser(username = RESIDENT_USERNAME)
    void createCropAsApprovedResidentSuccessfully() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel savedCrop = createCropModel(3L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername(RESIDENT_USERNAME)).thenReturn(Optional.of(residentUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));

        // novo contrato: precisa membership + permissão EDIT_CROPS aprovada
        allowEditCrops(residentUser, ownerPlot);

        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.empty());
        when(cropRepository.save(any(CropModel.class))).thenReturn(savedCrop);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nome").value("SOJA"));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenFolderDoesNotExist() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = SECRETARY_USERNAME)
    void createCrop_ForSecretary_ReturnsForbidden() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername(SECRETARY_USERNAME)).thenReturn(Optional.of(secretarioUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));

        // mesmo que tenha approvals, a regra de negócio do sistema bloqueia secretário em hasEditPermission
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value("/crop/register"))
                .andExpect(jsonPath("$.message").value(SECRETARY_FORBIDDEN_MESSAGE));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenCropAlreadyExists() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        CropModel existingCrop = createCropModel(5L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(annualCropFolderRepository.findById(OWNER_FOLDER_ID)).thenReturn(Optional.of(ownerFolder));
        when(cropRepository.findByNameAndVarietyAndFolder(requestDto.getName(), requestDto.getVariety(), ownerFolder))
                .thenReturn(Optional.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropSucceeds_WhenOccupiedAreasDoNotOverlapByMonth() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(80.0);
        requestDto.setPlantingDate(new Date(1, 4, 2024));
        requestDto.setEmergenceDate(new Date(3, 4, 2024));
        requestDto.setButtoningDate(new Date(1, 5, 2024));
        requestDto.setFloweringDate(new Date(1, 6, 2024));
        requestDto.setHarvestDate(new Date(30, 6, 2024));

        CropModel existingCrop = createCropModel(
                10L, NomeComum.MILHO, "AG 8088", ownerFolder,
                80.0, new Date(1, 1, 2024), new Date(31, 3, 2024));
        CropModel savedCrop = createCropModel(11L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        mockOwnerCreateFlow(requestDto, savedCrop);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11L));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropSucceeds_WhenPartiallyOverlappingAreasEqualPlotArea() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(45.0);
        requestDto.setPlantingDate(new Date(1, 5, 2024));
        requestDto.setEmergenceDate(new Date(3, 5, 2024));
        requestDto.setButtoningDate(new Date(1, 6, 2024));
        requestDto.setFloweringDate(new Date(1, 7, 2024));
        requestDto.setHarvestDate(new Date(31, 8, 2024));

        CropModel existingCrop = createCropModel(
                12L, NomeComum.MILHO, "AG 8088", ownerFolder,
                55.0, new Date(1, 1, 2024), new Date(31, 5, 2024));
        CropModel savedCrop = createCropModel(13L, requestDto.getName(), requestDto.getVariety(), ownerFolder);

        mockOwnerCreateFlow(requestDto, savedCrop);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(13L));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenPartiallyOverlappingAreasExceedPlotArea() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(50.0);
        requestDto.setPlantingDate(new Date(1, 5, 2024));
        requestDto.setEmergenceDate(new Date(3, 5, 2024));
        requestDto.setButtoningDate(new Date(1, 6, 2024));
        requestDto.setFloweringDate(new Date(1, 7, 2024));
        requestDto.setHarvestDate(new Date(31, 8, 2024));

        CropModel existingCrop = createCropModel(
                14L, NomeComum.MILHO, "AG 8088", ownerFolder,
                60.0, new Date(1, 1, 2024), new Date(31, 5, 2024));

        mockOwnerCreateFlow(requestDto, createCropModel(15L, requestDto.getName(), requestDto.getVariety(), ownerFolder));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenFullyOverlappingAreasExceedPlotArea() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(50.0);
        requestDto.setPlantingDate(new Date(1, 1, 2024));
        requestDto.setEmergenceDate(new Date(3, 1, 2024));
        requestDto.setButtoningDate(new Date(1, 2, 2024));
        requestDto.setFloweringDate(new Date(1, 3, 2024));
        requestDto.setHarvestDate(new Date(31, 5, 2024));

        CropModel existingCrop = createCropModel(
                16L, NomeComum.MILHO, "AG 8088", ownerFolder,
                55.0, new Date(1, 1, 2024), new Date(31, 5, 2024));

        mockOwnerCreateFlow(requestDto, createCropModel(17L, requestDto.getName(), requestDto.getVariety(), ownerFolder));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenMonthlySumAcrossMultipleOverlapsExceedsPlotArea() throws Exception {
        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(30.0);
        requestDto.setPlantingDate(new Date(1, 5, 2024));
        requestDto.setEmergenceDate(new Date(3, 5, 2024));
        requestDto.setButtoningDate(new Date(1, 6, 2024));
        requestDto.setFloweringDate(new Date(1, 7, 2024));
        requestDto.setHarvestDate(new Date(31, 8, 2024));

        CropModel firstExistingCrop = createCropModel(
                18L, NomeComum.MILHO, "AG 8088", ownerFolder,
                40.0, new Date(1, 4, 2024), new Date(31, 5, 2024));
        CropModel secondExistingCrop = createCropModel(
                19L, NomeComum.ALGODAO, "FM 985", ownerFolder,
                40.0, new Date(1, 5, 2024), new Date(31, 6, 2024));

        mockOwnerCreateFlow(requestDto, createCropModel(20L, requestDto.getName(), requestDto.getVariety(), ownerFolder));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(firstExistingCrop, secondExistingCrop));

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createCropFails_WhenOverlappingCropInAnotherFolderOfSamePlotExceedsPlotArea() throws Exception {
        AnnualCropFolderModel previousFolder = AnnualCropFolderModel.builder()
                .id(OWNER_FOLDER_ID - 1)
                .plot(ownerPlot)
                .cropsYear(2023)
                .build();

        CropCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setUsedAreaInThePlot(50.0);
        requestDto.setPlantingDate(new Date(1, 1, 2024));
        requestDto.setEmergenceDate(new Date(3, 1, 2024));
        requestDto.setButtoningDate(new Date(1, 2, 2024));
        requestDto.setFloweringDate(new Date(1, 3, 2024));
        requestDto.setHarvestDate(new Date(31, 3, 2024));

        CropModel existingCrop = createCropModel(
                29L, NomeComum.MILHO, "AG 8088", previousFolder,
                60.0, new Date(1, 2, 2024), new Date(31, 5, 2024));

        mockOwnerCreateFlow(requestDto, createCropModel(30L, requestDto.getName(), requestDto.getVariety(), ownerFolder));
        when(annualCropFolderRepository.findAllByPlot(ownerPlot)).thenReturn(List.of(previousFolder, ownerFolder));
        when(cropRepository.findAllByFolder(previousFolder)).thenReturn(List.of(existingCrop));
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of());

        mockMvc.perform(post("/crop/register")
                        .param("folderId", OWNER_FOLDER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getCropSuccessfully() throws Exception {
        CropModel crop = createCropModel(1L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        mockMvc.perform(get("/crop/get")
                        .param("cropId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("SOJA"));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getCropFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/crop/get")
                        .param("cropId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateCropSuccessfully() throws Exception {
        CropModel existingCrop = createCropModel(1L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder);
        CropPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
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
                .andExpect(jsonPath("$.nome").value("MILHO"))
                .andExpect(jsonPath("$.variedade").value("TBIO Toruk"))
                .andExpect(jsonPath("$.ciclo").value(135));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateCropSucceeds_WhenOccupiedAreasDoNotOverlapByMonth() throws Exception {
        CropModel existingCrop = createCropModel(
                21L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder,
                40.0, new Date(1, 1, 2024), new Date(29, 2, 2024));
        CropModel otherCrop = createCropModel(
                22L, NomeComum.MILHO, "AG 8088", ownerFolder,
                90.0, new Date(1, 3, 2024), new Date(30, 4, 2024));
        CropPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setUsedAreaInThePlot(90.0);
        updateRequestDto.setPlantingDate(new Date(1, 1, 2024));
        updateRequestDto.setEmergenceDate(new Date(3, 1, 2024));
        updateRequestDto.setButtoningDate(new Date(1, 2, 2024));
        updateRequestDto.setFloweringDate(new Date(10, 2, 2024));
        updateRequestDto.setHarvestDate(new Date(29, 2, 2024));

        mockOwnerUpdateFlow(existingCrop, updateRequestDto);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop, otherCrop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", existingCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area_usada_no_talhao").value(90.0));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateCropSucceeds_WhenOverlappingAreasEqualPlotArea() throws Exception {
        CropModel existingCrop = createCropModel(
                23L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder,
                40.0, new Date(1, 1, 2024), new Date(31, 5, 2024));
        CropModel otherCrop = createCropModel(
                24L, NomeComum.MILHO, "AG 8088", ownerFolder,
                60.0, new Date(1, 3, 2024), new Date(31, 5, 2024));
        CropPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setUsedAreaInThePlot(40.0);
        updateRequestDto.setPlantingDate(new Date(1, 1, 2024));
        updateRequestDto.setEmergenceDate(new Date(3, 1, 2024));
        updateRequestDto.setButtoningDate(new Date(1, 2, 2024));
        updateRequestDto.setFloweringDate(new Date(1, 3, 2024));
        updateRequestDto.setHarvestDate(new Date(31, 5, 2024));

        mockOwnerUpdateFlow(existingCrop, updateRequestDto);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop, otherCrop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", existingCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area_usada_no_talhao").value(40.0));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateCropFails_WhenPartiallyOverlappingAreasExceedPlotArea() throws Exception {
        CropModel existingCrop = createCropModel(
                25L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder,
                40.0, new Date(1, 1, 2024), new Date(31, 3, 2024));
        CropModel otherCrop = createCropModel(
                26L, NomeComum.MILHO, "AG 8088", ownerFolder,
                60.0, new Date(1, 3, 2024), new Date(31, 5, 2024));
        CropPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setUsedAreaInThePlot(50.0);
        updateRequestDto.setPlantingDate(new Date(1, 1, 2024));
        updateRequestDto.setEmergenceDate(new Date(3, 1, 2024));
        updateRequestDto.setButtoningDate(new Date(1, 2, 2024));
        updateRequestDto.setFloweringDate(new Date(1, 3, 2024));
        updateRequestDto.setHarvestDate(new Date(31, 3, 2024));

        mockOwnerUpdateFlow(existingCrop, updateRequestDto);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop, otherCrop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", existingCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateCropFails_WhenFullyOverlappingAreasExceedPlotArea() throws Exception {
        CropModel existingCrop = createCropModel(
                27L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder,
                40.0, new Date(1, 1, 2024), new Date(31, 5, 2024));
        CropModel otherCrop = createCropModel(
                28L, NomeComum.MILHO, "AG 8088", ownerFolder,
                55.0, new Date(1, 1, 2024), new Date(31, 5, 2024));
        CropPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setUsedAreaInThePlot(50.0);
        updateRequestDto.setPlantingDate(new Date(1, 1, 2024));
        updateRequestDto.setEmergenceDate(new Date(3, 1, 2024));
        updateRequestDto.setButtoningDate(new Date(1, 2, 2024));
        updateRequestDto.setFloweringDate(new Date(1, 3, 2024));
        updateRequestDto.setHarvestDate(new Date(31, 5, 2024));

        mockOwnerUpdateFlow(existingCrop, updateRequestDto);
        when(cropRepository.findAllByFolder(ownerFolder)).thenReturn(List.of(existingCrop, otherCrop));

        mockMvc.perform(put("/crop/update")
                        .param("cropId", existingCrop.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void deleteCropSuccessfully() throws Exception {
        CropModel crop = createCropModel(1L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        doNothing().when(cropRepository).delete(crop);

        mockMvc.perform(delete("/crop/delete")
                        .param("cropId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = SECRETARY_USERNAME)
    void deleteCrop_ForSecretary_ReturnsForbidden() throws Exception {
        CropModel crop = createCropModel(1L, NomeComum.SOJA, "TMG 7062 IPRO", ownerFolder);

        when(userRepository.findByUsername(SECRETARY_USERNAME)).thenReturn(Optional.of(secretarioUser));
        when(cropRepository.findById(anyLong())).thenReturn(Optional.of(crop));

        // secretário não passa no existsByPropertyAndRequester...
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(), any()
        )).thenReturn(false);

        mockMvc.perform(delete("/crop/delete")
                        .param("cropId", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value("/crop/delete"))
                .andExpect(jsonPath("$.message").value(SECRETARY_FORBIDDEN_MESSAGE));
    }
}
