package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeableSodiumResponseDto {
    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("ctc_menor_4_3_veryLowLessThan")
    private Double ctcLessThan43VeryLowLessThan;

    @JsonProperty("ctc_menor_4_3_lowMin")
    private Double ctcLessThan43LowMin;

    @JsonProperty("ctc_menor_4_3_lowMax")
    private Double ctcLessThan43LowMax;

    @JsonProperty("ctc_menor_4_3_mediumMin")
    private Double ctcLessThan43MediumMin;

    @JsonProperty("ctc_menor_4_3_mediumMax")
    private Double ctcLessThan43MediumMax;

    @JsonProperty("ctc_menor_4_3_highMin")
    private Double ctcLessThan43HighMin;

    @JsonProperty("ctc_menor_4_3_highMax")
    private Double ctcLessThan43HighMax;

    @JsonProperty("ctc_menor_4_3_veryHighGreaterThan")
    private Double ctcLessThan43VeryHighGreaterThan;

    @JsonProperty("ctc_4_3_a_8_6_veryLowLessThan")
    private Double ctcFrom43To86VeryLowLessThan;

    @JsonProperty("ctc_4_3_a_8_6_lowMin")
    private Double ctcFrom43To86LowMin;

    @JsonProperty("ctc_4_3_a_8_6_lowMax")
    private Double ctcFrom43To86LowMax;

    @JsonProperty("ctc_4_3_a_8_6_mediumMin")
    private Double ctcFrom43To86MediumMin;

    @JsonProperty("ctc_4_3_a_8_6_mediumMax")
    private Double ctcFrom43To86MediumMax;

    @JsonProperty("ctc_4_3_a_8_6_highMin")
    private Double ctcFrom43To86HighMin;

    @JsonProperty("ctc_4_3_a_8_6_highMax")
    private Double ctcFrom43To86HighMax;

    @JsonProperty("ctc_4_3_a_8_6_veryHighGreaterThan")
    private Double ctcFrom43To86VeryHighGreaterThan;

    @JsonProperty("ctc_8_7_a_15_0_veryLowLessThan")
    private Double ctcFrom87To150VeryLowLessThan;

    @JsonProperty("ctc_8_7_a_15_0_lowMin")
    private Double ctcFrom87To150LowMin;

    @JsonProperty("ctc_8_7_a_15_0_lowMax")
    private Double ctcFrom87To150LowMax;

    @JsonProperty("ctc_8_7_a_15_0_mediumMin")
    private Double ctcFrom87To150MediumMin;

    @JsonProperty("ctc_8_7_a_15_0_mediumMax")
    private Double ctcFrom87To150MediumMax;

    @JsonProperty("ctc_8_7_a_15_0_highMin")
    private Double ctcFrom87To150HighMin;

    @JsonProperty("ctc_8_7_a_15_0_highMax")
    private Double ctcFrom87To150HighMax;

    @JsonProperty("ctc_8_7_a_15_0_veryHighGreaterThan")
    private Double ctcFrom87To150VeryHighGreaterThan;

    @JsonProperty("ctc_maior_15_veryLowLessThan")
    private Double ctcGreaterThan15VeryLowLessThan;

    @JsonProperty("ctc_maior_15_lowMin")
    private Double ctcGreaterThan15LowMin;

    @JsonProperty("ctc_maior_15_lowMax")
    private Double ctcGreaterThan15LowMax;

    @JsonProperty("ctc_maior_15_mediumMin")
    private Double ctcGreaterThan15MediumMin;

    @JsonProperty("ctc_maior_15_mediumMax")
    private Double ctcGreaterThan15MediumMax;

    @JsonProperty("ctc_maior_15_highMin")
    private Double ctcGreaterThan15HighMin;

    @JsonProperty("ctc_maior_15_highMax")
    private Double ctcGreaterThan15HighMax;

    @JsonProperty("ctc_maior_15_veryHighGreaterThan")
    private Double ctcGreaterThan15VeryHighGreaterThan;

}
