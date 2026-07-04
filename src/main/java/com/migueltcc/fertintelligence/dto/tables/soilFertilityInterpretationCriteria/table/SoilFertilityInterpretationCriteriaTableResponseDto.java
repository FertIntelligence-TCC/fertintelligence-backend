package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Schema(example = "Critérios aplicáveis a amostras compostas da camada de 0-20 cm.")
    @JsonProperty("observacoes")
    private String observations;

    @Schema(example = "Manual de calagem e adubação; boletins técnicos regionais")
    @JsonProperty("fontes")
    private String sources;

    @Schema(example = "false")
    @JsonProperty("tabela_publica")
    private boolean public_table;

    @JsonProperty("adubacao_corretiva_p2o5")
    private List<CorrectiveP2O5FertilizationResponseDto> correctiveP2O5Fertilization;

    @JsonProperty("adubacao_corretiva_k2o")
    private List<CorrectiveK2OFertilizationResponseDto> correctiveK2OFertilization;
}
