package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose;

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
public class SulfurDoseCreateRequestDto {

    @JsonProperty("muito_baixo_dose_argila_menor_400")
    private Double less400VeryLowDose;
    @JsonProperty("baixo_dose_argila_menor_400")
    private Double less400LowDose;
    @JsonProperty("medio_dose_argila_menor_400")
    private Double less400MediumDose;
    @JsonProperty("alto_dose_argila_menor_400")
    private Double less400HighDose;
    @JsonProperty("muito_alto_dose_argila_menor_400")
    private Double less400VeryHighDose;

    @JsonProperty("muito_baixo_dose_argila_maior_400")
    private Double greater400VeryLowDose;
    @JsonProperty("baixo_dose_argila_maior_400")
    private Double greater400LowDose;
    @JsonProperty("medio_dose_argila_maior_400")
    private Double greater400MediumDose;
    @JsonProperty("alto_dose_argila_maior_400")
    private Double greater400HighDose;
    @JsonProperty("muito_alto_dose_argila_maior_400")
    private Double greater400VeryHighDose;

    @JsonProperty("observacoes")
    private String observations;
    @JsonProperty("fontes")
    private String sources;
}
