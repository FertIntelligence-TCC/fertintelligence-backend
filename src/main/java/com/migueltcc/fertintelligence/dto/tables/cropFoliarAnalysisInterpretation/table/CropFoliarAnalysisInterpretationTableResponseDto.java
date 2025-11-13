package com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFoliarAnalysisInterpretationTableResponseDto {

    @Schema(example = "10")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "5")
    @JsonProperty("id_criador")
    private Long creator_id;

    @Schema(example = "João Silva")
    @JsonProperty("nome_criador")
    private String creator_name;

}
