package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ChelatedFertilizerControllerImplTest extends AbstractControllerTest {

    @Autowired
    private ObjectMapper mapper;

    private UserModel owner;

    @BeforeEach
    void setUp() {
        owner = UserModel.builder()
                .id(1L)
                .username("owner")
                .name("Test Owner")
                .cargo(Cargo.PROPRIETARIO)
                .build();
    }

    private ChelatedFertilizerCreateRequestDto createRequestDto() {
        return ChelatedFertilizerCreateRequestDto.builder()
                .name("Adubo Quelatado Teste")
                .n(10.0)
                .p2o5(5.0)
                .k2o(8.0)
                .ca(1.2)
                .mg(0.8)
                .s(0.5)
                .b(0.05)
                .cu(0.02)
                .fe(0.5)
                .mn(0.3)
                .mo(0.01)
                .zn(0.4)
                .indiceSalino(12.0)
                .indiceAcidez(6.5)
                .build();
    }

    private ChelatedFertilizerModel createModel(Long id) {
        return ChelatedFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Adubo Quelatado Teste")
                .N(10.0)
                .P2O5(5.0)
                .K2O(8.0)
                .Ca(1.2)
                .Mg(0.8)
                .S(0.5)
                .B(0.05)
                .Cu(0.02)
                .Fe(0.5)
                .Mn(0.3)
                .Mo(0.01)
                .Zn(0.4)
                .indiceSalino(12.0)
                .indiceAcidez(6.5)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createChelatedFertilizerSuccessfully() throws Exception {
        ChelatedFertilizerCreateRequestDto requestDto = createRequestDto();
        ChelatedFertilizerModel savedModel = createModel(1L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.save(any(ChelatedFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/chelated-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/chelated-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Quelatado Teste"))
                .andExpect(jsonPath("$.n").value(10.0))
                .andExpect(jsonPath("$.indice_salino").value(12.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getChelatedFertilizerSuccessfully() throws Exception {
        ChelatedFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        mockMvc.perform(get("/chelated-fertilizer/get")
                        .param("chelatedFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Quelatado Teste"))
                .andExpect(jsonPath("$.k2o").value(8.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getAllChelatedFertilizersSuccessfully() throws Exception {
        ChelatedFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.findAllByUser(owner)).thenReturn(List.of(model));

        mockMvc.perform(get("/chelated-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].user_id").doesNotExist());
    }

    @Test
    @WithMockUser(username = "owner")
    void getChelatedFertilizersByNameSuccessfully() throws Exception {
        ChelatedFertilizerModel model = createModel(6L);
        String searchName = "Adubo";

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.findAllByNameContainingIgnoreCaseAndUser(eq(searchName), eq(owner)))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/chelated-fertilizer/get-by-name")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Adubo Quelatado Teste"));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateChelatedFertilizerSuccessfully() throws Exception {
        ChelatedFertilizerModel existing = createModel(4L);
        ChelatedFertilizerPostRequestDto updateDto = ChelatedFertilizerPostRequestDto.builder()
                .name("Adubo Quelatado Atualizado")
                .n(12.0)
                .indiceAcidez(6.8)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(chelatedFertilizerRepository.save(any(ChelatedFertilizerModel.class))).thenAnswer(invocation -> {
            ChelatedFertilizerModel arg = invocation.getArgument(0);
            arg.setName("Adubo Quelatado Atualizado");
            arg.setN(12.0);
            arg.setIndiceAcidez(6.8);
            return arg;
        });

        mockMvc.perform(put("/chelated-fertilizer/update")
                        .param("chelatedFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Quelatado Atualizado"))
                .andExpect(jsonPath("$.n").value(12.0))
                .andExpect(jsonPath("$.indice_acidez").value(6.8));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteChelatedFertilizerSuccessfully() throws Exception {
        ChelatedFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(chelatedFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(chelatedFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/chelated-fertilizer/delete")
                        .param("chelatedFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}