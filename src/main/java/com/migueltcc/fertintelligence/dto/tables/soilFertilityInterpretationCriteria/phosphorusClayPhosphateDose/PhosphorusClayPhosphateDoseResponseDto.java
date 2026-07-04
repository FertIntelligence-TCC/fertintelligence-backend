package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose;

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
public class PhosphorusClayPhosphateDoseResponseDto {

    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("nome_exibicao")
    private String displayName;

    @JsonProperty("unidade_teor_argila")
    private String clayContentUnit;

    @JsonProperty("unidade_dose")
    private String doseUnit;

    @JsonProperty("sistemas_sequeiro")
    private PhosphorusClayPhosphateDoseSectionDto drylandSection;

    @JsonProperty("sistemas_irrigados")
    private PhosphorusClayPhosphateDoseSectionDto irrigatedSection;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
