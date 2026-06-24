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
public class SaturationExtractAnalysisExtractPostRequestDto {

    @JsonProperty("novo_ph")
    @JsonAlias("ph")
    Double ph;

    @JsonProperty("novo_ce")
    @JsonAlias("ce")
    Double ce;

    @JsonProperty("novo_teor_co3")
    @JsonAlias({"teor_co3", "teorCO3"})
    Double teorCO3;

    @JsonProperty("novo_teor_hco3")
    @JsonAlias({"teor_hco3", "teorHCO3"})
    Double teorHCO3;

    @JsonProperty("novo_teor_no3")
    @JsonAlias({"teor_no3", "teorNO3"})
    Double teorNO3;

    @JsonProperty("novo_teor_h2po4")
    @JsonAlias({"teor_h2po4", "teorH2PO4"})
    Double teorH2PO4;

    @JsonProperty("novo_teor_so4")
    @JsonAlias({"teor_so4", "teorSO4"})
    Double teorSO4;

    @JsonProperty("novo_teor_cl")
    @JsonAlias({"teor_cl", "teorCl"})
    Double teorCl;

    @JsonProperty("novo_teor_na")
    Double teorNa;

    @JsonProperty("novo_teor_k")
    Double teorK;

    @JsonProperty("novo_teor_ca")
    Double teorCa;

    @JsonProperty("novo_teor_mg")
    Double teorMg;

    @JsonProperty("novos_residuos_suspensao")
    @JsonAlias({"residuos_suspensao", "residuosSuspensao", "novo_residuos_suspensao"})
    Double residuosSuspensao;

    @JsonProperty("nova_dureza_total_caco3")
    @JsonAlias({"dureza_total_caco3", "durezaTotalCaCO3"})
    Double durezaTotalCaCO3;

    @JsonProperty("novo_ras")
    @JsonAlias("ras")
    Double ras;

}
