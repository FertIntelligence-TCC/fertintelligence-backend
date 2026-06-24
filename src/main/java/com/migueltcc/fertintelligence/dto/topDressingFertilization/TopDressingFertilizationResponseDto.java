package com.migueltcc.fertintelligence.dto.topDressingFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopDressingFertilizationResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    Long id;

    @Schema(example = "10")
    @JsonProperty("id_cultura") // Corrigido: @JsonProperty era "id"
    Long crop_id;

    @Schema(example = "{\"day\": 16, \"month\": 5, \"year\": 2018}")
    @JsonProperty("data")
    Date date;

    @Schema(example = "1")
    @JsonProperty("ordem")
    Integer order;

    @Schema(example = "kg/ha")
    @JsonProperty("unidade_dose")
    String doseUnit;

    @Schema(example = "150.0")
    @JsonProperty("formulado")
    Double formulated;

    @Schema(example = "120.0")
    @JsonProperty("sulfato_de_amonio")
    Double ammonium_sulfate;

    @Schema(example = "80.5")
    @JsonProperty("ureia")
    Double urea;

    @Schema(example = "200.0")
    @JsonProperty("cloreto_de_potassio")
    Double potassium_chloride;

    @Schema(example = "0.0")
    @JsonProperty("superfosfato_triplo")
    Double triple_superphosphate;

    @Schema(example = "0.0")
    @JsonProperty("superfosfato_simples")
    Double simple_superphosphate;

    @Schema(example = "50.0")
    @JsonProperty("monoamonio_fosfato")
    Double monoammonium_phosphate;
}
