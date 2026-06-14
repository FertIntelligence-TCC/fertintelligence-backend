package com.migueltcc.fertintelligence.dto.tables.contentRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRangeReplaceItemDto {

    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("ordem_teor")
    private Integer order;

    @JsonProperty("menor_teor")
    private Double smallest;

    @JsonProperty("maior_teor")
    private Double largest;

    @JsonProperty("aplicacao_recomendada_plantio")
    private Double application;

    @Valid
    @JsonProperty("coberturas")
    private List<ContentRangeReplaceCoverageDto> coverages;
}
