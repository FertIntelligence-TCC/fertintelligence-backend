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
public class PurchaseListResponseDto {

    @Builder.Default
    @JsonProperty("blocks")
    private List<PurchaseListBlockResponseDto> blocks = new ArrayList<>();

    @Builder.Default
    @JsonProperty("calculationDetails")
    private List<PurchaseListCalculationDetailResponseDto> calculationDetails = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseListCalculationDetailResponseDto {

        @JsonProperty("blockKey")
        private String blockKey;

        @JsonProperty("optionKey")
        private String optionKey;

        @JsonProperty("sourceName")
        private String sourceName;

        @JsonProperty("nutrientTarget")
        private String nutrientTarget;

        @JsonProperty("calculationMemory")
        private String calculationMemory;

        @JsonProperty("technicalNote")
        private String technicalNote;
    }
}
