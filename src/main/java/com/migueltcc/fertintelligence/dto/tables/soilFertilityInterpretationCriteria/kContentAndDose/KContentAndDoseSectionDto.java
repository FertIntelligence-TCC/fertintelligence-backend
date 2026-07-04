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
public class KContentAndDoseSectionDto {

    @JsonProperty("teor_baixo_menor_que")
    @JsonAlias("teorBaixoMenorQue")
    private Double lowContentLessThan;

    @JsonProperty("dose_para_teor_baixo")
    @JsonAlias("doseParaTeorBaixo")
    private Double doseForLowContent;

    @JsonProperty("medio_menor_teor")
    @JsonAlias("medioMenorTeor")
    private Double mediumLowerContent;

    @JsonProperty("medio_maior_teor")
    @JsonAlias("medioMaiorTeor")
    private Double mediumHigherContent;

    @JsonProperty("dose_para_teor_medio")
    @JsonAlias("doseParaTeorMedio")
    private Double doseForMediumContent;

    @JsonProperty("adequado_menor_teor")
    @JsonAlias("adequadoMenorTeor")
    private Double adequateLowerContent;

    @JsonProperty("adequado_maior_teor")
    @JsonAlias("adequadoMaiorTeor")
    private Double adequateHigherContent;

    @JsonProperty("dose_para_teor_adequado")
    @JsonAlias("doseParaTeorAdequado")
    private Double doseForAdequateContent;

    @JsonProperty("teor_alto_maior_que")
    @JsonAlias("teorAltoMaiorQue")
    private Double highContentGreaterThan;

    @JsonProperty("dose_para_teor_alto")
    @JsonAlias("doseParaTeorAlto")
    private Double doseForHighContent;
}
