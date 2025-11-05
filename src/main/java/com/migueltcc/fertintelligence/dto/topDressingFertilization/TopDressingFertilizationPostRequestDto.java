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
public class TopDressingFertilizationPostRequestDto {

    @Schema(example = "15/05/2025")
    @JsonProperty("novo_data")
    Date date;

    @Schema(example = "1")
    @JsonProperty("novo_ordem")
    Integer order;

    @Schema(example = "150.0")
    @JsonProperty("novo_formulado")
    Double formulated;

    @Schema(example = "120.0")
    @JsonProperty("novo_sulfato_de_amonio")
    Double ammonium_sulfate;

    @Schema(example = "80.5")
    @JsonProperty("novo_ureia")
    Double urea;

    @Schema(example = "200.0")
    @JsonProperty("novo_cloreto_de_potassio")
    Double potassium_chloride;

    @Schema(example = "0.0")
    @JsonProperty("novo_superfosfato_triplo")
    Double triple_superphosphate;

    @Schema(example = "0.0")
    @JsonProperty("novo_superfosfato_simples")
    Double simple_superphosphate;

    @Schema(example = "50.0")
    @JsonProperty("novo_monoamonio_fosfato")
    Double monoammonium_phosphate;
}