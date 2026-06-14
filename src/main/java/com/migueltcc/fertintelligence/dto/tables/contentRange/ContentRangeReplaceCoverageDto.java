package com.migueltcc.fertintelligence.dto.tables.contentRange;

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
public class ContentRangeReplaceCoverageDto {

    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("ordem_cobertura")
    private Integer order;

    @JsonProperty("aplicacao_recomendada_cobertura")
    private Double application;
}
