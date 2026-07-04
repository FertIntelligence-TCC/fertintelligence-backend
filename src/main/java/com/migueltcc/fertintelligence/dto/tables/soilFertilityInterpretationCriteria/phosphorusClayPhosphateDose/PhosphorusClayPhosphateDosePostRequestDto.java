package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhosphorusClayPhosphateDosePostRequestDto {

    @JsonProperty("sistemas_sequeiro")
    @JsonAlias({"novo_sistemas_sequeiro", "sequeiro", "dryland_section"})
    private PhosphorusClayPhosphateDoseSectionDto drylandSection;

    @JsonProperty("sistemas_irrigados")
    @JsonAlias({"novo_sistemas_irrigados", "irrigado", "irrigated_section"})
    private PhosphorusClayPhosphateDoseSectionDto irrigatedSection;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;

    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
