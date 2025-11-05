package com.migueltcc.fertintelligence.dto.soilAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SoilAnalysisResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "2018")
    @JsonProperty("ano_analise")
    Integer analysisYear;

    @Schema(example = "Embrapa Algodão")
    @JsonProperty("laboratorio_responsavel")
    String responsibleLaboratory;

    @Schema(example = "CAMADAS")
    @JsonProperty("tipo_extrato")
    TipoExtrato extractType;

    @Schema(example = "404")
    @JsonProperty("id_talhao")
    Long plotId;

    @Schema(example = "kd2br")
    @JsonProperty("identificacao_talhao")
    String plotIdentification;

}
