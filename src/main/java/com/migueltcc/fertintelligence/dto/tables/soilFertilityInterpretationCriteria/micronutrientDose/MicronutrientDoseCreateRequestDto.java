package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose;

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
public class MicronutrientDoseCreateRequestDto {

    @JsonProperty("boro_baixo_dose")
    private Double boronLowDose;
    @JsonProperty("boro_medio_dose")
    private Double boronMediumDose;
    @JsonProperty("boro_alto_dose")
    private Double boronHighDose;

    @JsonProperty("cobre_baixo_dose")
    private Double copperLowDose;
    @JsonProperty("cobre_medio_dose")
    private Double copperMediumDose;
    @JsonProperty("cobre_alto_dose")
    private Double copperHighDose;

    @JsonProperty("ferro_baixo_dose")
    private Double ironLowDose;
    @JsonProperty("ferro_medio_dose")
    private Double ironMediumDose;
    @JsonProperty("ferro_alto_dose")
    private Double ironHighDose;

    @JsonProperty("manganes_baixo_dose")
    private Double manganeseLowDose;
    @JsonProperty("manganes_medio_dose")
    private Double manganeseMediumDose;
    @JsonProperty("manganes_alto_dose")
    private Double manganeseHighDose;

    @JsonProperty("zinco_baixo_dose")
    private Double zincLowDose;
    @JsonProperty("zinco_medio_dose")
    private Double zincMediumDose;
    @JsonProperty("zinco_alto_dose")
    private Double zincHighDose;

    @JsonProperty("observacoes")
    private String observations;
    @JsonProperty("fontes")
    private String sources;
}
