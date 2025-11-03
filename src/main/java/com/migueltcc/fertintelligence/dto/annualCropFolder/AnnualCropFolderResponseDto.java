package com.migueltcc.fertintelligence.dto.annualCropFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AnnualCropFolderResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "2018")
    @JsonProperty("ano_culturas")
    Integer cropsYear;

    @Schema(example = "404")
    @JsonProperty("id_talhao")
    Long plotId;

    @Schema(example = "kd2br")
    @JsonProperty("identificacao_talhao")
    String plotIdentification;

}
