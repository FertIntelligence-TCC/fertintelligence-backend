package com.migueltcc.fertintelligence.dto.crop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPostRequestDto {

    @Schema(example = "SAFRINHA")
    @JsonProperty("novo_tipo_cultivo")
    CultivationType cultivationType;

    @Schema(example = "Trigo")
    @JsonProperty("novo_nome")
    NomeComum name;

    @Schema(example = "TBIO Toruk") // Exemplo de variedade de trigo
    @JsonProperty("novo_variedade")
    String variety;

    @Schema(example = "135") // Ciclo em dias para o trigo
    @JsonProperty("novo_ciclo")
    Integer cycle;

    @Schema(example = "0.17")
    @JsonProperty("novo_distancia_entre_linhas")
    Double distanceBetweenLines;

    @Schema(example = "330.0")
    @JsonProperty("novo_numero_plantas_por_metro")
    Double plantsPerMeter;

    @Schema(example = "4200")
    @JsonProperty("novo_produtividade_esperada")
    Double expectedProductivity;

    @Schema(example = "4200")
    @JsonProperty("novo_produtividade_obtida")
    Double obtainedProductivity;

    @Schema(example = "50.0")
    @JsonProperty("novo_area_usada_no_talhao")
    Double usedAreaInThePlot;

    @Schema(example = "20/03/2025")
    @JsonProperty("novo_data_plantio")
    Date plantingDate;

    @Schema(example = "28/03/2025")
    @JsonProperty("novo_data_emergencia")
    Date emergenceDate;

    @Schema(example = "10/05/2025")
    @JsonProperty("novo_data_botonamento")
    Date buttoningDate;

    @Schema(example = "30/05/2025")
    @JsonProperty("novo_data_florescimento")
    Date floweringDate;

    @Schema(example = "25/07/2025")
    @JsonProperty("novo_data_colheita")
    Date harvestDate;

}