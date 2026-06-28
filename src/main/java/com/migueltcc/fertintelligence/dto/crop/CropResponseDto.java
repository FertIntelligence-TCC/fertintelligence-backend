package com.migueltcc.fertintelligence.dto.crop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CropResponseDto {

    @Schema(example = "1042")
    @JsonProperty("id")
    Long id;

    @Schema(example = "SAFRA")
    @JsonProperty("tipo_cultivo")
    CultivationType cultivationType;

    @Schema(example = "Soja")
    @JsonProperty("nome")
    NomeComum name;

    @Schema(example = "TMG 7062 IPRO")
    @JsonProperty("variedade")
    String variety;

    @Schema(example = "180")
    @JsonProperty("ciclo")
    Integer cycle;

    @Schema(example = "4.7")
    @JsonProperty("distancia_entre_linhas")
    Double distanceBetweenLines;

    @Schema(example = "6.7")
    @JsonProperty("numero_plantas_por_metro")
    Double plantsPerMeter;

    @Schema(example = "PLANTS_PER_LINEAR_METER")
    @JsonProperty("modo_espacamento")
    CropSpacingMode spacingMode;

    @Schema(example = "0.3")
    @JsonProperty("distancia_entre_covas")
    Double distanceBetweenPits;

    @Schema(example = "2.0")
    @JsonProperty("numero_plantas_por_cova")
    Double plantsPerPit;

    @Schema(example = "3400")
    @JsonProperty("produtividade_esperada")
    Double expectedProductivity;

    @Schema(example = "2900")
    @JsonProperty("produtividade_obtida")
    Double obtainedProductivity;

    @Schema(example = "45")
    @JsonProperty("area_usada_no_talhao")
    Double usedAreaInThePlot;

    @Schema(example = "15/10/2024")
    @JsonProperty("data_plantio")
    Date plantingDate;

    @Schema(example = "23/10/2024")
    @JsonProperty("data_emergencia")
    Date emergenceDate;

    @Schema(example = "05/12/2024")
    @JsonProperty("data_botonamento")
    Date buttoningDate;

    @Schema(example = "20/12/2024")
    @JsonProperty("data_florescimento")
    Date floweringDate;

    @Schema(example = "12/03/2025")
    @JsonProperty("data_colheita")
    Date harvestDate;

    @Schema(example = "665f3f4f31c6d31d7c31ec1a")
    @JsonProperty("idfoto")
    String idFoto;

    @Schema(example = "51")
    @JsonProperty("id_pasta_culturas_anuais")
    Long folder_id;

    @Schema(example = "2024")
    @JsonProperty("ano_culturas")
    Integer cropsYear;

}
