package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeableSodiumRequestDtoJsonMappingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRequestDtoReadsFrontendFieldNames() throws Exception {
        String payload = """
                {
                  "menor_teor_sodio_ctc_menor_4_3": 1,
                  "teor_inicial_baixo_sodio_ctc_menor_4_3": 2,
                  "maior_teor_sodio_ctc_maior_15": 32
                }
                """;

        ExchangeableSodiumCreateRequestDto dto = objectMapper.readValue(payload, ExchangeableSodiumCreateRequestDto.class);

        assertThat(dto.getCtcLessThan43VeryLowLessThan()).isEqualTo(1.0);
        assertThat(dto.getCtcLessThan43LowMin()).isEqualTo(2.0);
        assertThat(dto.getCtcGreaterThan15VeryHighGreaterThan()).isEqualTo(32.0);
    }

    @Test
    void postRequestDtoReadsFrontendFieldNames() throws Exception {
        String payload = """
                {
                  "menor_teor_sodio_ctc_menor_4_3": 1,
                  "teor_inicial_baixo_sodio_ctc_menor_4_3": 2,
                  "maior_teor_sodio_ctc_maior_15": 32
                }
                """;

        ExchangeableSodiumPostRequestDto dto = objectMapper.readValue(payload, ExchangeableSodiumPostRequestDto.class);

        assertThat(dto.getCtcLessThan43VeryLowLessThan()).isEqualTo(1.0);
        assertThat(dto.getCtcLessThan43LowMin()).isEqualTo(2.0);
        assertThat(dto.getCtcGreaterThan15VeryHighGreaterThan()).isEqualTo(32.0);
    }
}
