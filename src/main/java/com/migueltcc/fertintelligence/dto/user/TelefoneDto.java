package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TelefoneDto {

    @Schema(example = "+55")
    @JsonProperty("pais")
    private String pais;

    @Schema(example = "83")
    @JsonProperty("ddd")
    private String ddd;

    @Schema(example = "99121-4231")
    @JsonProperty("numero")
    private String numero;
}
