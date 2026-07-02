package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.FertilizationTableUnit;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.FertilizationTableUnitConverter;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageResponseDto;
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
@Table(name = "COBERTURA")
public class CoverageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INTERVALO_TEORES", nullable = false)
    ContentRangeModel range;

    @Column(name = "ORDEM_COBERTURA", nullable = true)
    Integer order; // Indica se é a 1ª, 2ª, ... cobertura.

    @Column(name = "APLICACAO_RECOMENDADA_COBERTURA", nullable = true)
    Double application;

    @Convert(converter = FertilizationTableUnitConverter.class)
    @Column(name = "UNIDADE_APLICACAO_COBERTURA", nullable = false)
    @Builder.Default
    FertilizationTableUnit applicationUnit = FertilizationTableUnit.KG_PER_HA;

    @PrePersist
    @PreUpdate
    public void normalizeUnits() {
        this.applicationUnit = FertilizationTableUnit.KG_PER_HA;
    }

    public CoverageResponseDto toDto() {
        normalizeUnits();
        return CoverageResponseDto.builder()
                .id(this.id)
                .contentRangeId(this.range.getId())
                .order(this.order)
                .application(this.application)
                .applicationUnit(this.applicationUnit.getSymbol())
                .build();
    }

}
