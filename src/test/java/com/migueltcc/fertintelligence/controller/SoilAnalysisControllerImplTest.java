package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
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
public class SoilAnalysisControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;

    private PropertyModel ownerProperty;
    private PropertyModel otherProperty;

    private PlotModel ownerPlot;
    private PlotModel otherPlot;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username("testuser")
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
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

        when(plotAccessRequestRepository.findByPlotAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());

    }

    private SoilAnalysisCreateRequestDto createCreateRequestDto() {
        return SoilAnalysisCreateRequestDto.builder()
                .analysisYear(2024)
                .responsibleLaboratory("Laboratório X")
                .extractType(TipoExtrato.CAMADAS)
                .plotId(ownerPlot.getId())
                .plotIdentification(ownerPlot.getIdentification())
                .build();
    }

    private SoilAnalysisPostRequestDto createPostRequestDto() {
        return SoilAnalysisPostRequestDto.builder()
                .analysisYear(2025)
                .responsibleLaboratory("Laboratório Y")
                .extractType(TipoExtrato.INTERVALOS)
                .build();
    }

    private SoilAnalysisModel createSoilAnalysisModel(Long id, Integer year, PlotModel plot) {
        return SoilAnalysisModel.builder()
                .id(id)
                .analysisYear(year)
                .responsibleLaboratory("Laboratório X")
                .extractType(TipoExtrato.CAMADAS)
                .plot(plot)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilAnalysisSuccessfully() throws Exception {
        SoilAnalysisCreateRequestDto requestDto = createCreateRequestDto();
        SoilAnalysisModel savedAnalysis = SoilAnalysisModel.builder()
                .id(1L)
                .analysisYear(requestDto.getAnalysisYear())
                .responsibleLaboratory(requestDto.getResponsibleLaboratory())
                .extractType(requestDto.getExtractType())
                .plot(ownerPlot)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(soilAnalysisRepository.findByPlotAndAnalysisYear(ownerPlot, requestDto.getAnalysisYear()))
                .thenReturn(Optional.empty());
        when(soilAnalysisRepository.save(any(SoilAnalysisModel.class))).thenReturn(savedAnalysis);

        mockMvc.perform(post("/soil-analysis/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/soil-analysis/get?analysisId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_analise").value(2024))
                .andExpect(jsonPath("$.laboratorio_responsavel").value("Laboratório X"))
                .andExpect(jsonPath("$.tipo_extrato").value("CAMADAS"))
                .andExpect(jsonPath("$.id_talhao").value(100L))
                .andExpect(jsonPath("$.identificacao_talhao").value("Talhao 01"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilAnalysisFails_WhenUserIsNotProprietario() throws Exception {
        SoilAnalysisCreateRequestDto requestDto = createCreateRequestDto();

        // Garante que o usuário NÃO tem um cargo permitido (não PROPRIETARIO, GERENTE ou SECRETARIO)
        funcionarioUser.setCargo(Cargo.AGRONOMO_CONSULTOR);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(funcionarioUser));

        mockMvc.perform(post("/soil-analysis/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "testuser")
    void createSoilAnalysisFails_WhenPlotBelongsToAnotherOwner() throws Exception {
        SoilAnalysisCreateRequestDto requestDto = SoilAnalysisCreateRequestDto.builder()
                .analysisYear(2024)
                .responsibleLaboratory("Laboratório X")
                .extractType(TipoExtrato.CAMADAS)
                .plotId(otherPlot.getId())
                .plotIdentification(otherPlot.getIdentification())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(otherPlot.getId())).thenReturn(Optional.of(otherPlot));

        mockMvc.perform(post("/soil-analysis/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSoilAnalysisFails_WhenDuplicateYear() throws Exception {
        SoilAnalysisCreateRequestDto requestDto = createCreateRequestDto();
        SoilAnalysisModel existing = createSoilAnalysisModel(2L, requestDto.getAnalysisYear(), ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(soilAnalysisRepository.findByPlotAndAnalysisYear(ownerPlot, requestDto.getAnalysisYear()))
                .thenReturn(Optional.of(existing));

        mockMvc.perform(post("/soil-analysis/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilAnalysisSuccessfully() throws Exception {
        SoilAnalysisModel analysis = createSoilAnalysisModel(1L, 2024, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(get("/soil-analysis/get")
                        .param("analysisId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_analise").value(2024))
                .andExpect(jsonPath("$.id_talhao").value(100L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilAnalysisFails_WhenNotOwner() throws Exception {
        SoilAnalysisModel analysis = createSoilAnalysisModel(1L, 2024, otherPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(get("/soil-analysis/get")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilAnalysisFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/soil-analysis/get")
                        .param("analysisId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSoilAnalysesByPlotSuccessfully() throws Exception {
        SoilAnalysisModel analysis1 = createSoilAnalysisModel(1L, 2023, ownerPlot);
        SoilAnalysisModel analysis2 = createSoilAnalysisModel(2L, 2024, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(ownerPlot.getId())).thenReturn(Optional.of(ownerPlot));
        when(soilAnalysisRepository.findAllByPlot(ownerPlot)).thenReturn(List.of(analysis1, analysis2));

        mockMvc.perform(get("/soil-analysis/get-by-plot")
                        .param("plotId", String.valueOf(ownerPlot.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ano_analise").value(2023))
                .andExpect(jsonPath("$[1].ano_analise").value(2024));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSoilAnalysisSuccessfully() throws Exception {
        SoilAnalysisModel existing = createSoilAnalysisModel(1L, 2023, ownerPlot);
        SoilAnalysisPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(soilAnalysisRepository.findByPlotAndAnalysisYear(ownerPlot, updateRequestDto.getAnalysisYear()))
                .thenReturn(Optional.empty());
        when(soilAnalysisRepository.save(any(SoilAnalysisModel.class))).thenAnswer(invocation -> {
            SoilAnalysisModel toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });

        mockMvc.perform(put("/soil-analysis/update")
                        .param("analysisId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ano_analise").value(2025))
                .andExpect(jsonPath("$.laboratorio_responsavel").value("Laboratório Y"))
                .andExpect(jsonPath("$.tipo_extrato").value("INTERVALOS"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSoilAnalysisFails_WhenDuplicateYear() throws Exception {
        SoilAnalysisModel existing = createSoilAnalysisModel(1L, 2023, ownerPlot);
        SoilAnalysisPostRequestDto updateRequestDto = SoilAnalysisPostRequestDto.builder()
                .analysisYear(2024)
                .build();
        SoilAnalysisModel conflicting = createSoilAnalysisModel(2L, 2024, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(soilAnalysisRepository.findByPlotAndAnalysisYear(ownerPlot, updateRequestDto.getAnalysisYear()))
                .thenReturn(Optional.of(conflicting));

        mockMvc.perform(put("/soil-analysis/update")
                        .param("analysisId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSoilAnalysisSuccessfully() throws Exception {
        SoilAnalysisModel analysis = createSoilAnalysisModel(1L, 2024, ownerPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));
        doNothing().when(soilAnalysisRepository).delete(analysis);

        mockMvc.perform(delete("/soil-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSoilAnalysisFails_WhenNotOwner() throws Exception {
        SoilAnalysisModel analysis = createSoilAnalysisModel(1L, 2024, otherPlot);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(analysis));

        mockMvc.perform(delete("/soil-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteSoilAnalysisFails_WhenUserIsNotProprietario() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));
        when(soilAnalysisRepository.findById(1L)).thenReturn(Optional.of(createSoilAnalysisModel(1L, 2024, ownerPlot)));

        mockMvc.perform(delete("/soil-analysis/delete")
                        .param("analysisId", "1"))
                .andExpect(status().isForbidden());
    }
}