package com.migueltcc.fertintelligence.dto.propertyAccessRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyAccessRequestCreateRequestDto {

    @JsonProperty("estado_da_requisicao")
    @NotNull
    private AccessRequestStatus status;

    @JsonProperty("data_de_criacao")
    @NotNull
    private LocalDateTime createdAt;

}
