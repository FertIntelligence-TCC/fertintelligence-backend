package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SoilFertilityInterpretationCriteriaTableCreateRequestDto {

    @JsonProperty("nome")
    @NotNull
    @Schema(example = "Critérios de Interpretação de Fertilidade - Safra 2025")
    private String name;

    @JsonProperty("descricao")
    @Schema(example = "Tabelas baseadas no Manual de Adubação e Calagem para os estados do Rio Grande do Sul e Santa Catarina, focada em culturas de grãos.")
    private String description;

    @JsonProperty("regiao")
    @NotNull
    @Schema(example = "NORDESTE")
    private Regiao region;
}