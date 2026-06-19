package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class SimpleMineralFertilizerControllerImplTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // Mocks dos Repositórios (Usados pelo Service real)
    @MockBean
    private SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    @MockBean
    private UserRepository userRepository;

    private UserModel owner;
    private SimpleMineralFertilizerModel fertilizer;

    @BeforeEach
    void setUp() {
        owner = UserModel.builder()
                .id(1L)
                .username("owner")
                .cargo(Cargo.PROPRIETARIO)
                .build();

        fertilizer = createModel(1L, "Ureia", 45.0, 0.0, 0.0);
    }

    private SimpleMineralFertilizerModel createModel(Long id, String name, double n, double p, double k) {
        return SimpleMineralFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name(name)
                .N(n)
                .P2O5(p)
                .K2O(k)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .indiceSalino(75.0)
                .indiceAcidez(0.0)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createSimpleMineralFertilizerSuccessfully() throws Exception {
        SimpleMineralFertilizerCreateRequestDto requestDto = SimpleMineralFertilizerCreateRequestDto.builder()
                .name("Ureia Agrícola")
                .n(45.0).p2o5(0.0).k2o(0.0)
                .ca(0.0).mg(0.0).s(0.0)
                .b(0.0).cu(0.0).fe(0.0).mn(0.0).mo(0.0).zn(0.0)
                .indiceSalino(75.0)
                .indiceAcidez(0.0)
                .build();

        SimpleMineralFertilizerModel savedModel = createModel(1L, "Ureia Agrícola", 45.0, 0.0, 0.0);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.save(any(SimpleMineralFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/simple-mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                // Verificando chaves em snake_case
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome_adubo").value("Ureia Agrícola"))
                .andExpect(jsonPath("$.n").value(45.0))
                .andExpect(jsonPath("$.indice_salino").value(75.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getSimpleMineralFertilizersSuccessfully() throws Exception {
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(fertilizer));

        mockMvc.perform(get("/simple-mineral-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome_adubo").value("Ureia"))
                .andExpect(jsonPath("$[0].n").value(45.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getSimpleMineralFertilizersByNameSuccessfully() throws Exception {
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(eq("Ureia"), eq(owner), eq(Cargo.USUARIO_SUPREMO)))
                .thenReturn(List.of(fertilizer));

        mockMvc.perform(get("/simple-mineral-fertilizer/get-by-name")
                        .param("name", "Ureia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome_adubo").value("Ureia"));
    }

    @Test
    @WithMockUser(username = "owner")
    void getSimpleMineralFertilizersEmpty() throws Exception {
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/simple-mineral-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "owner")
    void updateSimpleMineralFertilizerSuccessfully() throws Exception {
        // Payload com prefixo novo_
        SimpleMineralFertilizerPostRequestDto updateDto = SimpleMineralFertilizerPostRequestDto.builder()
                .name("Ureia Atualizada")
                .n(46.0).p2o5(0.0).k2o(0.0)
                .ca(0.0).mg(0.0).s(0.0)
                .b(0.0).cu(0.0).fe(0.0).mn(0.0).mo(0.0).zn(0.0)
                .indiceSalino(76.0)
                .indiceAcidez(0.0)
                .build();

        SimpleMineralFertilizerModel existing = createModel(1L, "Ureia Antiga", 45.0, 0.0, 0.0);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.findById(1L)).thenReturn(Optional.of(existing));

        // Simula o comportamento do Service de atualizar a entidade e salvar
        when(simpleMineralFertilizerRepository.save(any(SimpleMineralFertilizerModel.class))).thenAnswer(invocation -> {
            SimpleMineralFertilizerModel arg = invocation.getArgument(0);
            return arg; // Retorna o próprio objeto modificado
        });

        mockMvc.perform(put("/simple-mineral-fertilizer/update")
                        .param("simpleMineralFertilizerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                // Verifica a resposta com snake_case
                .andExpect(jsonPath("$.nome_adubo").value("Ureia Atualizada"))
                .andExpect(jsonPath("$.n").value(46.0))
                .andExpect(jsonPath("$.indice_salino").value(76.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteSimpleMineralFertilizerSuccessfully() throws Exception {
        SimpleMineralFertilizerModel existing = createModel(1L, "Ureia", 45.0, 0.0, 0.0);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(simpleMineralFertilizerRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(simpleMineralFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/simple-mineral-fertilizer/delete")
                        .param("simpleMineralFertilizerId", "1"))
                .andExpect(status().isNoContent());
    }
}