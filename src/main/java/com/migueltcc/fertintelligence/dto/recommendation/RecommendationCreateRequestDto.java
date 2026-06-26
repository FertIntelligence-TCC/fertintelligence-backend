package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.recommendation.deserializer.NullableLimingCriteriaDeserializer;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationCreateRequestDto {

    @JsonProperty("tipo_recomendacao")
    @NotNull
    private RecommendationType recommendationType;

    @JsonProperty("id_propriedade")
    @JsonAlias("propertyId")
    @NotNull
    private Long propertyId;

    @JsonProperty("id_talhao")
    @JsonAlias("plotId")
    @NotNull
    private Long plotId;

    @JsonProperty("nome_pasta_recomendacao")
    @JsonAlias({"recommendationFolderName", "nomePastaRecomendacao"})
    private String recommendationFolderName;

    @JsonProperty("id_extrato_analise_fisica")
    @JsonAlias({"physicalAnalysisExtractId", "physicalAnalysisId", "id_analise_fisica", "id_extrato_fisico"})
    @NotNull
    private Long physicalAnalysisExtractId;

    @JsonProperty("id_analise_fertilidade_solo")
    @JsonAlias({"soilFertilityAnalysisId", "fertilityAnalysisId", "id_extrato_analise_fertilidade", "id_analise_fertilidade"})
    @NotNull
    private Long soilFertilityAnalysisId;

    @JsonProperty("id_extrato_analise_extrato_saturacao")
    @JsonAlias({"saturationExtractAnalysisExtractId", "saturationAnalysisId", "id_extrato_saturacao", "id_analise_extrato_saturacao"})
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
    @JsonAlias("cropFertilizationTableGroup")
    @NotNull
    private TechnicalTableGroup cropFertilizationTableGroup;

    @JsonProperty("id_tabela_interpretacao_fertilidade_solo")
    @NotNull
    private Long soilFertilityInterpretationCriteriaTableId;

    @JsonProperty("grupo_tabela_interpretacao_fertilidade_solo")
    @JsonAlias("soilFertilityInterpretationCriteriaTableGroup")
    @NotNull
    private TechnicalTableGroup soilFertilityInterpretationCriteriaTableGroup;

    @JsonProperty("id_tabela_interpretacao_analise_foliar")
    @NotNull
    private Long cropFoliarAnalysisInterpretationTableId;

    @JsonProperty("grupo_tabela_interpretacao_analise_foliar")
    @JsonAlias("cropFoliarAnalysisInterpretationTableGroup")
    @NotNull
    private TechnicalTableGroup cropFoliarAnalysisInterpretationTableGroup;

    @JsonProperty("criterio_calagem")
    @JsonDeserialize(using = NullableLimingCriteriaDeserializer.class)
    private CriterioCalagem limingCriteria;

    @JsonProperty("origem_adubos")
    @JsonAlias({"fertilizerSourceOption", "origemAdubos"})
    @NotNull
    private FertilizerSourceOption origemAdubos;
}
