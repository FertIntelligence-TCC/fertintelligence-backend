package com.migueltcc.fertintelligence.dto.propertyAccessRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyAccessRequestCreateRequestDto {

    @JsonProperty("id_propriedade")
    @NotNull
    private Long propertyId;

}