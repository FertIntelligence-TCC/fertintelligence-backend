package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose;

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
public class PhosphorusClayPhosphateDoseSectionDto {

    @JsonProperty("menor_teor_argila")
    @JsonAlias("menorTeorArgila")
    private Double lowerClayContent;

    @JsonProperty("menor_teor_argila_dose_p_muito_baixo")
    @JsonAlias("menorTeorArgilaDosePMuitoBaixo")
    private Double lowerClayVeryLowPDose;

    @JsonProperty("menor_teor_argila_dose_p_baixo")
    @JsonAlias("menorTeorArgilaDosePBaixo")
    private Double lowerClayLowPDose;

    @JsonProperty("menor_teor_argila_dose_p_medio")
    @JsonAlias("menorTeorArgilaDosePMedio")
    private Double lowerClayMediumPDose;

    @JsonProperty("intervalo_1_menor_teor_argila")
    @JsonAlias("intervalo1MenorTeorArgila")
    private Double interval1LowerClayContent;

    @JsonProperty("intervalo_1_maior_teor_argila")
    @JsonAlias("intervalo1MaiorTeorArgila")
    private Double interval1HigherClayContent;

    @JsonProperty("intervalo_1_dose_p_muito_baixo")
    @JsonAlias("intervalo1DosePMuitoBaixo")
    private Double interval1VeryLowPDose;

    @JsonProperty("intervalo_1_dose_p_baixo")
    @JsonAlias("intervalo1DosePBaixo")
    private Double interval1LowPDose;

    @JsonProperty("intervalo_1_dose_p_medio")
    @JsonAlias("intervalo1DosePMedio")
    private Double interval1MediumPDose;

    @JsonProperty("intervalo_2_menor_teor_argila")
    @JsonAlias("intervalo2MenorTeorArgila")
    private Double interval2LowerClayContent;

    @JsonProperty("intervalo_2_maior_teor_argila")
    @JsonAlias("intervalo2MaiorTeorArgila")
    private Double interval2HigherClayContent;

    @JsonProperty("intervalo_2_dose_p_muito_baixo")
    @JsonAlias("intervalo2DosePMuitoBaixo")
    private Double interval2VeryLowPDose;

    @JsonProperty("intervalo_2_dose_p_baixo")
    @JsonAlias("intervalo2DosePBaixo")
    private Double interval2LowPDose;

    @JsonProperty("intervalo_2_dose_p_medio")
    @JsonAlias("intervalo2DosePMedio")
    private Double interval2MediumPDose;

    @JsonProperty("maior_teor_argila")
    @JsonAlias("maiorTeorArgila")
    private Double higherClayContent;

    @JsonProperty("maior_teor_argila_dose_p_muito_baixo")
    @JsonAlias("maiorTeorArgilaDosePMuitoBaixo")
    private Double higherClayVeryLowPDose;

    @JsonProperty("maior_teor_argila_dose_p_baixo")
    @JsonAlias("maiorTeorArgilaDosePBaixo")
    private Double higherClayLowPDose;

    @JsonProperty("maior_teor_argila_dose_p_medio")
    @JsonAlias("maiorTeorArgilaDosePMedio")
    private Double higherClayMediumPDose;
}
