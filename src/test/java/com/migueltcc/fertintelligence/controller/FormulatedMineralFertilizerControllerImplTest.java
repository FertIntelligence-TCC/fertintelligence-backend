package com.migueltcc.fertintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
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
public class FormulatedMineralFertilizerControllerImplTest extends AbstractControllerTest {

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

    private FormulatedMineralFertilizerCreateRequestDto createRequestDto() {
        return FormulatedMineralFertilizerCreateRequestDto.builder()
                .formulate(new FormulateDto(4, 14, 8))
                .relation(new NPKrelationDto(1.0, 3.5, 2.0))
                .indicatedFormulaNumber(101)
                .n(4.0)
                .p2o5(14.0)
                .k2o(8.0)
                .ca(1.2)
                .mg(0.7)
                .s(0.5)
                .b(0.01)
                .cu(0.02)
                .fe(0.4)
                .mn(0.3)
                .mo(0.01)
                .zn(0.2)
                .build();
    }

    private FormulatedMineralFertilizerModel createModel(Long id) {
        return FormulatedMineralFertilizerModel.builder()
                .id(id)
                .user(owner)
                .formulate(new Formulate(4, 14, 8))
                .relation(new NPKrelation(1.0, 3.5, 2.0))
                .N(4.0)
                .P2O5(14.0)
                .K2O(8.0)
                .Ca(1.2)
                .Mg(0.7)
                .S(0.5)
                .B(0.01)
                .Cu(0.02)
                .Fe(0.4)
                .Mn(0.3)
                .Mo(0.01)
                .Zn(0.2)
                .indicatedFormulaNumber(101)
                .build();
    }

    @Test
    @WithMockUser(username = "owner")
    void createFormulatedMineralFertilizerSuccessfully() throws Exception {
        FormulatedMineralFertilizerCreateRequestDto requestDto = createRequestDto();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.save(any(FormulatedMineralFertilizerModel.class))).thenAnswer(invocation -> {
            FormulatedMineralFertilizerModel saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        mockMvc.perform(post("/formulated-mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/formulated-mineral-fertilizer/get?formulatedMineralFertilizerId=1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.formula.n").value(4))
                .andExpect(jsonPath("$.relacao.p").value(3.5))
                .andExpect(jsonPath("$.k2o").value(8.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getFormulatedMineralFertilizerSuccessfully() throws Exception {
        FormulatedMineralFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        mockMvc.perform(get("/formulated-mineral-fertilizer/get")
                        .param("formulatedMineralFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.relacao.k").value(2.0))
                .andExpect(jsonPath("$.p2o5").value(14.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getFormulatedMineralFertilizersByUserSuccessfully() throws Exception {
        FormulatedMineralFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findAllByUser(owner)).thenReturn(List.of(model));

        mockMvc.perform(get("/formulated-mineral-fertilizer/get-by-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].user_id").value(owner.getId()));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateFormulatedMineralFertilizerSuccessfully() throws Exception {
        FormulatedMineralFertilizerModel existing = createModel(4L);
        FormulatedMineralFertilizerPostRequestDto updateDto = FormulatedMineralFertilizerPostRequestDto.builder()
                .n(6.0)
                .p2o5(12.0)
                .k2o(6.0)
                .indicatedFormulaNumber(202)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        when(formulatedMineralFertilizerRepository.save(any(FormulatedMineralFertilizerModel.class))).thenAnswer(invocation -> {
            FormulatedMineralFertilizerModel updated = invocation.getArgument(0);
            updated.setId(4L);
            return updated;
        });

        mockMvc.perform(put("/formulated-mineral-fertilizer/update")
                        .param("formulatedMineralFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formula.n").value(6))
                .andExpect(jsonPath("$.relacao.p").value(2.0))
                .andExpect(jsonPath("$.numero_formula_indicada").value(202));
    }

    @Test
    @WithMockUser(username = "owner")
    void deleteFormulatedMineralFertilizerSuccessfully() throws Exception {
        FormulatedMineralFertilizerModel existing = createModel(5L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findById(5L)).thenReturn(Optional.of(existing));
        doNothing().when(formulatedMineralFertilizerRepository).delete(existing);

        mockMvc.perform(delete("/formulated-mineral-fertilizer/delete")
                        .param("formulatedMineralFertilizerId", "5"))
                .andExpect(status().isNoContent());
    }
}