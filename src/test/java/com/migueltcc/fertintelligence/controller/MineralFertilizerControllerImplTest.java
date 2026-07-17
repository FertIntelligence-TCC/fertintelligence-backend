package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NaturezaFisica;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MineralFertilizerControllerImplTest extends AbstractControllerTest {

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

    private MineralFertilizerCreateRequestDto createRequestDto() {
        return MineralFertilizerCreateRequestDto.builder()
                .name("Adubo Foliar Teste")
                .naturezaFisica(NaturezaFisica.LIQUIDO)
                .densidadeGml(1.2)
                .concentracaoVolumeGl(240.0)
                .concentracaoMassaGkg(200.0)
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

    private MineralFertilizerModel createModel(Long id) {
        return MineralFertilizerModel.builder()
                .id(id)
                .user(owner)
                .name("Adubo Foliar Teste")
                .naturezaFisica(NaturezaFisica.LIQUIDO)
                .densidadeGml(1.2)
                .concentracaoVolumeGl(240.0)
                .concentracaoMassaGkg(200.0)
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
    void createMineralFertilizerSuccessfully() throws Exception {
        MineralFertilizerCreateRequestDto requestDto = createRequestDto();
        MineralFertilizerModel savedModel = createModel(1L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.save(any(MineralFertilizerModel.class))).thenReturn(savedModel);

        mockMvc.perform(post("/mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/mineral-fertilizer/register/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Foliar Teste"))
                .andExpect(jsonPath("$.natureza_fisica").value("LÍQUIDO"))
                .andExpect(jsonPath("$.densidade_g_ml").value(1.2))
                .andExpect(jsonPath("$.concentracao_volume_g_l").value(240.0))
                .andExpect(jsonPath("$.concentracao_massa_g_kg").value(200.0))
                .andExpect(jsonPath("$.n").value(10.0))
                .andExpect(jsonPath("$.indice_salino").value(12.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void createSolidMineralFertilizerIgnoresLiquidFields() throws Exception {
        MineralFertilizerCreateRequestDto requestDto = createRequestDto();
        requestDto.setNaturezaFisica(NaturezaFisica.SOLIDO);
        requestDto.setDensidadeGml(-1.0);
        requestDto.setConcentracaoVolumeGl(-2.0);
        requestDto.setConcentracaoMassaGkg(-3.0);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.save(any(MineralFertilizerModel.class))).thenAnswer(invocation -> {
            MineralFertilizerModel saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });

        mockMvc.perform(post("/mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.natureza_fisica").value("SÓLIDO"))
                .andExpect(jsonPath("$.densidade_g_ml").isEmpty())
                .andExpect(jsonPath("$.concentracao_volume_g_l").isEmpty())
                .andExpect(jsonPath("$.concentracao_massa_g_kg").isEmpty());

        ArgumentCaptor<MineralFertilizerModel> captor = ArgumentCaptor.forClass(MineralFertilizerModel.class);
        verify(mineralFertilizerRepository).save(captor.capture());
        MineralFertilizerModel saved = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getDensidadeGml()).isNull();
        org.assertj.core.api.Assertions.assertThat(saved.getConcentracaoVolumeGl()).isNull();
        org.assertj.core.api.Assertions.assertThat(saved.getConcentracaoMassaGkg()).isNull();
    }

    @Test
    @WithMockUser(username = "owner")
    void getMineralFertilizerSuccessfully() throws Exception {
        MineralFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        mockMvc.perform(get("/mineral-fertilizer/get")
                        .param("mineralFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Foliar Teste"))
                .andExpect(jsonPath("$.natureza_fisica").value("LÍQUIDO"))
                .andExpect(jsonPath("$.k2o").value(8.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getAllMineralFertilizersSuccessfully() throws Exception {
        MineralFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findAllByUserUsernameOrderByNameAsc("owner")).thenReturn(List.of(model));

        mockMvc.perform(get("/mineral-fertilizer/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].user_id").value(owner.getId()));
    }

    @Test
    @WithMockUser(username = "owner")
    void getMineralFertilizersByNameSuccessfully() throws Exception {
        MineralFertilizerModel model = createModel(6L);
        String searchName = "Adubo";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(eq(searchName), eq(owner), eq(Cargo.USUARIO_SUPREMO)))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/mineral-fertilizer/get-by-name")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[0].nome_adubo").value("Adubo Foliar Teste"))
                .andExpect(jsonPath("$[0].densidade_g_ml").value(1.2));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateMineralFertilizerSuccessfully() throws Exception {
        MineralFertilizerModel existing = createModel(4L);
        MineralFertilizerPostRequestDto updateDto = MineralFertilizerPostRequestDto.builder()
                .name("Adubo Foliar Atualizado")
                .naturezaFisica(NaturezaFisica.LIQUIDO)
                .densidadeGml(1.3)
                .concentracaoVolumeGl(260.0)
                .concentracaoMassaGkg(220.0)
                .n(12.0)
                .indiceAcidez(6.8)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(mineralFertilizerRepository.save(any(MineralFertilizerModel.class))).thenAnswer(invocation -> {
            MineralFertilizerModel arg = invocation.getArgument(0);
            arg.setName("Adubo Foliar Atualizado");
            arg.setNaturezaFisica(NaturezaFisica.LIQUIDO);
            arg.setDensidadeGml(1.3);
            arg.setConcentracaoVolumeGl(260.0);
            arg.setConcentracaoMassaGkg(220.0);
            arg.setN(12.0);
            arg.setIndiceAcidez(6.8);
            return arg;
        });

        mockMvc.perform(put("/mineral-fertilizer/update")
                        .param("mineralFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome_adubo").value("Adubo Foliar Atualizado"))
                .andExpect(jsonPath("$.natureza_fisica").value("LÍQUIDO"))
                .andExpect(jsonPath("$.densidade_g_ml").value(1.3))
                .andExpect(jsonPath("$.concentracao_volume_g_l").value(260.0))
                .andExpect(jsonPath("$.concentracao_massa_g_kg").value(220.0))
                .andExpect(jsonPath("$.n").value(12.0))
                .andExpect(jsonPath("$.indice_acidez").value(6.8));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateSolidMineralFertilizerClearsLiquidFields() throws Exception {
        MineralFertilizerModel existing = createModel(8L);
        MineralFertilizerPostRequestDto updateDto = MineralFertilizerPostRequestDto.builder()
                .naturezaFisica(NaturezaFisica.SOLIDO)
                .densidadeGml(-1.0)
                .concentracaoVolumeGl(-2.0)
                .concentracaoMassaGkg(-3.0)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(mineralFertilizerRepository.save(any(MineralFertilizerModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/mineral-fertilizer/update")
                        .param("mineralFertilizerId", "8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.natureza_fisica").value("SÓLIDO"))
                .andExpect(jsonPath("$.densidade_g_ml").isEmpty())
                .andExpect(jsonPath("$.concentracao_volume_g_l").isEmpty())
                .andExpect(jsonPath("$.concentracao_massa_g_kg").isEmpty());
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteMineralFertilizerSuccessfully() throws Exception {
        MineralFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(mineralFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(mineralFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/mineral-fertilizer/delete")
                        .param("mineralFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}
