package com.migueltcc.fertintelligence.dto.directRecommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectRecommendationMicronutrientFertilizerLineResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("micronutriente")
    private AppliedMicronutrient micronutrient;

    @JsonProperty("dose_micronutriente_kg_ha")
    private Double micronutrientDoseKgHa;

    @JsonProperty("id_adubo")
    private Long fertilizerId;

    @JsonProperty("nome_adubo")
    private String fertilizerName;

    @JsonProperty("teor_micronutriente_percentual")
    private Double micronutrientConcentrationPercent;

    @JsonProperty("dose_adubo_kg_ha")
    private Double fertilizerDoseKgHa;

    @JsonProperty("dose_unit_mode")
    private String doseUnitMode;

    @JsonProperty("dose_unit_label")
    private String doseUnitLabel;

    @JsonProperty("dose_g_m_linear")
    private Double gramsPerLinearMeter;

    @JsonProperty("dose_g_cova")
    private Double gramsPerPit;

    @JsonProperty("observacao_tecnica")
    private String technicalObservation;
}
