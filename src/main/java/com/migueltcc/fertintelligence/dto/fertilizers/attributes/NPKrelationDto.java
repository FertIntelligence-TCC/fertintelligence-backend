package com.migueltcc.fertintelligence.dto.fertilizers.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NPKrelationDto {
    @JsonProperty("n") private Double n;
    @JsonProperty("p") private Double p;
    @JsonProperty("k") private Double k;
}