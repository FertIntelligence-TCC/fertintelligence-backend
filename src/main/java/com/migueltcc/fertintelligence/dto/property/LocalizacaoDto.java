package com.migueltcc.fertintelligence.dto.property;

import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoDto {

    @Schema(example = "-15.7801")
    @JsonProperty("latitude")
    private Double latitude;

    @Schema(example = "15")
    @JsonProperty("latitude_graus")
    @Min(value = 0, message = "Latitude em graus deve ser maior ou igual a 0")
    @Max(value = 90, message = "Latitude em graus deve ser menor ou igual a 90")
    private Integer latitudeGraus;

    @Schema(example = "46")
    @JsonProperty("latitude_minutos")
    @Min(value = 0, message = "Latitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Latitude em minutos deve ser menor ou igual a 59")
    private Integer latitudeMinutos;

    @Schema(example = "48.36")
    @JsonProperty("latitude_segundos")
    @DecimalMin(value = "0.0", message = "Latitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Latitude em segundos deve ser menor que 60")
    private Double latitudeSegundos;

    @Schema(example = "SUL")
    @JsonProperty("latitudeDirection")
    private LatitudeDirection latitudeDirection;

    @Schema(example = "-47.9292")
    @JsonProperty("longitude")
    private Double longitude;

    @Schema(example = "47")
    @JsonProperty("longitude_graus")
    @Min(value = 0, message = "Longitude em graus deve ser maior ou igual a 0")
    @Max(value = 180, message = "Longitude em graus deve ser menor ou igual a 180")
    private Integer longitudeGraus;

    @Schema(example = "55")
    @JsonProperty("longitude_minutos")
    @Min(value = 0, message = "Longitude em minutos deve ser maior ou igual a 0")
    @Max(value = 59, message = "Longitude em minutos deve ser menor ou igual a 59")
    private Integer longitudeMinutos;

    @Schema(example = "45.12")
    @JsonProperty("longitude_segundos")
    @DecimalMin(value = "0.0", message = "Longitude em segundos deve ser maior ou igual a 0")
    @DecimalMax(value = "59.999999", message = "Longitude em segundos deve ser menor que 60")
    private Double longitudeSegundos;

    @Schema(example = "OESTE")
    @JsonProperty("longitudeDirection")
    private LongitudeDirection longitudeDirection;

    @Schema(example = "1172.0")
    @JsonProperty("altitude")
    private Double altitude;

    public LocalizacaoDto(Double latitude,
                          LatitudeDirection latitudeDirection,
                          Double longitude,
                          LongitudeDirection longitudeDirection,
                          Double altitude) {
        this.latitude = latitude;
        this.latitudeDirection = latitudeDirection;
        this.longitude = longitude;
        this.longitudeDirection = longitudeDirection;
        this.altitude = altitude;
    }
}
