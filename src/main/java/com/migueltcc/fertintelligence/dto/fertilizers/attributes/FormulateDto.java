package com.migueltcc.fertintelligence.dto.fertilizers.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormulateDto {
    @JsonProperty("n") private Integer n;
    @JsonProperty("p") private Integer p;
    @JsonProperty("k") private Integer k;
}