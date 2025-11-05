package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ANALISES_SOLO")
@EqualsAndHashCode
public class SoilAnalysisModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TALHAO", nullable = false)
    PlotModel plot;

    @Column(name = "ANO_ANALISE", nullable = false)
    Integer analysisYear;

    @Column(name = "LABORATORIO_RESPONSAVEL", nullable = false)
    String responsibleLaboratory;

    @Column(name = "TIPO_EXTRATO", nullable = false)
    TipoExtrato extractType;

    public SoilAnalysisResponseDto toDto() {
        return SoilAnalysisResponseDto.builder()
                .id(this.id)
                .analysisYear(this.analysisYear)
                .responsibleLaboratory(this.responsibleLaboratory)
                .extractType(this.extractType)
                .plotId(this.plot.getId())
                .plotIdentification(this.plot.getIdentification())
                .build();
    }

}
