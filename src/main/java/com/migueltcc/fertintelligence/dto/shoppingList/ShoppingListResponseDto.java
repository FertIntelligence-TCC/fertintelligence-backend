package com.migueltcc.fertintelligence.dto.shoppingList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListResponseDto;
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

    @JsonProperty("area_usada_no_talhao")
    private Double usedAreaInThePlot;

    @Builder.Default
    @JsonProperty("itens")
    private List<ShoppingListItemResponseDto> items = new ArrayList<>();

    @Builder.Default
    @JsonProperty("blocos")
    private List<ShoppingListBlockResponseDto> blocks = new ArrayList<>();

    @JsonProperty("purchaseList")
    private PurchaseListResponseDto purchaseList;

    @JsonProperty("custo_total_estimado_insumos")
    private String estimatedInputTotalCost;

    @JsonProperty("itens_sem_preco")
    private Integer itemsWithoutPrice;

    @JsonProperty("observacao_estimativa_custos")
    private String costEstimateObservation;

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

    @JsonProperty("laudoTecnico")
    public String getLaudoTecnico() {
        return technicalReport;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShoppingListBlockResponseDto {

        @JsonProperty("codigo")
        private String code;

        @JsonProperty("nome")
        private String name;

        @Builder.Default
        @JsonProperty("opcoes")
        private List<ShoppingListOptionResponseDto> options = new ArrayList<>();

        @JsonProperty("observacao_tecnica")
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShoppingListOptionResponseDto {

        @JsonProperty("codigo")
        private String code;

        @JsonProperty("nome")
        private String name;

        @JsonProperty("mutuamente_exclusiva")
        private Boolean mutuallyExclusive;

        @Builder.Default
        @JsonProperty("itens")
        private List<ShoppingListItemResponseDto> items = new ArrayList<>();

        @JsonProperty("observacao_tecnica")
        private String technicalObservation;

        @JsonProperty("custo_total_estimado")
        private String estimatedTotalCost;

        @JsonProperty("itens_sem_preco")
        private Integer itemsWithoutPrice;
    }
}
