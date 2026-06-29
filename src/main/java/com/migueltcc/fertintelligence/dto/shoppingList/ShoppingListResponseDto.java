package com.migueltcc.fertintelligence.dto.shoppingList;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("id_recomendacao")
    private Long recommendationId;

    @JsonProperty("nome_documento")
    private String documentName;

    @JsonProperty("laudo_tecnico")
    private String technicalReport;

    @JsonProperty("conteudo")
    private String content;

    @Builder.Default
    @JsonProperty("itens")
    private List<ShoppingListItemResponseDto> items = new ArrayList<>();

    @Builder.Default
    @JsonProperty("observacoes_tecnicas")
    private List<String> technicalObservations = new ArrayList<>();

    @Builder.Default
    @JsonProperty("formato_conteudo")
    private String contentFormat = "markdown";

    @Builder.Default
    @JsonProperty("fonte_recomendada")
    private String recommendedSource = "Aptos";

    @Builder.Default
    @JsonProperty("tamanho_fonte")
    private Integer fontSize = 10;

    @Builder.Default
    @JsonProperty("gerado")
    private Boolean generated = true;

    @JsonProperty("criado_em")
    private LocalDateTime createdAt;

    @JsonProperty("atualizado_em")
    private LocalDateTime updatedAt;
}
