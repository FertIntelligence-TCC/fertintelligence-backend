package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.dto.recommendation.deserializer.NullableLimingCriteriaDeserializer;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;
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

    @JsonProperty("physicalAnalysisId")
    @JsonAlias({"id_analise_fisica"})
    private Long physicalAnalysisId;

    @JsonProperty("id_extrato_analise_fisica")
    @JsonAlias({"physicalAnalysisExtractId", "id_extrato_fisico"})
    private Long physicalAnalysisExtractId;

    @JsonProperty("id_analise_fertilidade_solo")
    @JsonAlias({"soilFertilityAnalysisId", "fertilityAnalysisId", "id_extrato_analise_fertilidade", "id_analise_fertilidade"})
    @NotNull
    private Long soilFertilityAnalysisId;

    @JsonProperty("saturationExtractAnalysisId")
    @JsonAlias({"saturationAnalysisId", "id_analise_extrato_saturacao"})
    private Long saturationExtractAnalysisId;

    @JsonProperty("id_extrato_analise_extrato_saturacao")
    @JsonAlias({"saturationExtractAnalysisExtractId", "id_extrato_saturacao"})
    private Long saturationExtractAnalysisExtractId;

    @JsonProperty("id_pasta_cultura_anual")
    @NotNull
    private Long annualCropFolderId;

    @JsonProperty("id_cultura")
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
    private Long cropFoliarAnalysisInterpretationTableId;

    @JsonProperty("grupo_tabela_interpretacao_analise_foliar")
    @JsonAlias("cropFoliarAnalysisInterpretationTableGroup")
    private TechnicalTableGroup cropFoliarAnalysisInterpretationTableGroup;

    @JsonProperty("criterio_calagem")
    @JsonDeserialize(using = NullableLimingCriteriaDeserializer.class)
    private CriterioCalagem limingCriteria;

    @JsonProperty("classificacao_textural")
    @JsonAlias({"texturalClassification", "classificacaoTextural"})
    private TexturalClassification texturalClassification;

    @JsonProperty("origem_adubos")
    @JsonAlias({"fertilizerSourceOption", "origemAdubos"})
    @NotNull
    private FertilizerSourceOption origemAdubos;

    @JsonProperty("usar_adubo_organico")
    @JsonAlias({"useOrganicFertilizer", "usarAduboOrganico"})
    private Boolean useOrganicFertilizer;

    @JsonProperty("usar_organomineral")
    @JsonAlias({"useOrganoMineralFertilizer", "usarOrganomineral", "usarAduboOrganomineral"})
    private Boolean useOrganoMineralFertilizer;

    @JsonProperty("usar_biofertilizante")
    @JsonAlias({"useBioFertilizer", "usarBiofertilizante", "usarAduboBiologico"})
    private Boolean useBioFertilizer;

    @JsonProperty("adubacaoCorretivaSolo")
    @JsonAlias({"adubacao_corretiva_solo", "soilCorrectiveFertilization"})
    private Boolean soilCorrectiveFertilization;

    @JsonProperty("areaIncorporacaoConversaoRecente")
    @JsonAlias({"area_incorporacao_conversao_recente", "recentNativeOrPastureConversionArea"})
    private Boolean recentNativeOrPastureConversionArea;

    @JsonProperty("areaDegradadaMaisCincoAnosSemAdubacao")
    @JsonAlias({"area_degradada_mais_5_anos_sem_adubacao", "degradedAreaMoreThanFiveYearsWithoutFertilization"})
    private Boolean degradedAreaMoreThanFiveYearsWithoutFertilization;

    @JsonProperty("areaRecuperacaoErosao")
    @JsonAlias({"area_recuperacao_erosao", "erosionRecoveryArea"})
    private Boolean erosionRecoveryArea;

    @JsonProperty("areaAltaTecnologiaAltasProdutividades")
    @JsonAlias({"area_alta_tecnologia_altas_produtividades", "highTechnologyHighProductivityArea"})
    private Boolean highTechnologyHighProductivityArea;

    @JsonProperty("nutriente_referencia_adubo_organico")
    @JsonAlias({"organicFertilizerReferenceNutrient", "nutrienteReferenciaAduboOrganico"})
    private Nutriente organicFertilizerReferenceNutrient;

    @JsonProperty("id_adubo_organico")
    @JsonAlias({"organicFertilizerId", "aduboOrganicoId"})
    private Long organicFertilizerId;

    @JsonProperty("usar_adubo_verde")
    @JsonAlias({"useGreenFertilizer", "usarAduboVerde"})
    private Boolean useGreenFertilizer;

    @JsonProperty("especie_adubo_verde")
    @JsonAlias({"especie_utilizada", "especie_utilizada_adubo_verde", "greenFertilizerSpecies", "greenFertilizerUsedSpecies", "especieAduboVerde"})
    private String greenFertilizerSpecies;

    @JsonProperty("massa_verde_adubo_verde")
    @JsonAlias({"massa_verde", "massa_verde_adubo_verde_kg_ha", "greenMass", "greenFertilizerGreenMass", "greenFertilizerGreenMassKgHa", "massaVerdeAduboVerde"})
    @DecimalMin(value = "0.0", message = "A massa verde do adubo verde não pode ser negativa.")
    private Double greenFertilizerGreenMass;

    @JsonProperty("umidade_adubo_verde_percentual")
    @JsonAlias({"umidade", "umidade_adubo_verde", "moisture", "greenFertilizerMoisturePercentage", "umidadeAduboVerdePercentual"})
    @DecimalMin(value = "0.0", message = "A umidade do adubo verde não pode ser negativa.")
    @DecimalMax(value = "100.0", message = "A umidade do adubo verde não pode ser maior que 100%.")
    private Double greenFertilizerMoisturePercentage;

    @JsonProperty("massa_seca_adubo_verde")
    @JsonAlias({"massa_seca", "massa_seca_adubo_verde_kg_ha", "dryMass", "greenFertilizerDryMass", "greenFertilizerDryMassKgHa", "massaSecaAduboVerde"})
    @DecimalMin(value = "0.0", message = "A massa seca do adubo verde não pode ser negativa.")
    private Double greenFertilizerDryMass;

    @JsonIgnore
    @AssertTrue(message = "O nutriente de referência do adubo orgânico só pode ser informado quando o uso de adubo orgânico estiver habilitado.")
    public boolean isOrganicFertilizerReferenceNutrientConsistent() {
        return organicFertilizerReferenceNutrient == null || Boolean.TRUE.equals(useOrganicFertilizer);
    }

    @JsonIgnore
    @AssertTrue(message = "O adubo orgânico deve ser selecionado quando seu uso estiver habilitado.")
    public boolean isOrganicFertilizerSelectionConsistent() {
        return Boolean.TRUE.equals(useOrganicFertilizer)
                ? organicFertilizerId != null
                : organicFertilizerId == null;
    }

    @JsonIgnore
    @AssertTrue(message = "Os dados de adubo verde só podem ser informados quando o uso de adubo verde estiver habilitado.")
    public boolean isGreenFertilizerDataConsistentWithUsage() {
        return !hasGreenFertilizerData() || Boolean.TRUE.equals(useGreenFertilizer);
    }

    @JsonIgnore
    @AssertTrue(message = "A massa seca do adubo verde deve ser menor ou igual à massa verde.")
    public boolean isGreenFertilizerDryMassNotGreaterThanGreenMass() {
        return greenFertilizerGreenMass == null
                || greenFertilizerDryMass == null
                || greenFertilizerDryMass <= greenFertilizerGreenMass;
    }

    @JsonIgnore
    @AssertTrue(message = "A massa seca do adubo verde deve ser consistente com massa verde e umidade informadas.")
    public boolean isGreenFertilizerMassAndMoistureConsistent() {
        if (greenFertilizerGreenMass == null
                || greenFertilizerMoisturePercentage == null
                || greenFertilizerDryMass == null) {
            return true;
        }
        double expectedDryMass = greenFertilizerGreenMass * (100.0 - greenFertilizerMoisturePercentage) / 100.0;
        double tolerance = Math.max(0.01, Math.abs(expectedDryMass) * 0.001);
        return Math.abs(greenFertilizerDryMass - expectedDryMass) <= tolerance;
    }

    @JsonIgnore
    private boolean hasGreenFertilizerData() {
        return greenFertilizerSpecies != null
                || greenFertilizerGreenMass != null
                || greenFertilizerMoisturePercentage != null
                || greenFertilizerDryMass != null;
    }
}
