package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.composedAttributes.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.Localizacao;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.migueltcc.fertintelligence.composedAttributes.LatitudeDirection.SUL;
import static com.migueltcc.fertintelligence.composedAttributes.LongitudeDirection.OESTE;
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
@TestPropertySource(locations = "classpath:application-test.properties")
public class PropertyControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyRepository propertyRepository;

    @MockitoBean
    private UserRepository userRepository;

    // --- Entidades Mock ---
    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;

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
    }

    // --- HELPER METHODS ---

    private PropertyCreateRequestDto createCreateRequestDto() {
        LocalizacaoDto localizacaoDto = new LocalizacaoDto(7.11, SUL, 34.86, OESTE, 10.0);
        return PropertyCreateRequestDto.builder()
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .localizacao(localizacaoDto)
                .build();
    }

    private PropertyPostRequestDto createPostRequestDto() {
        LocalizacaoDto localizacaoDto = new LocalizacaoDto(7.11, SUL, 34.86, OESTE, 10.0);
        return PropertyPostRequestDto.builder()
                .nome("Fazenda Santa Clara")
                .cnpj("12.345.678/0001-99")
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
                .localizacao(new Localizacao(7.11, SUL, 34.86, OESTE, 10.0))
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
                .andDo(print());
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
    void deletePropertyFails_WhenUserIsNotProprietario() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser)); // Mock: Usuário NÃO é proprietário
        // Adicionamos um mock para findById para evitar NullPointerException caso o service
        // tente buscar a propriedade antes de verificar o cargo do usuário.
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(createPropertyModel(1L, "Dummy", proprietarioUser)));

        mockMvc.perform(delete("/property/delete") // URL: DELETE /property/delete
                        .param("propertyId", "1")) // ID como parâmetro
                .andExpect(status().isForbidden()) // Espera 403 Forbidden (checkUserIsProprietario falha)
                .andDo(print());
    }
}