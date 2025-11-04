package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.Property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.Property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.Property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.User.Cargo;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
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
public class LayerExtractControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LayerExtractRepository layerExtractRepository;

    @MockitoBean
    private SoilAnalysisRepository soilAnalysisRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UserModel proprietarioUser;
    private UserModel otherProprietarioUser;

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
                .extractType(TipoExtrato.CAMADAS)
                .build();

        otherAnalysis = SoilAnalysisModel.builder()
                .id(201L)
                .plot(otherPlot)
                .analysisYear(2023)
                .responsibleLaboratory("Laboratório Y")
                .extractType(TipoExtrato.INTERVALOS)
                .build();
    }

    private LayerExtractCreateRequestDto createCreateRequestDto() {
        return LayerExtractCreateRequestDto.builder()
                .initialDepth(0)
                .finalDepth(20)
                .layer(Camada.A)
                .subLayer(1)
                .build();
    }

    private LayerExtractPostRequestDto createUpdateRequestDto() {
        return LayerExtractPostRequestDto.builder()
                .initialDepth(10)
                .finalDepth(30)
                .layer(Camada.B)
                .subLayer(2)
                .build();
    }

    private LayerExtractModel createLayerExtractModel(Long id, SoilAnalysisModel analysis) {
        return LayerExtractModel.builder()
                .id(id)
                .analysis(analysis)
                .profundidade_inicial(0)
                .profundidade_final(20)
                .layer(Camada.A)
                .sub_layer(1)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createLayerExtractSuccessfully() throws Exception {
        LayerExtractCreateRequestDto requestDto = createCreateRequestDto();
        LayerExtractModel savedExtract = createLayerExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(layerExtractRepository.save(any(LayerExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/layer-extract/register")
                        .param("analysisId", ownerAnalysis.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/layer-extract/get?layerExtractId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.profundidade_inicial").value(0))
                .andExpect(jsonPath("$.profundidade_final").value(20))
                .andExpect(jsonPath("$.camada").value("A"))
                .andExpect(jsonPath("$.subcamada").value(1))
                .andExpect(jsonPath("$.id_analise").value(ownerAnalysis.getId()))
                .andExpect(jsonPath("$.ano_analise").value(ownerAnalysis.getAnalysisYear()))
                .andExpect(jsonPath("$.laboratorio_responsavel").value(ownerAnalysis.getResponsibleLaboratory()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getLayerExtractSuccessfully() throws Exception {
        LayerExtractModel layerExtract = createLayerExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(1L)).thenReturn(Optional.of(layerExtract));

        mockMvc.perform(get("/layer-extract/get")
                        .param("layerExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.camada").value("A"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getLayerExtractsByAnalysisSuccessfully() throws Exception {
        LayerExtractModel layerExtract = createLayerExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(ownerAnalysis.getId())).thenReturn(Optional.of(ownerAnalysis));
        when(layerExtractRepository.findAllByAnalysis(ownerAnalysis)).thenReturn(List.of(layerExtract));

        mockMvc.perform(get("/layer-extract/get-by-analysis")
                        .param("analysisId", ownerAnalysis.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].camada").value("A"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateLayerExtractSuccessfully() throws Exception {
        LayerExtractModel existingExtract = createLayerExtractModel(1L, ownerAnalysis);
        LayerExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(layerExtractRepository.save(any(LayerExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/layer-extract/update")
                        .param("layerExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profundidade_inicial").value(10))
                .andExpect(jsonPath("$.profundidade_final").value(30))
                .andExpect(jsonPath("$.camada").value("B"))
                .andExpect(jsonPath("$.subcamada").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteLayerExtractSuccessfully() throws Exception {
        LayerExtractModel existingExtract = createLayerExtractModel(1L, ownerAnalysis);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(layerExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/layer-extract/delete")
                        .param("layerExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}