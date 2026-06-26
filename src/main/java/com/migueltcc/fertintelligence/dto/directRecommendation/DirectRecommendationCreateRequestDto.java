package com.migueltcc.fertintelligence.dto.directRecommendation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectRecommendationCreateRequestDto {

    @JsonProperty("id_recomendacao")
    @JsonAlias("recommendationId")
    @NotNull
    private Long recommendationId;

    @JsonProperty("laudo_tecnico")
    @JsonAlias({"technicalReport", "conteudo"})
    @NotBlank
    private String technicalReport;
}
