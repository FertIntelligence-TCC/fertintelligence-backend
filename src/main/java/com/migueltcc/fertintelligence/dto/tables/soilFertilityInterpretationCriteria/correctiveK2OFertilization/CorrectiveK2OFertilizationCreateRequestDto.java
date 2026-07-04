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
public class CorrectiveK2OFertilizationCreateRequestDto {

    @JsonProperty("ctc_minima")
    @JsonAlias({"ctc_ph_7_minima", "ctcMinimum"})
    private Double ctcMinimum;

    @JsonProperty("ctc_maxima")
    @JsonAlias({"ctc_ph_7_maxima", "ctcMaximum"})
    private Double ctcMaximum;

    @JsonProperty("k_minimo")
    @JsonAlias({"k_mmolc_minimo", "exchangeableKMinimum"})
    private Double exchangeableKMinimum;

    @JsonProperty("k_maximo")
    @JsonAlias({"k_mmolc_maximo", "exchangeableKMaximum"})
    private Double exchangeableKMaximum;

    @JsonProperty("dose_k2o")
    @JsonAlias({"dose_recomendada_k2o", "recommendedK2ODose"})
    private Double recommendedK2ODose;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
