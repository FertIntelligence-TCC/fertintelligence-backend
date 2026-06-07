package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCreateRequestDto {

    @JsonProperty("tipo_recomendacao")
    @NotNull
    private RecommendationType recommendationType;

    @JsonProperty("id_propriedade")
    @NotNull
    private Long propertyId;

    @JsonProperty("id_talhao")
    @NotNull
    private Long plotId;

    @JsonProperty("id_extrato_analise_fisica")
    @NotNull
    private Long physicalAnalysisExtractId;

    @JsonProperty("id_analise_fertilidade_solo")
    @NotNull
    private Long soilFertilityAnalysisId;

    @JsonProperty("id_extrato_analise_extrato_saturacao")
    @NotNull
    private Long saturationExtractAnalysisExtractId;

    @JsonProperty("id_pasta_cultura_anual")
    @NotNull
    private Long annualCropFolderId;

    @JsonProperty("id_cultura")
    @NotNull
    private Long cropId;

    @JsonProperty("id_tabela_adubacao_cultura")
    @NotNull
    private Long cropFertilizationTableId;

    @JsonProperty("grupo_tabela_adubacao_cultura")
    @NotNull
    private TechnicalTableGroup cropFertilizationTableGroup;

    @JsonProperty("id_tabela_interpretacao_fertilidade_solo")
    @NotNull
    private Long soilFertilityInterpretationCriteriaTableId;

    @JsonProperty("grupo_tabela_interpretacao_fertilidade_solo")
    @NotNull
    private TechnicalTableGroup soilFertilityInterpretationCriteriaTableGroup;

    @JsonProperty("id_tabela_interpretacao_analise_foliar")
    @NotNull
    private Long cropFoliarAnalysisInterpretationTableId;

    @JsonProperty("grupo_tabela_interpretacao_analise_foliar")
    @NotNull
    private TechnicalTableGroup cropFoliarAnalysisInterpretationTableGroup;

    @JsonProperty("criterio_calagem")
    private CriterioCalagem limingCriteria;

    @JsonProperty("origem_adubos")
    @NotNull
    private FertilizerSourceOption origemAdubos;
}
