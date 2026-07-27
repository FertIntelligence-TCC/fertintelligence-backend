package com.migueltcc.fertintelligence.model;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FertilizerExtendedFieldsSchemaTest {

    private static final Path MIGRATION = Path.of(
            "src/main/resources/db/migration/V20260727_01__add_fertilizer_moisture_fourth_year_and_heavy_metals.sql");

    private static final Map<String, List<String>> TABLE_COLUMNS = new LinkedHashMap<>();
    static {
        TABLE_COLUMNS.put("adubos_verdes", List.of(
                "porcentagem_umidade_incorporacao", "taxa_mineralizacao_quarto_ano_percentual"));
        TABLE_COLUMNS.put("adubos_organicos", List.of(
                "taxa_mineralizacao_quarto_ano_percentual", "arsenio_mg_kg", "cadmio_mg_kg",
                "cromio_mg_kg", "chumbo_mg_kg", "mercurio_mg_kg", "niquel_mg_kg", "selenio_mg_kg"));
        TABLE_COLUMNS.put("adubos_organo_minerais", List.of(
                "taxa_mineralizacao_primeiro_ano_percentual",
                "taxa_mineralizacao_segundo_ano_percentual",
                "taxa_mineralizacao_terceiro_ano_percentual",
                "taxa_mineralizacao_quarto_ano_percentual"));
    }

    @Test
    void entitiesAndDtosExposeEveryNullableField() throws Exception {
        assertContract(GreenFertilizerModel.class, GreenFertilizerCreateRequestDto.class,
                GreenFertilizerPostRequestDto.class, GreenFertilizerResponseDto.class,
                Map.of("umidadeIncorporacaoPercentual", "PORCENTAGEM_UMIDADE_INCORPORACAO",
                        "taxaMineralizacaoQuartoAnoPercentual", "TAXA_MINERALIZACAO_QUARTO_ANO_PERCENTUAL"));
        assertContract(OrganicFertilizerModel.class, OrganicFertilizerCreateRequestDto.class,
                OrganicFertilizerPostRequestDto.class, OrganicFertilizerResponseDto.class,
                Map.of("taxaMineralizacaoQuartoAnoPercentual", "TAXA_MINERALIZACAO_QUARTO_ANO_PERCENTUAL",
                        "arsenioMgKg", "ARSENIO_MG_KG", "cadmioMgKg", "CADMIO_MG_KG",
                        "cromioMgKg", "CROMIO_MG_KG", "chumboMgKg", "CHUMBO_MG_KG",
                        "mercurioMgKg", "MERCURIO_MG_KG", "niquelMgKg", "NIQUEL_MG_KG",
                        "selenioMgKg", "SELENIO_MG_KG"));
        assertContract(OrganoMineralFertilizerModel.class, OrganoMineralFertilizerCreateRequestDto.class,
                OrganoMineralFertilizerPostRequestDto.class, OrganoMineralFertilizerResponseDto.class,
                Map.of("taxaMineralizacaoPrimeiroAnoPercentual", "TAXA_MINERALIZACAO_PRIMEIRO_ANO_PERCENTUAL",
                        "taxaMineralizacaoSegundoAnoPercentual", "TAXA_MINERALIZACAO_SEGUNDO_ANO_PERCENTUAL",
                        "taxaMineralizacaoTerceiroAnoPercentual", "TAXA_MINERALIZACAO_TERCEIRO_ANO_PERCENTUAL",
                        "taxaMineralizacaoQuartoAnoPercentual", "TAXA_MINERALIZACAO_QUARTO_ANO_PERCENTUAL"));
    }

    @Test
    void migrationIsIncrementalNullableAndHasNoDefaultsOrBackfill() throws Exception {
        String sql = Files.readString(MIGRATION);
        String normalized = sql.toUpperCase();
        TABLE_COLUMNS.values().stream().flatMap(List::stream).forEach(column ->
                assertThat(normalized).contains("ADD COLUMN IF NOT EXISTS "
                        + column.toUpperCase() + " DOUBLE PRECISION"));
        assertThat(normalized).doesNotContain("NOT NULL", "DEFAULT", "UPDATE ", "INSERT ", "DELETE ",
                "DROP COLUMN", "DROP TABLE");
    }

    @Test
    void migrationRunsTwiceOnPostgresAndLeavesEveryColumnNullableWithoutDefault() throws Exception {
        String url = System.getenv("MIGRATION_TEST_POSTGRES_URL");
        assumeTrue(url != null && !url.isBlank(),
                "Defina MIGRATION_TEST_POSTGRES_URL para executar a regressao em PostgreSQL real");
        String user = System.getenv().getOrDefault("MIGRATION_TEST_POSTGRES_USER", "postgres");
        String password = System.getenv().getOrDefault("MIGRATION_TEST_POSTGRES_PASSWORD", "postgres");
        String sql = Files.readString(MIGRATION);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            connection.createStatement().execute("DROP SCHEMA IF EXISTS fertilizer_fields CASCADE");
            connection.createStatement().execute("CREATE SCHEMA fertilizer_fields");
            connection.createStatement().execute("SET search_path TO fertilizer_fields");
            for (String table : TABLE_COLUMNS.keySet()) {
                connection.createStatement().execute("CREATE TABLE " + table + " (id BIGINT PRIMARY KEY)");
            }
            connection.createStatement().execute(
                    "ALTER TABLE adubos_organicos ADD COLUMN arsenio_mg_kg DOUBLE PRECISION");
            connection.createStatement().execute(sql);
            connection.createStatement().execute(sql);

            for (Map.Entry<String, List<String>> entry : TABLE_COLUMNS.entrySet()) {
                for (String column : entry.getValue()) {
                    try (ResultSet result = connection.createStatement().executeQuery("""
                            SELECT data_type, is_nullable, column_default
                            FROM information_schema.columns
                            WHERE table_schema = 'fertilizer_fields'
                              AND table_name = '%s' AND column_name = '%s'
                            """.formatted(entry.getKey(), column))) {
                        assertThat(result.next()).isTrue();
                        assertThat(result.getString("data_type")).isEqualTo("double precision");
                        assertThat(result.getString("is_nullable")).isEqualTo("YES");
                        assertThat(result.getObject("column_default")).isNull();
                    }
                }
            }
            connection.createStatement().execute("DROP SCHEMA fertilizer_fields CASCADE");
        }
    }

    private void assertContract(Class<?> entity, Class<?> createDto, Class<?> updateDto, Class<?> responseDto,
                                Map<String, String> fields) throws Exception {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            Column column = entity.getDeclaredField(entry.getKey()).getAnnotation(Column.class);
            assertThat(column).isNotNull();
            assertThat(column.name()).isEqualTo(entry.getValue());
            assertThat(column.nullable()).isTrue();
            assertThat(createDto.getDeclaredField(entry.getKey()).getType()).isEqualTo(Double.class);
            assertThat(updateDto.getDeclaredField(entry.getKey()).getType()).isEqualTo(Double.class);
            assertThat(responseDto.getDeclaredField(entry.getKey()).getType()).isEqualTo(Double.class);
        }
    }
}
