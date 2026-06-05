package com.migueltcc.fertintelligence.dto.plot;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Schema(example = "-15.7801")
    @JsonProperty("nova_latitude")
    Double latitude;

    @Schema(example = "15")
    @JsonProperty("nova_latitude_graus")
    @Min(value = 0, message = "Latitude em graus deve ser maior ou igual a 0")
    @Max(value = 90, message = "Latitude em graus deve ser menor ou igual a 90")
    Integer latitudeGraus;

    @Schema(example = "46")
    @JsonProperty("nova_latitude_minutos")
    @Min(value = 0, message = "Latitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Latitude em minutos deve ser menor ou igual a 59")
    Integer latitudeMinutos;

    @Schema(example = "48.36")
    @JsonProperty("nova_latitude_segundos")
    @DecimalMin(value = "0.0", message = "Latitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Latitude em segundos deve ser menor que 60")
    Double latitudeSegundos;

    @Schema(example = "SUL")
    @JsonProperty("nova_latitudeDirection")
    LatitudeDirection latitudeDirection;

    @Schema(example = "-47.9292")
    @JsonProperty("nova_longitude")
    Double longitude;

    @Schema(example = "47")
    @JsonProperty("nova_longitude_graus")
    @Min(value = 0, message = "Longitude em graus deve ser maior ou igual a 0")
    @Max(value = 180, message = "Longitude em graus deve ser menor ou igual a 180")
    Integer longitudeGraus;

    @Schema(example = "55")
    @JsonProperty("nova_longitude_minutos")
    @Min(value = 0, message = "Longitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Longitude em minutos deve ser menor ou igual a 59")
    Integer longitudeMinutos;

    @Schema(example = "45.12")
    @JsonProperty("nova_longitude_segundos")
    @DecimalMin(value = "0.0", message = "Longitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Longitude em segundos deve ser menor que 60")
    Double longitudeSegundos;

    @Schema(example = "OESTE")
    @JsonProperty("nova_longitudeDirection")
    LongitudeDirection longitudeDirection;

    @Schema(example = "1172.0")
    @JsonProperty("nova_altitude")
    Double altitude;

    @Schema(example = "665f3f4f31c6d31d7c31ec1a")
    @JsonProperty("novo_idfoto")
    @JsonAlias({"novoIdFoto", "novo_idFoto"})
    String idFoto;

}
