package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization;

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
public class CorrectiveK2OFertilizationPostRequestDto {

    @JsonProperty("nova_ctc_minima")
    @JsonAlias({"ctc_minima", "ctc_ph_7_minima", "ctcMinimum"})
    private Double ctcMinimum;

    @JsonProperty("nova_ctc_maxima")
    @JsonAlias({"ctc_maxima", "ctc_ph_7_maxima", "ctcMaximum"})
    private Double ctcMaximum;

    @JsonProperty("novo_k_minimo")
    @JsonAlias({"k_minimo", "k_mmolc_minimo", "exchangeableKMinimum"})
    private Double exchangeableKMinimum;

    @JsonProperty("novo_k_maximo")
    @JsonAlias({"k_maximo", "k_mmolc_maximo", "exchangeableKMaximum"})
    private Double exchangeableKMaximum;

    @JsonProperty("nova_dose_k2o")
    @JsonAlias({"dose_k2o", "dose_recomendada_k2o", "recommendedK2ODose"})
    private Double recommendedK2ODose;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;

    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
