package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_INTERVALOS")
@EqualsAndHashCode
public class RangeExtractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ANALISE", nullable = false)
    SoilAnalysisModel analysis;

    // Profundidade Inicial, em cm
    @Column(name = "PROFUNDIDADE_INICIAL", nullable = false)
    Integer profundidade_inicial;

    // Profundidade Final, em cm
    @Column(name = "PROFUNDIDADE_FINAL", nullable = false)
    Integer profundidade_final;

    public RangeExtractResponseDto toDto() {
        return RangeExtractResponseDto.builder()
                .id(this.id)
                .initialDepth(this.profundidade_inicial)
                .finalDepth(this.profundidade_final)
                .analysisId(this.analysis.getId())
                .analysisYear(this.analysis.getAnalysisYear())
                .responsibleLaboratory(this.analysis.getResponsibleLaboratory())
                .build();
    }

}
