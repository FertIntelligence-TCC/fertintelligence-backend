package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeableSodiumPostRequestDto {
    @JsonProperty("menor_teor_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_veryLowLessThan", "novo_ctc_menor_4_3_veryLowLessThan"})
    private Double ctcLessThan43VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_lowMin", "novo_ctc_menor_4_3_lowMin"})
    private Double ctcLessThan43LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_lowMax", "novo_ctc_menor_4_3_lowMax"})
    private Double ctcLessThan43LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_mediumMin", "novo_ctc_menor_4_3_mediumMin"})
    private Double ctcLessThan43MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_mediumMax", "novo_ctc_menor_4_3_mediumMax"})
    private Double ctcLessThan43MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_highMin", "novo_ctc_menor_4_3_highMin"})
    private Double ctcLessThan43HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_highMax", "novo_ctc_menor_4_3_highMax"})
    private Double ctcLessThan43HighMax;

    @JsonProperty("maior_teor_sodio_ctc_menor_4_3")
    @JsonAlias({"ctc_menor_4_3_veryHighGreaterThan", "novo_ctc_menor_4_3_veryHighGreaterThan"})
    private Double ctcLessThan43VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_veryLowLessThan", "novo_ctc_4_3_a_8_6_veryLowLessThan"})
    private Double ctcFrom43To86VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_lowMin", "novo_ctc_4_3_a_8_6_lowMin"})
    private Double ctcFrom43To86LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_lowMax", "novo_ctc_4_3_a_8_6_lowMax"})
    private Double ctcFrom43To86LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_mediumMin", "novo_ctc_4_3_a_8_6_mediumMin"})
    private Double ctcFrom43To86MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_mediumMax", "novo_ctc_4_3_a_8_6_mediumMax"})
    private Double ctcFrom43To86MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_highMin", "novo_ctc_4_3_a_8_6_highMin"})
    private Double ctcFrom43To86HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_highMax", "novo_ctc_4_3_a_8_6_highMax"})
    private Double ctcFrom43To86HighMax;

    @JsonProperty("maior_teor_sodio_ctc_4_3_a_8_6")
    @JsonAlias({"ctc_4_3_a_8_6_veryHighGreaterThan", "novo_ctc_4_3_a_8_6_veryHighGreaterThan"})
    private Double ctcFrom43To86VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_veryLowLessThan", "novo_ctc_8_7_a_15_0_veryLowLessThan"})
    private Double ctcFrom87To150VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_lowMin", "novo_ctc_8_7_a_15_0_lowMin"})
    private Double ctcFrom87To150LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_lowMax", "novo_ctc_8_7_a_15_0_lowMax"})
    private Double ctcFrom87To150LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_mediumMin", "novo_ctc_8_7_a_15_0_mediumMin"})
    private Double ctcFrom87To150MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_mediumMax", "novo_ctc_8_7_a_15_0_mediumMax"})
    private Double ctcFrom87To150MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_highMin", "novo_ctc_8_7_a_15_0_highMin"})
    private Double ctcFrom87To150HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_highMax", "novo_ctc_8_7_a_15_0_highMax"})
    private Double ctcFrom87To150HighMax;

    @JsonProperty("maior_teor_sodio_ctc_8_7_a_15_0")
    @JsonAlias({"ctc_8_7_a_15_0_veryHighGreaterThan", "novo_ctc_8_7_a_15_0_veryHighGreaterThan"})
    private Double ctcFrom87To150VeryHighGreaterThan;

    @JsonProperty("menor_teor_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_veryLowLessThan", "novo_ctc_maior_15_veryLowLessThan"})
    private Double ctcGreaterThan15VeryLowLessThan;

    @JsonProperty("teor_inicial_baixo_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_lowMin", "novo_ctc_maior_15_lowMin"})
    private Double ctcGreaterThan15LowMin;

    @JsonProperty("teor_final_baixo_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_lowMax", "novo_ctc_maior_15_lowMax"})
    private Double ctcGreaterThan15LowMax;

    @JsonProperty("teor_inicial_medio_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_mediumMin", "novo_ctc_maior_15_mediumMin"})
    private Double ctcGreaterThan15MediumMin;

    @JsonProperty("teor_final_medio_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_mediumMax", "novo_ctc_maior_15_mediumMax"})
    private Double ctcGreaterThan15MediumMax;

    @JsonProperty("teor_inicial_alto_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_highMin", "novo_ctc_maior_15_highMin"})
    private Double ctcGreaterThan15HighMin;

    @JsonProperty("teor_final_alto_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_highMax", "novo_ctc_maior_15_highMax"})
    private Double ctcGreaterThan15HighMax;

    @JsonProperty("maior_teor_sodio_ctc_maior_15")
    @JsonAlias({"ctc_maior_15_veryHighGreaterThan", "novo_ctc_maior_15_veryHighGreaterThan"})
    private Double ctcGreaterThan15VeryHighGreaterThan;

}
