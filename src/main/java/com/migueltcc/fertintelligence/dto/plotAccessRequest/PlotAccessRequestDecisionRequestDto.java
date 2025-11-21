package com.migueltcc.fertintelligence.dto.plotAccessRequest;

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
public class PlotAccessRequestDecisionRequestDto {

    @JsonProperty("solicitacao_aprovada")
    @NotNull
    private Boolean approve;
}