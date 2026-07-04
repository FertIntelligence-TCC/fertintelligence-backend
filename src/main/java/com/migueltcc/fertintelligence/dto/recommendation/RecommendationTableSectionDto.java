package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecommendationTableSectionDto {

    @JsonProperty("titulo")
    private String title;

    @JsonProperty("chave_secao")
    private String sectionKey;

    @JsonProperty("opcao")
    private String option;

    @JsonProperty("tipo_item")
    private String itemType;

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

    @Builder.Default
    @JsonProperty("mensagens_usuario")
    private List<String> userMessages = new ArrayList<>();

    @Builder.Default
    @JsonProperty("avisos_tecnicos")
    private List<String> technicalWarnings = new ArrayList<>();

    @Builder.Default
    @JsonProperty("memoria_calculo")
    private List<String> calculationMemory = new ArrayList<>();
}
