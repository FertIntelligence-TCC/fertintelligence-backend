package com.migueltcc.fertintelligence.dto.annualCropFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import jakarta.validation.constraints.NotBlank;
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

    @JsonProperty("id_talhao")
    @NotNull
    Long plotId;

    @JsonProperty("identificacao_talhao")
    @NotBlank
    String plotIdentification;

}
