package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose;

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
public class MicronutrientDosePostRequestDto {

    @JsonProperty("novo_boro_baixo_dose")
    @JsonAlias("boro_baixo_dose")
    private Double boronLowDose;
    @JsonProperty("novo_boro_medio_dose")
    @JsonAlias("boro_medio_dose")
    private Double boronMediumDose;
    @JsonProperty("novo_boro_alto_dose")
    @JsonAlias("boro_alto_dose")
    private Double boronHighDose;

    @JsonProperty("novo_cobre_baixo_dose")
    @JsonAlias("cobre_baixo_dose")
    private Double copperLowDose;
    @JsonProperty("novo_cobre_medio_dose")
    @JsonAlias("cobre_medio_dose")
    private Double copperMediumDose;
    @JsonProperty("novo_cobre_alto_dose")
    @JsonAlias("cobre_alto_dose")
    private Double copperHighDose;

    @JsonProperty("novo_ferro_baixo_dose")
    @JsonAlias("ferro_baixo_dose")
    private Double ironLowDose;
    @JsonProperty("novo_ferro_medio_dose")
    @JsonAlias("ferro_medio_dose")
    private Double ironMediumDose;
    @JsonProperty("novo_ferro_alto_dose")
    @JsonAlias("ferro_alto_dose")
    private Double ironHighDose;

    @JsonProperty("novo_manganes_baixo_dose")
    @JsonAlias("manganes_baixo_dose")
    private Double manganeseLowDose;
    @JsonProperty("novo_manganes_medio_dose")
    @JsonAlias("manganes_medio_dose")
    private Double manganeseMediumDose;
    @JsonProperty("novo_manganes_alto_dose")
    @JsonAlias("manganes_alto_dose")
    private Double manganeseHighDose;

    @JsonProperty("novo_zinco_baixo_dose")
    @JsonAlias("zinco_baixo_dose")
    private Double zincLowDose;
    @JsonProperty("novo_zinco_medio_dose")
    @JsonAlias("zinco_medio_dose")
    private Double zincMediumDose;
    @JsonProperty("novo_zinco_alto_dose")
    @JsonAlias("zinco_alto_dose")
    private Double zincHighDose;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;
    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
