package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFertilizationTableResolveLimingCriterionRequestDto {

    @NotNull
    @JsonProperty("cropFertilizationTableId")
    @JsonAlias({"id_tabela_adubacao_cultura", "crop_fertilization_table_id"})
    private Long cropFertilizationTableId;

    @NotNull
    @JsonProperty("propertyId")
    @JsonAlias({"id_propriedade", "property_id"})
    private Long propertyId;

    @NotNull
    @JsonProperty("plotId")
    @JsonAlias({"id_talhao", "plot_id"})
    private Long plotId;

    @JsonProperty("physicalAnalysisId")
    @JsonAlias({"id_extrato_analise_fisica", "id_analise_fisica", "id_analise_fisica_solo", "id_extrato_fisico", "id_extrato_analise_fisica_solo"})
    private Long physicalAnalysisId;

    @NotNull
    @JsonProperty("fertilityAnalysisId")
    @JsonAlias({"id_extrato_analise_fertilidade", "id_analise_fertilidade_solo", "id_analise_fertilidade", "id_extrato_fertilidade", "id_extrato_analise_fertilidade_solo"})
    private Long fertilityAnalysisId;
}
