package com.migueltcc.fertintelligence.dto.plot;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotCreateRequestDto {

    @JsonProperty("identificacao")
    @NotBlank(message = "Identificação não pode ser vazia")
    @Size(min = 3, max = 100, message = "Identificação deve ter entre 3 e 100 caracteres")
    String identification;

    @JsonProperty("area")
    @NotNull
    Double area;

    @JsonProperty("classe_solo")
    @NotNull
    ClasseSolo soilClass;

    @JsonProperty("textura_solo")
    @NotNull
    TexturaSolo soilTexture;

    @JsonProperty("ano_incorporacao_safra")
    @NotNull
    Integer cropIncorporationYear;

    @JsonProperty("area_irrigada")
    @NotNull
    AreaIrrigada irrigatedArea;

    @JsonProperty("declividade")
    @NotNull
    Double declivity;

    @JsonProperty("pluviosidade_mensal")
    @NotNull
    Double monthlyPluviosity;

    @JsonProperty("pluviosidade_anual")
    @NotNull
    Double annualPluviosity;

    @JsonProperty("latitude")
    Double latitude;

    @JsonProperty("latitude_graus")
    @Min(value = 0, message = "Latitude em graus deve ser maior ou igual a 0")
    @Max(value = 90, message = "Latitude em graus deve ser menor ou igual a 90")
    Integer latitudeGraus;

    @JsonProperty("latitude_minutos")
    @Min(value = 0, message = "Latitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Latitude em minutos deve ser menor ou igual a 59")
    Integer latitudeMinutos;

    @JsonProperty("latitude_segundos")
    @DecimalMin(value = "0.0", message = "Latitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Latitude em segundos deve ser menor que 60")
    Double latitudeSegundos;

    @JsonProperty("latitudeDirection")
    LatitudeDirection latitudeDirection;

    @JsonProperty("longitude")
    Double longitude;

    @JsonProperty("longitude_graus")
    @Min(value = 0, message = "Longitude em graus deve ser maior ou igual a 0")
    @Max(value = 180, message = "Longitude em graus deve ser menor ou igual a 180")
    Integer longitudeGraus;

    @JsonProperty("longitude_minutos")
    @Min(value = 0, message = "Longitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Longitude em minutos deve ser menor ou igual a 59")
    Integer longitudeMinutos;

    @JsonProperty("longitude_segundos")
    @DecimalMin(value = "0.0", message = "Longitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Longitude em segundos deve ser menor que 60")
    Double longitudeSegundos;

    @JsonProperty("longitudeDirection")
    LongitudeDirection longitudeDirection;

    @JsonProperty("altitude")
    Double altitude;

    @JsonProperty("id_foto")
    @JsonAlias({"idFoto", "idfoto"})
    String idFoto;

}
