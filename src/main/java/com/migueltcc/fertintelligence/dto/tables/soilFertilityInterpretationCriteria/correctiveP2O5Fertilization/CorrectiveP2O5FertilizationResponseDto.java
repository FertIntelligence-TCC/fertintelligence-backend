package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectiveP2O5FertilizationResponseDto {

    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("nome_exibicao")
    private String displayName;

    @JsonProperty("unidade_argila")
    private String clayContentUnit;

    @JsonProperty("unidade_p_mehlich")
    private String availablePMehlich1Unit;

    @JsonProperty("unidade_dose")
    private String doseUnit;

    @JsonProperty("argila_minima")
    private Double clayContentMinimum;

    @JsonProperty("argila_maxima")
    private Double clayContentMaximum;

    @JsonProperty("p_mehlich_minimo")
    private Double availablePMehlich1Minimum;

    @JsonProperty("p_mehlich_maximo")
    private Double availablePMehlich1Maximum;

    @JsonProperty("dose_p2o5")
    private Double recommendedP2O5Dose;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
