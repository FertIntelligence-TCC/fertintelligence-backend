package com.migueltcc.fertintelligence.model;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DiversePotassiumSchemaRegressionTest {

    private static final List<String> JAVA_FIELDS = List.of(
            "potassium_low_f", "potassium_medium_i", "potassium_medium_f", "potassium_hight_i");
    private static final List<String> DATABASE_COLUMNS = List.of(
            "TEOR_FINAL_BAIXO_POTASSIO", "TEOR_INICIAL_MEDIO_POTASSIO",
            "TEOR_FINAL_MEDIO_POTASSIO", "TEOR_INICIAL_ALTO_POTASSIO");
    private static final List<String> LEGACY_COLUMNS = List.of(
            "LEGACY_TEOR_FINAL_BAIXO_POTASSIO", "LEGACY_TEOR_INICIAL_MEDIO_POTASSIO",
            "LEGACY_TEOR_FINAL_MEDIO_POTASSIO", "LEGACY_TEOR_INICIAL_ALTO_POTASSIO");
    private static final Path MIGRATION = Path.of(
            "src/main/resources/db/migration/V20260711_01__activate_diverse_potassium_ranges.sql");

    @Test
    void entityAndDtosExposeTheSameFourNullablePotassiumFields() throws Exception {
        for (int index = 0; index < JAVA_FIELDS.size(); index++) {
            String javaField = JAVA_FIELDS.get(index);
            Column column = DiverseContentRangeModel.class.getDeclaredField(javaField).getAnnotation(Column.class);

            assertThat(column).isNotNull();
            assertThat(column.name()).isEqualTo(DATABASE_COLUMNS.get(index));
            assertThat(column.nullable()).isTrue();
            assertThat(DiverseContentRangeCreateRequestDto.class.getDeclaredField(javaField)).isNotNull();
            assertThat(DiverseContentRangePostRequestDto.class.getDeclaredField(javaField)).isNotNull();
            assertThat(DiverseContentRangeResponseDto.class.getDeclaredField(javaField)).isNotNull();
        }

        DiverseContentRangeResponseDto dto = DiverseContentRangeModel.builder().build().toDto();
        assertThat(dto.getPotassium_low_f()).isNull();
        assertThat(dto.getPotassium_medium_i()).isNull();
        assertThat(dto.getPotassium_medium_f()).isNull();
        assertThat(dto.getPotassium_hight_i()).isNull();
    }

    @Test
    void migrationUsesAdditiveNullableColumnsAndGuardsEveryOptionalLegacyReference() throws Exception {
        String sql = Files.readString(MIGRATION);

        for (String column : DATABASE_COLUMNS) {
            assertThat(sql).contains("ADD COLUMN IF NOT EXISTS " + column + " DOUBLE PRECISION");
        }
        for (String legacy : LEGACY_COLUMNS) {
            assertThat(sql).contains("column_name = '" + legacy.toLowerCase() + "'");
            assertThat(sql).contains(legacy.toLowerCase() + ")'");
        }
        assertThat(sql).doesNotContain("NOT NULL", "DEFAULT", "DROP COLUMN", "DROP TABLE", "RENAME TO",
                "\nUPDATE FAIXAS_DE_TEORES_DIVERSOS");
        assertThat(sql.split("EXECUTE '", -1)).hasSize(5);
    }

    @Test
    void migrationRunsOnPostgresWithAbsentCurrentAndOptionalLegacyColumnsAndIsIdempotent() throws Exception {
        String url = System.getenv("MIGRATION_TEST_POSTGRES_URL");
        assumeTrue(url != null && !url.isBlank(),
                "Defina MIGRATION_TEST_POSTGRES_URL para executar a regressao em PostgreSQL real");
        String user = System.getenv().getOrDefault("MIGRATION_TEST_POSTGRES_USER", "postgres");
        String password = System.getenv().getOrDefault("MIGRATION_TEST_POSTGRES_PASSWORD", "postgres");
        String sql = Files.readString(MIGRATION);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            verifyWithoutPotassiumColumns(connection, sql);
            verifyWithCurrentColumnsOnly(connection, sql);
            verifyLegacyCopyAndCurrentValuePreservation(connection, sql);
        }
    }

    private void verifyWithoutPotassiumColumns(Connection connection, String sql) throws Exception {
        resetSchema(connection, "k_none");
        connection.createStatement().execute("CREATE TABLE faixas_de_teores_diversos (id BIGINT PRIMARY KEY)");
        connection.createStatement().execute("INSERT INTO faixas_de_teores_diversos VALUES (1)");
        executeTwice(connection, sql);
        assertCurrentValues(connection, null, null, null, null);
    }

    private void verifyWithCurrentColumnsOnly(Connection connection, String sql) throws Exception {
        resetSchema(connection, "k_current");
        connection.createStatement().execute("""
                CREATE TABLE faixas_de_teores_diversos (
                    id BIGINT PRIMARY KEY,
                    teor_final_baixo_potassio DOUBLE PRECISION,
                    teor_inicial_medio_potassio DOUBLE PRECISION,
                    teor_final_medio_potassio DOUBLE PRECISION,
                    teor_inicial_alto_potassio DOUBLE PRECISION)
                """);
        connection.createStatement().execute(
                "INSERT INTO faixas_de_teores_diversos VALUES (1, 10.1, 10.2, 10.3, 10.4)");
        executeTwice(connection, sql);
        assertCurrentValues(connection, 10.1, 10.2, 10.3, 10.4);
    }

    private void verifyLegacyCopyAndCurrentValuePreservation(Connection connection, String sql) throws Exception {
        resetSchema(connection, "k_legacy");
        connection.createStatement().execute("""
                CREATE TABLE faixas_de_teores_diversos (
                    id BIGINT PRIMARY KEY,
                    teor_final_baixo_potassio DOUBLE PRECISION,
                    legacy_teor_final_baixo_potassio DOUBLE PRECISION,
                    legacy_teor_inicial_medio_potassio DOUBLE PRECISION,
                    legacy_teor_final_medio_potassio DOUBLE PRECISION,
                    legacy_teor_inicial_alto_potassio DOUBLE PRECISION)
                """);
        connection.createStatement().execute(
                "INSERT INTO faixas_de_teores_diversos VALUES (1, 99.0, 1.1, 1.2, 2.2, 2.3)");
        executeTwice(connection, sql);
        assertCurrentValues(connection, 99.0, 1.2, 2.2, 2.3);
    }

    private void resetSchema(Connection connection, String schema) throws Exception {
        connection.createStatement().execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
        connection.createStatement().execute("CREATE SCHEMA " + schema);
        connection.createStatement().execute("SET search_path TO " + schema);
    }

    private void executeTwice(Connection connection, String sql) throws Exception {
        connection.createStatement().execute(sql);
        connection.createStatement().execute(sql);
    }

    private void assertCurrentValues(Connection connection, Double... expected) throws Exception {
        try (ResultSet row = connection.createStatement().executeQuery("""
                SELECT teor_final_baixo_potassio, teor_inicial_medio_potassio,
                       teor_final_medio_potassio, teor_inicial_alto_potassio
                FROM faixas_de_teores_diversos WHERE id = 1
                """)) {
            assertThat(row.next()).isTrue();
            for (int index = 0; index < expected.length; index++) {
                assertThat(row.getObject(index + 1, Double.class)).isEqualTo(expected[index]);
            }
        }
    }
}
