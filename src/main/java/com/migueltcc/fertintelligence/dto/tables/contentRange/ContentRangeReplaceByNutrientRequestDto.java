package com.migueltcc.fertintelligence.dto.tables.contentRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRangeReplaceByNutrientRequestDto {

    @Valid
    @NotEmpty(message = "A lista de intervalos não pode ser vazia.")
    @JsonProperty("faixas")
    private List<ContentRangeReplaceItemDto> ranges;
}
