package com.migueltcc.fertintelligence.model.fertintelligence.fertigram;

import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "FERTIGRAMA")
public class FertigramModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ANALISE_FOLIAR", nullable = false)
    FoliarAnalysisModel foliarAnalysis;

    @ManyToOne
    @JoinColumn(name = "ID_TABELA_INTERPRETACAO_FOLIAR", nullable = false)
    CropFoliarAnalysisInterpretationTableModel table;

    @Column(name = "WARNING")
    String warning;

    public FertigramResponseDto toDto() {
        return FertigramResponseDto.builder()
                .id(id)
                .foliarAnalysisId(foliarAnalysis.getId())
                .tableId(table.getId())
                .cropName(foliarAnalysis.getCrop().getName())
                .warning(warning)
                .build();
    }
}
