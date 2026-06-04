package com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table;

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
public class CropFoliarAnalysisInterpretationTableResponseDto {

    @Schema(example = "10")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome_tabela")
    private String name;

    @Schema(example = "5")
    @JsonProperty("id_criador")
    private Long creator_id;

    @Schema(example = "João Silva")
    @JsonProperty("nome_criador")
    private String creator_name;

    @Schema(example = "SUL")
    @JsonProperty("regiao_analise_foliar_culturas")
    private Regiao region;

    @Schema(example = "Faixas adequadas para folhas diagnósticas coletadas no florescimento.")
    @JsonProperty("observacoes")
    private String observations;

    @Schema(example = "Manual de diagnose foliar; boletins técnicos regionais")
    @JsonProperty("fontes")
    private String sources;

    @Schema(example = "false")
    @JsonProperty("tabela_publica")
    private boolean publicTable;

    // Getter extra para facilitar o frontend se necessário
    @JsonProperty("region")
    public Regiao getRegionSimple() {
        return region;
    }

}