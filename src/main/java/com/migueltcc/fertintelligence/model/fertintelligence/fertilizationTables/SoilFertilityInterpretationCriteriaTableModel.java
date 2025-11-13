package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTableResponseDto;
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
@Table(name = "TABELAS_DE_CRITERIOS_DE_INTERPRETACAO_DA_FERTILIDADE_DO_SOLO")
public class SoilFertilityInterpretationCriteriaTableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel creator;

    @Column(name = "REGIAO_INTERPRETACAO_FERTLIDADE_SOLO", nullable = false)
    private Regiao region;

    /**
     * As classes a seguir serão implementadas separadamente (vínculo OneToMany ou similar):
     * - Critérios para interpretar salinidade do solo
     * - Critérios para interpretar fertilidade do solo (P Mehlich-1, P Resina, K, S)
     * - Faixas de interpretação para diversos teores
     */

    public SoilFertilityInterpretationCriteriaTableResponseDto toDto() {
        return SoilFertilityInterpretationCriteriaTableResponseDto.builder()
                .id(this.id)
                .creator_id(this.creator != null ? this.creator.getId() : null)
                .creator_name(this.creator != null ? this.creator.getName() : null)
                .region(this.region)
                .build();
    }
}