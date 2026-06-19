package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

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
class RangeExtractControllerImplTest extends AbstractControllerTest {

    private static final String USER_OWNER = "testuser";
    private static final String USER_OTHER_OWNER = "otheruser";
    private static final String USER_MANAGER = "manager";
    private static final String USER_CONSULTOR = "consultor";

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;
    private UserModel consultorUser;

    private PropertyModel ownerProperty;
    private PropertyModel otherProperty;

    private PlotModel ownerPlot;
    private PlotModel otherPlot;

    private SoilAnalysisModel ownerAnalysis;
    private SoilAnalysisModel otherAnalysis;

    @BeforeEach
    void setUp() {
        proprietarioUser = user(1L, USER_OWNER, "Test User Proprietario", Cargo.PROPRIETARIO);
        otherProprietarioUser = user(2L, USER_OTHER_OWNER, "Other User Proprietario", Cargo.PROPRIETARIO);
        managerUser = user(3L, USER_MANAGER, "Manager User", Cargo.GERENTE);
        consultorUser = user(4L, USER_CONSULTOR, "Consultor User", Cargo.AGRONOMO_CONSULTOR);

        ownerProperty = property(10L, "Fazenda Santa Clara", proprietarioUser,
                new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0));
        otherProperty = property(11L, "Fazenda Boa Esperança", otherProprietarioUser,
                new Localizacao(6.50, LatitudeDirection.NORTE, 35.12, LongitudeDirection.LESTE, 15.0));

        ownerPlot = plot(100L, ownerProperty, "Talhao 01");
        otherPlot = plot(101L, otherProperty, "Talhao 02");

        ownerAnalysis = analysis(200L, ownerPlot, 2024, "Laboratório X", TipoExtrato.INTERVALOS);
        otherAnalysis = analysis(201L, otherPlot, 2023, "Laboratório Y", TipoExtrato.CAMADAS);

