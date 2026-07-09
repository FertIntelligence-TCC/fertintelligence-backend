package com.migueltcc.fertintelligence.dto.purchaseList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseListItemResponseDto {

    @JsonProperty("sourceName")
    private String sourceName;

    @JsonProperty("sourceType")
    private String sourceType;

    @JsonProperty("nutrientTarget")
    private String nutrientTarget;

    @JsonProperty("doseKgHa")
    private Double doseKgHa;

    @JsonProperty("areaQuantity")
    private String areaQuantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("shortTechnicalNote")
    private String shortTechnicalNote;
}
