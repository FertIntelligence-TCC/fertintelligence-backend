package com.migueltcc.fertintelligence.dto.purchaseList;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseListOptionResponseDto {

    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

    @Builder.Default
    @JsonProperty("items")
    private List<PurchaseListItemResponseDto> items = new ArrayList<>();
}
