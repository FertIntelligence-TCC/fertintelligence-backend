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
public class CorrectiveP2O5FertilizationPostRequestDto {

    @JsonProperty("nova_argila_minima")
    @JsonAlias({"argila_minima", "teor_argila_minimo", "clayContentMinimum"})
    private Double clayContentMinimum;

    @JsonProperty("nova_argila_maxima")
    @JsonAlias({"argila_maxima", "teor_argila_maximo", "clayContentMaximum"})
    private Double clayContentMaximum;

    @JsonProperty("novo_p_mehlich_minimo")
    @JsonAlias({"p_mehlich_minimo", "p_disponivel_minimo", "availablePMehlich1Minimum"})
    private Double availablePMehlich1Minimum;

    @JsonProperty("novo_p_mehlich_maximo")
    @JsonAlias({"p_mehlich_maximo", "p_disponivel_maximo", "availablePMehlich1Maximum"})
    private Double availablePMehlich1Maximum;

    @JsonProperty("nova_dose_p2o5")
    @JsonAlias({"dose_p2o5", "dose_recomendada_p2o5", "recommendedP2O5Dose"})
    private Double recommendedP2O5Dose;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;

    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
