package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationResponseDto;
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
@Table(name = "SATURACAO_NA_CTC")
public class CtcSaturationModel {

    public static final String DISPLAY_NAME = "Saturação na CTC(T), em %";
    public static final String DEFAULT_UNIT = "%";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "UNIDADE", nullable = false)
    @Builder.Default
    String unit = DEFAULT_UNIT;

    @Column(name = "K_BAIXO", nullable = false)
    Double kLow;
    @Column(name = "K_MEDIO_MENOR_TEOR", nullable = false)
    Double kMediumLowerContent;
    @Column(name = "K_MEDIO_MAIOR_TEOR", nullable = false)
    Double kMediumHigherContent;
    @Column(name = "K_ADEQUADO_MENOR_TEOR", nullable = false)
    Double kAdequateLowerContent;
    @Column(name = "K_ADEQUADO_MAIOR_TEOR", nullable = false)
    Double kAdequateHigherContent;
    @Column(name = "K_ALTO", nullable = false)
    Double kHigh;

    @Column(name = "CA_BAIXO", nullable = false)
    Double caLow;
    @Column(name = "CA_MEDIO_MENOR_TEOR", nullable = false)
    Double caMediumLowerContent;
    @Column(name = "CA_MEDIO_MAIOR_TEOR", nullable = false)
    Double caMediumHigherContent;
    @Column(name = "CA_ADEQUADO_MENOR_TEOR", nullable = false)
    Double caAdequateLowerContent;
    @Column(name = "CA_ADEQUADO_MAIOR_TEOR", nullable = false)
    Double caAdequateHigherContent;
    @Column(name = "CA_ALTO", nullable = false)
    Double caHigh;

    @Column(name = "MG_BAIXO", nullable = false)
    Double mgLow;
    @Column(name = "MG_MEDIO_MENOR_TEOR", nullable = false)
    Double mgMediumLowerContent;
    @Column(name = "MG_MEDIO_MAIOR_TEOR", nullable = false)
    Double mgMediumHigherContent;
    @Column(name = "MG_ADEQUADO_MENOR_TEOR", nullable = false)
    Double mgAdequateLowerContent;
    @Column(name = "MG_ADEQUADO_MAIOR_TEOR", nullable = false)
    Double mgAdequateHigherContent;
    @Column(name = "MG_ALTO", nullable = false)
    Double mgHigh;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    @PrePersist
    @PreUpdate
    private void normalizeUnit() {
        this.unit = DEFAULT_UNIT;
    }

    public CtcSaturationResponseDto toDto() {
        return CtcSaturationResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .displayName(DISPLAY_NAME)
                .unit(DEFAULT_UNIT)
                .kLow(this.kLow)
                .kMediumLowerContent(this.kMediumLowerContent)
                .kMediumHigherContent(this.kMediumHigherContent)
                .kAdequateLowerContent(this.kAdequateLowerContent)
                .kAdequateHigherContent(this.kAdequateHigherContent)
                .kHigh(this.kHigh)
                .caLow(this.caLow)
                .caMediumLowerContent(this.caMediumLowerContent)
                .caMediumHigherContent(this.caMediumHigherContent)
                .caAdequateLowerContent(this.caAdequateLowerContent)
                .caAdequateHigherContent(this.caAdequateHigherContent)
                .caHigh(this.caHigh)
                .mgLow(this.mgLow)
                .mgMediumLowerContent(this.mgMediumLowerContent)
                .mgMediumHigherContent(this.mgMediumHigherContent)
                .mgAdequateLowerContent(this.mgAdequateLowerContent)
                .mgAdequateHigherContent(this.mgAdequateHigherContent)
                .mgHigh(this.mgHigh)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
