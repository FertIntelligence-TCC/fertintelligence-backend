package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
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
     * As classes a seguir serão implementadas separadamente, e são componentes dessa principal:
     * - Critérios para interpretar salinidade do solo;
     * - Critério para interpretar fertilidade do solo (P disponível com extrator Mehlich-1);
     * - Critério para interpretar fertilidade do solo (P disponivel com extrator Resina);
     * - Critério para interpretar fertilidade do solo (K);
     * - Critério para interpretar fertilidade do solo (S);
     * - Faixas de interpretação para diversos teores.
     * Relacionamento @OneToOne entre essa classe e cada uma das demais.
     * Regra de negócio:
     * - A classe SoilFertilityInterpretationCriteriaTableModel.java deve possuir 1 instância
     * de cada uma das classes supracitadas.
     * - Adicionar essa condição aos testes de controlador.
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