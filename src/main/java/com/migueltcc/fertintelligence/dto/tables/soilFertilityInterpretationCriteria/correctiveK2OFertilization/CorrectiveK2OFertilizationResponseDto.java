package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization;

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
public class CorrectiveK2OFertilizationResponseDto {

    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("nome_exibicao")
    private String displayName;

    @JsonProperty("unidade_ctc")
    private String ctcUnit;

    @JsonProperty("unidade_k")
    private String exchangeableKUnit;

    @JsonProperty("unidade_dose")
    private String doseUnit;

    @JsonProperty("ctc_minima")
    private Double ctcMinimum;

    @JsonProperty("ctc_maxima")
    private Double ctcMaximum;

    @JsonProperty("k_minimo")
    private Double exchangeableKMinimum;

    @JsonProperty("k_maximo")
    private Double exchangeableKMaximum;

    @JsonProperty("dose_k2o")
    private Double recommendedK2ODose;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
