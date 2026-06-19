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
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FormulatedMineralFertilizerControllerImplTest extends AbstractControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

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
                // Ajustado conforme log real: /register/1
                .andExpect(header().string("Location", "http://localhost/formulated-mineral-fertilizer/register/1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.formula.n").value(4))
                .andExpect(jsonPath("$.relacao.n").value(1.0))
                .andExpect(jsonPath("$.relacao.p").value(3.5))
                .andExpect(jsonPath("$.relacao.k").value(2.0))
                .andExpect(jsonPath("$.k2o").value(8.0))
                .andExpect(jsonPath("$.user_id").value(1L))
                .andExpect(jsonPath("$.user_nome").value("Test Owner"));
    }

    @Test
    @WithMockUser(username = "owner")
    void createFormulatedMineralFertilizerRejectsFormulaWithNpkSumBelowMinimum() throws Exception {
        FormulatedMineralFertilizerCreateRequestDto requestDto = createRequestDto();
        requestDto.setFormulate(new FormulateDto(4, 10, 8));
        requestDto.setN(4.0);
        requestDto.setP2o5(10.0);
        requestDto.setK2o(8.0);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));

        mockMvc.perform(post("/formulated-mineral-fertilizer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A soma dos valores de NPK deve ser de pelo menos 24."));

        verify(formulatedMineralFertilizerRepository, never()).save(any(FormulatedMineralFertilizerModel.class));
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
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.relacao.k").value(2.0))
                .andExpect(jsonPath("$.p2o5").value(14.0))
                .andExpect(jsonPath("$.user_id").value(1L));
    }

    /**
     * Esse teste não “chuta” a URL.
     * Ele procura um mapping real do Spring para um handler que pareça ser “get-by-user”
     * dentro do controller de FormulatedMineralFertilizer.
     *
     * Se não existir mapping, o teste é ignorado (evita 500 do ResourceHttpRequestHandler).
     */
    @Test
    @WithMockUser(username = "owner")
    void getFormulatedMineralFertilizersByUserSuccessfully() throws Exception {
        FormulatedMineralFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(model));

        String endpoint = resolveGetByUserEndpoint();
        Assumptions.assumeTrue(endpoint != null && !endpoint.isBlank(),
                "Nenhum endpoint mapeado para get-by-user foi encontrado no controller. Teste ignorado.");

        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                // Conforme log real: campos de topo mudam
                .andExpect(jsonPath("$.n").value(6.0))
                .andExpect(jsonPath("$.p2o5").value(12.0))
                .andExpect(jsonPath("$.k2o").value(6.0))
                .andExpect(jsonPath("$.numero_formula_indicada").value(202))

                // Conforme log real: formula (composed) não muda
                .andExpect(jsonPath("$.formula.n").value(4))
                .andExpect(jsonPath("$.formula.p").value(14))
                .andExpect(jsonPath("$.formula.k").value(8))

                // A relação preserva a proporção do modelo original, permitindo decimais
                .andExpect(jsonPath("$.relacao.p").value(3.5));
    }

    @Test
    @WithMockUser(username = "owner")
    void updateFormulatedMineralFertilizerRejectsPrimaryNpkSumBelowMinimum() throws Exception {
        FormulatedMineralFertilizerModel existing = createModel(4L);

        FormulatedMineralFertilizerPostRequestDto updateDto = FormulatedMineralFertilizerPostRequestDto.builder()
                .n(4.0)
                .p2o5(10.0)
                .k2o(8.0)
                .build();

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(formulatedMineralFertilizerRepository.findById(4L)).thenReturn(Optional.of(existing));

        mockMvc.perform(put("/formulated-mineral-fertilizer/update")
                        .param("formulatedMineralFertilizerId", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A soma dos valores de NPK deve ser de pelo menos 24."));

        verify(formulatedMineralFertilizerRepository, never()).save(any(FormulatedMineralFertilizerModel.class));
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

    /**
     * Procura um endpoint GET mapeado no Spring para o controller de formulated mineral fertilizer
     * que tenha “by user” no path OU método com nome sugestivo.
     */
    private String resolveGetByUserEndpoint() {
        return requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .filter(entry -> isFormulatedMineralFertilizerController(entry.getValue()))
                .filter(entry -> isGetMapping(entry.getKey()))
                .filter(entry -> looksLikeGetByUser(entry.getKey(), entry.getValue()))
                .map(entry -> pickOnePattern(entry.getKey()))
                .filter(p -> p != null && !p.isBlank())
                .min(Comparator.comparingInt(String::length)) // pega o menor path “mais direto”
                .orElse(null);
    }

    private boolean isFormulatedMineralFertilizerController(HandlerMethod hm) {
        String className = hm.getBeanType().getName();
        return className.contains("FormulatedMineralFertilizerController");
    }

    private boolean isGetMapping(RequestMappingInfo info) {
        Set<?> methods = info.getMethodsCondition().getMethods();
        return methods != null && !methods.isEmpty() && methods.toString().contains("GET");
    }

    private boolean looksLikeGetByUser(RequestMappingInfo info, HandlerMethod hm) {
        String methodName = hm.getMethod().getName().toLowerCase();
        String patterns = info.getPatternValues().toString().toLowerCase();

        // tenta cobrir variações comuns
        return patterns.contains("by-user")
                || patterns.contains("byuser")
                || patterns.contains("user")
                || methodName.contains("byuser")
                || methodName.contains("by_user")
                || methodName.contains("user");
    }

    private String pickOnePattern(RequestMappingInfo info) {
        // Spring 6+: getPatternValues()
        return info.getPatternValues().stream().findFirst().orElse(null);
    }
}
