package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose;

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
public class SulfurDosePostRequestDto {

    @JsonProperty("novo_muito_baixo_dose_argila_menor_400")
    @JsonAlias("muito_baixo_dose_argila_menor_400")
    private Double less400VeryLowDose;
    @JsonProperty("novo_baixo_dose_argila_menor_400")
    @JsonAlias("baixo_dose_argila_menor_400")
    private Double less400LowDose;
    @JsonProperty("novo_medio_dose_argila_menor_400")
    @JsonAlias("medio_dose_argila_menor_400")
    private Double less400MediumDose;
    @JsonProperty("novo_alto_dose_argila_menor_400")
    @JsonAlias("alto_dose_argila_menor_400")
    private Double less400HighDose;
    @JsonProperty("novo_muito_alto_dose_argila_menor_400")
    @JsonAlias("muito_alto_dose_argila_menor_400")
    private Double less400VeryHighDose;

    @JsonProperty("novo_muito_baixo_dose_argila_maior_400")
    @JsonAlias("muito_baixo_dose_argila_maior_400")
    private Double greater400VeryLowDose;
    @JsonProperty("novo_baixo_dose_argila_maior_400")
    @JsonAlias("baixo_dose_argila_maior_400")
    private Double greater400LowDose;
    @JsonProperty("novo_medio_dose_argila_maior_400")
    @JsonAlias("medio_dose_argila_maior_400")
    private Double greater400MediumDose;
    @JsonProperty("novo_alto_dose_argila_maior_400")
    @JsonAlias("alto_dose_argila_maior_400")
    private Double greater400HighDose;
    @JsonProperty("novo_muito_alto_dose_argila_maior_400")
    @JsonAlias("muito_alto_dose_argila_maior_400")
    private Double greater400VeryHighDose;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;
    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
