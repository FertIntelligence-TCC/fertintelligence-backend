package com.migueltcc.fertintelligence.dto.plot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.Plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.Plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.Plot.TexturaSolo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PlotResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "kd2br")
    @JsonProperty("identificacao")
    String identification;

    @Schema(example = "16.4")
    @JsonProperty("area")
    Double area;

    @Schema(example = "PLINTOSSOLO")
    @JsonProperty("classe_solo")
    ClasseSolo soilClass;

    @Schema(example = "ARGISSOLO")
    @JsonProperty("textura_solo")
    TexturaSolo soilTexture;

    @Schema(example = "2018")
    @JsonProperty("ano_incorporacao_safra")
    Integer cropIncorporationYear;

    @Schema(example = "SIM")
    @JsonProperty("area_irrigada")
    AreaIrrigada irrigatedArea;

    @Schema(example = "30")
    @JsonProperty("declividade")
    Double declivity;

    @Schema(example = "300")
    @JsonProperty("pluviosidade_mensal")
    Double monthlyPluviosity;

    @Schema(example = "1200")
    @JsonProperty("pluviosidade_anual")
    Double annualPluviosity;

    @Schema(example = "42")
    @JsonProperty("id_propriedade")
    Long propertyId;

    @Schema(example = "Fazenda 4 Irmãos")
    @JsonProperty("nome_propriedade")
    String propertyName;


}
