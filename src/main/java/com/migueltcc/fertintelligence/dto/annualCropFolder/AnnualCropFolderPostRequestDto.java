package com.migueltcc.fertintelligence.dto.annualCropFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnualCropFolderPostRequestDto {

    @Schema(example = "2018")
    @JsonProperty("novo_ano_culturas")
    Integer cropsYear;

}
