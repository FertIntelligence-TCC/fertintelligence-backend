package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationTableSectionDto {

    @JsonProperty("titulo")
    private String title;

    @JsonProperty("origem")
    private String source;

    @Builder.Default
    @JsonProperty("colunas")
    private List<String> columns = new ArrayList<>();

    @Builder.Default
    @JsonProperty("linhas")
    private List<List<String>> rows = new ArrayList<>();

    @Builder.Default
    @JsonProperty("observacoes_tecnicas")
    private List<String> technicalObservations = new ArrayList<>();
}
