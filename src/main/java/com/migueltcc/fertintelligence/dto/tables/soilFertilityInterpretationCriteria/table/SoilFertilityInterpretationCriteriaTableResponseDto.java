package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Schema(example = "SUL")
    @JsonProperty("regiao")
    private Regiao region;

    @Schema(example = "Critérios de Interpretação de Fertilidade - Safra 2025")
    @JsonProperty("nome_criterios")
    private String name;

    @Schema(example = "Tabelas baseadas no Manual de Adubação e Calagem para os estados do Rio Grande do Sul e Santa Catarina, focada em culturas de grãos.")
    @JsonProperty("descricao_criterios")
    private String description;
}