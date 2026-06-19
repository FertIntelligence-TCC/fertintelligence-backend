package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection.SUL;
import static com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection.OESTE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PropertyControllerImplTest extends AbstractControllerTest {

    // --- Entidades Mock ---
    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;
    private UserModel managerUser;
    private UserModel residenteUser;

    @BeforeEach
    void setUp() {
        // Configura o ObjectMapper para suportar Java Time (se necessário)
        objectMapper.registerModule(new JavaTimeModule());

        // Criação de usuários mock
        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario")
                .cargo(Cargo.PROPRIETARIO) // Assume enum Cargo existe
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username("testuser") // Pode ter o mesmo username se o ID for diferente
                .name("Test User Funcionario")
                .cargo(Cargo.SECRETARIO) // Ajustado para refletir outro cargo
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(3L)
                .username("otheruser")
                .name("Other User Proprietario")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        managerUser = UserModel.builder()
                .id(4L)
                .username("manager")
                .name("Manager User")
                .cargo(Cargo.GERENTE)
                .build();

        residenteUser = UserModel.builder()
                .id(5L)
                .username("residente")
                .name("Residente User")
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build();

        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());
    }

    // --- HELPER METHODS ---

    private PropertyCreateRequestDto createCreateRequestDto() {
        LocalizacaoDto localizacaoDto = new LocalizacaoDto(7.11, SUL, 34.86, OESTE, 10.0);
        localizacaoDto.setLatitudeGraus(7);
        localizacaoDto.setLatitudeMinutos(6);
        localizacaoDto.setLatitudeSegundos(36.0);
        localizacaoDto.setLongitudeGraus(34);
        localizacaoDto.setLongitudeMinutos(51);
        localizacaoDto.setLongitudeSegundos(36.0);
        return PropertyCreateRequestDto.builder()
                .nome("Fazenda Santa Clara")
                .cnpj("12345678000199")
                .endereco("Rodovia PB 031, KM 25")
                .localizacao(localizacaoDto)
                .build();
    }

    private PropertyPostRequestDto createPostRequestDto() {
        LocalizacaoDto localizacaoDto = new LocalizacaoDto(7.11, SUL, 34.86, OESTE, 10.0);
        localizacaoDto.setLatitudeGraus(7);
        localizacaoDto.setLatitudeMinutos(6);
        localizacaoDto.setLatitudeSegundos(36.0);
        localizacaoDto.setLongitudeGraus(34);
        localizacaoDto.setLongitudeMinutos(51);
        localizacaoDto.setLongitudeSegundos(36.0);
        return PropertyPostRequestDto.builder()
                .nome("Fazenda Santa Clara")
                .cnpj("12345678000199")
                .endereco("Rodovia PB 031, KM 25")
                .localizacao(localizacaoDto)
                .build();
    }

    private PropertyModel createPropertyModel(Long id, String nome, UserModel owner) {
        return PropertyModel.builder()
                .id(id)
                .nome(nome)
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .localizacao(new Localizacao(7.11, 7, 6, 36.0, SUL, 34.86, 34, 51, 36.0, OESTE, 10.0))
                .owner(owner)
                .build();
    }

    // --- TESTES DE CRIAÇÃO (CREATE) ---
    @Test
    @WithMockUser(username = "testuser") // Simula usuário autenticado
    void createPropertySuccessfully() throws Exception {
        PropertyCreateRequestDto requestDto = createCreateRequestDto();
        PropertyModel savedProperty = createPropertyModel(1L, requestDto.getNome(), proprietarioUser);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.save(any(PropertyModel.class))).thenReturn(savedProperty);

        mockMvc.perform(post("/property/register") // URL: POST /property/register
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara"))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$.localizacao.latitude_graus").value(7))
                .andExpect(jsonPath("$.localizacao.latitude_minutos").value(6))
                .andExpect(jsonPath("$.localizacao.latitude_segundos").value(36.0))
                .andExpect(jsonPath("$.localizacao.longitude_graus").value(34))
                .andExpect(jsonPath("$.localizacao.longitude_minutos").value(51))
                .andExpect(jsonPath("$.localizacao.longitude_segundos").value(36.0))
                .andExpect(jsonPath("$.owner_id").value(1L))
                .andDo(print()); // Imprime detalhes da requisição/resposta
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPropertyFails_WhenUserIsNotProprietario() throws Exception {
        PropertyCreateRequestDto requestDto = createCreateRequestDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser)); // Mock: Usuário NÃO é proprietário

        mockMvc.perform(post("/property/register") // URL: POST /property/register
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden()) // Espera 403 Forbidden (AccessDeniedException)
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPropertyFails_WhenCnpjAlreadyExists() throws Exception {
        PropertyCreateRequestDto requestDto = createCreateRequestDto();
        PropertyModel existingProperty = createPropertyModel(99L, "Outra Fazenda", otherProprietarioUser);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findByNome(requestDto.getNome())).thenReturn(Optional.empty());
        when(propertyRepository.findByCnpj(requestDto.getCnpj())).thenReturn(Optional.of(existingProperty)); // Mock: CNPJ já existe

        mockMvc.perform(post("/property/register") // URL: POST /property/register
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request (EntityExistsException)
                .andDo(print());
    }

    // --- TESTES DE LEITURA (READ) ---
    @Test
    @WithMockUser(username = "testuser")
    void getPropertySuccessfully() throws Exception {
        PropertyModel property = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        mockMvc.perform(get("/property/get") // URL: GET /property/get
                        .param("propertyId", "1")) // Passa o ID como parâmetro
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara"))
                .andExpect(jsonPath("$.localizacao.latitude_graus").value(7))
                .andExpect(jsonPath("$.localizacao.longitude_graus").value(34))
                .andExpect(jsonPath("$.owner_id").value(proprietarioUser.getId()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPropertyFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser); // Propriedade de outro usuário

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther));

        mockMvc.perform(get("/property/get") // URL: GET /property/get
                        .param("propertyId", "1")) // ID como parâmetro
                .andExpect(status().isForbidden()) // Espera 403 Forbidden (checkOwnerPermission falha)
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPropertyFails_WhenNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty()); // Mock: Propriedade não encontrada

        mockMvc.perform(get("/property/get") // URL: GET /property/get
                        .param("propertyId", "99")) // ID como parâmetro
                .andExpect(status().isNotFound()) // Espera 404 Not Found (EntityNotFoundException)
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyPropertiesSuccessfully() throws Exception {
        PropertyModel property1 = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);
        PropertyModel property2 = createPropertyModel(2L, "Fazenda Boa Esperança", proprietarioUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findAllByOwner(proprietarioUser)).thenReturn(List.of(property1, property2)); // Mock: Retorna lista de propriedades

        mockMvc.perform(get("/property/get-my-properties")) // URL: GET /property/get-my-properties
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Espera 2 itens na lista
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andDo(print());
    }

    // --- TESTES DE ATUALIZAÇÃO (UPDATE) ---
    @Test
    @WithMockUser(username = "testuser")
    void updatePropertySuccessfully() throws Exception {
        PropertyModel originalProperty = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);
        PropertyPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setNome("Fazenda Santa Clara (Atualizada)");
        String requestBody = objectMapper.writeValueAsString(updateRequestDto);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(originalProperty));
        when(propertyRepository.findByNome(anyString())).thenReturn(Optional.empty()); // Assume que o novo nome não conflita
        when(propertyRepository.save(any(PropertyModel.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o objeto modificado

        mockMvc.perform(put("/property/update") // URL: PUT /property/update
                        .param("propertyId", "1") // ID como parâmetro
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara (Atualizada)"))
                .andExpect(jsonPath("$.localizacao.latitude_graus").value(7))
                .andExpect(jsonPath("$.localizacao.longitude_graus").value(34))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "manager")
    void updatePropertyAsManager() throws Exception {
        PropertyModel originalProperty = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);
        originalProperty.setManager(managerUser);

        PropertyPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setNome("Fazenda Santa Clara (Gerente)");

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(managerUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(originalProperty));
        when(propertyRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.save(any(PropertyModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/property/update")
                        .param("propertyId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara (Gerente)"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePropertyFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser);
        PropertyPostRequestDto updateRequestDto = createPostRequestDto();
        updateRequestDto.setNome("Tentativa de Hack");
        String requestBody = objectMapper.writeValueAsString(updateRequestDto);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther)); // Mock: Propriedade de outro dono

        mockMvc.perform(put("/property/update") // URL: PUT /property/update
                        .param("propertyId", "1") // ID como parâmetro
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden()) // Espera 403 Forbidden (checkOwnerPermission falha)
                .andDo(print());
    }

    // --- TESTES DE EXCLUSÃO (DELETE) ---
    @Test
    @WithMockUser(username = "testuser")
    void deletePropertySuccessfully() throws Exception {
        PropertyModel property = createPropertyModel(1L, "Fazenda a Deletar", proprietarioUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        doNothing().when(propertyRepository).delete(property); // Mock: delete não faz nada

        mockMvc.perform(delete("/property/delete") // URL: DELETE /property/delete
                        .param("propertyId", "1")) // ID como parâmetro
                .andExpect(status().isNoContent()) // Espera 204 No Content
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "residente")
    void deletePropertyWithResidentApproval() throws Exception {
        PropertyModel property = createPropertyModel(1L, "Fazenda a Deletar", proprietarioUser);

        when(userRepository.findByUsername("residente")).thenReturn(Optional.of(residenteUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, residenteUser, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel.builder()
                        .property(property)
                        .requester(residenteUser)
                        .status(AccessRequestStatus.APPROVED)
                        .build()));
        mockMvc.perform(delete("/property/delete")
                        .param("propertyId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePropertyFails_WhenUserIsNotOwner() throws Exception {
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther)); // Mock: Propriedade de outro dono

        mockMvc.perform(delete("/property/delete") // URL: DELETE /property/delete
                        .param("propertyId", "1")) // ID como parâmetro
                .andExpect(status().isForbidden()) // Espera 403 Forbidden (checkOwnerPermission falha)
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePropertyFails_WhenUserRoleIsNotAllowed() throws Exception {
        UserModel supervisorUser = funcionarioUser.builder()
                .cargo(Cargo.SUPERVISOR_DE_AREA)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(supervisorUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(createPropertyModel(1L, "Dummy", proprietarioUser)));

        mockMvc.perform(delete("/property/delete")
                        .param("propertyId", "1"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
