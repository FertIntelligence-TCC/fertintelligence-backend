package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DataNascDto {

    @Schema(example = "08")
    @JsonProperty("dia")
    private String dia;

    @Schema(example = "05")
    @JsonProperty("mes")
    private String mes;

    @Schema(example = "2001")
    @JsonProperty("ano")
    private String ano;
}
