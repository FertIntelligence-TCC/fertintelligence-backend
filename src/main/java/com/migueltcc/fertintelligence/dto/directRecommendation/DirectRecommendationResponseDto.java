package com.migueltcc.fertintelligence.dto.directRecommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectRecommendationResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("id_recomendacao")
    private Long recommendationId;

    @JsonProperty("nome_documento")
    private String documentName;

    @JsonProperty("laudo_tecnico")
    private String technicalReport;

    @JsonProperty("criado_em")
    private LocalDateTime createdAt;

    @JsonProperty("atualizado_em")
    private LocalDateTime updatedAt;
}
