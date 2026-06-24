package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

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
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GreenFertilizerControllerImplTest extends AbstractControllerTest {

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

    private GreenFertilizerCreateRequestDto createRequestDto() {
        return GreenFertilizerCreateRequestDto.builder()
                .name("Adubo Verde Teste")
                .c(15.0)
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
                .produtividadeEsperadaKgHa(12000.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(50.0)
                .taxaMineralizacaoSegundoAnoPercentual(30.0)
                .taxaMineralizacaoTerceiroAnoPercentual(20.0)
                .build();
    }

    private GreenFertilizerModel createModel(Long id) {
        return GreenFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Adubo Verde Teste")
                .C(15.0)
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
                .produtividadeEsperadaKgHa(12000.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(50.0)
                .taxaMineralizacaoSegundoAnoPercentual(30.0)
                .taxaMineralizacaoTerceiroAnoPercentual(20.0)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createGreenFertilizerSuccessfully() throws Exception {
        GreenFertilizerCreateRequestDto requestDto = createRequestDto();
        GreenFertilizerModel savedModel = createModel(1L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.save(any(GreenFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/green-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/green-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Verde Teste"))
                .andExpect(jsonPath("$.c").value(15.0))
                .andExpect(jsonPath("$.produtividade_esperada_kg_ha").value(12000.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(50.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_segundo_ano_percentual").value(30.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_terceiro_ano_percentual").value(20.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getGreenFertilizerSuccessfully() throws Exception {
        GreenFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        mockMvc.perform(get("/green-fertilizer/get")
                        .param("greenFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Verde Teste"))
                .andExpect(jsonPath("$.c").value(15.0))
                .andExpect(jsonPath("$.produtividade_esperada_kg_ha").value(12000.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(50.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_segundo_ano_percentual").value(30.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_terceiro_ano_percentual").value(20.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getAllGreenFertilizersSuccessfully() throws Exception {
        GreenFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(model));

        mockMvc.perform(get("/green-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].user_id").value(nullValue()));
    }

    @Test
    @WithMockUser(username = "owner")
    void getGreenFertilizersByNameSuccessfully() throws Exception {
        GreenFertilizerModel model = createModel(6L);
        String searchName = "Adubo";

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(eq(searchName), eq(owner), eq(Cargo.USUARIO_SUPREMO)))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/green-fertilizer/get-by-name")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Adubo Verde Teste"));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateGreenFertilizerSuccessfully() throws Exception {
        GreenFertilizerModel existing = createModel(4L);
        GreenFertilizerPostRequestDto updateDto = GreenFertilizerPostRequestDto.builder()
                .name("Adubo Verde Atualizado")
                .c(18.0)
                .produtividadeEsperadaKgHa(14000.0)
                .taxaMineralizacaoPrimeiroAnoPercentual(55.0)
                .taxaMineralizacaoSegundoAnoPercentual(25.0)
                .taxaMineralizacaoTerceiroAnoPercentual(15.0)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(greenFertilizerRepository.save(any(GreenFertilizerModel.class))).thenAnswer(invocation -> {
            GreenFertilizerModel arg = invocation.getArgument(0);
            arg.setName("Adubo Verde Atualizado");
            arg.setC(18.0);
            arg.setProdutividadeEsperadaKgHa(14000.0);
            arg.setTaxaMineralizacaoPrimeiroAnoPercentual(55.0);
            arg.setTaxaMineralizacaoSegundoAnoPercentual(25.0);
            arg.setTaxaMineralizacaoTerceiroAnoPercentual(15.0);
            return arg;
        });

        mockMvc.perform(put("/green-fertilizer/update")
                        .param("greenFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Verde Atualizado"))
                .andExpect(jsonPath("$.c").value(18.0))
                .andExpect(jsonPath("$.produtividade_esperada_kg_ha").value(14000.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(55.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_segundo_ano_percentual").value(25.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_terceiro_ano_percentual").value(15.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteGreenFertilizerSuccessfully() throws Exception {
        GreenFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(greenFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(greenFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/green-fertilizer/delete")
                        .param("greenFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}
