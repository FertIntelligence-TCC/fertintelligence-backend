package com.migueltcc.fertintelligence.controller;

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
import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
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
public class PlotControllerImplTest extends AbstractControllerTest {

    private static final String OWNER_USERNAME = "testuser";
    private static final String FUNCIONARIO_USERNAME = "funcionario"; // Corrigido
    private static final String MANAGER_USERNAME = "manager";
    private static final String RESIDENTE_USERNAME = "residente";
    private static final String CONSULTOR_USERNAME = "consultor";

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;
    private UserModel residenteUser;
    private UserModel consultorUser;

    private PropertyModel ownerProperty;

    @BeforeEach
    void setUp() {
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username(OWNER_USERNAME)
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username(FUNCIONARIO_USERNAME) // Corrigido
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(4L)
                .username(MANAGER_USERNAME)
                .name("Gerente")
                .cargo(Cargo.GERENTE)
                .build();

        residenteUser = UserModel.builder()
                .id(5L)
                .username(RESIDENTE_USERNAME)
                .name("Agrônomo Residente")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        consultorUser = UserModel.builder()
                .id(6L)
                .username(CONSULTOR_USERNAME)
                .name("Agrônomo Consultor")
                .cargo(Cargo.AGRONOMO_CONSULTOR)
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
                .manager(managerUser)
                .localizacao(new Localizacao(7.11, LatitudeDirection.SUL, 34.86, LongitudeDirection.OESTE, 10.0))
                .build();

        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());

