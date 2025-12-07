package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
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
public class RangeExtractControllerImplTest extends AbstractControllerTest {

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

        managerUser = UserModel.builder()
                .id(3L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        consultorUser = UserModel.builder()
                .id(4L)
                .username("consultor")
                .name("Consultor User")
                .cargo(Cargo.AGRONOMO_CONSULTOR)
                .build();

        ownerProperty = PropertyModel.builder()
                .id(10L)
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(proprietarioUser)
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
                .build();

        otherProperty = PropertyModel.builder()
                .id(11L)
                .nome("Fazenda Boa Esperança")
                .cnpj("98.765.432/0001-11")
                .endereco("Estrada BR 020, KM 12")
                .owner(otherProprietarioUser)
                .localizacao(new Localizacao(6.50, LatitudeDirection.NORTE, 35.12, LongitudeDirection.LESTE, 15.0))
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

        ownerAnalysis = SoilAnalysisModel.builder()
                .id(200L)
                .plot(ownerPlot)
                .analysisYear(2024)
                .responsibleLaboratory("Laboratório X")
                .extractType(TipoExtrato.INTERVALOS)
                .build();

        otherAnalysis = SoilAnalysisModel.builder()
                .id(201L)
                .plot(otherPlot)
                .analysisYear(2023)
                .responsibleLaboratory("Laboratório Y")
                .extractType(TipoExtrato.CAMADAS)
                .build();

        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());
    }

    private RangeExtractCreateRequestDto createCreateRequestDto() {
        return RangeExtractCreateRequestDto.builder()
                .initialDepth(0)
                .finalDepth(20)
                .build();
    }

    private RangeExtractPostRequestDto createUpdateRequestDto() {
        return RangeExtractPostRequestDto.builder()
                .initialDepth(5)
                .finalDepth(25)
                .build();
    }

    private RangeExtractModel createRangeExtractModel(Long id, SoilAnalysisModel analysis) {
        return RangeExtractModel.builder()
                .id(id)
                .analysis(analysis)
                .profundidade_inicial(0)
                .profundidade_final(20)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createRangeExtractSuccessfully() throws Exception {
        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();
        RangeExtractModel savedExtract = createRangeExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
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
    @WithMockUser(username = "manager")
    void createRangeExtractAsManager() throws Exception {
        ownerProperty.setManager(managerUser);
        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();
        RangeExtractModel savedExtract = createRangeExtractModel(2L, ownerAnalysis);

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
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
    @WithMockUser(username = "testuser")
    void getRangeExtractSuccessfully() throws Exception {
        RangeExtractModel rangeExtract = createRangeExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(rangeExtract));

        mockMvc.perform(get("/range-extract/get")
                        .param("rangeExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profundidade_inicial").value(0));
    }

    @Test
    @WithMockUser(username = "consultor")
    void createRangeExtractFailsWithoutPlotApproval() throws Exception {
        RangeExtractCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("consultor")).thenReturn(Optional.of(consultorUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(ownerPlot, consultorUser, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/range-extract/register")
                        .param("analysisId", ownerAnalysis.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRangeExtractsByAnalysisSuccessfully() throws Exception {
        RangeExtractModel rangeExtract = createRangeExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(rangeExtractRepository.findAllByAnalysis(ownerAnalysis)).thenReturn(List.of(rangeExtract));

        mockMvc.perform(get("/range-extract/get-by-analysis")
                        .param("analysisId", ownerAnalysis.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].profundidade_inicial").value(0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateRangeExtractSuccessfully() throws Exception {
        RangeExtractModel existingExtract = createRangeExtractModel(1L, ownerAnalysis);
        RangeExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(rangeExtractRepository.save(any(RangeExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/range-extract/update")
                        .param("rangeExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profundidade_inicial").value(5))
                .andExpect(jsonPath("$.profundidade_final").value(25));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteRangeExtractSuccessfully() throws Exception {
        RangeExtractModel existingExtract = createRangeExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(rangeExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/range-extract/delete")
                        .param("rangeExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}