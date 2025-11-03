package com.migueltcc.fertintelligence.dto.annualCropFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnualCropFolderCreateRequestDto {

    @JsonProperty("ano_culturas")
    @NotNull
    Integer cropsYear;

}