        // Defaults: nenhum acesso aprovado (usado para usuários não-owner/manager)
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());

        // >>> FIX DO ERRO: mocka os métodos QUE EXISTEM no PlotAccessRequestRepository (usados no PermissionManager)
        when(plotAccessRequestRepository.findByPropertyAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any()
        )).thenReturn(Optional.empty());

        when(plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(Optional.empty());
    }

    /* =========================
       TESTS
       ========================= */

    @Test
    @WithMockUser(username = USER_OWNER)
    void createRangeExtractSuccessfully() throws Exception {
        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();
        RangeExtractModel savedExtract = rangeExtract(1L, ownerAnalysis, 0, 20);

        when(userRepository.findByUsername(USER_OWNER)).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(rangeExtractRepository.save(any(RangeExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/range-extract/register")
                        .param("analysisId", ownerAnalysis.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/range-extract/get?rangeExtractId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profundidade_inicial").value(0))
                .andExpect(jsonPath("$.profundidade_final").value(20))
                .andExpect(jsonPath("$.id_analise").value(ownerAnalysis.getId()))
                .andExpect(jsonPath("$.ano_analise").value(ownerAnalysis.getAnalysisYear()))
                .andExpect(jsonPath("$.laboratorio_responsavel").value(ownerAnalysis.getResponsibleLaboratory()));
    }

    @Test
    @WithMockUser(username = USER_MANAGER)
    void createRangeExtractAsManager() throws Exception {
        ownerProperty.setManager(managerUser);

        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();
        RangeExtractModel savedExtract = rangeExtract(2L, ownerAnalysis, 0, 20);

        when(userRepository.findByUsername(USER_MANAGER)).thenReturn(Optional.of(managerUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(rangeExtractRepository.save(any(RangeExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/range-extract/register")
                        .param("analysisId", ownerAnalysis.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.id_analise").value(ownerAnalysis.getId()));
    }

    @Test
    @WithMockUser(username = USER_OWNER)
    void getRangeExtractSuccessfully() throws Exception {
        RangeExtractModel extract = rangeExtract(1L, ownerAnalysis, 0, 20);

        when(userRepository.findByUsername(USER_OWNER)).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(extract));

        mockMvc.perform(get("/range-extract/get")
                        .param("rangeExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profundidade_inicial").value(0))
                .andExpect(jsonPath("$.profundidade_final").value(20));
    }

    @Test
    @WithMockUser(username = USER_OWNER)
    void getRangeExtractsByAnalysisSuccessfully() throws Exception {
        RangeExtractModel extract = rangeExtract(1L, ownerAnalysis, 0, 20);

        when(userRepository.findByUsername(USER_OWNER)).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(rangeExtractRepository.findAllByAnalysis(ownerAnalysis)).thenReturn(List.of(extract));

        mockMvc.perform(get("/range-extract/get-by-analysis")
                        .param("analysisId", ownerAnalysis.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].profundidade_inicial").value(0));
    }

    @Test
    @WithMockUser(username = USER_OWNER)
    void updateRangeExtractSuccessfully() throws Exception {
        RangeExtractModel existing = rangeExtract(1L, ownerAnalysis, 0, 20);
        RangeExtractPostRequestDto updateDto = createUpdateRequestDto();

        when(userRepository.findByUsername(USER_OWNER)).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(rangeExtractRepository.save(any(RangeExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/range-extract/update")
                        .param("rangeExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profundidade_inicial").value(5))
                .andExpect(jsonPath("$.profundidade_final").value(25));
    }

    @Test
    @WithMockUser(username = USER_OWNER)
    void deleteRangeExtractSuccessfully() throws Exception {
        RangeExtractModel existing = rangeExtract(1L, ownerAnalysis, 0, 20);

        when(userRepository.findByUsername(USER_OWNER)).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(rangeExtractRepository).delete(existing);

        mockMvc.perform(delete("/range-extract/delete")
                        .param("rangeExtractId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = USER_CONSULTOR)
    void createRangeExtractFailsWithoutPropertyMembershipApproval() throws Exception {
        // consultor sem aprovação de entrada na propriedade => Forbidden
        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername(USER_CONSULTOR)).thenReturn(Optional.of(consultorUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));

        // membership continua Optional.empty() (setUp)
        mockMvc.perform(post("/range-extract/register")
                        .param("analysisId", ownerAnalysis.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    /* =========================
       HELPERS (builders)
       ========================= */

    private static UserModel user(Long id, String username, String name, Cargo cargo) {
        return UserModel.builder()
                .id(id)
                .username(username)
                .name(name)
                .cargo(cargo)
                .build();
    }

    private static PropertyModel property(Long id, String nome, UserModel owner, Localizacao loc) {
        return PropertyModel.builder()
                .id(id)
                .nome(nome)
                .cnpj("00.000.000/0000-00")
                .endereco("Endereco " + id)
                .owner(owner)
                .localizacao(loc)
                .build();
    }

    private static PlotModel plot(Long id, PropertyModel property, String identification) {
        return PlotModel.builder()
                .id(id)
                .property(property)
                .identification(identification)
                .area(15.0)
                .soilClass(null)
                .soilTexture(null)
                .cropIncorporationYear(2020)
                .irrigatedArea(null)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .build();
    }

    private static SoilAnalysisModel analysis(
            Long id,
            PlotModel plot,
            Integer year,
            String lab,
            TipoExtrato extractType
    ) {
        return SoilAnalysisModel.builder()
                .id(id)
                .plot(plot)
                .analysisYear(year)
                .responsibleLaboratory(lab)
                .extractType(extractType)
                .build();
    }

    private static RangeExtractCreateRequestDto createCreateRequestDto() {
        return RangeExtractCreateRequestDto.builder()
                .initialDepth(0)
                .finalDepth(20)
                .build();
    }

    private static RangeExtractPostRequestDto createUpdateRequestDto() {
        return RangeExtractPostRequestDto.builder()
                .initialDepth(5)
                .finalDepth(25)
                .build();
    }

    private static RangeExtractModel rangeExtract(Long id, SoilAnalysisModel analysis, int ini, int fin) {
        return RangeExtractModel.builder()
                .id(id)
                .analysis(analysis)
                .profundidade_inicial(ini)
                .profundidade_final(fin)
                .build();
    }
}