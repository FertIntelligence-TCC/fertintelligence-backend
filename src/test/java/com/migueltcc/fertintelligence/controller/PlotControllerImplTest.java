package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PlotControllerImplTest extends AbstractControllerTest {

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;
    private PropertyModel ownerProperty;

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
    }

    private PlotCreateRequestDto createCreateRequestDto() {
        return PlotCreateRequestDto.builder()
                .identification("Talhao 01")
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

    private PlotPostRequestDto createPostRequestDto() {
        return PlotPostRequestDto.builder()
                .identification("Talhao Atualizado")
                .area(18.0)
                .soilClass(ClasseSolo.LATOSSOLO)
                .soilTexture(TexturaSolo.ARGILA)
                .cropIncorporationYear(2022)
                .irrigatedArea(AreaIrrigada.NAO)
                .declivity(8.0)
                .monthlyPluviosity(250.0)
                .annualPluviosity(1500.0)
                .build();
    }

    private PlotModel createPlotModel(Long id, String identification, PropertyModel property) {
        return PlotModel.builder()
                .id(id)
                .identification(identification)
                .area(15.0)
                .soilClass(ClasseSolo.ARGISSOLO)
                .soilTexture(TexturaSolo.FRANCO_ARGILOSO_ARENOSA)
                .cropIncorporationYear(2020)
                .irrigatedArea(AreaIrrigada.SIM)
                .declivity(5.0)
                .monthlyPluviosity(200.0)
                .annualPluviosity(1200.0)
                .property(property)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPlotSuccessfully() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();
        PlotModel savedPlot = createPlotModel(1L, requestDto.getIdentification(), ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));
        when(plotRepository.findByIdentificationAndProperty(requestDto.getIdentification(), ownerProperty))
                .thenReturn(Optional.empty());
        when(plotRepository.save(any(PlotModel.class))).thenReturn(savedPlot);

        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.identificacao").value("Talhao 01"))
                .andExpect(jsonPath("$.id_propriedade").value(10L))
                .andExpect(jsonPath("$.nome_propriedade").value("Fazenda Santa Clara"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPlotFails_WhenUserIsNotProprietario() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPlotFails_WhenIdentificationAlreadyExists() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();
        PlotModel existingPlot = createPlotModel(5L, requestDto.getIdentification(), ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));
        when(plotRepository.findByIdentificationAndProperty(requestDto.getIdentification(), ownerProperty))
                .thenReturn(Optional.of(existingPlot));

        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlotSuccessfully() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.id_propriedade").value(10L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .cnpj("98.765.432/0001-99")
                .endereco("Rua das Palmeiras, 50")
                .owner(otherProprietarioUser)
                .localizacao(new Localizacao(8.0, LatitudeDirection.NORTE, 35.0, LongitudeDirection.LESTE, 50.0))
                .build();
        PlotModel plot = createPlotModel(1L, "Talhao 01", otherProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlotFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlotsByPropertySuccessfully() throws Exception {
        PlotModel plot1 = createPlotModel(1L, "Talhao 01", ownerProperty);
        PlotModel plot2 = createPlotModel(2L, "Talhao 02", ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));
        when(plotRepository.findAllByProperty(ownerProperty)).thenReturn(List.of(plot1, plot2));

        mockMvc.perform(get("/plot/get-by-property")
                        .param("propertyId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePlotSuccessfully() throws Exception {
        PlotModel existingPlot = createPlotModel(1L, "Talhao 01", ownerProperty);
        PlotPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(existingPlot));
        when(plotRepository.findByIdentificationAndProperty(updateRequestDto.getIdentification(), ownerProperty))
                .thenReturn(Optional.empty());
        when(plotRepository.save(any(PlotModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/plot/update")
                        .param("plotId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.identificacao").value("Talhao Atualizado"))
                .andExpect(jsonPath("$.area").value(18.0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .cnpj("98.765.432/0001-99")
                .endereco("Rua das Palmeiras, 50")
                .owner(otherProprietarioUser)
                .localizacao(new Localizacao(8.0, LatitudeDirection.NORTE, 35.0, LongitudeDirection.LESTE, 50.0))
                .build();
        PlotModel plot = createPlotModel(1L, "Talhao 01", otherProperty);
        PlotPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(put("/plot/update")
                        .param("plotId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePlotSuccessfully() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));
        doNothing().when(plotRepository).delete(plot);

        mockMvc.perform(delete("/plot/delete")
                        .param("plotId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePlotFails_WhenUserIsNotProprietario() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(delete("/plot/delete")
                        .param("plotId", "1"))
                .andExpect(status().isForbidden());
    }
}