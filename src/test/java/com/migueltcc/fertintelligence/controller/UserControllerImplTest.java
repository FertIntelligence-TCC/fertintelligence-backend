package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.migueltcc.fertintelligence.composedAtributes.*;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
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
        "jwt.public.key=classpath:public.pem", // Você provavelmente precisará disso também
        "jwt.private.key=classpath:private.pem" // E disso
})
@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static MockRestServiceServer mockServer;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

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
