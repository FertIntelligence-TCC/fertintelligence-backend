package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.*;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "NOME_INTERPRETACAO_FERTLIDADE_SOLO", nullable = false)
    private String name;

    @Column(name = "DESCRICAO_INTERPRETACAO_FERTLIDADE_SOLO")
    private String description;

    @Column(name = "REGIAO_INTERPRETACAO_FERTLIDADE_SOLO", nullable = false)
    private Regiao region;

    @Column(name = "OBSERVACOES")
    private String observations;

    @Column(name = "FONTES")
    private String sources;

    @Column(name = "TABELA_PUBLICA", nullable = false)
    @Builder.Default
    private boolean publicTable = false;

    /**
     * As classes a seguir serão implementadas separadamente, e são componentes dessa principal:
     * - Critérios para interpretar salinidade do solo;
     * - Critério para interpretar fertilidade do solo (P disponível com extrator Mehlich-1);
     * - Critério para interpretar fertilidade do solo (P disponivel com extrator Resina);
     * - Critério para interpretar fertilidade do solo (K);
     * - Critério para interpretar fertilidade do solo (S);
     * - Faixas de interpretação para diversos teores.
     */

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private AvailablePAnionExchangeResinExtractorModel availablePAnionExchangeResinExtractor;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private AvailablePMehlich1ExtractorModel availablePMehlich1Extractor;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private AvailableSModel availableS;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private DiverseContentRangeModel diverseContentRange;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private KExchangeableContentModel kExchangeableContent;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private SalinityInterpretationModel salinityInterpretation;

    public SoilFertilityInterpretationCriteriaTableResponseDto toDto() {
        return SoilFertilityInterpretationCriteriaTableResponseDto.builder()
                .id(this.id)
                .creator_id(this.creator != null ? this.creator.getId() : null)
                .creator_name(this.creator != null ? this.creator.getName() : null)
                .name(this.name)
                .description(this.description)
                .region(this.region)
                .observations(this.observations)
                .sources(this.sources)
                .public_table(this.publicTable)
                .build();
    }
}