package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
import lombok.*;

import java.time.LocalDate;
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
    @JsonProperty("nome_pasta_recomendacao")
    private String recommendationFolderName;
    @JsonProperty("tipo_recomendacao")
    private RecommendationType recommendationType;
    @JsonProperty("cultura")
    private NomeComum cropName;
    @JsonProperty("ano_safra")
    private Integer cropYear;
    @JsonProperty("data_plantio")
    private Date cropPlantingDate;
    @JsonProperty("cliente_produtor_relatorio")
    private String reportClientProducer;
    @JsonProperty("propriedade_relatorio")
    private String reportPropertyName;
    @JsonProperty("municipio_relatorio")
    private String reportMunicipality;
    @JsonProperty("uf_relatorio")
    private String reportState;
    @JsonProperty("talhao_relatorio")
    private String reportPlotIdentification;
    @JsonProperty("area_avaliada_ha_relatorio")
    private Double reportEvaluatedAreaHa;
    @JsonProperty("responsavel_tecnico_relatorio")
    private String reportTechnicalResponsible;
    @JsonProperty("registro_profissional_relatorio")
    private String reportProfessionalRegistration;
    @JsonProperty("telefone_responsavel_relatorio")
    private String reportResponsiblePhone;
    @JsonProperty("email_responsavel_relatorio")
    private String reportResponsibleEmail;
    @JsonProperty("data_emissao_relatorio")
    private LocalDate reportIssueDate;
    @JsonProperty("autor_assinatura_relatorio")
    private String reportSignatureAuthor;
    @JsonProperty("criterio_calagem")
    private CriterioCalagem limingCriteria;
    @JsonProperty("classificacao_textural")
    private TexturalClassification texturalClassification;
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
    @JsonProperty("id_recomendacao_geral")
    private Long generalRecommendationId;
    @JsonProperty("recomendacao_geral_gerada")
    private Boolean generalRecommendationGenerated;
    @JsonProperty("nome_documento_recomendacao_geral")
    private String generalRecommendationDocumentName;
    @JsonProperty("recomendacao_geral")
    private GeneralRecommendationResponseDto generalRecommendation;
    @JsonProperty("id_recomendacao_resumida")
    private Long summaryRecommendationId;
    @JsonProperty("recomendacao_resumida_gerada")
    private Boolean summaryRecommendationGenerated;
    @JsonProperty("nome_documento_recomendacao_resumida")
    private String summaryRecommendationDocumentName;
    @JsonProperty("recomendacao_resumida")
    private SummaryRecommendationResponseDto summaryRecommendation;
    @JsonProperty("id_recomendacao_direta")
    private Long directRecommendationId;
    @JsonProperty("recomendacao_direta_gerada")
    private Boolean directRecommendationGenerated;
    @JsonProperty("nome_documento_recomendacao_direta")
    private String directRecommendationDocumentName;
    @JsonProperty("recomendacao_direta")
    private DirectRecommendationResponseDto directRecommendation;
    @JsonProperty("id_lista_compras")
    private Long shoppingListId;
    @JsonProperty("lista_compras_gerada")
    private Boolean shoppingListGenerated;
    @JsonProperty("nome_documento_lista_compras")
    private String shoppingListDocumentName;
    @JsonProperty("lista_compras")
    private ShoppingListResponseDto shoppingList;
    @JsonProperty("purchaseList")
    private PurchaseListResponseDto purchaseList;
    @JsonProperty("imprimivel")
    private Boolean printable;
    @JsonProperty("criado_em")
    private LocalDateTime createdAt;
    @JsonProperty("atualizado_em")
    private LocalDateTime updatedAt;

    public static boolean isPrintableForRole(Cargo cargo) {
        return cargo == Cargo.AGRONOMO_RESIDENTE
                || cargo == Cargo.AGRONOMO_CONSULTOR
                || cargo == Cargo.USUARIO_SUPREMO;
    }

    @JsonProperty("laudoTecnico")
    public String getLaudoTecnico() {
        return technicalReport;
    }
}
