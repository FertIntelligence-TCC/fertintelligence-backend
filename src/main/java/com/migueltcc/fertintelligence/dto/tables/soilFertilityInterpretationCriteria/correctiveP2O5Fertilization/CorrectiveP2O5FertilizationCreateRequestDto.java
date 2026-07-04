package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization;

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
public class CorrectiveP2O5FertilizationCreateRequestDto {

    @JsonProperty("argila_minima")
    @JsonAlias({"teor_argila_minimo", "clayContentMinimum"})
    private Double clayContentMinimum;

    @JsonProperty("argila_maxima")
    @JsonAlias({"teor_argila_maximo", "clayContentMaximum"})
    private Double clayContentMaximum;

    @JsonProperty("p_mehlich_minimo")
    @JsonAlias({"p_disponivel_minimo", "availablePMehlich1Minimum"})
    private Double availablePMehlich1Minimum;

    @JsonProperty("p_mehlich_maximo")
    @JsonAlias({"p_disponivel_maximo", "availablePMehlich1Maximum"})
    private Double availablePMehlich1Maximum;

    @JsonProperty("dose_p2o5")
    @JsonAlias({"dose_recomendada_p2o5", "recommendedP2O5Dose"})
    private Double recommendedP2O5Dose;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
