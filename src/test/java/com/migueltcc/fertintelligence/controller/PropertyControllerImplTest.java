package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.composedAtributes.Cargo;
import com.migueltcc.fertintelligence.composedAtributes.Localizacao;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyUpdateRequestDto;
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
// Importe a anotação correta
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.frontend.url=http://dummy-test-url.com",
        "jwt.public.key=classpath:public.pem",
        "jwt.private.key=classpath:private.pem"
})
// --- FIM DA CORREÇÃO ---
@ExtendWith(MockitoExtension.class)
public class PropertyControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyRepository propertyRepository;

    @MockitoBean
    private UserRepository userRepository;

    private UserModel proprietarioUser;
    private UserModel funcionarioUser;
    private UserModel otherProprietarioUser;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        proprietarioUser = UserModel.builder()
                .id(1L)
                .username("testuser")
                .name("Test User Proprietario") // <-- NOME ADICIONADO
                .cargo(Cargo.PROPRIETARIO)
                .build();

        funcionarioUser = UserModel.builder()
                .id(2L)
                .username("testuser") // Pode ter o mesmo username se o ID for diferente
                .name("Test User Funcionario") // <-- NOME ADICIONADO
                .cargo(Cargo.SECRETARIO)
                .build();

        otherProprietarioUser = UserModel.builder()
                .id(3L)
                .username("otheruser")
                .name("Other User Proprietario") // <-- NOME ADICIONADO
                .cargo(Cargo.PROPRIETARIO)
                .build();
    }

    // --- HELPER METHODS ---

    private PropertyPostRequestDto createPostRequestDto() {
        LocalizacaoDto localizacaoDto = new LocalizacaoDto(-7.11, -34.86, 10.0);
        PropertyPostRequestDto requestDto = new PropertyPostRequestDto();
        requestDto.setNome("Fazenda Santa Clara");
        requestDto.setCnpj("12.345.678/0001-99");
        requestDto.setEndereco("Rodovia PB 031, KM 25");
        requestDto.setLocalizacao(localizacaoDto);
        return requestDto;
    }

    private PropertyModel createPropertyModel(Long id, String nome, UserModel owner) {
        return PropertyModel.builder()
                .id(id)
                .nome(nome)
                .cnpj("12.345.678/0001-99")
                .endereco("Rodovia PB 031, KM 25")
                .localizacao(new Localizacao(-7.11, -34.86, 10.0))
                .owner(owner)
                .build();
    }

    // --- TESTES DE CRIAÇÃO (CREATE) ---

    @Test
    @WithMockUser(username = "testuser")
    void createPropertySuccessfully() throws Exception {
        // Arrange
        PropertyPostRequestDto requestDto = createPostRequestDto();
        PropertyModel savedProperty = createPropertyModel(1L, requestDto.getNome(), proprietarioUser);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.findByNome(anyString())).thenReturn(Optional.empty());
        when(propertyRepository.save(any(PropertyModel.class))).thenReturn(savedProperty);

        // Act & Assert
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara"))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-99"))
                .andExpect(jsonPath("$.ownerId").value(1L))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPropertyFails_WhenUserIsNotProprietario() throws Exception {
        // Arrange
        PropertyPostRequestDto requestDto = createPostRequestDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Mock: Usuário é FUNCIONARIO, não PROPRIETARIO
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        // Act & Assert
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden()) // AccessDeniedException
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPropertyFails_WhenCnpjAlreadyExists() throws Exception {
        // Arrange
        PropertyPostRequestDto requestDto = createPostRequestDto();
        PropertyModel existingProperty = createPropertyModel(99L, "Outra Fazenda", otherProprietarioUser);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        // Mock: CNPJ já existe
        when(propertyRepository.findByCnpj(requestDto.getCnpj())).thenReturn(Optional.of(existingProperty));

        // Act & Assert
        mockMvc.perform(post("/api/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()) // EntityExistsException
                .andDo(print());
    }

    // --- TESTES DE LEITURA (READ) ---

    @Test
    @WithMockUser(username = "testuser")
    void getPropertyByIdSuccessfully() throws Exception {
        // Arrange
        PropertyModel property = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        // Act & Assert
        mockMvc.perform(get("/api/v1/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara"))
                .andExpect(jsonPath("$.ownerId").value(proprietarioUser.getId()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPropertyByIdFails_WhenUserIsNotOwner() throws Exception {
        // Arrange
        // Propriedade pertence a 'otherProprietarioUser' (ID 3)
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser);

        // Mocks
        // O usuário logado é 'proprietarioUser' (ID 1)
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther));

        // Act & Assert
        mockMvc.perform(get("/api/v1/properties/1"))
                .andExpect(status().isForbidden()) // AccessDeniedException
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPropertyByIdFails_WhenNotFound() throws Exception {
        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty()); // Não encontrado

        // Act & Assert
        mockMvc.perform(get("/api/v1/properties/99"))
                .andExpect(status().isNotFound()) // EntityNotFoundException
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyPropertiesSuccessfully() throws Exception {
        // Arrange
        PropertyModel property1 = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);
        PropertyModel property2 = createPropertyModel(2L, "Fazenda Boa Esperança", proprietarioUser);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findAllByOwner(proprietarioUser)).thenReturn(List.of(property1, property2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/properties/my-properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andDo(print());
    }

    // --- TESTES DE ATUALIZAÇÃO (UPDATE) ---

    @Test
    @WithMockUser(username = "testuser")
    void updatePropertySuccessfully() throws Exception {
        // Arrange
        PropertyModel originalProperty = createPropertyModel(1L, "Fazenda Santa Clara", proprietarioUser);

        PropertyUpdateRequestDto updateRequestDto = new PropertyUpdateRequestDto();
        updateRequestDto.setNome("Fazenda Santa Clara (Atualizada)");
        String requestBody = objectMapper.writeValueAsString(updateRequestDto);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(originalProperty));
        when(propertyRepository.findByNome(anyString())).thenReturn(Optional.empty());
        // Mock save para retornar a entidade atualizada
        when(propertyRepository.save(any(PropertyModel.class))).thenAnswer(invocation -> {
            PropertyModel updated = invocation.getArgument(0);
            return updated;
        });

        // Act & Assert
        mockMvc.perform(put("/api/v1/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Fazenda Santa Clara (Atualizada)"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePropertyFails_WhenUserIsNotOwner() throws Exception {
        // Arrange
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser);
        PropertyUpdateRequestDto updateRequestDto = new PropertyUpdateRequestDto();
        updateRequestDto.setNome("Tentativa de Hack");
        String requestBody = objectMapper.writeValueAsString(updateRequestDto);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther)); // Propriedade de outro dono

        // Act & Assert
        mockMvc.perform(put("/api/v1/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    // --- TESTES DE EXCLUSÃO (DELETE) ---

    @Test
    @WithMockUser(username = "testuser")
    void deletePropertySuccessfully() throws Exception {
        // Arrange
        PropertyModel property = createPropertyModel(1L, "Fazenda a Deletar", proprietarioUser);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        doNothing().when(propertyRepository).delete(property);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/properties/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePropertyFails_WhenUserIsNotOwner() throws Exception {
        // Arrange
        PropertyModel propertyOfOther = createPropertyModel(1L, "Fazenda Secreta", otherProprietarioUser);

        // Mocks
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(proprietarioUser));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(propertyOfOther)); // Propriedade de outro dono

        // Act & Assert
        mockMvc.perform(delete("/api/v1/properties/1"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePropertyFails_WhenUserIsNotProprietario() throws Exception {
        // Mocks
        // Usuário é FUNCIONARIO, não PROPRIETARIO
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(funcionarioUser));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/properties/1"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}