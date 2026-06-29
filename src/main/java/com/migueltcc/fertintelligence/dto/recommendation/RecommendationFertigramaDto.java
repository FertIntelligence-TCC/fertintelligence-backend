package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFertigramaDto {

    @JsonProperty("titulo")
    private String title;

    @JsonProperty("chave_grupo")
    private String groupKey;

    @JsonProperty("secao_origem")
    private String sourceSection;

    @Builder.Default
    @JsonProperty("itens")
    private List<RecommendationFertigramaItemDto> items = new ArrayList<>();

    @JsonProperty("aviso_tecnico")
    private String technicalWarning;
}
