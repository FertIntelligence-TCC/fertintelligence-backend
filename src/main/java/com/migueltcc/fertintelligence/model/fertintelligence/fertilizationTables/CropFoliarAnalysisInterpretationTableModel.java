package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
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
@Table(name = "TABELAS_DE_INTERPRETACAO_DE_ANÁLISE_FOLIAR_DE CULTURAS")
public class CropFoliarAnalysisInterpretationTableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel creator;

    @Column(name = "REGIAO_ANALISE_FOLIAR_CULTURAS", nullable = false)
    private Regiao region;

    public CropFoliarAnalysisInterpretationTableResponseDto toDto() {
        return CropFoliarAnalysisInterpretationTableResponseDto.builder()
                .id(this.id)
                .creator_id(this.creator.getId())
                .creator_name(this.creator.getName())
                .region(this.region)
                .build();
    }

}
