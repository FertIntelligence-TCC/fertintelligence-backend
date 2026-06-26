package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.recommendation.deserializer.NullableLimingCriteriaDeserializer;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationPostRequestDto {

    @JsonProperty("novo_tipo_recomendacao")
    private RecommendationType newRecommendationType;

    @JsonProperty("novo_ano_safra")
    private Integer newCropYear;

    @JsonProperty("nova_cultura")
    private NomeComum newCropName;

    @JsonProperty("novo_nome_pasta_recomendacao")
    @JsonAlias({"nome_pasta_recomendacao", "recommendationFolderName", "newRecommendationFolderName"})
    private String newRecommendationFolderName;

    @JsonProperty("novo_id_tabela_adubacao_cultura")
    @JsonAlias({"id_tabela_adubacao_cultura", "cropFertilizationTableId", "newCropFertilizationTableId"})
    private Long newCropFertilizationTableId;

    @JsonProperty("novo_grupo_tabela_adubacao_cultura")
    @JsonAlias({"grupo_tabela_adubacao_cultura", "cropFertilizationTableGroup", "newCropFertilizationTableGroup"})
    private TechnicalTableGroup newCropFertilizationTableGroup;

    @JsonProperty("novo_id_tabela_interpretacao_fertilidade_solo")
    @JsonAlias({"id_tabela_interpretacao_fertilidade_solo", "soilFertilityInterpretationCriteriaTableId", "newSoilFertilityInterpretationCriteriaTableId"})
    private Long newSoilFertilityInterpretationCriteriaTableId;

    @JsonProperty("novo_grupo_tabela_interpretacao_fertilidade_solo")
    @JsonAlias({"grupo_tabela_interpretacao_fertilidade_solo", "soilFertilityInterpretationCriteriaTableGroup", "newSoilFertilityInterpretationCriteriaTableGroup"})
    private TechnicalTableGroup newSoilFertilityInterpretationCriteriaTableGroup;

    @JsonProperty("novo_id_tabela_interpretacao_analise_foliar")
    @JsonAlias({"id_tabela_interpretacao_analise_foliar", "cropFoliarAnalysisInterpretationTableId", "newCropFoliarAnalysisInterpretationTableId"})
    private Long newCropFoliarAnalysisInterpretationTableId;

    @JsonProperty("novo_grupo_tabela_interpretacao_analise_foliar")
    @JsonAlias({"grupo_tabela_interpretacao_analise_foliar", "cropFoliarAnalysisInterpretationTableGroup", "newCropFoliarAnalysisInterpretationTableGroup"})
    private TechnicalTableGroup newCropFoliarAnalysisInterpretationTableGroup;

    @JsonProperty("novo_criterio_calagem")
    @JsonDeserialize(using = NullableLimingCriteriaDeserializer.class)
    private CriterioCalagem newLimingCriteria;

}
