package com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.saturationExtract.SaturationExtractUnit;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaturationExtractAnalysisExtractResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "101")
    @JsonProperty("id_extrato_intervalo")
    Long rangeExtractId;

    @Schema(example = "202")
    @JsonProperty("id_extrato_camada")
    Long layerExtractId;

    @Schema(example = "0")
    @JsonProperty("profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("profundidade_final")
    Integer finalDepth;

    @Schema(example = "A")
    @JsonProperty("camada")
    Camada layer;

    @Schema(example = "1")
    @JsonProperty("subcamada")
    Integer subLayer;

    @Schema(example = "7.2")
    @JsonProperty("ph")
    Double ph;

    @Schema(example = "0.5")
    @JsonProperty("ce")
    Double ce;

    @Schema(example = "12.0")
    @JsonProperty("teor_co3")
    Double teorCO3;

    @Schema(example = "18.0")
    @JsonProperty("teor_hco3")
    Double teorHCO3;

    @Schema(example = "25.0")
    @JsonProperty("teor_no3")
    Double teorNO3;

    @Schema(example = "5.0")
    @JsonProperty("teor_h2po4")
    Double teorH2PO4;

    @Schema(example = "14.0")
    @JsonProperty("teor_so4")
    Double teorSO4;

    @Schema(example = "16.0")
    @JsonProperty("teor_cl")
    Double teorCl;

    @Schema(example = "10.0")
    @JsonProperty("teor_na")
    Double teorNa;

    @Schema(example = "8.0")
    @JsonProperty("teor_k")
    Double teorK;

    @Schema(example = "20.0")
    @JsonProperty("teor_ca")
    Double teorCa;

    @Schema(example = "12.0")
    @JsonProperty("teor_mg")
    Double teorMg;

    @Schema(example = "30.0")
    @JsonProperty("residuos_suspensao")
    Double residuosSuspensao;

    @Schema(example = "80.0")
    @JsonProperty("dureza_total_caco3")
    Double durezaTotalCaCO3;

    @Schema(example = "12.5")
    @JsonProperty("ras")
    Double ras;

    @Schema(example = "(mmolc)**0.5")
    @JsonProperty("unidade_ras")
    SaturationExtractUnit unidadeRas;

}
