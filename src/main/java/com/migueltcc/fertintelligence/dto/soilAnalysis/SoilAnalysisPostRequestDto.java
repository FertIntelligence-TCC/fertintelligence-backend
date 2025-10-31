package com.migueltcc.fertintelligence.dto.soilAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilAnalysisPostRequestDto {

    @Schema(example = "2018")
    @JsonProperty("novo_ano_analise")
    Integer analysisYear;

    @Schema(example = "Embrapa Algodão")
    @JsonProperty("novo_laboratorio_responsavel")
    String responsibleLaboratory;

    @Schema(example = "INTERVALOS")
    @JsonProperty("novo_tipo_extrato")
    TipoExtrato extractType;

}
