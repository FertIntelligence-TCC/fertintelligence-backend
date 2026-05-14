package com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class CropFoliarAnalysisInterpretationTablePostRequestDto {

    @JsonProperty("novo_nome_tabela")
    private String name;

    @JsonProperty("novo_regiao_analise_foliar_culturas")
    @JsonAlias({"nova_region", "region"})
    @Schema(example = "NORDESTE")
    private Regiao region;

    @JsonProperty("tabela_publica")
    private Boolean publicTable;
}