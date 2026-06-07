package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("id_usuario_criador")
    private Long creatorUserId;
    @JsonProperty("nome_usuario_criador")
    private String creatorUserName;
    @JsonProperty("id_propriedade")
    private Long propertyId;
    @JsonProperty("nome_propriedade")
    private String propertyName;
    @JsonProperty("id_talhao")
    private Long plotId;
    @JsonProperty("identificacao_talhao")
    private String plotIdentification;
    @JsonProperty("tipo_recomendacao")
    private RecommendationType recommendationType;
    @JsonProperty("cultura")
    private NomeComum cropName;
    @JsonProperty("ano_safra")
    private Integer cropYear;
    @JsonProperty("criterio_calagem")
    private CriterioCalagem limingCriteria;
    @JsonProperty("origem_adubos")
    private FertilizerSourceOption origemAdubos;
    @JsonProperty("id_tabela_adubacao_cultura")
    private Long cropFertilizationTableId;
    @JsonProperty("grupo_tabela_adubacao_cultura")
    private TechnicalTableGroup cropFertilizationTableGroup;
    @JsonProperty("id_tabela_interpretacao_fertilidade_solo")
    private Long soilFertilityInterpretationCriteriaTableId;
    @JsonProperty("grupo_tabela_interpretacao_fertilidade_solo")
    private TechnicalTableGroup soilFertilityInterpretationCriteriaTableGroup;
    @JsonProperty("id_tabela_interpretacao_analise_foliar")
    private Long cropFoliarAnalysisInterpretationTableId;
    @JsonProperty("grupo_tabela_interpretacao_analise_foliar")
    private TechnicalTableGroup cropFoliarAnalysisInterpretationTableGroup;
    @JsonProperty("laudo_tecnico")
    private String technicalReport;
    @JsonProperty("imprimivel")
    private Boolean printable;
    @JsonProperty("criado_em")
    private LocalDateTime createdAt;
    @JsonProperty("atualizado_em")
    private LocalDateTime updatedAt;

    public static boolean isPrintableForRole(Cargo cargo) {
        return cargo == Cargo.AGRONOMO_RESIDENTE || cargo == Cargo.AGRONOMO_CONSULTOR;
    }
}
