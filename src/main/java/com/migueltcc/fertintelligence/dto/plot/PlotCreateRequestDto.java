package com.migueltcc.fertintelligence.dto.plot;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
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

    @JsonProperty("latitudeDirection")
    LatitudeDirection latitudeDirection;

    @JsonProperty("longitude")
    Double longitude;

    @JsonProperty("longitudeDirection")
    LongitudeDirection longitudeDirection;

    @JsonProperty("altitude")
    Double altitude;

    @JsonProperty("id_foto")
    @JsonAlias({"idFoto", "idfoto"})
    String idFoto;

}
