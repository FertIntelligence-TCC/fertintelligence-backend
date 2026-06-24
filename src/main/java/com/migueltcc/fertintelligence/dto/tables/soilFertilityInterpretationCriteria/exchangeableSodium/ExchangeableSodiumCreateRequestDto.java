package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeableSodiumCreateRequestDto {
    @JsonProperty("novo_menor_teor_sodio_ctc_menor_4_3")
    @JsonAlias("menor_teor_sodio_ctc_menor_4_3")
    private Double ctcLessThan43VeryLowLessThan;

    @JsonProperty("novo_teor_inicial_baixo_sodio_ctc_menor_4_3")
    @JsonAlias("teor_inicial_baixo_sodio_ctc_menor_4_3")
    private Double ctcLessThan43LowMin;

    @JsonProperty("novo_teor_final_baixo_sodio_ctc_menor_4_3")
    @JsonAlias("teor_final_baixo_sodio_ctc_menor_4_3")
    private Double ctcLessThan43LowMax;

    @JsonProperty("novo_teor_inicial_medio_sodio_ctc_menor_4_3")
    @JsonAlias("teor_inicial_medio_sodio_ctc_menor_4_3")
    private Double ctcLessThan43MediumMin;

    @JsonProperty("novo_teor_final_medio_sodio_ctc_menor_4_3")
    @JsonAlias("teor_final_medio_sodio_ctc_menor_4_3")
    private Double ctcLessThan43MediumMax;

    @JsonProperty("novo_teor_inicial_alto_sodio_ctc_menor_4_3")
    @JsonAlias("teor_inicial_alto_sodio_ctc_menor_4_3")
    private Double ctcLessThan43HighMin;

    @JsonProperty("novo_teor_final_alto_sodio_ctc_menor_4_3")
    @JsonAlias("teor_final_alto_sodio_ctc_menor_4_3")
    private Double ctcLessThan43HighMax;

    @JsonProperty("novo_maior_teor_sodio_ctc_menor_4_3")
    @JsonAlias("maior_teor_sodio_ctc_menor_4_3")
    private Double ctcLessThan43VeryHighGreaterThan;

    @JsonProperty("novo_menor_teor_sodio_ctc_4_3_a_8_6")
    @JsonAlias("menor_teor_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86VeryLowLessThan;

    @JsonProperty("novo_teor_inicial_baixo_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_inicial_baixo_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86LowMin;

    @JsonProperty("novo_teor_final_baixo_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_final_baixo_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86LowMax;

    @JsonProperty("novo_teor_inicial_medio_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_inicial_medio_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86MediumMin;

    @JsonProperty("novo_teor_final_medio_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_final_medio_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86MediumMax;

    @JsonProperty("novo_teor_inicial_alto_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_inicial_alto_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86HighMin;

    @JsonProperty("novo_teor_final_alto_sodio_ctc_4_3_a_8_6")
    @JsonAlias("teor_final_alto_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86HighMax;

    @JsonProperty("novo_maior_teor_sodio_ctc_4_3_a_8_6")
    @JsonAlias("maior_teor_sodio_ctc_4_3_a_8_6")
    private Double ctcFrom43To86VeryHighGreaterThan;

    @JsonProperty("novo_menor_teor_sodio_ctc_8_7_a_15_0")
    @JsonAlias("menor_teor_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150VeryLowLessThan;

    @JsonProperty("novo_teor_inicial_baixo_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_inicial_baixo_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150LowMin;

    @JsonProperty("novo_teor_final_baixo_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_final_baixo_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150LowMax;

    @JsonProperty("novo_teor_inicial_medio_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_inicial_medio_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150MediumMin;

    @JsonProperty("novo_teor_final_medio_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_final_medio_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150MediumMax;

    @JsonProperty("novo_teor_inicial_alto_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_inicial_alto_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150HighMin;

    @JsonProperty("novo_teor_final_alto_sodio_ctc_8_7_a_15_0")
    @JsonAlias("teor_final_alto_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150HighMax;

    @JsonProperty("novo_maior_teor_sodio_ctc_8_7_a_15_0")
    @JsonAlias("maior_teor_sodio_ctc_8_7_a_15_0")
    private Double ctcFrom87To150VeryHighGreaterThan;

    @JsonProperty("novo_menor_teor_sodio_ctc_maior_15")
    @JsonAlias("menor_teor_sodio_ctc_maior_15")
    private Double ctcGreaterThan15VeryLowLessThan;

    @JsonProperty("novo_teor_inicial_baixo_sodio_ctc_maior_15")
    @JsonAlias("teor_inicial_baixo_sodio_ctc_maior_15")
    private Double ctcGreaterThan15LowMin;

    @JsonProperty("novo_teor_final_baixo_sodio_ctc_maior_15")
    @JsonAlias("teor_final_baixo_sodio_ctc_maior_15")
    private Double ctcGreaterThan15LowMax;

    @JsonProperty("novo_teor_inicial_medio_sodio_ctc_maior_15")
    @JsonAlias("teor_inicial_medio_sodio_ctc_maior_15")
    private Double ctcGreaterThan15MediumMin;

    @JsonProperty("novo_teor_final_medio_sodio_ctc_maior_15")
    @JsonAlias("teor_final_medio_sodio_ctc_maior_15")
    private Double ctcGreaterThan15MediumMax;

    @JsonProperty("novo_teor_inicial_alto_sodio_ctc_maior_15")
    @JsonAlias("teor_inicial_alto_sodio_ctc_maior_15")
    private Double ctcGreaterThan15HighMin;

    @JsonProperty("novo_teor_final_alto_sodio_ctc_maior_15")
    @JsonAlias("teor_final_alto_sodio_ctc_maior_15")
    private Double ctcGreaterThan15HighMax;

    @JsonProperty("novo_maior_teor_sodio_ctc_maior_15")
    @JsonAlias("maior_teor_sodio_ctc_maior_15")
    private Double ctcGreaterThan15VeryHighGreaterThan;

}
