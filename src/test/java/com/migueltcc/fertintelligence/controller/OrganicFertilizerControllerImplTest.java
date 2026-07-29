package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
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
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrganicFertilizerControllerImplTest extends AbstractControllerTest {

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

    private OrganicFertilizerCreateRequestDto createRequestDto() {
        return OrganicFertilizerCreateRequestDto.builder()
                .name("Composto Orgânico Teste")
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
                .teorUmidade(12.0)
                .teorMateriaOrganicaPercentual(34.48)
                .taxaMineralizacaoPrimeiroAnoPercentual(50.0)
                .taxaMineralizacaoSegundoAnoPercentual(30.0)
                .taxaMineralizacaoTerceiroAnoPercentual(20.0)
                .taxaMineralizacaoQuartoAnoPercentual(10.0)
                .precoSaco1000Kg(new BigDecimal("120.00"))
                .valorFreteTonelada(new BigDecimal("30.00"))
                .arsenioMgKg(0.0)
                .cadmioMgKg(0.12)
                .cromioMgKg(1.23)
                .chumboMgKg(2.34)
                .mercurioMgKg(0.045)
                .niquelMgKg(3.45)
                .selenioMgKg(0.56)
                .build();
    }

    private OrganicFertilizerModel createModel(Long id) {
        return OrganicFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Composto Orgânico Teste")
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
                .teorUmidade(12.0)
                .teorMateriaOrganicaPercentual(34.48)
                .taxaMineralizacaoPrimeiroAnoPercentual(50.0)
                .taxaMineralizacaoSegundoAnoPercentual(30.0)
                .taxaMineralizacaoTerceiroAnoPercentual(20.0)
                .taxaMineralizacaoQuartoAnoPercentual(10.0)
                .precoSaco1000Kg(new BigDecimal("120.00"))
                .valorFreteTonelada(new BigDecimal("30.00"))
                .arsenioMgKg(0.0)
                .cadmioMgKg(0.12)
                .cromioMgKg(1.23)
                .chumboMgKg(2.34)
                .mercurioMgKg(0.045)
                .niquelMgKg(3.45)
                .selenioMgKg(0.56)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createOrganicFertilizerWithOrganicMatterAndMineralizationSuccessfully() throws Exception {
        OrganicFertilizerCreateRequestDto requestDto = createRequestDto();
        OrganicFertilizerModel savedModel = createModel(1L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organicFertilizerRepository.save(any(OrganicFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/organic-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/organic-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Composto Orgânico Teste"))
                .andExpect(jsonPath("$.teor_umidade").value(12.0))
                .andExpect(jsonPath("$.teor_materia_organica_percentual").value(34.48))
                .andExpect(jsonPath("$.teor_carbono_organico_percentual").value(20.0))
                .andExpect(jsonPath("$.relacao_carbono_nitrogenio").value(6.666666666667))
                .andExpect(jsonPath("$.teor_cinzas").doesNotExist())
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(50.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_segundo_ano_percentual").value(30.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_terceiro_ano_percentual").value(20.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_quarto_ano_percentual").value(10.0))
                .andExpect(jsonPath("$.preco_saco_1000kg").value(120.0))
                .andExpect(jsonPath("$.valor_frete_tonelada").value(30.0))
                .andExpect(jsonPath("$.arsenio_mg_kg").value(0.0))
                .andExpect(jsonPath("$.cadmio_mg_kg").value(0.12))
                .andExpect(jsonPath("$.cromio_mg_kg").value(1.23))
                .andExpect(jsonPath("$.chumbo_mg_kg").value(2.34))
                .andExpect(jsonPath("$.mercurio_mg_kg").value(0.045))
                .andExpect(jsonPath("$.niquel_mg_kg").value(3.45))
                .andExpect(jsonPath("$.selenio_mg_kg").value(0.56));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateOrganicFertilizerWithOrganicMatterAndMineralizationSuccessfully() throws Exception {
        OrganicFertilizerModel existing = createModel(4L);
        OrganicFertilizerPostRequestDto updateDto = OrganicFertilizerPostRequestDto.builder()
                .name("Composto Orgânico Atualizado")
                .teorMateriaOrganicaPercentual(43.1)
                .taxaMineralizacaoPrimeiroAnoPercentual(55.0)
                .taxaMineralizacaoSegundoAnoPercentual(25.0)
                .taxaMineralizacaoTerceiroAnoPercentual(15.0)
                .taxaMineralizacaoQuartoAnoPercentual(7.5)
                .valorFreteTonelada(new BigDecimal("0.00"))
                .arsenioMgKg(0.25)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(owner));
        when(organicFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(organicFertilizerRepository.save(any(OrganicFertilizerModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(organicFertilizerPhotoRepository.findAllByFertilizerIdOrderByOrdemAsc(4L)).thenReturn(List.of());

        mockMvc.perform(put("/organic-fertilizer/update")
                        .param("organicFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Composto Orgânico Atualizado"))
                .andExpect(jsonPath("$.teor_materia_organica_percentual").value(43.1))
                .andExpect(jsonPath("$.teor_carbono_organico_percentual").value(25.0))
                .andExpect(jsonPath("$.teor_cinzas").doesNotExist())
                .andExpect(jsonPath("$.taxa_mineralizacao_primeiro_ano_percentual").value(55.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_segundo_ano_percentual").value(25.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_terceiro_ano_percentual").value(15.0))
                .andExpect(jsonPath("$.taxa_mineralizacao_quarto_ano_percentual").value(7.5))
                .andExpect(jsonPath("$.valor_frete_tonelada").value(0.0))
                .andExpect(jsonPath("$.arsenio_mg_kg").value(0.25));
    }
}
