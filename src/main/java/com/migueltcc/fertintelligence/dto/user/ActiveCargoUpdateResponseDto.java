package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCargoUpdateResponseDto {

    @JsonProperty("cargo")
    private Cargo cargo;

    @JsonProperty("token")
    private String token;
}
