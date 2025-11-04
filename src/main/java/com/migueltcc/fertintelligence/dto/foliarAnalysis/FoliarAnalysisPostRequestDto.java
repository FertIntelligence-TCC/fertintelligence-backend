package com.migueltcc.fertintelligence.dto.foliarAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.Crop.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoliarAnalysisPostRequestDto {

    @Schema(example = "20/02/2025")
    @JsonProperty("novo_data_coleta")
    Date collectDate;

    @Schema(example = "Novo Laboratório Agro")
    @JsonProperty("novo_laboratorio")
    String laboratory;

}