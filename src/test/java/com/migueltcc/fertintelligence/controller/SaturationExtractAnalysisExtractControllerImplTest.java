package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.Property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.Property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.Property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.User.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SaturationExtractAnalysisExtractRepository;
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
public class SaturationExtractAnalysisExtractControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;

    @MockitoBean
    private RangeExtractRepository rangeExtractRepository;

    @MockitoBean
    private LayerExtractRepository layerExtractRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UserModel proprietarioUser;
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
                .username("testuser")
                .name("Test User Proprietario")
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

    private SaturationExtractAnalysisExtractCreateRequestDto createCreateRequestDto() {
        return SaturationExtractAnalysisExtractCreateRequestDto.builder()
                .ph(7.2)
                .ce(0.5)
                .teorCO3(12.0)
                .teorHCO3(18.0)
                .teorNO3(25.0)
                .teorH2PO4(5.0)
                .teorSO4(14.0)
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

    @Test
    @WithMockUser(username = "testuser")
    void createSaturationExtractAnalysisExtractWithRangeSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        SaturationExtractAnalysisExtractModel savedExtract = createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/saturation-extract-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/saturation-extract-analysis-extract/get?saturationExtractAnalysisExtractId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_extrato_intervalo").value(ownerRangeExtract.getId()))
                .andExpect(jsonPath("$.ph").value(7.2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract = createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get")
                        .param("saturationExtractAnalysisExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ph").value(7.2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSaturationExtractAnalysisExtractsByRangeSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract = createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(saturationExtractAnalysisExtractRepository.findAllByRangeExtract(ownerRangeExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get-by-range")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ph").value(7.2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSaturationExtractAnalysisExtractsByLayerSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel analysisExtract = createSaturationExtractAnalysisExtractModel(2L, null, ownerLayerExtract);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(ownerLayerExtract.getId())).thenReturn(Optional.of(ownerLayerExtract));
        when(saturationExtractAnalysisExtractRepository.findAllByLayerExtract(ownerLayerExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/saturation-extract-analysis-extract/get-by-layer")
                        .param("layerExtractId", ownerLayerExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].id_extrato_camada").value(ownerLayerExtract.getId()))
                .andExpect(jsonPath("$[0].camada").value(Camada.A.name()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel existingExtract = createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);
        SaturationExtractAnalysisExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(saturationExtractAnalysisExtractRepository.save(any(SaturationExtractAnalysisExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/saturation-extract-analysis-extract/update")
                        .param("saturationExtractAnalysisExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ph").value(7.5))
                .andExpect(jsonPath("$.ce").value(0.6));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSaturationExtractAnalysisExtractSuccessfully() throws Exception {
        SaturationExtractAnalysisExtractModel existingExtract = createSaturationExtractAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(saturationExtractAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(saturationExtractAnalysisExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/saturation-extract-analysis-extract/delete")
                        .param("saturationExtractAnalysisExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}