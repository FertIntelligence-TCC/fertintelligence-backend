package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
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

    @JsonProperty("ano_safra")
    @NotNull
    private Integer cropYear;

    @JsonProperty("cultura")
    @NotNull
    private NomeComum cropName;

    @JsonProperty("id_tabela_adubacao_cultura")
    private Long cropFertilizationTableId;

    @JsonProperty("id_tabela_interpretacao_fertilidade_solo")
    private Long soilFertilityInterpretationCriteriaTableId;

    @JsonProperty("id_tabela_interpretacao_analise_foliar")
    private Long cropFoliarAnalysisInterpretationTableId;

    @JsonProperty("criterio_calagem")
    private CriterioCalagem limingCriteria;

    @JsonProperty("origem_adubos")
    @NotNull
    private FertilizerSourceOption origemAdubos;
}
