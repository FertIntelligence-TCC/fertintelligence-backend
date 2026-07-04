package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.TestProfileResolver;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.user.DataNasc;
import com.migueltcc.fertintelligence.composedAttributes.user.Formacao;
import com.migueltcc.fertintelligence.composedAttributes.user.Genero;
import com.migueltcc.fertintelligence.composedAttributes.user.Telefone;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KContentAndDoseModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.KContentAndDoseRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.ApplicationEventsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:fertintelligence_endpoint_sql_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.properties.hibernate.session_factory.statement_inspector=com.migueltcc.fertintelligence.controller.SoilFertilityInterpretationCriteriaTableEndpointSqlRegressionTest$SqlCaptureInspector"
})
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestExecutionListeners(
        listeners = {
                ServletTestExecutionListener.class,
                DirtiesContextBeforeModesTestExecutionListener.class,
                ApplicationEventsTestExecutionListener.class,
                DependencyInjectionTestExecutionListener.class,
                DirtiesContextTestExecutionListener.class,
                WithSecurityContextTestExecutionListener.class
        },
        mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class SoilFertilityInterpretationCriteriaTableEndpointSqlRegressionTest {

    private static final List<String> LEGACY_POTASSIUM_COLUMNS = List.of(
            "menor_teor_potassio",
            "teor_inicial_baixo_potassio",
            "teor_final_baixo_potassio",
            "teor_inicial_medio_potassio",
            "teor_final_medio_potassio",
            "teor_inicial_alto_potassio",
            "teor_final_alto_potassio",
            "maior_teor_potassio"
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository tableRepository;

    @Autowired
    private DiverseContentRangeRepository diverseContentRangeRepository;

    @Autowired
    private KContentAndDoseRepository kContentAndDoseRepository;

    @BeforeEach
    void setUp() {
        kContentAndDoseRepository.deleteAll();
        diverseContentRangeRepository.deleteAll();
        tableRepository.deleteAll();
        userRepository.deleteAll();
        SqlCaptureInspector.clear();

        UserModel owner = user("testuser", "11111111111", Cargo.PROPRIETARIO);
        UserModel supreme = user("supreme", "22222222222", Cargo.USUARIO_SUPREMO);
        UserModel publicCreator = user("publicuser", "33333333333", Cargo.GERENTE);
        userRepository.saveAll(List.of(owner, supreme, publicCreator));

        SoilFertilityInterpretationCriteriaTableModel ownerTable = table("Tabela minhas", owner, false);
        SoilFertilityInterpretationCriteriaTableModel defaultTable = table("Tabela padrao", supreme, false);
        SoilFertilityInterpretationCriteriaTableModel publicTable = table("Tabela publica", publicCreator, true);
        tableRepository.saveAll(List.of(ownerTable, defaultTable, publicTable));

        saveAuxiliaries(ownerTable);
        saveAuxiliaries(defaultTable);
        saveAuxiliaries(publicTable);

        SqlCaptureInspector.clear();
    }

    @Test
    @WithMockUser(username = "testuser")
    void managementListEndpointsDoNotSelectLegacyPotassiumColumnsFromDiverseContentRanges() throws Exception {
        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get-all")
                        .param("grupo", "MINHAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get-all-default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(get("/soil-fertility-interpretation-criteria-table/get-all-public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        assertThat(kContentAndDoseRepository.count()).isEqualTo(3);
        assertThat(diverseContentRangeRepository.count()).isEqualTo(3);
        assertThat(SqlCaptureInspector.sql())
                .noneMatch(this::referencesLegacyPotassiumColumn);
    }

    private boolean referencesLegacyPotassiumColumn(String sql) {
        String normalized = sql.toLowerCase(Locale.ROOT);
        return normalized.contains("faixas_de_teores_diversos")
                && LEGACY_POTASSIUM_COLUMNS.stream().anyMatch(normalized::contains);
    }

    private UserModel user(String username, String cpf, Cargo cargo) {
        return UserModel.builder()
                .username(username)
                .cpf(cpf)
                .email(username + "@fertintelligence.test")
                .datanasc(new DataNasc(1, 1, 1990))
                .genero(Genero.OUTRO)
                .telefone(new Telefone("+55", "85", "999999999"))
                .formacao(Formacao.GRADUACAO)
                .profissao("Agronomo")
                .cargo(cargo)
                .password("password")
                .name(username)
                .build();
    }

    private SoilFertilityInterpretationCriteriaTableModel table(String name, UserModel creator, boolean publicTable) {
        return SoilFertilityInterpretationCriteriaTableModel.builder()
                .creator(creator)
                .name(name)
                .description(name)
                .region(Regiao.NORDESTE)
                .publicTable(publicTable)
                .build();
    }

    private void saveAuxiliaries(SoilFertilityInterpretationCriteriaTableModel table) {
        diverseContentRangeRepository.save(filledDiverseContentRange(table));
        kContentAndDoseRepository.save(kContentAndDose(table));
    }

    private DiverseContentRangeModel filledDiverseContentRange(SoilFertilityInterpretationCriteriaTableModel table) {
        DiverseContentRangeModel model = DiverseContentRangeModel.builder()
                .table(table)
                .build();
        for (Field field : DiverseContentRangeModel.class.getDeclaredFields()) {
            if (field.getType().equals(Double.class)) {
                field.setAccessible(true);
                try {
                    field.set(model, 1.0);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Nao foi possivel preencher " + field.getName(), e);
                }
            }
        }
        return model;
    }

    private KContentAndDoseModel kContentAndDose(SoilFertilityInterpretationCriteriaTableModel table) {
        return KContentAndDoseModel.builder()
                .table(table)
                .lessThan40LowContentLessThan(1.0)
                .lessThan40DoseForLowContent(60.0)
                .lessThan40MediumLowerContent(1.0)
                .lessThan40MediumHigherContent(2.0)
                .lessThan40DoseForMediumContent(40.0)
                .lessThan40AdequateLowerContent(2.0)
                .lessThan40AdequateHigherContent(3.0)
                .lessThan40DoseForAdequateContent(20.0)
                .lessThan40HighContentGreaterThan(3.0)
                .lessThan40DoseForHighContent(0.0)
                .greaterOrEqual40LowContentLessThan(1.0)
                .greaterOrEqual40DoseForLowContent(80.0)
                .greaterOrEqual40MediumLowerContent(1.0)
                .greaterOrEqual40MediumHigherContent(2.0)
                .greaterOrEqual40DoseForMediumContent(60.0)
                .greaterOrEqual40AdequateLowerContent(2.0)
                .greaterOrEqual40AdequateHigherContent(3.0)
                .greaterOrEqual40DoseForAdequateContent(40.0)
                .greaterOrEqual40HighContentGreaterThan(3.0)
                .greaterOrEqual40DoseForHighContent(0.0)
                .build();
    }

    public static final class SqlCaptureInspector implements StatementInspector {
        private static final List<String> SQL = new CopyOnWriteArrayList<>();

        @Override
        public String inspect(String sql) {
            SQL.add(sql);
            return sql;
        }

        static void clear() {
            SQL.clear();
        }

        static List<String> sql() {
            return List.copyOf(SQL);
        }
    }
}
