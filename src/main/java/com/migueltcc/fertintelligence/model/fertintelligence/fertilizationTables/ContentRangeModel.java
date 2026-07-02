package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.FertilizationTableUnit;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.FertilizationTableUnitConverter;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
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
@Table(name = "INTERVALO_TEOR_NUTRIENTE")
public class ContentRangeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    CropFertilizationTableModel table;

    @Column(name = "NUTRIENTE", nullable = false)
    Nutriente nutrient;

    @Column(name = "ORDEM_TEOR", nullable = true)
    Integer order;

    @Column(name = "MENOR_TEOR", nullable = true)
    Double smallest;

    @Column(name = "MAIOR_TEOR", nullable = true)
    Double largest;

    @Convert(converter = FertilizationTableUnitConverter.class)
    @Column(name = "UNIDADE_TEOR", nullable = true)
    @Builder.Default
    FertilizationTableUnit contentUnit = null;

    @Column(name = "APLICACAO_RECOMENDADA_PLANTIO", nullable = true)
    Double application;

    @Convert(converter = FertilizationTableUnitConverter.class)
    @Column(name = "UNIDADE_APLICACAO_PLANTIO", nullable = false)
    @Builder.Default
    FertilizationTableUnit applicationUnit = FertilizationTableUnit.KG_PER_HA;

    @PrePersist
    @PreUpdate
    public void normalizeUnits() {
        this.contentUnit = defaultContentUnit(this.nutrient);
        this.applicationUnit = FertilizationTableUnit.KG_PER_HA;
    }

    public ContentRangeResponseDto toDto() {
        normalizeUnits();
        return ContentRangeResponseDto.builder()
                .id(this.id)
                .tableId(this.table.getId())
                .nutrient(this.nutrient)
                .contentDescription(contentDescription(this.nutrient))
                .order(this.order)
                .smallest(this.smallest)
                .largest(this.largest)
                .contentUnit(this.contentUnit != null ? this.contentUnit.getSymbol() : null)
                .application(this.application)
                .applicationUnit(this.applicationUnit.getSymbol())
                .build();
    }

    private FertilizationTableUnit defaultContentUnit(Nutriente nutrient) {
        if (nutrient == Nutriente.FOSFORO) {
            return FertilizationTableUnit.MG_PER_DM3;
        }
        if (nutrient == Nutriente.POTASSIO) {
            return FertilizationTableUnit.MMOLC_PER_DM3;
        }
        return null;
    }

    private String contentDescription(Nutriente nutrient) {
        if (nutrient == Nutriente.FOSFORO) {
            return "Fósforo (P) disponível";
        }
        if (nutrient == Nutriente.POTASSIO) {
            return "Potássio (K) trocável";
        }
        if (nutrient == Nutriente.NITROGENIO) {
            return "Nitrogênio (N)";
        }
        return null;
    }
}
