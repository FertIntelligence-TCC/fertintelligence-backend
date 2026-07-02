package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "TIPOS_DE_CALCARIO_RECOMENDADOS")
public class RecommendedLimestoneTypeModel {

    public static final String DISPLAY_NAME = "Tipos de calcário recomendados";
    public static final String LOW_LEGEND = "Calcário Calcídico (Teor de MgO menor que 5%)";
    public static final String MEDIUM_LEGEND = "Calcário Magnesiano (Teor de MgO entre 5% e 12%)";
    public static final String HIGH_LEGEND = "Calcário Dolomítico (Teor de MgO maior que 12%)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "RELACAO_CA_MG_BAIXA", nullable = false)
    Double caMgLowRatio;
    @Column(name = "RELACAO_CA_MG_MEDIA_MENOR_VALOR", nullable = false)
    Double caMgMediumLowerValue;
    @Column(name = "RELACAO_CA_MG_MEDIA_MAIOR_VALOR", nullable = false)
    Double caMgMediumHigherValue;
    @Column(name = "RELACAO_CA_MG_ALTA", nullable = false)
    Double caMgHighRatio;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public RecommendedLimestoneTypeResponseDto toDto() {
        return RecommendedLimestoneTypeResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .displayName(DISPLAY_NAME)
                .caMgLowRatio(this.caMgLowRatio)
                .caMgMediumLowerValue(this.caMgMediumLowerValue)
                .caMgMediumHigherValue(this.caMgMediumHigherValue)
                .caMgHighRatio(this.caMgHighRatio)
                .caMgLowLegend(LOW_LEGEND)
                .caMgMediumLowerLegend(MEDIUM_LEGEND)
                .caMgMediumHigherLegend(MEDIUM_LEGEND)
                .caMgHighLegend(HIGH_LEGEND)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
