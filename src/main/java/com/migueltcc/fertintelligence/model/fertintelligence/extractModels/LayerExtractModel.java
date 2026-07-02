package com.migueltcc.fertintelligence.model.fertintelligence.extractModels;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_CAMADAS")
@EqualsAndHashCode
public class LayerExtractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ANALISE", nullable = false)
    SoilAnalysisModel analysis;

    // Profundidade Inicial, em cm
    @Column(name = "PROFUNDIDADE_INICIAL", nullable = false)
    Integer profundidade_inicial;

    // Profundidade Final, em cm
    @Column(name = "PROFUNDIDADE_FINAL", nullable = false)
    Integer profundidade_final;

    // Camada O, A, B, E, C
    @Column(name = "CAMADA", nullable = false)
    Camada layer;

    @Column(name = "SUBCAMADA", nullable = false)
    Integer sub_layer;

    public LayerExtractResponseDto toDto() {
        return LayerExtractResponseDto.builder()
                .id(this.id)
                .initialDepth(this.profundidade_inicial)
                .finalDepth(this.profundidade_final)
                .layer(this.layer)
                .subLayer(this.sub_layer)
                .analysisId(this.analysis.getId())
                .analysisYear(this.analysis.getAnalysisYear())
                .responsibleLaboratory(this.analysis.getResponsibleLaboratory())
                .build();
    }

}
