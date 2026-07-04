package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose;

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
public class KContentAndDoseResponseDto {

    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("nome_exibicao")
    private String displayName;

    @JsonProperty("unidade_teor")
    private String contentUnit;

    @JsonProperty("unidade_dose")
    private String doseUnit;

    @JsonProperty("ctc_ph_7_menor_40")
    private KContentAndDoseSectionDto lessThan40Section;

    @JsonProperty("ctc_ph_7_maior_igual_40")
    private KContentAndDoseSectionDto greaterOrEqual40Section;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
