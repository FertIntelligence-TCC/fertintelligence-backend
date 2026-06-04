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

    @JsonProperty("nome_criterios")
    @NotNull(message = "O nome é obrigatório")
    @Schema(example = "Critérios de Interpretação de Fertilidade - Safra 2025")
    private String name;

    @JsonProperty("descricao_criterios")
    @Schema(example = "Tabelas baseadas no Manual de Adubação e Calagem...")
    private String description;

    @JsonProperty("regiao")
    @NotNull(message = "A região é obrigatória")
    @Schema(example = "NORDESTE")
    private Regiao region;

    @JsonProperty("observacoes")
    @Schema(example = "Critérios aplicáveis a amostras compostas da camada de 0-20 cm.")
    private String observations;

    @JsonProperty("fontes")
    @Schema(example = "Manual de calagem e adubação; boletins técnicos regionais")
    private String sources;

    @JsonProperty("tabela_publica")
    private Boolean public_table;
}