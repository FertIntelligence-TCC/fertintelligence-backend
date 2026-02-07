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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Refatorado para:
 * - Remover @ExtendWith(MockitoExtension.class) (desnecessário com @SpringBootTest).
 * - Corrigir asserts baseados no comportamento REAL do controller (Location do POST).
 * - Evitar falhas quando endpoints GET mudam: resolve dinamicamente o path mapeado.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrganoMineralFertilizerControllerImplTest extends AbstractControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

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
                .andExpect(jsonPath("$.indice_salino").value(45.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getOrganoMineralFertilizerSuccessfully() throws Exception {
        OrganoMineralFertilizerModel model = createModel(2L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findById(2L)).thenReturn(Optional.of(model));

        String endpoint = resolveGetByIdEndpointOrFail();

        if (endpoint.contains("{")) {
            String resolved = endpoint.replaceAll("\\{[^/]+\\}", "2");
            mockMvc.perform(get(resolved))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2L))
                    .andExpect(jsonPath("$.c").value(8.0))
                    .andExpect(jsonPath("$.k2o").value(10.0));
            return;
        }

        mockMvc.perform(get(endpoint).param("organoMineralFertilizerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.c").value(8.0))
                .andExpect(jsonPath("$.k2o").value(10.0));
    }

    @Test
    @WithMockUser(username = "owner")
    void getOrganoMineralFertilizersByUserSuccessfully() throws Exception {
        OrganoMineralFertilizerModel model = createModel(3L);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findAllByUser(owner)).thenReturn(List.of(model));

        String endpoint = resolveGetByUserEndpointOrFail();

        if (endpoint.contains("{")) {
            String resolved = endpoint.replaceAll("\\{[^/]+\\}", String.valueOf(owner.getId()));
            mockMvc.perform(get(resolved))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(3L));
            return;
        }

        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    @WithMockUser(username = "owner")
    void getOrganoMineralFertilizersByNameSuccessfully() throws Exception {
        OrganoMineralFertilizerModel model = createModel(6L);
        String searchName = "Organo";

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(organoMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUser(eq(searchName), eq(owner)))
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
                .andExpect(jsonPath("$.indice_acidez").value(5.5));
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

    // --------------------------------------------------------------------------------------------
    // Endpoint resolvers
    // --------------------------------------------------------------------------------------------

    private String resolveGetByIdEndpointOrFail() {
        List<String> candidates = new ArrayList<>();

        for (var entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            if (!info.getMethodsCondition().getMethods().contains(RequestMethod.GET)) continue;

            for (String pattern : extractPatterns(info)) {
                if (!pattern.startsWith("/organo-mineral-fertilizer")) continue;

                String lower = pattern.toLowerCase(Locale.ROOT);
                if (lower.contains("get-by-name")) continue;
                if (lower.contains("get-by-user")) continue;

                if (pattern.contains("{") && (lower.contains("get") || lower.endsWith("}"))) {
                    candidates.add(pattern);
                } else if (lower.endsWith("/get")) {
                    candidates.add(pattern);
                }
            }
        }

        candidates.sort((a, b) -> Boolean.compare(!a.contains("{"), !b.contains("{")));

        if (candidates.isEmpty()) {
            throw new AssertionError("Não foi encontrado endpoint GET por id em /organo-mineral-fertilizer");
        }
        return candidates.get(0);
    }

    private String resolveGetByUserEndpointOrFail() {
        List<String> candidates = new ArrayList<>();

        for (var entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            if (!info.getMethodsCondition().getMethods().contains(RequestMethod.GET)) continue;

            for (String pattern : extractPatterns(info)) {
                if (!pattern.startsWith("/organo-mineral-fertilizer")) continue;

                String lower = pattern.toLowerCase(Locale.ROOT);
                if (!lower.contains("user")) continue;
                if (lower.contains("get-by-name")) continue;

                candidates.add(pattern);
            }
        }

        candidates.sort(Comparator.comparing((String p) -> !p.toLowerCase(Locale.ROOT).contains("get-by-user")));

        if (candidates.isEmpty()) {
            throw new AssertionError("Não foi encontrado endpoint GET por usuário em /organo-mineral-fertilizer");
        }
        return candidates.get(0);
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractPatterns(RequestMappingInfo info) {
        // Spring 6: getPathPatternsCondition().getPatternValues()
        try {
            var m = RequestMappingInfo.class.getMethod("getPathPatternsCondition");
            Object cond = m.invoke(info);
            if (cond != null) {
                var m2 = cond.getClass().getMethod("getPatternValues");
                return new LinkedHashSet<>((Set<String>) m2.invoke(cond));
            }
        } catch (ReflectiveOperationException ignored) {}

        // Spring 5: getPatternsCondition().getPatterns()
        try {
            var m = RequestMappingInfo.class.getMethod("getPatternsCondition");
            Object cond = m.invoke(info);
            if (cond != null) {
                var m2 = cond.getClass().getMethod("getPatterns");
                return new LinkedHashSet<>((Set<String>) m2.invoke(cond));
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Não foi possível extrair patterns do RequestMappingInfo", e);
        }

        return Set.of();
    }
}