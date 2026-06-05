package com.migueltcc.fertintelligence.dto.topDressingFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDressingFertilizationCreateRequestDto {

    @JsonProperty("data")
    @NotNull
    Date date;

    @JsonProperty("ordem")
    @NotNull
    Integer order;

    @JsonProperty("formulado")
    Double formulated;

    @JsonProperty("sulfato_de_amonio")
    Double ammonium_sulfate;

    @JsonProperty("ureia")
    Double urea;

    @JsonProperty("cloreto_de_potassio")
    Double potassium_chloride;

    @JsonProperty("superfosfato_triplo")
    Double triple_superphosphate;

    @JsonProperty("superfosfato_simples")
    Double simple_superphosphate;

    @JsonProperty("monoamonio_fosfato")
    Double monoammonium_phosphate;

    @JsonProperty("observacoes")
    String observations;

    @JsonProperty("fontes")
    String sources;
}
