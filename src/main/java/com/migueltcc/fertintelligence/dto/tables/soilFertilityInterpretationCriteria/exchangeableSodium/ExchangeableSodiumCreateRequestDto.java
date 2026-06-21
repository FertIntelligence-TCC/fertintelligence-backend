package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeableSodiumCreateRequestDto {
    @JsonProperty("menor_teor_sodio_ctc_menor_4_3")
    private Double ctcLessThan43VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_menor_4_3")
    private Double ctcLessThan43LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_menor_4_3")
    private Double ctcLessThan43LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_menor_4_3")
    private Double ctcLessThan43MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_menor_4_3")
    private Double ctcLessThan43MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_menor_4_3")
    private Double ctcLessThan43HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_menor_4_3")
    private Double ctcLessThan43HighMax;

    @JsonProperty("maior_teor_sodio_ctc_menor_4_3")
    private Double ctcLessThan43VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86HighMax;

    @JsonProperty("maior_teor_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150HighMax;

    @JsonProperty("maior_teor_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_maior_15")
    private Double ctcGreaterThan15VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_maior_15")
    private Double ctcGreaterThan15LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_maior_15")
    private Double ctcGreaterThan15LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_maior_15")
    private Double ctcGreaterThan15MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_maior_15")
    private Double ctcGreaterThan15MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_maior_15")
    private Double ctcGreaterThan15HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_maior_15")
    private Double ctcGreaterThan15HighMax;

    @JsonProperty("maior_teor_sodio_ctc_maior_15")
    private Double ctcGreaterThan15VeryHighGreaterThan;

}
