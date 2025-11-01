package com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaturationExtractAnalysisExtractPostRequestDto {

    @JsonProperty("novo_ph")
    Double ph;

    @JsonProperty("novo_ce")
    Double ce;

    @JsonProperty("novo_teor_co3")
    Double teorCO3;

    @JsonProperty("novo_teor_hco3")
    Double teorHCO3;

    @JsonProperty("novo_teor_no3")
    Double teorNO3;

    @JsonProperty("novo_teor_h2po4")
    Double teorH2PO4;

    @JsonProperty("novo_teor_so4")
    Double teorSO4;

    @JsonProperty("novo_teor_na")
    Double teorNa;

    @JsonProperty("novo_teor_k")
    Double teorK;

    @JsonProperty("novo_teor_ca")
    Double teorCa;

    @JsonProperty("novo_teor_mg")
    Double teorMg;

    @JsonProperty("novos_residuos_suspensao")
    Double residuosSuspensao;

    @JsonProperty("nova_dureza_caco3")
    Double durezaCaCO3;

    @JsonProperty("nova_dureza_total_caco3")
    Double durezaTotalCaCO3;

    @JsonProperty("novo_ras")
    Double ras;

    @JsonProperty("novo_pst")
    Double pst;
}