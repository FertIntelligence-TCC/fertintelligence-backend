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
public class PhosphorusClayPhosphateDoseCreateRequestDto {

    @JsonProperty("sistemas_sequeiro")
    @JsonAlias({"sequeiro", "dryland_section"})
    private PhosphorusClayPhosphateDoseSectionDto drylandSection;

    @JsonProperty("sistemas_irrigados")
    @JsonAlias({"irrigado", "irrigated_section"})
    private PhosphorusClayPhosphateDoseSectionDto irrigatedSection;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
