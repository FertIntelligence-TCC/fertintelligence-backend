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
    public static final String LOW_LEGEND = "Calcário calcítico (Teor de MgO menor que 5%)";
    public static final String HIGH_LEGEND = "Calcário dolomítico (Teor de MgO igual ou maior que 5%)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "RELACAO_CA_MG_BAIXA", nullable = false)
    Double caMgLowRatio;
    @Deprecated
    @Column(name = "RELACAO_CA_MG_MEDIA_MENOR_VALOR")
    Double caMgMediumLowerValue;
    @Deprecated
    @Column(name = "RELACAO_CA_MG_MEDIA_MAIOR_VALOR")
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
                .caMgHighRatio(this.caMgHighRatio)
                .caMgLowLegend(LOW_LEGEND)
                .caMgHighLegend(HIGH_LEGEND)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
