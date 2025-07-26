package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DataNascDto {

    @Schema(example = "8")
    @JsonProperty("dia")
    private int dia;

    @Schema(example = "5")
    @JsonProperty("mes")
    private int mes;

    @Schema(example = "2001")
    @JsonProperty("ano")
    private int ano;
}
