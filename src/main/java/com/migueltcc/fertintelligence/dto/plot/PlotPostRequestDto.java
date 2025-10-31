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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotPostRequestDto {

    @Schema(example = "kd2br")
    @JsonProperty("nova_identificacao")
    String identification;

    @Schema(example = "16.4")
    @JsonProperty("nova_area")
    Double area;

    @Schema(example = "PLINTOSSOLO")
    @JsonProperty("nova_classe_solo")
    ClasseSolo soilClass;

    @Schema(example = "ARGISSOLO")
    @JsonProperty("nova_textura_solo")
    TexturaSolo soilTexture;

    @Schema(example = "2018")
    @JsonProperty("novo_ano_incorporacao_safra")
    Integer cropIncorporationYear;

    @Schema(example = "SIM")
    @JsonProperty("nova_area_irrigada")
    AreaIrrigada irrigatedArea;

    @Schema(example = "30")
    @JsonProperty("nova_declividade")
    Double declivity;

    @Schema(example = "300")
    @JsonProperty("nova_pluviosidade_mensal")
    Double monthlyPluviosity;

    @Schema(example = "1200")
    @JsonProperty("nova_pluviosidade_anual")
    Double annualPluviosity;

    @Schema(example = "42")
    @JsonProperty("id")
    Long propertyId;

    @Schema(example = "Fazenda 4 Irmãos")
    @JsonProperty("nova_nome_propriedade")
    String propertyName;

}
