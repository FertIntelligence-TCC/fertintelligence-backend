package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
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
public class BioFertilizerControllerImplTest extends AbstractControllerTest {

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

    private BioFertilizerCreateRequestDto createRequestDto() {
        return BioFertilizerCreateRequestDto.builder()
                .name("Adubo Bio Teste")
                .n(3.0)
                .p2o5(2.0)
                .k2o(4.0)
                .ca(1.0)
                .mg(0.5)
                .s(0.2)
                .b(0.05)
                .cu(0.02)
                .fe(0.3)
                .mn(0.1)
                .mo(0.01)
                .zn(0.07)
                .indiceSalino(8.5)
                .indiceAcidez(6.0)
                .build();
    }

    private BioFertilizerModel createModel(Long id) {
        return BioFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Adubo Bio Teste")
                .N(3.0)
                .P2O5(2.0)
                .K2O(4.0)
                .Ca(1.0)
                .Mg(0.5)
                .S(0.2)
                .B(0.05)
                .Cu(0.02)
                .Fe(0.3)
                .Mn(0.1)
                .Mo(0.01)
                .Zn(0.07)
                .indiceSalino(8.5)
                .indiceAcidez(6.0)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createBioFertilizerSuccessfully() throws Exception {
        BioFertilizerCreateRequestDto requestDto = createRequestDto();
        BioFertilizerModel savedModel = createModel(1L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.save(any(BioFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/bio-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/bio-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Bio Teste"))
                .andExpect(jsonPath("$.n").value(3.0))
                .andExpect(jsonPath("$.indice_salino").value(8.5));
    }

    @Test
    @WithMockUser(username = "owner")
    void getBioFertilizerSuccessfully() throws Exception {
        BioFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        mockMvc.perform(get("/bio-fertilizer/get")
                        .param("bioFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Bio Teste"))
                .andExpect(jsonPath("$.n").value(3.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getAllBioFertilizersSuccessfully() throws Exception {
        BioFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(model));

        mockMvc.perform(get("/bio-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].user_id").doesNotExist());
    }

    @Test
    @WithMockUser(username = "owner")
    void getBioFertilizersByNameSuccessfully() throws Exception {
        BioFertilizerModel model = createModel(6L);
        String searchName = "Adubo";

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(eq(searchName), eq(owner), eq(Cargo.USUARIO_SUPREMO)))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/bio-fertilizer/get-by-name")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Adubo Bio Teste"));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateBioFertilizerSuccessfully() throws Exception {
        BioFertilizerModel existing = createModel(4L);
        BioFertilizerPostRequestDto updateDto = BioFertilizerPostRequestDto.builder()
                .name("Adubo Bio Atualizado")
                .n(3.5)
                .indiceAcidez(6.3)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(bioFertilizerRepository.save(any(BioFertilizerModel.class))).thenAnswer(invocation -> {
            BioFertilizerModel arg = invocation.getArgument(0);
            arg.setName("Adubo Bio Atualizado");
            arg.setN(3.5);
            arg.setIndiceAcidez(6.3);
            return arg;
        });

        mockMvc.perform(put("/bio-fertilizer/update")
                        .param("bioFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Bio Atualizado"))
                .andExpect(jsonPath("$.n").value(3.5))
                .andExpect(jsonPath("$.indice_acidez").value(6.3));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteBioFertilizerSuccessfully() throws Exception {
        BioFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(bioFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(bioFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/bio-fertilizer/delete")
                        .param("bioFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}