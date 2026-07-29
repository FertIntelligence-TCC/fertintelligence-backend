package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.*;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.frontend.url=http://dummy-test-url.com",
        "jwt.public.key=classpath:public.pem",
        "jwt.private.key=classpath:private.pem"
})
@ExtendWith(MockitoExtension.class)
class UserControllerImplTest extends AbstractControllerTest {

    private static MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @AfterEach
    void tearDown() {
        mockServer.reset();
    }

    @Test
    void createUserSuccessfully() throws Exception {
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .password("password123")
                .username("Mikeru")
                .name("Miguel Macedo Ferreira")
                .email("miguel.ferreira@ccc.ufcg.edu.com.br")
                .cpf("13600319442")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO)
                .telefone(new Telefone("+55", "11", "99121-4231"))
                .formacao(Formacao.GRADUACAO)
                .profissao("Engenheiro de Software")
                .cargo(Cargo.SECRETARIO)
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(requestDto);

        Mockito.when(userRepository.existsByUsername(Mockito.any(String.class)))
                .thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(UserModel.class)))
                .thenReturn(new UserModel());

        String responseJsonString = mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void createUserRejectsSupremeUserCargo() throws Exception {
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .password("password123")
                .username("supreme")
                .name("Supreme User")
                .email("supreme@example.com")
                .cpf("13600319442")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO)
                .telefone(new Telefone("+55", "11", "99121-4231"))
                .formacao(Formacao.GRADUACAO)
                .profissao("Engenheiro de Software")
                .cargo(Cargo.USUARIO_SUPREMO)
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(requestDto);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {""})
    void updateUserSuccessfully() throws Exception {
        UserPostRequestDto requestDto = UserPostRequestDto.builder()
                .password("password")
                .nome("name")
                .email("email")
                .telefone(new Telefone("+55", "83", "99381-0404"))
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(requestDto);

        Mockito.when(userRepository.findByUsername(Mockito.any(String.class)))
                .thenReturn(Optional.of(new UserModel()));
        Mockito.when(userRepository.save(Mockito.any(UserModel.class)))
                .thenReturn(new UserModel());

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully!"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {""})
    void updateUserRejectsSupremeUserCargo() throws Exception {
        UserPostRequestDto requestDto = UserPostRequestDto.builder()
                .cargo(Cargo.USUARIO_SUPREMO)
                .build();
        String requestBody = new ObjectMapper().writeValueAsString(requestDto);

        Mockito.when(userRepository.findByUsername(Mockito.any(String.class)))
                .thenReturn(Optional.of(new UserModel()));

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
    }

    @Test
    @WithMockUser(username = "owner")
    void profileUpdateRejectsAnyCargoChange() throws Exception {
        UserModel user = UserModel.builder()
                .username("owner")
                .cargo(Cargo.PROPRIETARIO)
                .build();
        Mockito.when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"novo_cargo":"AGRONOMO_RESIDENTE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "O cargo não pode ser alterado pela edição de perfil. Use o endpoint de cargo ativo."));

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
        org.assertj.core.api.Assertions.assertThat(user.getCargo()).isEqualTo(Cargo.PROPRIETARIO);
    }

    @Test
    @WithMockUser(username = "owner")
    void changesOnlyActiveCargoAndReturnsFreshToken() throws Exception {
        UserModel user = UserModel.builder()
                .id(21L)
                .username("owner")
                .cargo(Cargo.PROPRIETARIO)
                .build();
        Mockito.when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Jwt encodedJwt = Mockito.mock(Jwt.class);
        Mockito.when(encodedJwt.getTokenValue()).thenReturn("fresh-token");
        Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(encodedJwt);

        mockMvc.perform(put("/user/active-cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cargo":"AGRONOMO_RESIDENTE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("AGRONOMO_RESIDENTE"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        org.assertj.core.api.Assertions.assertThat(user.getId()).isEqualTo(21L);
        org.assertj.core.api.Assertions.assertThat(user.getCargo()).isEqualTo(Cargo.AGRONOMO_RESIDENTE);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    @WithMockUser(username = "owner")
    void rejectsReservedSupremeCargoInActiveCargoEndpoint() throws Exception {
        mockMvc.perform(put("/user/active-cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cargo":"USUARIO_SUPREMO"}
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
    }

    @Test
    @WithMockUser(username = "owner")
    void rejectsUnknownActiveCargoWithoutChangingUser() throws Exception {
        mockMvc.perform(put("/user/active-cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cargo":"ADMINISTRADOR"}
                                """))
                .andExpect(status().isBadRequest());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
    }

    @Test
    void rejectsUnauthenticatedActiveCargoChange() throws Exception {
        mockMvc.perform(put("/user/active-cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cargo":"GERENTE"}
                                """))
                .andExpect(status().isUnauthorized());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserModel.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {""})
    void deleteUserSuccessfully() throws Exception {
        Mockito.when(userRepository.findByUsername(Mockito.any(String.class)))
                .thenReturn(Optional.of(new UserModel()));
        Mockito.doNothing().when(userRepository).delete(Mockito.any(UserModel.class));

        mockMvc.perform(delete("/user/delete")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully!"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {""})
    void getUserSuccessfully() throws Exception {
        UserModel user = UserModel.builder()
                .id(1L)
                .name("Miguel Macedo Ferreira")
                .username("Mikeru")
                .cpf("13600319442")
                .email("miguel.ferreira@ccc.ufcg.edu.com.br")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO) // Enum
                .telefone(new Telefone("+55", "83", "99121-4231"))
                .formacao(Formacao.GRADUACAO) // Enum
                .profissao("Engenheiro de Software")
                .cargo(Cargo.SECRETARIO)
                .password("senha123")
                .build();
        UserResponseDto responseDto = user.toDto();
        String responseBody = new ObjectMapper().writeValueAsString(responseDto);

        Mockito.when(userRepository.findByUsername(Mockito.any(String.class)))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/get")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody))
                .andDo(print());
    }
}
