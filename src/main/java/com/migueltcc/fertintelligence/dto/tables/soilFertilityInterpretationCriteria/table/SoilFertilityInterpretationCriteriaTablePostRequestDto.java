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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilFertilityInterpretationCriteriaTablePostRequestDto {

    @JsonProperty("novo_nome")
    @Schema(example = "Critérios de Interpretação de Fertilidade - Safra 2026")
    private String name;

    @JsonProperty("nova_descricao")
    @Schema(example = "Tabelas baseadas no Manual de Adubação e Calagem para os estados de São Paulo e Minas Gerais, focada em culturas de grãos.")
    private String description;

    @JsonProperty("nova_regiao")
    @Schema(example = "CENTRO_OESTE")
    private Regiao region;
}