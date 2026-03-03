package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.AbstractControllerTest;
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
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;

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
import static org.mockito.ArgumentMatchers.anyInt;
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
@TestPropertySource(locations = "classpath:application-test.properties")
public class AnnualCropFolderControllerImplTest extends AbstractControllerTest {

    private static final String AUTH_USERNAME = "testuser";

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;
    private UserModel gerenteUser;
    private UserModel residenteUser;
    private UserModel consultorUser;

    private PropertyModel ownerProperty;
    private PlotModel ownerPlot;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        proprietarioUser = UserModel.builder()
                .id(1L)
                .username(AUTH_USERNAME)
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username(AUTH_USERNAME)
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(3L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        gerenteUser = UserModel.builder()
                .id(4L)
                .username("manager")
                .name("Gerente User")
                .cargo(Cargo.GERENTE)
                .build();

        residenteUser = UserModel.builder()
                .id(5L)
                .username("residente")
                .name("Agronomo Residente")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        consultorUser = UserModel.builder()
                .id(6L)
                .username("consultor")
                .name("Agronomo Consultor")
                .cargo(Cargo.AGRONOMO_CONSULTOR)
                .build();

        ownerProperty = createProperty(10L, "Fazenda Santa Clara", proprietarioUser);
        ownerPlot = createPlotModel(100L, "Talhao 01", ownerProperty);
    }

    // -------------------- Factories --------------------

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

    // -------------------- Stubs helpers --------------------

    private void stubAuthUser(UserModel user) {
        when(userRepository.findByUsername(AUTH_USERNAME)).thenReturn(Optional.of(user));
    }

    private void stubPlotExists(PlotModel plot) {
        when(plotRepository.findById(plot.getId())).thenReturn(Optional.of(plot));
        when(plotRepository.findByIdAndPropertyId(plot.getId(), plot.getProperty().getId())).thenReturn(Optional.of(plot));
    }

    private void stubPlotNotFound(Long plotId) {
        when(plotRepository.findById(plotId)).thenReturn(Optional.empty());
        when(plotRepository.findByIdAndPropertyId(eq(plotId), anyLong())).thenReturn(Optional.empty());
    }

    private void stubPropertyExists(PropertyModel property) {
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
    }