        when(plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(PropertyModel.class), any(PlotModel.class), any(UserModel.class), any(PermissionScope.class),
                any(PermissionType.class), any(AccessRequestStatus.class)
        )).thenReturn(Optional.empty());
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
                .latitude(7.11)
                .latitudeGraus(7)
                .latitudeMinutos(6)
                .latitudeSegundos(36.0)
                .latitudeDirection(LatitudeDirection.SUL)
                .longitude(34.86)
                .longitudeGraus(34)
                .longitudeMinutos(51)
                .longitudeSegundos(36.0)
                .longitudeDirection(LongitudeDirection.OESTE)
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
                .latitude(8.0)
                .latitudeGraus(8)
                .latitudeMinutos(1)
                .latitudeSegundos(12.0)
                .latitudeDirection(LatitudeDirection.SUL)
                .longitude(35.0)
                .longitudeGraus(35)
                .longitudeMinutos(2)
                .longitudeSegundos(24.0)
                .longitudeDirection(LongitudeDirection.OESTE)
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
                .latitude(7.11)
                .latitudeGraus(7)
                .latitudeMinutos(6)
                .latitudeSegundos(36.0)
                .latitudeDirection(LatitudeDirection.SUL)
                .longitude(34.86)
                .longitudeGraus(34)
                .longitudeMinutos(51)
                .longitudeSegundos(36.0)
                .longitudeDirection(LongitudeDirection.OESTE)
                .property(property)
                .build();
    }

    private PropertyAccessRequestModel approvedPropertyAccess(UserModel user, PropertyModel property) {
        return PropertyAccessRequestModel.builder()
                .property(property)
                .requester(user)
                .status(AccessRequestStatus.APPROVED)
                .build();
    }

    private PlotAccessRequestModel approvedPlotAccess(UserModel user, PlotModel plot) {
        return PlotAccessRequestModel.builder()
                .plot(plot)
                .requester(user)
                .status(AccessRequestStatus.APPROVED)
                .build();
    }

    private void stubApprovedPlotAccess(PropertyModel property, PlotModel plot, UserModel requester) {
        when(plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                eq(property), eq(plot), eq(requester), eq(PermissionScope.PLOT),
                any(PermissionType.class), eq(AccessRequestStatus.APPROVED)
        )).thenReturn(Optional.of(approvedPlotAccess(requester, plot)));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createPlotSuccessfully() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();
        PlotModel savedPlot = createPlotModel(1L, requestDto.getIdentification(), ownerProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
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
                .andExpect(jsonPath("$.latitude_graus").value(7))
                .andExpect(jsonPath("$.latitude_minutos").value(6))
                .andExpect(jsonPath("$.latitude_segundos").value(36.0))
                .andExpect(jsonPath("$.longitude_graus").value(34))
                .andExpect(jsonPath("$.longitude_minutos").value(51))
                .andExpect(jsonPath("$.longitude_segundos").value(36.0))
                .andExpect(jsonPath("$.id_propriedade").value(10L));
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME)
    void createPlotAsManagerSuccessfully() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();
        PlotModel savedPlot = createPlotModel(2L, requestDto.getIdentification(), ownerProperty);

        when(userRepository.findByUsername(MANAGER_USERNAME)).thenReturn(Optional.of(managerUser));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));
        when(plotRepository.findByIdentificationAndProperty(requestDto.getIdentification(), ownerProperty))
                .thenReturn(Optional.empty());
        when(plotRepository.save(any(PlotModel.class))).thenReturn(savedPlot);

        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(username = RESIDENTE_USERNAME)
    void createPlotAsResident_ReturnsForbidden() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername(RESIDENTE_USERNAME)).thenReturn(Optional.of(residenteUser));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));

        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = FUNCIONARIO_USERNAME) // Corrigido
    void createPlotFails_WhenUserIsNotProprietario() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();

        when(userRepository.findByUsername(FUNCIONARIO_USERNAME)).thenReturn(Optional.of(funcionarioUser)); // Corrigido
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(ownerProperty));

        // Um Secretário NÃO PODE criar talhão. DEVE dar 403.
        mockMvc.perform(post("/plot/register")
                        .param("propertyId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void createPlotFails_WhenIdentificationAlreadyExists() throws Exception {
        PlotCreateRequestDto requestDto = createCreateRequestDto();
        PlotModel existingPlot = createPlotModel(5L, requestDto.getIdentification(), ownerProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
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
    @WithMockUser(username = OWNER_USERNAME)
    void getPlotSuccessfully() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.latitude_graus").value(7))
                .andExpect(jsonPath("$.longitude_graus").value(34))
                .andExpect(jsonPath("$.id_propriedade").value(10L));
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getPlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .owner(otherProprietarioUser)
                .build();
        PlotModel plot = createPlotModel(1L, "Talhao 01", otherProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getPlotFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/plot/get")
                        .param("plotId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void getPlotsByPropertySuccessfully() throws Exception {
        PlotModel plot1 = createPlotModel(1L, "Talhao 01", ownerProperty);
        PlotModel plot2 = createPlotModel(2L, "Talhao 02", ownerProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
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
    @WithMockUser(username = OWNER_USERNAME)
    void updatePlotSuccessfully() throws Exception {
        PlotModel existingPlot = createPlotModel(1L, "Talhao 01", ownerProperty);
        PlotPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
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
                .andExpect(jsonPath("$.area").value(18.0))
                .andExpect(jsonPath("$.latitude_graus").value(8))
                .andExpect(jsonPath("$.longitude_graus").value(35));
    }

    @Test
    @WithMockUser(username = CONSULTOR_USERNAME)
    void updatePlotAsConsultant_ReturnsForbidden() throws Exception {
        PlotModel existingPlot = createPlotModel(1L, "Talhao 01", ownerProperty);
        PlotPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername(CONSULTOR_USERNAME)).thenReturn(Optional.of(consultorUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(existingPlot));

        stubApprovedPlotAccess(ownerProperty, existingPlot, consultorUser);

        mockMvc.perform(put("/plot/update")
                        .param("plotId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void updatePlotFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel otherProperty = PropertyModel.builder()
                .id(20L)
                .nome("Fazenda Outro Dono")
                .owner(otherProprietarioUser)
                .build();
        PlotModel plot = createPlotModel(1L, "Talhao 01", otherProperty);
        PlotPostRequestDto updateRequestDto = createPostRequestDto();

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        mockMvc.perform(put("/plot/update")
                        .param("plotId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = OWNER_USERNAME)
    void deletePlotSuccessfully() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(proprietarioUser));
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));
        doNothing().when(plotRepository).delete(plot);

        mockMvc.perform(delete("/plot/delete")
                        .param("plotId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = FUNCIONARIO_USERNAME) // Corrigido
    void deletePlotFails_WhenUserIsNotProprietario() throws Exception {
        PlotModel plot = createPlotModel(1L, "Talhao 01", ownerProperty);

        when(userRepository.findByUsername(FUNCIONARIO_USERNAME)).thenReturn(Optional.of(funcionarioUser)); // Corrigido
        when(plotRepository.findById(1L)).thenReturn(Optional.of(plot));

        // O Secretário NÃO PODE deletar talhões, deve retornar 403.
        mockMvc.perform(delete("/plot/delete")
                        .param("plotId", "1"))
                .andExpect(status().isForbidden());
    }
}
