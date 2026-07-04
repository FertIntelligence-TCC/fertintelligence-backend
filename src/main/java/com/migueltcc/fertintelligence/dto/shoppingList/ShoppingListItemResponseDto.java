package com.migueltcc.fertintelligence.dto.shoppingList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingListItemResponseDto {

    @JsonProperty("insumo")
    private String inputName;

    @JsonProperty("tipo_grupo")
    private String typeGroup;

    @JsonProperty("fase")
    private String phase;

    @JsonProperty("secao")
    private String section;

    @JsonProperty("opcao")
    private String option;

    @JsonProperty("flag_item")
    private String itemFlag;

    @JsonProperty("quantidade_kg_ha")
    private Double quantityKgHa;

    @JsonProperty("quantidade_por_hectare")
    private String quantityPerHectare;

    @JsonProperty("unidade_localizada")
    private String localizedUnit;

    @JsonProperty("total_area")
    private String totalForArea;

    @JsonProperty("decisao_custo_oportunidade")
    private String opportunityCostDecision;
}