    private void stubSecretaryPlotApproval(PropertyModel property, PlotModel plot, UserModel requester) {
        PlotAccessRequestModel approved = PlotAccessRequestModel.builder()
                .property(property)
                .plot(plot)
                .requester(requester)
                .scope(PermissionScope.PLOT)
                .permissionType(PermissionType.EDIT_ANALYSES)
                .status(AccessRequestStatus.APPROVED)
                .build();

        when(plotAccessRequestRepository
                .findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                        eq(property), eq(plot), eq(requester), eq(PermissionScope.PLOT),
                        org.mockito.ArgumentMatchers.nullable(PermissionType.class), eq(AccessRequestStatus.APPROVED)
                ))
                .thenReturn(Optional.of(approved));
    }

    // -------------------- CREATE --------------------

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void createAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        AnnualCropFolderModel saved = createAnnualCropFolderModel(1L, requestDto.getCropsYear(), ownerPlot);

        stubAuthUser(proprietarioUser);
        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, requestDto.getCropsYear()))
                .thenReturn(Optional.empty());
        when(annualCropFolderRepository.save(any(AnnualCropFolderModel.class))).thenReturn(saved);

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
    @WithMockUser(username = AUTH_USERNAME)
    void createAnnualCropFolderFails_WhenUserIsSecretary() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();

        // Simulamos o Secretário
        stubAuthUser(funcionarioUser);
        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

        // Simulamos que o gerente aprovou ele, MAS a aprovação de Secretário é apenas EDIT_ANALYSES
        stubSecretaryPlotApproval(ownerProperty, ownerPlot, funcionarioUser);

        // O teste deve ESPERAR um erro 403 Forbidden, já que ele não pode editar Culturas!
        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden()) // ✅ CORRETO: Sistema barrou a tentativa
                .andExpect(jsonPath("$.message").value("Você não tem permissão para editar culturas neste talhão."));
    }

    @Test
    @WithMockUser(username = "consultor")
    void createAnnualCropFolderAsConsultantWithApproval() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        AnnualCropFolderModel saved = createAnnualCropFolderModel(1L, requestDto.getCropsYear(), ownerPlot);

        // ✅ CORREÇÃO: Mockamos diretamente a busca pelo username "consultor" usado na anotação
        when(userRepository.findByUsername("consultor")).thenReturn(Optional.of(consultorUser));

        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

        // Simulamos a aprovação dele, que possui o escopo de Culturas (EDIT_ANALYSES_AND_CROPS)
        PlotAccessRequestModel approvedConsultant = PlotAccessRequestModel.builder()
                .property(ownerProperty)
                .plot(ownerPlot)
                .requester(consultorUser)
                .scope(PermissionScope.PLOT)
                .permissionType(PermissionType.EDIT_ANALYSES_AND_CROPS) // Permissão total
                .status(AccessRequestStatus.APPROVED)
                .build();

        // Configura o Mockito para retornar a aprovação do consultor
        when(plotAccessRequestRepository
                .findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                        eq(ownerProperty), eq(ownerPlot), eq(consultorUser), eq(PermissionScope.PLOT),
                        org.mockito.ArgumentMatchers.nullable(PermissionType.class), eq(AccessRequestStatus.APPROVED)
                ))
                .thenReturn(Optional.of(approvedConsultant));

        when(annualCropFolderRepository.findByPlotAndCropsYear(any(), anyInt()))
                .thenReturn(Optional.empty());
        when(annualCropFolderRepository.save(any(AnnualCropFolderModel.class)))
                .thenReturn(saved);

        // O Agrônomo deve conseguir criar e receber 201 Created!
        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()) // ✅ SUCESSO!
                .andExpect(jsonPath("$.id_talhao").value(ownerPlot.getId()));
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void createAnnualCropFolderFails_WhenPlotDoesNotBelongToUser() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        stubAuthUser(proprietarioUser);
        stubPlotExists(otherPlot);
        stubPropertyExists(otherProperty);

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", otherPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void createAnnualCropFolderFails_WhenPlotNotFound() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();

        stubAuthUser(proprietarioUser);
        stubPlotNotFound(999L);

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void createAnnualCropFolderFails_WhenCropsYearAlreadyExists() throws Exception {
        AnnualCropFolderCreateRequestDto requestDto = createCreateRequestDto();
        AnnualCropFolderModel existing = createAnnualCropFolderModel(2L, requestDto.getCropsYear(), ownerPlot);

        stubAuthUser(proprietarioUser);
        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, requestDto.getCropsYear()))
                .thenReturn(Optional.of(existing));

        mockMvc.perform(post("/annual-crop-folder/register")
                        .param("plotId", ownerPlot.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // -------------------- READ --------------------

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void getAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, ownerPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_culturas").value(2023))
                .andExpect(jsonPath("$.id_talhao").value(ownerPlot.getId()));
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void getAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);
        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        stubPlotExists(otherPlot);
        stubPropertyExists(otherProperty);

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void getAnnualCropFolderFails_WhenNotFound() throws Exception {
        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/annual-crop-folder/get")
                        .param("annualCropFolderId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void getAnnualCropFoldersByPlotSuccessfully() throws Exception {
        AnnualCropFolderModel folder2022 = createAnnualCropFolderModel(1L, 2022, ownerPlot);
        AnnualCropFolderModel folder2023 = createAnnualCropFolderModel(2L, 2023, ownerPlot);

        stubAuthUser(proprietarioUser);
        stubPlotExists(ownerPlot);
        stubPropertyExists(ownerProperty);

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
    @WithMockUser(username = AUTH_USERNAME)
    void getAnnualCropFoldersByPlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);

        stubAuthUser(proprietarioUser);
        stubPlotExists(otherPlot);
        stubPropertyExists(otherProperty);

        mockMvc.perform(get("/annual-crop-folder/get-by-plot")
                        .param("plotId", otherPlot.getId().toString()))
                .andExpect(status().isForbidden());
    }

    // -------------------- UPDATE --------------------

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void updateAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel existing = createAnnualCropFolderModel(1L, 2023, ownerPlot);
        AnnualCropFolderPostRequestDto updateDto = createPostRequestDto();

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubPropertyExists(ownerProperty);
        stubPlotExists(ownerPlot);

        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, updateDto.getCropsYear()))
                .thenReturn(Optional.empty());
        when(annualCropFolderRepository.save(existing)).thenReturn(existing);

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano_culturas").value(2024));
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void updateAnnualCropFolderFails_WhenCropsYearAlreadyExists() throws Exception {
        AnnualCropFolderModel existing = createAnnualCropFolderModel(1L, 2023, ownerPlot);
        AnnualCropFolderPostRequestDto updateDto = createPostRequestDto();
        AnnualCropFolderModel conflicting = createAnnualCropFolderModel(2L, 2024, ownerPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubPropertyExists(ownerProperty);
        stubPlotExists(ownerPlot);

        when(annualCropFolderRepository.findByPlotAndCropsYear(ownerPlot, updateDto.getCropsYear()))
                .thenReturn(Optional.of(conflicting));

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void updateAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);
        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        stubPlotExists(otherPlot);
        stubPropertyExists(otherProperty);

        mockMvc.perform(put("/annual-crop-folder/update")
                        .param("annualCropFolderId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequestDto())))
                .andExpect(status().isForbidden());
    }

    // -------------------- DELETE --------------------

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void deleteAnnualCropFolderSuccessfully() throws Exception {
        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, ownerPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        stubPropertyExists(ownerProperty);
        stubPlotExists(ownerPlot);

        doNothing().when(annualCropFolderRepository).delete(folder);

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void deleteAnnualCropFolderFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = createProperty(20L, "Fazenda Secreta", otherProprietarioUser);
        PlotModel otherPlot = createPlotModel(200L, "Talhao 02", otherProperty);
        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, otherPlot);

        stubAuthUser(proprietarioUser);
        when(annualCropFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        stubPlotExists(otherPlot);
        stubPropertyExists(otherProperty);

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    void deleteAnnualCropFolderFails_WhenUserIsNotProprietario() throws Exception {
        stubAuthUser(funcionarioUser);

        AnnualCropFolderModel folder = createAnnualCropFolderModel(1L, 2023, ownerPlot);
        when(annualCropFolderRepository.findById(anyLong())).thenReturn(Optional.of(folder));
        stubPropertyExists(ownerProperty);
        stubPlotExists(ownerPlot);

        mockMvc.perform(delete("/annual-crop-folder/delete")
                        .param("annualCropFolderId", "1"))
                .andExpect(status().isForbidden());
    }
}