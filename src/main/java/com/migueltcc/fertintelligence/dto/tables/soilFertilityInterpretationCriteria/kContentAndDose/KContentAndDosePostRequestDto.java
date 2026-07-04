package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose;

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
public class KContentAndDosePostRequestDto {

    @JsonProperty("ctc_ph_7_menor_40")
    @JsonAlias({"novo_ctc_ph_7_menor_40", "ctcMenor40", "less_than_40_section"})
    private KContentAndDoseSectionDto lessThan40Section;

    @JsonProperty("ctc_ph_7_maior_igual_40")
    @JsonAlias({"novo_ctc_ph_7_maior_igual_40", "ctcMaiorIgual40", "greater_or_equal_40_section"})
    private KContentAndDoseSectionDto greaterOrEqual40Section;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;

    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
