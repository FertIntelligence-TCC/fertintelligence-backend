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
public class PurchaseListBlockResponseDto {

    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @Builder.Default
    @JsonProperty("mutuallyExclusiveOptions")
    private Boolean mutuallyExclusiveOptions = true;

    @Builder.Default
    @JsonProperty("options")
    private List<PurchaseListOptionResponseDto> options = new ArrayList<>();
}
