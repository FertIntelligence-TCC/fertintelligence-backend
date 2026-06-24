package com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract;

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
public class SaturationExtractAnalysisExtractCreateRequestDto {

    @JsonProperty("ph")
    Double ph;

    @JsonProperty("ce")
    Double ce;

    @JsonProperty("teor_co3")
    @JsonAlias("teorCO3")
    Double teorCO3;

    @JsonProperty("teor_hco3")
    @JsonAlias("teorHCO3")
    Double teorHCO3;

    @JsonProperty("teor_no3")
    @JsonAlias("teorNO3")
    Double teorNO3;

    @JsonProperty("teor_h2po4")
    @JsonAlias("teorH2PO4")
    Double teorH2PO4;

    @JsonProperty("teor_so4")
    @JsonAlias("teorSO4")
    Double teorSO4;

    @JsonProperty("teor_cl")
    @JsonAlias("teorCl")
    Double teorCl;

    @JsonProperty("teor_na")
    Double teorNa;

    @JsonProperty("teor_k")
    Double teorK;

    @JsonProperty("teor_ca")
    Double teorCa;

    @JsonProperty("teor_mg")
    Double teorMg;

    @JsonProperty("residuos_suspensao")
    @JsonAlias("residuosSuspensao")
    Double residuosSuspensao;

    @JsonProperty("dureza_total_caco3")
    @JsonAlias("durezaTotalCaCO3")
    Double durezaTotalCaCO3;

    @JsonProperty("ras")
    Double ras;

}
