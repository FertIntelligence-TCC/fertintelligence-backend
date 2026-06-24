package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoilFertilityInterpretationCriteriaTablePostRequestDto {

    @JsonProperty("novo_nome_criterios")
    @Schema(example = "Critérios de Interpretação de Fertilidade - Safra 2026")
    private String name;

    @JsonProperty("nova_descricao_criterios")
    @Schema(example = "Nova descrição...")
    private String description;

    @JsonProperty("nova_regiao")
    @Schema(example = "CENTRO_OESTE")
    private Regiao region;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao", "observations", "observation", "notes"})
    @Schema(example = "Observações atualizadas para interpretação dos teores.")
    private String observations;

    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "sources", "source", "references"})
    @Schema(example = "Manual atualizado; recomendações oficiais estaduais")
    private String sources;

    @JsonProperty("tabela_publica")
    private Boolean public_table;
}
