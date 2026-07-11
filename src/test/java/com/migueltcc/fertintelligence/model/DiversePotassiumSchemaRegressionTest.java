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

class DiversePotassiumSchemaRegressionTest {

    private static final List<String> JAVA_FIELDS = List.of(
            "potassium_low_f",
            "potassium_medium_i",
            "potassium_medium_f",
            "potassium_hight_i"
    );
    private static final List<String> DATABASE_COLUMNS = List.of(
            "TEOR_FINAL_BAIXO_POTASSIO",
            "TEOR_INICIAL_MEDIO_POTASSIO",
            "TEOR_FINAL_MEDIO_POTASSIO",
            "TEOR_INICIAL_ALTO_POTASSIO"
    );
    private static final Path MIGRATION = Path.of(
            "src/main/resources/db/migration/V20260711_01__activate_diverse_potassium_ranges.sql");

    @Test
    void entityAndDtosExposeTheSameFourIndependentPotassiumFields() throws Exception {
        for (int index = 0; index < JAVA_FIELDS.size(); index++) {
            String javaField = JAVA_FIELDS.get(index);
            Column column = DiverseContentRangeModel.class.getDeclaredField(javaField).getAnnotation(Column.class);

            assertThat(column).isNotNull();
            assertThat(column.name()).isEqualTo(DATABASE_COLUMNS.get(index));
            assertThat(DiverseContentRangeCreateRequestDto.class.getDeclaredField(javaField)).isNotNull();
            assertThat(DiverseContentRangePostRequestDto.class.getDeclaredField(javaField)).isNotNull();
            assertThat(DiverseContentRangeResponseDto.class.getDeclaredField(javaField)).isNotNull();
        }
    }

    @Test
    void additiveMigrationCreatesAllFourNullableColumnsAndPreservesMatchingLegacyValues() throws Exception {
        String sql = Files.readString(MIGRATION);

        assertThat(sql).contains(
                "ADD COLUMN IF NOT EXISTS TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION",
                "ADD COLUMN IF NOT EXISTS TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION",
                "ADD COLUMN IF NOT EXISTS TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION",
                "ADD COLUMN IF NOT EXISTS TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION",
                "TEOR_FINAL_BAIXO_POTASSIO = COALESCE(TEOR_FINAL_BAIXO_POTASSIO, LEGACY_TEOR_FINAL_BAIXO_POTASSIO)",
                "TEOR_INICIAL_MEDIO_POTASSIO = COALESCE(TEOR_INICIAL_MEDIO_POTASSIO, LEGACY_TEOR_INICIAL_MEDIO_POTASSIO)",
                "TEOR_FINAL_MEDIO_POTASSIO = COALESCE(TEOR_FINAL_MEDIO_POTASSIO, LEGACY_TEOR_FINAL_MEDIO_POTASSIO)",
                "TEOR_INICIAL_ALTO_POTASSIO = COALESCE(TEOR_INICIAL_ALTO_POTASSIO, LEGACY_TEOR_INICIAL_ALTO_POTASSIO)")
                .doesNotContain("NOT NULL", "DEFAULT", "DROP COLUMN", "DROP TABLE", "RENAME TO");

        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:diverse_potassium_migration;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", "sa", "")) {
            connection.createStatement().execute("""
                    CREATE TABLE FAIXAS_DE_TEORES_DIVERSOS (
                        ID BIGINT PRIMARY KEY,
                        LEGACY_TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION,
                        LEGACY_TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION,
                        LEGACY_TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION,
                        LEGACY_TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION
                    )
                    """);
            connection.createStatement().execute("""
                    INSERT INTO FAIXAS_DE_TEORES_DIVERSOS VALUES
                        (1, 1.1, 1.2, 2.2, 2.3),
                        (2, NULL, NULL, NULL, NULL)
                    """);
            String h2CompatibleSql = sql.replace("""
                    ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS
                        ADD COLUMN IF NOT EXISTS TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION,
                        ADD COLUMN IF NOT EXISTS TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION,
                        ADD COLUMN IF NOT EXISTS TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION,
                        ADD COLUMN IF NOT EXISTS TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION;
                    """, """
                    ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS ADD COLUMN IF NOT EXISTS TEOR_FINAL_BAIXO_POTASSIO DOUBLE PRECISION;
                    ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS ADD COLUMN IF NOT EXISTS TEOR_INICIAL_MEDIO_POTASSIO DOUBLE PRECISION;
                    ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS ADD COLUMN IF NOT EXISTS TEOR_FINAL_MEDIO_POTASSIO DOUBLE PRECISION;
                    ALTER TABLE FAIXAS_DE_TEORES_DIVERSOS ADD COLUMN IF NOT EXISTS TEOR_INICIAL_ALTO_POTASSIO DOUBLE PRECISION;
                    """);
            h2CompatibleSql = h2CompatibleSql.lines()
                    .filter(line -> !line.stripLeading().startsWith("--"))
                    .reduce("", (left, right) -> left + right + System.lineSeparator());
            for (String statement : h2CompatibleSql.split(";")) {
                if (!statement.isBlank()) connection.createStatement().execute(statement);
            }

            try (ResultSet columns = connection.createStatement().executeQuery("""
                    SELECT column_name, is_nullable
                    FROM information_schema.columns
                    WHERE table_name = 'faixas_de_teores_diversos'
                      AND column_name LIKE '%potassio'
                      AND column_name NOT LIKE 'legacy_%'
                    ORDER BY ordinal_position
                    """)) {
                for (String expectedColumn : DATABASE_COLUMNS) {
                    assertThat(columns.next()).isTrue();
                    assertThat(columns.getString("column_name")).isEqualTo(expectedColumn.toLowerCase());
                    assertThat(columns.getString("is_nullable")).isEqualTo("YES");
                }
                assertThat(columns.next()).isFalse();
            }

            try (ResultSet rows = connection.createStatement().executeQuery("""
                    SELECT TEOR_FINAL_BAIXO_POTASSIO, TEOR_INICIAL_MEDIO_POTASSIO,
                           TEOR_FINAL_MEDIO_POTASSIO, TEOR_INICIAL_ALTO_POTASSIO
                    FROM FAIXAS_DE_TEORES_DIVERSOS ORDER BY ID
                    """)) {
                assertThat(rows.next()).isTrue();
                assertThat(List.of(rows.getDouble(1), rows.getDouble(2), rows.getDouble(3), rows.getDouble(4)))
                        .containsExactly(1.1, 1.2, 2.2, 2.3);
                assertThat(rows.next()).isTrue();
                assertThat(rows.getObject(1)).isNull();
                assertThat(rows.getObject(2)).isNull();
                assertThat(rows.getObject(3)).isNull();
                assertThat(rows.getObject(4)).isNull();
            }
        }
    }
}
