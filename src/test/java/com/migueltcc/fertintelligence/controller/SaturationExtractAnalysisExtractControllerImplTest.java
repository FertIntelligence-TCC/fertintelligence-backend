package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class SaturationExtractAnalysisExtractControllerImplTest extends AbstractControllerTest {

    private static final String OWNER_USERNAME = "testuser";
    private static final String MANAGER_USERNAME = "manager";
    private static final String RESIDENTE_USERNAME = "residente";
    private static final String CONSULTOR_USERNAME = "consultor";
    private static final String SECRETARIO_USERNAME = "secretario";

    private UserModel proprietarioUser;
    private UserModel gerenteUser;
    private UserModel residenteUser;
    private UserModel consultorUser;
    private UserModel secretarioUser;

    private PropertyModel ownerProperty;
    private PlotModel ownerPlot;

    private SoilAnalysisModel ownerRangeAnalysis;
    private SoilAnalysisModel ownerLayerAnalysis;

    private RangeExtractModel ownerRangeExtract;
    private LayerExtractModel ownerLayerExtract;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username(OWNER_USERNAME)
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        gerenteUser = UserModel.builder()
                .id(2L)
                .username(MANAGER_USERNAME)
                .name("Gerente da Propriedade")
                .cargo(Cargo.GERENTE)
                .build();

        residenteUser = UserModel.builder()
                .id(3L)
                .username(RESIDENTE_USERNAME)
                .name("Agrônomo Residente")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        consultorUser = UserModel.builder()
                .id(4L)
                .username(CONSULTOR_USERNAME)
                .name("Agrônomo Consultor")
                .cargo(Cargo.AGRONOMO_CONSULTOR)
                .build();

        secretarioUser = UserModel.builder()
                .id(5L)
                .username(SECRETARIO_USERNAME)
                .name("Secretário da Propriedade")
                .cargo(Cargo.SECRETARIO)
                .build();

        ownerProperty = PropertyModel.builder()
                .id(10L)
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(proprietarioUser)
                .manager(gerenteUser)
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
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

        ownerRangeAnalysis = SoilAnalysisModel.builder()
                .id(200L)
                .plot(ownerPlot)
                .analysisYear(2024)
                .responsibleLaboratory("Laboratório X")
                .extractType(TipoExtrato.INTERVALOS)
                .build();

        ownerLayerAnalysis = SoilAnalysisModel.builder()
                .id(201L)
                .plot(ownerPlot)
                .analysisYear(2023)
                .responsibleLaboratory("Laboratório Y")
                .extractType(TipoExtrato.CAMADAS)
                .build();

        ownerRangeExtract = RangeExtractModel.builder()
                .id(300L)
                .analysis(ownerRangeAnalysis)
                .profundidade_inicial(0)
                .profundidade_final(20)
                .build();

        ownerLayerExtract = LayerExtractModel.builder()
                .id(400L)
                .analysis(ownerLayerAnalysis)
                .profundidade_inicial(0)
                .profundidade_final(20)
                .layer(Camada.A)
                .sub_layer(1)
                .build();
    }

    // -------------------- DTOs / Models --------------------

    private SaturationExtractAnalysisExtractCreateRequestDto createCreateRequestDto() {
        return SaturationExtractAnalysisExtractCreateRequestDto.builder()
                .ph(7.2)
                .ce(0.5)
                .teorCO3(12.0)
                .teorHCO3(18.0)
                .teorNO3(25.0)
                .teorH2PO4(5.0)
                .teorSO4(14.0)
                .teorCl(16.0)
                .teorNa(10.0)
                .teorK(8.0)
                .teorCa(20.0)
                .teorMg(12.0)
                .residuosSuspensao(30.0)
                .durezaCaCO3(50.0)
                .durezaTotalCaCO3(80.0)
                .ras(12.5)
                .pst(8.0)
                .build();
    }

    private SaturationExtractAnalysisExtractPostRequestDto createUpdateRequestDto() {
        return SaturationExtractAnalysisExtractPostRequestDto.builder()
                .ph(7.5)
                .ce(0.6)
                .teorCl(17.0)
                .build();
    }

    private SaturationExtractAnalysisExtractModel createSaturationExtractAnalysisExtractModel(Long id,
                                                                                              RangeExtractModel rangeExtract,
                                                                                              LayerExtractModel layerExtract) {
        return SaturationExtractAnalysisExtractModel.builder()
                .id(id)
                .rangeExtract(rangeExtract)
                .layerExtract(layerExtract)
                .ph(7.2)
                .ce(0.5)
                .teorCO3(12.0)
                .teorHCO3(18.0)
                .teorNO3(25.0)
                .teorH2PO4(5.0)
                .teorSO4(14.0)
                .teorCl(16.0)
                .teorNa(10.0)
                .teorK(8.0)
                .teorCa(20.0)
                .teorMg(12.0)
                .residuosSuspensao(30.0)
                .durezaCaCO3(50.0)
                .durezaTotalCaCO3(80.0)
                .ras(12.5)
                .pst(8.0)
                .build();
    }

    private PropertyAccessRequestModel approvedPropertyAccess(UserModel requester, PropertyModel property) {
        return PropertyAccessRequestModel.builder()
                .id(60L)
                .property(property)
                .requester(requester)
                .status(AccessRequestStatus.APPROVED)
                .build();
    }

    private PlotAccessRequestModel approvedPlotAccess(UserModel requester,
                                                      PropertyModel property,
                                                      PlotModel plot,
                                                      PermissionScope scope,
                                                      PermissionType permissionType) {
        return PlotAccessRequestModel.builder()
                .id(50L)
                .property(property)
                .plot(plot)
                .requester(requester)
                .scope(scope)
                .permissionType(permissionType)
                .status(AccessRequestStatus.APPROVED)
                .build();
    }

    // -------------------- Stubs helpers --------------------

    private void stubUser(String username, UserModel user) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    }

    private void stubRangeExtractExists(RangeExtractModel rangeExtract) {
        when(rangeExtractRepository.findById(rangeExtract.getId())).thenReturn(Optional.of(rangeExtract));
    }

    private void stubLayerExtractExists(LayerExtractModel layerExtract) {
        when(layerExtractRepository.findById(layerExtract.getId())).thenReturn(Optional.of(layerExtract));
    }

    /**
     * IMPORTANTE: seu PlotAccessRequestRepository não possui mais
     * findByPlotAndRequesterAndStatus(plot, requester, status).
     *
     * Conforme a interface que você mostrou antes, o método correto é:
     * findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(...)
     *
     * Aqui centralizamos o stub para evitar repetir e quebrar testes no futuro.
     */
    private void stubApprovedPropertyAccess(PropertyModel property, UserModel requester) {
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                eq(property),
                eq(requester),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(Optional.of(approvedPropertyAccess(requester, property)));
    }

    private void stubApprovedPlotAccess(PropertyModel property, PlotModel plot, UserModel requester) {
        when(plotAccessRequestRepository
                .findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                        eq(property),
                        eq(plot),
                        eq(requester),
                        eq(PermissionScope.PLOT),
                        any(PermissionType.class),
                        eq(AccessRequestStatus.APPROVED)
                ))
                .thenReturn(Optional.of(
                        approvedPlotAccess(
                                requester,
                                property,
                                plot,
                                PermissionScope.PLOT,
                                PermissionType.EDIT_ANALYSES
                        )
                ));

        when(plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
                eq(property),
                eq(plot),
                eq(requester),
                eq(PermissionScope.PLOT),
                any(),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(true);
    }

    // -------------------- TESTS --------------------

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createSaturationExtractAnalysisExtractWithRangeSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract =
                createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        stubUser(OWNER_USERNAME, proprietarioUser);
        stubRangeExtractExists(ownerRangeExtract);

        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/saturation-extract-analysis-extract/get?saturationExtractAnalysisExtractId=1"
                ))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_extrato_intervalo").value(ownerRangeExtract.getId()))
                .andExpect(jsonPath("$.ph").value(7.2))
                .andExpect(jsonPath("$.teor_cl").value(16.0));
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME)
    void createSaturationExtractAnalysisExtractWithRangeAsGerenteSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract =
                createSaturationExtractAnalysisExtractModel(2L, ownerRangeExtract, null);

        stubUser(MANAGER_USERNAME, gerenteUser);
        stubRangeExtractExists(ownerRangeExtract);

        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(username = RESIDENTE_USERNAME)
    void createSaturationExtractAnalysisExtractWithRangeAsResidenteWithApproval() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract =
                createSaturationExtractAnalysisExtractModel(3L, ownerRangeExtract, null);

        stubUser(RESIDENTE_USERNAME, residenteUser);
        stubRangeExtractExists(ownerRangeExtract);

        stubApprovedPropertyAccess(ownerProperty, residenteUser);
        stubApprovedPlotAccess(ownerProperty, ownerPlot, residenteUser);

        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @WithMockUser(username = CONSULTOR_USERNAME)
    void createSaturationExtractAnalysisExtractWithLayerAsConsultorWithApproval() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract =
                createSaturationExtractAnalysisExtractModel(4L, null, ownerLayerExtract);

        stubUser(CONSULTOR_USERNAME, consultorUser);
        stubLayerExtractExists(ownerLayerExtract);

        stubApprovedPropertyAccess(ownerProperty, consultorUser);
        stubApprovedPlotAccess(ownerProperty, ownerPlot, consultorUser);

        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("layerExtractId", ownerLayerExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.id_extrato_camada").value(ownerLayerExtract.getId()));
    }

    @Test
    @WithMockUser(username = SECRETARIO_USERNAME)
    void createSaturationExtractAnalysisExtractWithLayerAsSecretarioWithApproval() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract =
                createSaturationExtractAnalysisExtractModel(5L, null, ownerLayerExtract);

        stubUser(SECRETARIO_USERNAME, secretarioUser);
        stubLayerExtractExists(ownerLayerExtract);

        stubApprovedPropertyAccess(ownerProperty, secretarioUser);
        stubApprovedPlotAccess(ownerProperty, ownerPlot, secretarioUser);

        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("layerExtractId", ownerLayerExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract =
                createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        stubUser(OWNER_USERNAME, proprietarioUser);
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get")
                        .param("saturationExtractAnalysisExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ph").value(7.2))
                .andExpect(jsonPath("$.teor_cl").value(16.0));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getSaturationExtractAnalysisExtractsByRangeSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract =
                createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        stubUser(OWNER_USERNAME, proprietarioUser);
        stubRangeExtractExists(ownerRangeExtract);

        when(saturationExtractAnalysisExtractRepository.findAllByRangeExtract(ownerRangeExtract))
                .thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get-by-range")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ph").value(7.2));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getSaturationExtractAnalysisExtractsByLayerSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract =
                createSaturationExtractAnalysisExtractModel(2L, null, ownerLayerExtract);

        stubUser(OWNER_USERNAME, proprietarioUser);
        stubLayerExtractExists(ownerLayerExtract);

        when(saturationExtractAnalysisExtractRepository.findAllByLayerExtract(ownerLayerExtract))
                .thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get-by-layer")
                        .param("layerExtractId", ownerLayerExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].id_extrato_camada").value(ownerLayerExtract.getId()))
                .andExpect(jsonPath("$[0].camada").value(Camada.A.name()));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updateSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel existingExtract =
                createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);
        SaturationExtractAnalysisExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        stubUser(OWNER_USERNAME, proprietarioUser);
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/saturation-extract-analysis-extract/update")
                        .param("saturationExtractAnalysisExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ph").value(7.5))
                .andExpect(jsonPath("$.ce").value(0.6))
                .andExpect(jsonPath("$.teor_cl").value(17.0));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void deleteSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel existingExtract =
                createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        stubUser(OWNER_USERNAME, proprietarioUser);
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(saturationExtractAnalysisExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/saturation-extract-analysis-extract/delete")
                        .param("saturationExtractAnalysisExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}
