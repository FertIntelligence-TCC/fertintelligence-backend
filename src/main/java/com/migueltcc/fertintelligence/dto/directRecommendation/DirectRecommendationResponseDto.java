package com.migueltcc.fertintelligence.dto.directRecommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectRecommendationResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("id_recomendacao")
    private Long recommendationId;

    @JsonProperty("nome_documento")
    private String documentName;

    @JsonProperty("laudo_tecnico")
    private String technicalReport;

    @JsonProperty("conteudo")
    private String content;

    @Builder.Default
    @JsonProperty("tabelas_estruturadas")
    private List<RecommendationTableSectionDto> structuredTables = new ArrayList<>();

    @Builder.Default
    @JsonProperty("formato_conteudo")
    private String contentFormat = "markdown";

    @Builder.Default
    @JsonProperty("fonte_recomendada")
    private String recommendedSource = "Aptos";

    @JsonProperty("dose_unit_mode")
    private String doseUnitMode;

    @JsonProperty("dose_unit_label")
    private String doseUnitLabel;

    @JsonProperty("applicable_dose_column")
    private String applicableDoseColumn;

    @Builder.Default
    @JsonProperty("observacoes_adubacao")
    private String fertilizationObservations = "";

    @Builder.Default
    @JsonProperty("observacoes_tecnicas")
    private List<String> technicalObservations = new ArrayList<>();

    @Builder.Default
    @JsonProperty("adubos_micronutrientes")
    private List<DirectRecommendationMicronutrientFertilizerLineResponseDto> micronutrientFertilizerLines = new ArrayList<>();

    @Builder.Default
    @JsonProperty("formulados_plantio")
    private List<DirectRecommendationPlantingFormulatedFertilizerLineResponseDto> plantingFormulatedFertilizerLines = new ArrayList<>();

    @Builder.Default
    @JsonProperty("formulados_cobertura")
    private List<DirectRecommendationCoverageFormulatedFertilizerLineResponseDto> coverageFormulatedFertilizerLines = new ArrayList<>();

    @Builder.Default
    @JsonProperty("tamanho_fonte")
    private Integer fontSize = 10;

    @Builder.Default
    @JsonProperty("gerado")
    private Boolean generated = true;

    @JsonProperty("criado_em")
    private LocalDateTime createdAt;

    @JsonProperty("atualizado_em")
    private LocalDateTime updatedAt;
}
