package com.migueltcc.fertintelligence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendedLimestoneTypeContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void activeContractSerializesOnlyLowAndHighRatios() throws Exception {
        JsonNode json = objectMapper.valueToTree(RecommendedLimestoneTypeResponseDto.builder()
                .id(1L).tableId(2L).caMgLowRatio(3d).caMgHighRatio(4d)
                .caMgLowLegend("Calcário calcítico (Teor de MgO menor que 5%)")
                .caMgHighLegend("Calcário dolomítico (Teor de MgO igual ou maior que 5%)")
                .observations("observação").sources("fonte").build());

        assertThat(json.get("relacao_ca_mg_baixa").asDouble()).isEqualTo(3d);
        assertThat(json.get("relacao_ca_mg_alta").asDouble()).isEqualTo(4d);
        assertThat(json.has("relacao_ca_mg_media_menor_valor")).isFalse();
        assertThat(json.has("relacao_ca_mg_media_maior_valor")).isFalse();
    }

    @Test
    void legacyIntermediateFieldsAreIgnoredOnCreateAndUpdate() throws Exception {
        String legacyJson = """
                {"relacao_ca_mg_baixa":3.0,"relacao_ca_mg_media_menor_valor":3.2,
                 "relacao_ca_mg_media_maior_valor":3.8,"relacao_ca_mg_alta":4.0,
                 "observacoes":"preservada","fontes":"preservada"}
                """;

        RecommendedLimestoneTypeCreateRequestDto create = objectMapper.readValue(
                legacyJson, RecommendedLimestoneTypeCreateRequestDto.class);
        RecommendedLimestoneTypePostRequestDto update = objectMapper.readValue(
                legacyJson, RecommendedLimestoneTypePostRequestDto.class);

        assertThat(create.getCaMgLowRatio()).isEqualTo(3d);
        assertThat(create.getCaMgHighRatio()).isEqualTo(4d);
        assertThat(create.getObservations()).isEqualTo("preservada");
        assertThat(update.getCaMgLowRatio()).isEqualTo(3d);
        assertThat(update.getCaMgHighRatio()).isEqualTo(4d);
    }
}
