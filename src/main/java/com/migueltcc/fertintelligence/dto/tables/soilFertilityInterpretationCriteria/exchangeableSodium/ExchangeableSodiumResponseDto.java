package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeableSodiumResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("tableId")
    private Long tableId;
    @JsonProperty("ctcLessThan4_3_veryLowLessThan")
    private Double ctcLessThan4_3_veryLowLessThan;

    @JsonProperty("ctcLessThan4_3_lowMin")
    private Double ctcLessThan4_3_lowMin;

    @JsonProperty("ctcLessThan4_3_lowMax")
    private Double ctcLessThan4_3_lowMax;

    @JsonProperty("ctcLessThan4_3_mediumMin")
    private Double ctcLessThan4_3_mediumMin;

    @JsonProperty("ctcLessThan4_3_mediumMax")
    private Double ctcLessThan4_3_mediumMax;

    @JsonProperty("ctcLessThan4_3_highMin")
    private Double ctcLessThan4_3_highMin;

    @JsonProperty("ctcLessThan4_3_highMax")
    private Double ctcLessThan4_3_highMax;

    @JsonProperty("ctcLessThan4_3_veryHighGreaterThan")
    private Double ctcLessThan4_3_veryHighGreaterThan;

    @JsonProperty("ctcFrom4_3To8_6_veryLowLessThan")
    private Double ctcFrom4_3To8_6_veryLowLessThan;

    @JsonProperty("ctcFrom4_3To8_6_lowMin")
    private Double ctcFrom4_3To8_6_lowMin;

    @JsonProperty("ctcFrom4_3To8_6_lowMax")
    private Double ctcFrom4_3To8_6_lowMax;

    @JsonProperty("ctcFrom4_3To8_6_mediumMin")
    private Double ctcFrom4_3To8_6_mediumMin;

    @JsonProperty("ctcFrom4_3To8_6_mediumMax")
    private Double ctcFrom4_3To8_6_mediumMax;

    @JsonProperty("ctcFrom4_3To8_6_highMin")
    private Double ctcFrom4_3To8_6_highMin;

    @JsonProperty("ctcFrom4_3To8_6_highMax")
    private Double ctcFrom4_3To8_6_highMax;

    @JsonProperty("ctcFrom4_3To8_6_veryHighGreaterThan")
    private Double ctcFrom4_3To8_6_veryHighGreaterThan;

    @JsonProperty("ctcFrom8_7To15_0_veryLowLessThan")
    private Double ctcFrom8_7To15_0_veryLowLessThan;

    @JsonProperty("ctcFrom8_7To15_0_lowMin")
    private Double ctcFrom8_7To15_0_lowMin;

    @JsonProperty("ctcFrom8_7To15_0_lowMax")
    private Double ctcFrom8_7To15_0_lowMax;

    @JsonProperty("ctcFrom8_7To15_0_mediumMin")
    private Double ctcFrom8_7To15_0_mediumMin;

    @JsonProperty("ctcFrom8_7To15_0_mediumMax")
    private Double ctcFrom8_7To15_0_mediumMax;

    @JsonProperty("ctcFrom8_7To15_0_highMin")
    private Double ctcFrom8_7To15_0_highMin;

    @JsonProperty("ctcFrom8_7To15_0_highMax")
    private Double ctcFrom8_7To15_0_highMax;

    @JsonProperty("ctcFrom8_7To15_0_veryHighGreaterThan")
    private Double ctcFrom8_7To15_0_veryHighGreaterThan;

    @JsonProperty("ctcGreaterThan15_veryLowLessThan")
    private Double ctcGreaterThan15_veryLowLessThan;

    @JsonProperty("ctcGreaterThan15_lowMin")
    private Double ctcGreaterThan15_lowMin;

    @JsonProperty("ctcGreaterThan15_lowMax")
    private Double ctcGreaterThan15_lowMax;

    @JsonProperty("ctcGreaterThan15_mediumMin")
    private Double ctcGreaterThan15_mediumMin;

    @JsonProperty("ctcGreaterThan15_mediumMax")
    private Double ctcGreaterThan15_mediumMax;

    @JsonProperty("ctcGreaterThan15_highMin")
    private Double ctcGreaterThan15_highMin;

    @JsonProperty("ctcGreaterThan15_highMax")
    private Double ctcGreaterThan15_highMax;

    @JsonProperty("ctcGreaterThan15_veryHighGreaterThan")
    private Double ctcGreaterThan15_veryHighGreaterThan;

}
