package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.closeTo;
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
public class PhysicalAnalysisExtractControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private PropertyModel ownerProperty;
    private UserModel managerUser;
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
                .name("Gerente")
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

    private PhysicalAnalysisExtractCreateRequestDto createCreateRequestDto() {
        return PhysicalAnalysisExtractCreateRequestDto.builder()
                .teorAreia(450.0)
                .unidadeTeorAreia(PhysicalAnalysisUnit.G_PER_KG)
                .teorSilte(200.0)
                .unidadeTeorSilte(PhysicalAnalysisUnit.G_PER_KG)
                .teorArgila(350.0)
                .unidadeTeorArgila(PhysicalAnalysisUnit.G_PER_KG)
                .densidadeAparente(1.2)
                .unidadeDensidadeAparente(PhysicalAnalysisUnit.G_PER_KG)
                .densidadeReal(2.6)
                .unidadeDensidadeReal(PhysicalAnalysisUnit.G_PER_KG)
                .porosidadeTotal(45.0)
                .microporosidade(30.0)
                .umidadeCapacidadeCampo(28.0)
                .umidadePontoMurchaPermanente(15.0)
                .aguaDisponivel(13.0)
                .resistenciaPenetracao(2.1)
                .percAgregados6_0mm(5.0)
                .percAgregados4_1a6_0mm(10.0)
                .percAgregados2_1a4_0mm(15.0)
                .percAgregados1_0a2_0mm(20.0)
                .percAgregados0_5a1_0mm(20.0)
                .percAgregados0_25a0_5mm(15.0)
                .percAgregadosMenor0_25mm(15.0)
                .build();
    }

    private PhysicalAnalysisExtractPostRequestDto createUpdateRequestDto() {
        return PhysicalAnalysisExtractPostRequestDto.builder()
                .teorAreia(500.0)
                .unidadeTeorAreia(PhysicalAnalysisUnit.G_PER_KG)
                .densidadeAparente(1.3)
                .unidadeDensidadeAparente(PhysicalAnalysisUnit.G_PER_KG)
                .densidadeReal(2.5)
                .unidadeDensidadeReal(PhysicalAnalysisUnit.G_PER_KG)
                .porosidadeTotal(999.0)
                .umidadeCapacidadeCampo(30.0)
                .umidadePontoMurchaPermanente(16.0)
                .aguaDisponivel(999.0)
                .build();
    }

    private PhysicalAnalysisExtractModel createPhysicalAnalysisExtractModel(Long id,
                                                                            RangeExtractModel rangeExtract,
                                                                            LayerExtractModel layerExtract) {
        return PhysicalAnalysisExtractModel.builder()
                .id(id)
                .rangeExtract(rangeExtract)
                .layerExtract(layerExtract)
                .teorAreia(450.0)
                .unidadeTeorAreia(PhysicalAnalysisUnit.G_PER_DM3)
                .teorSilte(200.0)
                .unidadeTeorSilte(PhysicalAnalysisUnit.G_PER_DM3)
                .teorArgila(350.0)
                .unidadeTeorArgila(PhysicalAnalysisUnit.G_PER_DM3)
                .densidadeAparente(1.2)
                .unidadeDensidadeAparente(PhysicalAnalysisUnit.G_PER_DM3)
                .densidadeReal(2.6)
                .unidadeDensidadeReal(PhysicalAnalysisUnit.G_PER_DM3)
                .porosidadeTotal(45.0)
                .microporosidade(30.0)
                .umidadeCapacidadeCampo(28.0)
                .umidadePontoMurchaPermanente(15.0)
                .aguaDisponivel(13.0)
                .resistenciaPenetracao(2.1)
                .percAgregados6_0mm(5.0)
                .percAgregados4_1a6_0mm(10.0)
                .percAgregados2_1a4_0mm(15.0)
                .percAgregados1_0a2_0mm(20.0)
                .percAgregados0_5a1_0mm(20.0)
                .percAgregados0_25a0_5mm(15.0)
                .percAgregadosMenor0_25mm(15.0)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPhysicalAnalysisExtractWithRangeSuccessfully() throws Exception {
        PhysicalAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        requestDto.setPorosidadeTotal(999.0);
        requestDto.setAguaDisponivel(999.0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(physicalAnalysisExtractRepository.save(any(PhysicalAnalysisExtractModel.class))).thenAnswer(invocation -> {
            PhysicalAnalysisExtractModel savedExtract = invocation.getArgument(0);
            savedExtract.setId(1L);
            return savedExtract;
        });

        mockMvc.perform(post("/physical-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/physical-analysis-extract/get?physicalAnalysisExtractId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_extrato_intervalo").value(ownerRangeExtract.getId()))
                .andExpect(jsonPath("$.teor_areia").value(450.0))
                .andExpect(jsonPath("$.unidade_teor_areia").value("g/kg"))
                .andExpect(jsonPath("$.unidade_teor_silte").value("g/kg"))
                .andExpect(jsonPath("$.unidade_teor_argila").value("g/kg"))
                .andExpect(jsonPath("$.unidade_densidade_aparente").value("g/kg"))
                .andExpect(jsonPath("$.unidade_densidade_real").value("g/kg"))
                .andExpect(jsonPath("$.porosidade_total", closeTo(53.84615384615385, 0.000001)))
                .andExpect(jsonPath("$.agua_disponivel").value(13.0));
    }

    @Test
    @WithMockUser(username = "manager")
    void createPhysicalAnalysisExtractAsManagerSuccessfully() throws Exception {
        PhysicalAnalysisExtractCreateRequestDto requestDto = createCreateRequestDto();
        PhysicalAnalysisExtractModel savedExtract = createPhysicalAnalysisExtractModel(3L, ownerRangeExtract, null);

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(physicalAnalysisExtractRepository.save(any(PhysicalAnalysisExtractModel.class))).thenReturn(savedExtract);

        mockMvc.perform(post("/physical-analysis-extract/register")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPhysicalAnalysisExtractSuccessfully() throws Exception {
        PhysicalAnalysisExtractModel analysisExtract = createPhysicalAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(physicalAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(analysisExtract));

        mockMvc.perform(get("/physical-analysis-extract/get")
                        .param("physicalAnalysisExtractId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teor_areia").value(450.0))
                .andExpect(jsonPath("$.unidade_teor_areia").value("g/kg"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPhysicalAnalysisExtractsByRangeSuccessfully() throws Exception {
        PhysicalAnalysisExtractModel analysisExtract = createPhysicalAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(rangeExtractRepository.findById(ownerRangeExtract.getId())).thenReturn(Optional.of(ownerRangeExtract));
        when(physicalAnalysisExtractRepository.findAllByRangeExtract(ownerRangeExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/physical-analysis-extract/get-by-range")
                        .param("rangeExtractId", ownerRangeExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].teor_areia").value(450.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPhysicalAnalysisExtractsByLayerSuccessfully() throws Exception {
        PhysicalAnalysisExtractModel analysisExtract = createPhysicalAnalysisExtractModel(2L, null, ownerLayerExtract);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(layerExtractRepository.findById(ownerLayerExtract.getId())).thenReturn(Optional.of(ownerLayerExtract));
        when(physicalAnalysisExtractRepository.findAllByLayerExtract(ownerLayerExtract)).thenReturn(List.of(analysisExtract));

        mockMvc.perform(get("/physical-analysis-extract/get-by-layer")
                        .param("layerExtractId", ownerLayerExtract.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].id_extrato_camada").value(ownerLayerExtract.getId()))
                .andExpect(jsonPath("$[0].camada").value(Camada.A.name()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePhysicalAnalysisExtractSuccessfully() throws Exception {
        PhysicalAnalysisExtractModel existingExtract = createPhysicalAnalysisExtractModel(1L, ownerRangeExtract, null);
        PhysicalAnalysisExtractPostRequestDto updateRequestDto = createUpdateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(physicalAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        when(physicalAnalysisExtractRepository.save(any(PhysicalAnalysisExtractModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/physical-analysis-extract/update")
                        .param("physicalAnalysisExtractId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teor_areia").value(500.0))
                .andExpect(jsonPath("$.unidade_teor_areia").value("g/kg"))
                .andExpect(jsonPath("$.unidade_densidade_aparente").value("g/kg"))
                .andExpect(jsonPath("$.unidade_densidade_real").value("g/kg"))
                .andExpect(jsonPath("$.porosidade_total").value(48.0))
                .andExpect(jsonPath("$.agua_disponivel").value(14.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePhysicalAnalysisExtractSuccessfully() throws Exception {
        PhysicalAnalysisExtractModel existingExtract = createPhysicalAnalysisExtractModel(1L, ownerRangeExtract, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(physicalAnalysisExtractRepository.findById(1L)).thenReturn(Optional.of(existingExtract));
        doNothing().when(physicalAnalysisExtractRepository).delete(existingExtract);

        mockMvc.perform(delete("/physical-analysis-extract/delete")
                        .param("physicalAnalysisExtractId", "1"))
                .andExpect(status().isNoContent());
    }
}
