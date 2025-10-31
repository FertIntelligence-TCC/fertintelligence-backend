package com.migueltcc.fertintelligence.dto.soilAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.Plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.Plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.Plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilAnalysisCreateRequestDto {

    @JsonProperty("ano_analise")
    @NotNull
    Integer analysisYear;

    @JsonProperty("laboratorio_responsavel")
    @NotNull
    String responsibleLaboratory;

    @JsonProperty("tipo_extrato")
    @NotNull
    TipoExtrato extractType;

    @JsonProperty("id_talhao")
    @NotNull
    Long plotId;

    @JsonProperty("identificacao_talhao")
    @NotBlank
    String plotIdentification;
}
