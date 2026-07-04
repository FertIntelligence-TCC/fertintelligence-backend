package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel creator;

    @Column(name = "NOME_INTERPRETACAO_FERTLIDADE_SOLO", nullable = false)
    private String name;

    @Column(name = "DESCRICAO_INTERPRETACAO_FERTLIDADE_SOLO")
    private String description;

    @Column(name = "REGIAO_INTERPRETACAO_FERTLIDADE_SOLO", nullable = false)
    private Regiao region;

    @Column(name = "OBSERVACOES", length = 1000)
    private String observations;

    @Column(name = "FONTES", length = 1000)
    private String sources;

    @Column(name = "TABELA_PUBLICA", nullable = false)
    @Builder.Default
    private boolean publicTable = false;

    /**
     * As classes a seguir serão implementadas separadamente, e são componentes dessa principal:
     * - Critérios para interpretar salinidade do solo;
     * - Critério para interpretar fertilidade do solo (P disponível com extrator Mehlich-1);
     * - Critério para interpretar fertilidade do solo (P disponivel com extrator Resina);
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
    private SulfurDoseModel sulfurDose;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private DiverseContentRangeModel diverseContentRange;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private SalinityInterpretationModel salinityInterpretation;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private CtcSaturationModel ctcSaturation;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private ExchangeableBaseRatioModel exchangeableBaseRatio;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private RecommendedLimestoneTypeModel recommendedLimestoneType;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private KContentAndDoseModel kContentAndDose;

    @OneToOne(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PhosphorusClayPhosphateDoseModel phosphorusClayPhosphateDose;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<CorrectiveP2O5FertilizationModel> correctiveP2O5Fertilization = new ArrayList<>();

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<CorrectiveK2OFertilizationModel> correctiveK2OFertilization = new ArrayList<>();

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
                .correctiveP2O5Fertilization(this.correctiveP2O5Fertilization != null
                        ? this.correctiveP2O5Fertilization.stream()
                        .map(model -> com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto.builder()
                                .id(model.getId())
                                .tableId(this.id)
                                .displayName(CorrectiveP2O5FertilizationModel.DISPLAY_NAME)
                                .clayContentUnit(CorrectiveP2O5FertilizationModel.CLAY_CONTENT_UNIT)
                                .availablePMehlich1Unit(CorrectiveP2O5FertilizationModel.AVAILABLE_P_MEHLICH_1_UNIT)
                                .doseUnit(CorrectiveP2O5FertilizationModel.DOSE_UNIT)
                                .clayContentMinimum(model.getClayContentMinimum())
                                .clayContentMaximum(model.getClayContentMaximum())
                                .availablePMehlich1Minimum(model.getAvailablePMehlich1Minimum())
                                .availablePMehlich1Maximum(model.getAvailablePMehlich1Maximum())
                                .recommendedP2O5Dose(model.getRecommendedP2O5Dose())
                                .observations(model.getObservations())
                                .sources(model.getSources())
                                .build())
                        .toList()
                        : List.of())
                .correctiveK2OFertilization(this.correctiveK2OFertilization != null
                        ? this.correctiveK2OFertilization.stream()
                        .map(model -> com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto.builder()
                                .id(model.getId())
                                .tableId(this.id)
                                .displayName(CorrectiveK2OFertilizationModel.DISPLAY_NAME)
                                .ctcUnit(CorrectiveK2OFertilizationModel.CTC_UNIT)
                                .exchangeableKUnit(CorrectiveK2OFertilizationModel.EXCHANGEABLE_K_UNIT)
                                .doseUnit(CorrectiveK2OFertilizationModel.DOSE_UNIT)
                                .ctcMinimum(model.getCtcMinimum())
                                .ctcMaximum(model.getCtcMaximum())
                                .exchangeableKMinimum(model.getExchangeableKMinimum())
                                .exchangeableKMaximum(model.getExchangeableKMaximum())
                                .recommendedK2ODose(model.getRecommendedK2ODose())
                                .observations(model.getObservations())
                                .sources(model.getSources())
                                .build())
                        .toList()
                        : List.of())
                .build();
    }
}
