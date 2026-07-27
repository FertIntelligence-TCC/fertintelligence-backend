package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrganoMineralFertilizerControllerImplTest extends AbstractControllerTest {

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

    private OrganoMineralFertilizerCreateRequestDto createRequestDto() {
        return OrganoMineralFertilizerCreateRequestDto.builder()
                .name("Organo Plus")
                .c(8.0)
                .n(10.0)
                .p2o5(10.0)
                .k2o(10.0)
                .ca(1.5)
                .mg(0.8)
                .s(0.6)
                .b(0.05)
                .cu(0.03)
                .fe(0.4)
                .mn(0.25)
                .mo(0.01)
                .zn(0.2)
                .indiceSalino(45.0)
                .indiceAcidez(5.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(40.0)
                .taxaMineralizacaoSegundoAnoPercentual(25.0)
                .taxaMineralizacaoTerceiroAnoPercentual(15.0)
                .taxaMineralizacaoQuartoAnoPercentual(5.0)
                .build();
    }

    private OrganoMineralFertilizerModel createModel(Long id) {
        return OrganoMineralFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Organo Plus")
                .C(8.0)
                .N(10.0)
                .P2O5(10.0)
                .K2O(10.0)
                .Ca(1.5)
                .Mg(0.8)
                .S(0.6)
                .B(0.05)
                .Cu(0.03)
                .Fe(0.4)
                .Mn(0.25)
                .Mo(0.01)
                .Zn(0.2)
                .indiceSalino(45.0)
                .indiceAcidez(5.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(40.0)
                .taxaMineralizacaoSegundoAnoPercentual(25.0)
                .taxaMineralizacaoTerceiroAnoPercentual(15.0)
                .taxaMineralizacaoQuartoAnoPercentual(5.0)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createOrganoMineralFertilizerSuccessfully() throws Exception {
        OrganoMineralFertilizerCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.save(any(OrganoMineralFertilizerModel.class))).thenAnswer(invocation -> {
            OrganoMineralFertilizerModel saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        mockMvc.perform(post("/organo-mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/organo-mineral-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Organo Plus"))
                .andExpect(jsonPath("$.indice_salino").value(45.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(40.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_quarto_ano_percentual").value(5.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getAllOrganoMineralFertilizersSuccessfully() throws Exception {
        OrganoMineralFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findAllByUserUsernameOrderByNameAsc("owner")).thenReturn(List.of(model));

        mockMvc.perform(get("/organo-mineral-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Organo Plus"))
                .andExpect(jsonPath("$[0].c").value(8.0))
                .andExpect(jsonPath("$[0].k2o").value(10.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getOrganoMineralFertilizersByNameSuccessfully() throws Exception {
        OrganoMineralFertilizerModel model = createModel(6L);
        String searchName = "Organo";

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(eq(searchName), eq(owner), eq(Cargo.USUARIO_SUPREMO)))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/organo-mineral-fertilizer/get-by-name")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Organo Plus"));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateOrganoMineralFertilizerSuccessfully() throws Exception {
        OrganoMineralFertilizerModel existing = createModel(4L);
        OrganoMineralFertilizerPostRequestDto updateDto = OrganoMineralFertilizerPostRequestDto.builder()
                .name("Organo Atualizado")
                .indiceAcidez(5.5)
                .n(12.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(42.5)
                .taxaMineralizacaoQuartoAnoPercentual(0.0)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(organoMineralFertilizerRepository.save(any(OrganoMineralFertilizerModel.class))).thenAnswer(invocation -> {
            OrganoMineralFertilizerModel updated = invocation.getArgument(0);
            updated.setName("Organo Atualizado");
            updated.setIndiceAcidez(5.5);
            updated.setN(12.0);
            return updated;
        });

        mockMvc.perform(put("/organo-mineral-fertilizer/update")
                        .param("organoMineralFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Organo Atualizado"))
                .andExpect(jsonPath("$.n").value(12.0))
                .andExpect(jsonPath("$.indice_acidez").value(5.5))
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(42.5))
                .andExpect(jsonPath("$.taxa_mineralizacao_quarto_ano_percentual").value(0.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteOrganoMineralFertilizerSuccessfully() throws Exception {
        OrganoMineralFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(organoMineralFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/organo-mineral-fertilizer/delete")
                        .param("organoMineralFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}
