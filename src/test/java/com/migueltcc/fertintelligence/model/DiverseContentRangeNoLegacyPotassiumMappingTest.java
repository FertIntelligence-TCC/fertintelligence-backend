package com.migueltcc.fertintelligence.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DiverseContentRangeNoLegacyPotassiumMappingTest {

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

    @Test
    void diverseContentRangeEntityDoesNotMapLegacyPotassiumColumns() {
        List<String> mappedColumnNames = Arrays.stream(DiverseContentRangeModel.class.getDeclaredFields())
                .map(field -> field.getAnnotation(Column.class))
                .filter(column -> column != null)
                .map(Column::name)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .toList();

        assertThat(mappedColumnNames).doesNotContainAnyElementsOf(LEGACY_POTASSIUM_COLUMNS);
        assertThat(fieldNames(DiverseContentRangeModel.class))
                .noneMatch(name -> containsLegacyPotassiumToken(name) || name.contains("potassium"));
    }

    @Test
    void diverseContentRangeDtosDoNotSerializeLegacyPotassiumPayload() {
        assertNoLegacyPotassiumJson(DiverseContentRangeCreateRequestDto.class);
        assertNoLegacyPotassiumJson(DiverseContentRangePostRequestDto.class);
        assertNoLegacyPotassiumJson(DiverseContentRangeResponseDto.class);
    }

    @Test
    void migrationsDoNotRecreateLegacyPotassiumColumnsOnDiverseContentRange() throws IOException {
        try (Stream<Path> migrations = Files.list(Path.of("src/main/resources/db/migration"))) {
            List<String> migrationSql = migrations
                    .filter(path -> path.getFileName().toString().endsWith(".sql"))
                    .map(this::readLowercase)
                    .toList();

            assertThat(migrationSql)
                    .noneMatch(sql -> sql.contains("alter table faixas_de_teores_diversos")
                            && sql.contains("add column")
                            && LEGACY_POTASSIUM_COLUMNS.stream().anyMatch(sql::contains));
        }
    }

    private List<String> fieldNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .map(Field::getName)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .toList();
    }

    private void assertNoLegacyPotassiumJson(Class<?> type) {
        assertThat(fieldNames(type)).noneMatch(this::containsLegacyPotassiumToken);

        List<String> jsonNames = Stream.concat(
                        Arrays.stream(type.getDeclaredFields()).flatMap(this::fieldJsonNames),
                        Arrays.stream(type.getDeclaredMethods()).flatMap(this::methodJsonNames))
                .map(name -> name.toLowerCase(Locale.ROOT))
                .toList();

        assertThat(jsonNames).doesNotContainAnyElementsOf(LEGACY_POTASSIUM_COLUMNS);
    }

    private Stream<String> fieldJsonNames(Field field) {
        return Stream.concat(jsonPropertyValue(field.getAnnotation(JsonProperty.class)),
                jsonAliasValues(field.getAnnotation(JsonAlias.class)));
    }

    private Stream<String> methodJsonNames(Method method) {
        return Stream.concat(jsonPropertyValue(method.getAnnotation(JsonProperty.class)),
                jsonAliasValues(method.getAnnotation(JsonAlias.class)));
    }

    private Stream<String> jsonPropertyValue(JsonProperty jsonProperty) {
        return jsonProperty == null || jsonProperty.value().isBlank()
                ? Stream.empty()
                : Stream.of(jsonProperty.value());
    }

    private Stream<String> jsonAliasValues(JsonAlias jsonAlias) {
        return jsonAlias == null ? Stream.empty() : Arrays.stream(jsonAlias.value());
    }

    private boolean containsLegacyPotassiumToken(String value) {
        return value.toLowerCase(Locale.ROOT).contains("potassio");
    }

    private String readLowercase(Path path) {
        try {
            return Files.readString(path).toLowerCase(Locale.ROOT);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler migration " + path, e);
        }
    }
}
