package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
public class FertilityAnalysisExtractControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel managerUser;
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

        managerUser = UserModel.builder()
                .id(2L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        ownerProperty = PropertyModel.builder()
                .id(10L)
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .owner(proprietarioUser)
                .manager(managerUser)
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

    private FertilityAnalysisExtractCreateRequestDto createCreateRequestDto() {
        return FertilityAnalysisExtractCreateRequestDto.builder()
                .phAgua(5.6)
                .phCacl2(5.2)
                .calcio(40.0)
                .magnesio(20.0)
                .potassio(3.5)
                .sodio(2.4)
                .aluminio(0.5)
                .aluminioMaisHidrogenio(4.2)
                .somaBases(60.0)
                .ctcEfetiva(70.0)
                .ctcPh7(80.0)
                .saturacaoBasesV(75.0)
                .saturacaoAluminioM(10.0)
                .fosforoMehlich1(15.0)
                .fosforoResina(18.0)
                .enxofre(22.0)
                .materiaOrganica(3.2)
                .boro(0.35)
                .cobre(1.8)
                .ferro(25.0)
                .manganes(12.0)
                .molibdenio(0.1)
                .zinco(3.4)
                .build();
    }

    private FertilityAnalysisExtractPostRequestDto createUpdateRequestDto() {
        return FertilityAnalysisExtractPostRequestDto.builder()
                .phAgua(6.0)
                .somaBases(65.0)
                .build();
    }

    private FertilityAnalysisExtractModel createFertilityAnalysisExtractModel(Long id,
                                                                              RangeExtractModel rangeExtract,
                                                                              LayerExtractModel layerExtract) {
        return FertilityAnalysisExtractModel.builder()
                .id(id)
                .rangeExtract(rangeExtract)
                .layerExtract(layerExtract)
                .phAgua(5.6)
                .phCacl2(5.2)
                .calcio(40.0)
                .magnesio(20.0)
                .potassio(3.5)
                .sodio(2.4)
                .aluminio(0.5)
                .aluminioMaisHidrogenio(4.2)
                .somaBases(60.0)
                .ctcEfetiva(70.0)
                .ctcPh7(80.0)
                .saturacaoBasesV(75.0)
                .saturacaoAluminioM(10.0)
                .fosforoMehlich1(15.0)
                .fosforoResina(18.0)
                .enxofre(22.0)
                .materiaOrganica(3.2)
                .boro(0.35)
                .cobre(1.8)
                .ferro(25.0)
                .manganes(12.0)
                .molibdenio(0.1)
                .zinco(3.4)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFertilityAnalysisExtractWithRangeSuccessfully() throws Exception {
        FertilityAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        FertilityAnalysisExtractModel savedExtract = createFertilityAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(fertilityAnalysisExtractRepository.save(any(FertilityAnalysisExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/fertility-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/fertility-analysis-extract/get?fertilityAnalysisExtractId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_extrato_intervalo").value(ownerRangeExtract.getId()))
                .andExpect(jsonPath("$.profundidade_inicial").value(0))
                .andExpect(jsonPath("$.ph_agua").value(5.6));
    }

    @Test
    @WithMockUser(username = "manager")
    void createFertilityAnalysisExtractWithRangeAsManager() throws Exception {
        FertilityAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        FertilityAnalysisExtractModel savedExtract = createFertilityAnalysisExtractModel(2L, ownerRangeExtract, null);

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(fertilityAnalysisExtractRepository.save(any(FertilityAnalysisExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/fertility-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFertilityAnalysisExtractSuccessfully() throws Exception {
        FertilityAnalysisExtractModel analysisExtract = createFertilityAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(fertilityAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(analysisExtract));

        mockMvc.perform(get("/fertility-analysis-extract/get")
                        .param("fertilityAnalysisExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ph_agua").value(5.6));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFertilityAnalysisExtractsByRangeSuccessfully() throws Exception {
        FertilityAnalysisExtractModel analysisExtract = createFertilityAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(fertilityAnalysisExtractRepository.findAllByRangeExtract(ownerRangeExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/fertility-analysis-extract/get-by-range")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].id_extrato_intervalo").value(ownerRangeExtract.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFertilityAnalysisExtractsByLayerSuccessfully() throws Exception {
        FertilityAnalysisExtractModel analysisExtract = createFertilityAnalysisExtractModel(2L, null, ownerLayerExtract);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(ownerLayerExtract.getId())).thenReturn(Optional.of(ownerLayerExtract));
        when(fertilityAnalysisExtractRepository.findAllByLayerExtract(ownerLayerExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/fertility-analysis-extract/get-by-layer")
                        .param("layerExtractId", ownerLayerExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].id_extrato_camada").value(ownerLayerExtract.getId()))
                .andExpect(jsonPath("$[0].camada").value(Camada.A.name()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateFertilityAnalysisExtractSuccessfully() throws Exception {
        FertilityAnalysisExtractModel existingExtract = createFertilityAnalysisExtractModel(1L, ownerRangeExtract, null);
        FertilityAnalysisExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(fertilityAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(fertilityAnalysisExtractRepository.save(any(FertilityAnalysisExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/fertility-analysis-extract/update")
                        .param("fertilityAnalysisExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ph_agua").value(6.0))
                .andExpect(jsonPath("$.soma_bases").value(65.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteFertilityAnalysisExtractSuccessfully() throws Exception {
        FertilityAnalysisExtractModel existingExtract = createFertilityAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(fertilityAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(fertilityAnalysisExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/fertility-analysis-extract/delete")
                        .param("fertilityAnalysisExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}