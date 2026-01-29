package com.migueltcc.fertintelligence.dto.crop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropCreateRequestDto {

    @JsonProperty("tipo_cultivo")
    @NotNull
    CultivationType cultivationType;

    @JsonProperty("nome")
    @NotNull
    NomeComum name;

    @JsonProperty("variedade")
    @NotNull
    String variety;

    @JsonProperty("ciclo")
    @NotNull
    Integer cycle;

    @JsonProperty("distancia_entre_linhas")
    @NotNull
    Double distanceBetweenLines;

    @JsonProperty("numero_plantas_por_metro")
    @NotNull
    Double plantsPerMeter;

    @JsonProperty("produtividade_esperada")
    @NotNull
    Double expectedProductivity;

    @JsonProperty("produtividade_obtida")
    @NotNull
    Double obtainedProductivity;

    @JsonProperty("area_usada_no_talhao")
    @NotNull
    Double usedAreaInThePlot;

    @JsonProperty("data_plantio")
    @NotNull
    Date plantingDate;

    @JsonProperty("data_emergencia")
    @NotNull
    Date emergenceDate;

    @JsonProperty("data_botonamento")
    @NotNull
    Date buttoningDate;

    @JsonProperty("data_florescimento")
    @NotNull
    Date floweringDate;

    @JsonProperty("data_colheita")
    @NotNull
    Date harvestDate;

}