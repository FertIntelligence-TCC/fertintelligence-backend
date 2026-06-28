package com.migueltcc.fertintelligence.dto.directRecommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectRecommendationPlantingFormulatedFertilizerLineResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("fase")
    private String phase;

    @JsonProperty("id_formulado")
    private Long fertilizerId;

    @JsonProperty("nome_formulado")
    private String fertilizerName;

    @JsonProperty("teor_n_percentual")
    private Double nitrogenPercent;

    @JsonProperty("teor_p2o5_percentual")
    private Double p2o5Percent;

    @JsonProperty("teor_k2o_percentual")
    private Double k2oPercent;

    @JsonProperty("relacao_usada")
    private String relationUsed;

    @JsonProperty("tipo_selecao")
    private String selectionType;

    @JsonProperty("dose_kg_ha")
    private Double doseKgHa;

    @JsonProperty("dose_unit_mode")
    private String doseUnitMode;

    @JsonProperty("dose_unit_label")
    private String doseUnitLabel;

    @JsonProperty("dose_g_m_linear")
    private Double gramsPerLinearMeter;

    @JsonProperty("dose_g_cova")
    private Double gramsPerPit;

    @JsonProperty("dose_aplicavel_valor")
    private Double applicableDoseValue;

    @JsonProperty("dose_aplicavel_unidade")
    private String applicableDoseUnit;

    @JsonProperty("dose_aplicavel_coluna")
    private String applicableDoseColumn;

    @JsonProperty("observacao_tecnica")
    private String technicalObservation;
}
