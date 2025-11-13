package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilFertilityInterpretationCriteriaTableResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "10")
    @JsonProperty("id_criador")
    private Long creator_id;

    @Schema(example = "Maria Souza")
    @JsonProperty("nome_criador")
    private String creator_name;

    @Schema(example = "SUL") // Exemplo hipotético, depende do Enum Regiao
    @JsonProperty("regiao")
    private Regiao region;
}