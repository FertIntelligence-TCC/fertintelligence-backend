package com.migueltcc.fertintelligence.dto.shoppingList;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingListPostRequestDto {

    @JsonProperty("novo_laudo_tecnico")
    @JsonAlias({"laudo_tecnico", "technicalReport", "newTechnicalReport"})
    private String newTechnicalReport;
}
