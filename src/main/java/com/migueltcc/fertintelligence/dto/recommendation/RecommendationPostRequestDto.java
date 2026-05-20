package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationPostRequestDto {

    @JsonProperty("novo_tipo_recomendacao")
    private RecommendationType newRecommendationType;

    @JsonProperty("novo_ano_safra")
    private Integer newCropYear;

    @JsonProperty("nova_cultura")
    private NomeComum newCropName;

    @JsonProperty("novo_id_tabela_adubacao_cultura")
    private Long newCropFertilizationTableId;

    @JsonProperty("novo_id_tabela_interpretacao_fertilidade_solo")
    private Long newSoilFertilityInterpretationCriteriaTableId;

    @JsonProperty("novo_id_tabela_interpretacao_analise_foliar")
    private Long newCropFoliarAnalysisInterpretationTableId;

    @JsonProperty("novo_criterio_calagem")
    private CriterioCalagem newLimingCriteria;
}
