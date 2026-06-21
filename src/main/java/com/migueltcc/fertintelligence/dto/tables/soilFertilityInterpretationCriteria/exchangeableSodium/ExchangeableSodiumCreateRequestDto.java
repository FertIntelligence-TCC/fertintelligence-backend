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
public class ExchangeableSodiumCreateRequestDto {
    @JsonProperty("ctc_menor_4_3_veryLowLessThan")
    @JsonAlias("ctcLessThan43VeryLowLessThan")
    private Double ctcLessThan43VeryLowLessThan;

    @JsonProperty("ctc_menor_4_3_lowMin")
    @JsonAlias("ctcLessThan43LowMin")
    private Double ctcLessThan43LowMin;

    @JsonProperty("ctc_menor_4_3_lowMax")
    @JsonAlias("ctcLessThan43LowMax")
    private Double ctcLessThan43LowMax;

    @JsonProperty("ctc_menor_4_3_mediumMin")
    @JsonAlias("ctcLessThan43MediumMin")
    private Double ctcLessThan43MediumMin;

    @JsonProperty("ctc_menor_4_3_mediumMax")
    @JsonAlias("ctcLessThan43MediumMax")
    private Double ctcLessThan43MediumMax;

    @JsonProperty("ctc_menor_4_3_highMin")
    @JsonAlias("ctcLessThan43HighMin")
    private Double ctcLessThan43HighMin;

    @JsonProperty("ctc_menor_4_3_highMax")
    @JsonAlias("ctcLessThan43HighMax")
    private Double ctcLessThan43HighMax;

    @JsonProperty("ctc_menor_4_3_veryHighGreaterThan")
    @JsonAlias("ctcLessThan43VeryHighGreaterThan")
    private Double ctcLessThan43VeryHighGreaterThan;

    @JsonProperty("ctc_4_3_a_8_6_veryLowLessThan")
    @JsonAlias("ctcFrom43To86VeryLowLessThan")
    private Double ctcFrom43To86VeryLowLessThan;

    @JsonProperty("ctc_4_3_a_8_6_lowMin")
    @JsonAlias("ctcFrom43To86LowMin")
    private Double ctcFrom43To86LowMin;

    @JsonProperty("ctc_4_3_a_8_6_lowMax")
    @JsonAlias("ctcFrom43To86LowMax")
    private Double ctcFrom43To86LowMax;

    @JsonProperty("ctc_4_3_a_8_6_mediumMin")
    @JsonAlias("ctcFrom43To86MediumMin")
    private Double ctcFrom43To86MediumMin;

    @JsonProperty("ctc_4_3_a_8_6_mediumMax")
    @JsonAlias("ctcFrom43To86MediumMax")
    private Double ctcFrom43To86MediumMax;

    @JsonProperty("ctc_4_3_a_8_6_highMin")
    @JsonAlias("ctcFrom43To86HighMin")
    private Double ctcFrom43To86HighMin;

    @JsonProperty("ctc_4_3_a_8_6_highMax")
    @JsonAlias("ctcFrom43To86HighMax")
    private Double ctcFrom43To86HighMax;

    @JsonProperty("ctc_4_3_a_8_6_veryHighGreaterThan")
    @JsonAlias("ctcFrom43To86VeryHighGreaterThan")
    private Double ctcFrom43To86VeryHighGreaterThan;

    @JsonProperty("ctc_8_7_a_15_0_veryLowLessThan")
    @JsonAlias("ctcFrom87To150VeryLowLessThan")
    private Double ctcFrom87To150VeryLowLessThan;

    @JsonProperty("ctc_8_7_a_15_0_lowMin")
    @JsonAlias("ctcFrom87To150LowMin")
    private Double ctcFrom87To150LowMin;

    @JsonProperty("ctc_8_7_a_15_0_lowMax")
    @JsonAlias("ctcFrom87To150LowMax")
    private Double ctcFrom87To150LowMax;

    @JsonProperty("ctc_8_7_a_15_0_mediumMin")
    @JsonAlias("ctcFrom87To150MediumMin")
    private Double ctcFrom87To150MediumMin;

    @JsonProperty("ctc_8_7_a_15_0_mediumMax")
    @JsonAlias("ctcFrom87To150MediumMax")
    private Double ctcFrom87To150MediumMax;

    @JsonProperty("ctc_8_7_a_15_0_highMin")
    @JsonAlias("ctcFrom87To150HighMin")
    private Double ctcFrom87To150HighMin;

    @JsonProperty("ctc_8_7_a_15_0_highMax")
    @JsonAlias("ctcFrom87To150HighMax")
    private Double ctcFrom87To150HighMax;

    @JsonProperty("ctc_8_7_a_15_0_veryHighGreaterThan")
    @JsonAlias("ctcFrom87To150VeryHighGreaterThan")
    private Double ctcFrom87To150VeryHighGreaterThan;

    @JsonProperty("ctc_maior_15_veryLowLessThan")
    @JsonAlias("ctcGreaterThan15VeryLowLessThan")
    private Double ctcGreaterThan15VeryLowLessThan;

    @JsonProperty("ctc_maior_15_lowMin")
    @JsonAlias("ctcGreaterThan15LowMin")
    private Double ctcGreaterThan15LowMin;

    @JsonProperty("ctc_maior_15_lowMax")
    @JsonAlias("ctcGreaterThan15LowMax")
    private Double ctcGreaterThan15LowMax;

    @JsonProperty("ctc_maior_15_mediumMin")
    @JsonAlias("ctcGreaterThan15MediumMin")
    private Double ctcGreaterThan15MediumMin;

    @JsonProperty("ctc_maior_15_mediumMax")
    @JsonAlias("ctcGreaterThan15MediumMax")
    private Double ctcGreaterThan15MediumMax;

    @JsonProperty("ctc_maior_15_highMin")
    @JsonAlias("ctcGreaterThan15HighMin")
    private Double ctcGreaterThan15HighMin;

    @JsonProperty("ctc_maior_15_highMax")
    @JsonAlias("ctcGreaterThan15HighMax")
    private Double ctcGreaterThan15HighMax;

    @JsonProperty("ctc_maior_15_veryHighGreaterThan")
    @JsonAlias("ctcGreaterThan15VeryHighGreaterThan")
    private Double ctcGreaterThan15VeryHighGreaterThan;

}
